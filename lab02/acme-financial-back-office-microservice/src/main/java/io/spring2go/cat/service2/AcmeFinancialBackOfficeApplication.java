package io.spring2go.cat.service2;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import io.spring2go.cathelper.CatRestInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootApplication
@RestController
public class AcmeFinancialBackOfficeApplication {

	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired RestTemplate restTemplate;
	@Value("${service3.address:localhost:9083}") String serviceAddress3;
	@Value("${service4.address:localhost:9084}") String serviceAddress4;
	private static final int MOCK_PORT = 8765;

	WireMock wireMock = new WireMock(MOCK_PORT);
	WireMockServer wireMockServer = new WireMockServer(MOCK_PORT);

	@PostConstruct
	public void setup() {
		wireMockServer.start();
		wireMock.register(any(urlMatching(".*")).willReturn(aResponse().withFixedDelay(3000)));
	}

	@PreDestroy
	public void shutdown() {
		wireMock.shutdown();
		wireMockServer.shutdown();
	}

	@RequestMapping("/startOfBackOffice-Service")
	public String service2MethodInController() throws InterruptedException {
		Thread.sleep(200);
		log.info("Hello from Acme Financial's Backend service. Calling Acme Financial's Account Microservice and then Customer Microservice");
		String service3 = restTemplate.getForObject("http://127.0.0.1:9083/startOfAccount-Microservice", String.class);
		log.info("Got response from Acme Financial's Account Service [{}]", service3);
		String service4 = restTemplate.getForObject("http://127.0.0.1:9084/startOfCustomer-Microservice", String.class);
		log.info("Got response from Acme Financial's Customer Service [{}]", service4);
		return String.format("Hello from Acme Financial's Backend service. Calling Acme Financial's Account Service [%s] and then Customer Service [%s]", service3, service4);
	}

	@RequestMapping("/readtimeout")
	public String connectionTimeout() throws InterruptedException {
		Transaction t = Cat.newTransaction(CatConstants.TYPE_REMOTE_CALL, "connectionTimeout");
		Thread.sleep(500);
		try {
			log.info("Calling a missing service");
			restTemplate.getForObject("http://localhost:" + MOCK_PORT + "/readtimeout", String.class);
			return "Should blow up";
		} catch(Exception e) {
			t.setStatus(e);
			Cat.getProducer().logError(e);
			throw e;
		} finally {
			t.complete();
		}
	}

	@Bean
	RestTemplate restTemplate() {
		SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(2000);
		clientHttpRequestFactory.setReadTimeout(3000);
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
		
		// 保存和传递调用链上下文
		restTemplate.setInterceptors(Collections.singletonList(new CatRestInterceptor()));
		
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override public boolean hasError(ClientHttpResponse response)
					throws IOException {
				try {
					return super.hasError(response);
				} catch (Exception e) {
					return true;
				}
			}

			@Override public void handleError(ClientHttpResponse response)
					throws IOException {
				try {
					super.handleError(response);
				} catch (Exception e) {
					log.error("Exception [" + e.getMessage() + "] occurred while trying to send the request", e);
					throw e;
				}
			}
		});
		return restTemplate;
	}

	public static void main(String... args) {
		SpringApplication.run(AcmeFinancialBackOfficeApplication.class, args);
	}
}


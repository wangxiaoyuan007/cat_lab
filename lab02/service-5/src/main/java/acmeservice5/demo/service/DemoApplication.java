package acmeservice5.demo.service;

import acmeservice5.demo.cathelper.CatRestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.invoke.MethodHandles;
import java.util.Collections;

@RestController
@SpringBootApplication
public class DemoApplication {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @RequestMapping("/service-5")
    public String start() throws InterruptedException {
        log.info("Welcome To service-5. Calling service-6");
        RestTemplate restTemplate = new RestTemplate();
        // 保存和传递调用链上下文
        restTemplate.setInterceptors(Collections.singletonList(new CatRestInterceptor()));
        String response = restTemplate.getForObject("http://127.0.0.1:9086/service-6", String.class);
        Thread.sleep(100);
        log.info("Got response from service-6 [{}]", response);
        return response;
    }




    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

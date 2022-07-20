package io.spring2go.cathelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.dianping.cat.Cat;
import com.dianping.cat.Cat.Context;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Transaction;

@Component
public class CatRestInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {

		Transaction t = Cat.newTransaction(CatConstants.TYPE_REMOTE_CALL, request.getURI().toString());

		try {
			HttpHeaders headers = request.getHeaders();

			// 保存和传递CAT调用链上下文
			Context ctx = new CatContext();
			Cat.logRemoteCallClient(ctx);
			headers.add(CatHttpConstants.CAT_HTTP_HEADER_ROOT_MESSAGE_ID, ctx.getProperty(Cat.Context.ROOT));
			headers.add(CatHttpConstants.CAT_HTTP_HEADER_PARENT_MESSAGE_ID, ctx.getProperty(Cat.Context.PARENT));
			headers.add(CatHttpConstants.CAT_HTTP_HEADER_CHILD_MESSAGE_ID, ctx.getProperty(Cat.Context.CHILD));
//			Cat.logMetricForCount("metric-count-one", 1);//C
//			Cat.logMetricForCount("metric-count-not-num");//C
//			Cat.logMetricForDuration("metric-duration-two", 2);//SC
//			Cat.logMetricForDuration("metric-duration-cacluete", 3*100);//SC
//			Map<String, String> m = new HashMap<>();
//			m.put("k","v");
//			m.put("k2","v2");
//			Cat.logMetricForCount("metric-count-one-map", 1,m);//TC
//			Cat.logMetricForCount("metric-count-not-num-map",m);//TC
//			Cat.logMetricForDuration("metric-duration-two-map", 2,m);//TD
//			Cat.logMetricForDuration("metric-duration-cacluete-map", 3*100,m);//TD
			// 保证请求继续被执行
			ClientHttpResponse response =  execution.execute(request, body);
			t.setStatus(Transaction.SUCCESS);
			return response;
		} catch (Exception e) {
			Cat.getProducer().logError(e);
			t.setStatus(e);
			throw e;
		} finally {
			t.complete();
		}
	}
}
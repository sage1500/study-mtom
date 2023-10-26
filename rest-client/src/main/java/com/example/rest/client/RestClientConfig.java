package com.example.rest.client;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.example.api.rest.client.api.PetsApi;
import com.example.api.rest.client.invoker.ApiClient;

@Configuration(proxyBeanMethods = false)
public class RestClientConfig {

	@Value("${demo.connect-timeout:10000}")
	private int connectTimeout = 10000;

	@Value("${demo.socket-timeout:15000}")
	private int socketTimeout = 15000;
	
	
	@Bean
	public PetsApi petApi() {
		
		// ConnectionManager
		// 【設定】
		// - コネクションプーリング
		// @formatter:off
		var connectionManager = 	PoolingHttpClientConnectionManagerBuilder.create()
			.setMaxConnPerRoute(20)
			.setMaxConnTotal(20)
			//.setDefaultSocketConfig(null)
			.setDefaultConnectionConfig(ConnectionConfig.custom()
					.setConnectTimeout(Timeout.ofMilliseconds(connectTimeout))
					.setSocketTimeout(Timeout.ofMilliseconds(socketTimeout))
					.build())
			.build();
		// @formatter:on
		
		// HttpClient
		// 【設定】
		// - 再送信
		// - プロキシ
		// @formatter:off
		var httpClient = HttpClientBuilder.create()
				.setConnectionManager(connectionManager)
				.setConnectionManagerShared(true)
				// .setBackoffManager(null)
				// .setConnectionBackoffStrategy(null)
				// .setConnectionReuseStrategy(null)
				// .setRetryStrategy(null) // デフォルトは new DefaultHttpRequestRetryStrategy()
				//.setRetryStrategy(new DefaultHttpRequestRetryStrategy(2, TimeValue.ofSeconds(1))) //
				.setRetryStrategy(new DefaultHttpRequestRetryStrategy(0, TimeValue.ofSeconds(1))) //
				// .setProxy(null)
				.build();
		// @formatter:on

		// RequestFactory
		// 【設定】
		// - タイムアウト
		var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectionRequestTimeout(connectTimeout); // コネクションプールから取得するタイムアウト
		requestFactory.setConnectTimeout(connectTimeout);

		// RestTemplate
        DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
        uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
		var restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(requestFactory);
        restTemplate.setUriTemplateHandler(uriBuilderFactory);

		// ApiClient
		// 【設定】
		// - ベースパス
		var apiClient = new ApiClient(restTemplate) //
					.setBasePath("http://localhost:8080"); // memo: 最後に "/" をつけない
		
		// PetStore
		return new PetsApi(apiClient);
	}
	
}

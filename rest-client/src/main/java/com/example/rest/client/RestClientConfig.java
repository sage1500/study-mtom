package com.example.rest.client;

import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.example.api.rest.client.api.PetsApi;
import com.example.api.rest.client.invoker.ApiClient;

@Configuration(proxyBeanMethods = false)
public class RestClientConfig {

	@Bean
	public PetsApi petApi() {
		
		// ConnectionManager
		// 【設定】
		// - コネクションプーリング
		// @formatter:off
		var connectionManager = 	PoolingHttpClientConnectionManagerBuilder.create()
			.setMaxConnPerRoute(10)
			.setMaxConnTotal(10)
			.build();
		// @formatter:on
		
		// HttpClient
		// 【設定】
		// - 再送信
		// - プロキシ
		// @formatter:off
		var httpClient = HttpClientBuilder.create()
				// .setBackoffManager(null)
				// .setConnectionBackoffStrategy(null)
				.setConnectionManager(connectionManager)
				.setConnectionManagerShared(true)
				// .setConnectionReuseStrategy(null)
				// .setProxy(null)
				// .setRetryStrategy(null)
				.build();
		// @formatter:on

		// RequestFactory
		// 【設定】
		// - タイムアウト
		var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectionRequestTimeout(10);
		requestFactory.setConnectTimeout(10);

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

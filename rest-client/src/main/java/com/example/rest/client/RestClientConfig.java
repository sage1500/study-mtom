package com.example.rest.client;

import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.example.api.rest.client.api.PetsClient;
import com.example.api.rest.client.invoker.ApiClient;

@Configuration(proxyBeanMethods = false)
public class RestClientConfig {

	@Value("${demo.connect-timeout:10000}")
	private int connectTimeout = 10000;

	@Value("${demo.socket-timeout:15000}")
	private int socketTimeout = 15000;

	@Value("${demo.max-conn-per-route:20}")
	private int maxConnPerRoute = 20;

	@Value("${demo.max-conn-total:20}")
	private int maxConnTotal = 20;

	@Value("${demo.max-retries:1}")
	private int maxRetries = 1;

	@Value("${demo.retry-interval:1000}")
	private int retryInterval = 1000;

	@Value("${demo.base-url:http://localhost:8080}")
	private String baseUrl = "http://localhost:8080";

	@Bean
	public RestTemplate restTemplateForPetsApi() {
		// ConnectionManager
		var connectionManager = PoolingHttpClientConnectionManagerBuilder.create() //
				.setDefaultConnectionConfig(ConnectionConfig.custom() //
						.setConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS) //
						.setSocketTimeout(socketTimeout, TimeUnit.MILLISECONDS) //
						.build()) //
				.setMaxConnPerRoute(maxConnPerRoute) //
				.setMaxConnTotal(maxConnTotal) //
				.build();

		// HttpClient
		var httpClient = HttpClientBuilder.create() //
				.setConnectionManager(connectionManager) //
				.setConnectionManagerShared(true) //
				.setRetryStrategy(
						new DefaultHttpRequestRetryStrategy(maxRetries, TimeValue.ofMilliseconds(retryInterval))) //
				.addExecInterceptorFirst("logging", new RestClientInterceptor()) //
				.build();
		
		// RequestFactory
		var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		requestFactory.setConnectionRequestTimeout(connectTimeout);
		
		// RestTemplate
		DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
		uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
		var restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(requestFactory);
		restTemplate.setUriTemplateHandler(uriBuilderFactory);

		return restTemplate;
	}

	@Bean
	public PetsClient petApi(RestTemplate restTemplateForPetsApi) {
		var apiClient = new ApiClient(restTemplateForPetsApi) //
				.setBasePath(baseUrl);
		return new PetsClient(apiClient);
	}

}

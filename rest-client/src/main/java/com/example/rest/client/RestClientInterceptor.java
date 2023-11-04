package com.example.rest.client;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChain.Scope;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestClientInterceptor implements ExecChainHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClassicHttpResponse execute(ClassicHttpRequest request, Scope scope, ExecChain chain)
			throws IOException, HttpException {
		try {
			logRequest(request);
			var response = chain.proceed(request, scope);
			logResponse(response);
			return response;
		} catch (IOException | HttpException e) {
			log.info("[REST-FAILED]");
			throw e;
		} catch (RuntimeException e) {
			log.info("[REST-FAILED]");
			throw e;
		}
	}

	private void logRequest(ClassicHttpRequest request) {
		try {
			log.info("[REST-REQ] {} {}", request.getMethod(), request.getUri());
		} catch (URISyntaxException e) {
		}
	}

	private void logResponse(ClassicHttpResponse response) {
		var code = response.getCode();
		var entity = response.getEntity();
		var contentType = entity.getContentType();
		var contentLength = entity.getContentLength();
		log.info("[REST-RSP] code={} type={} length={}", code, contentType, contentLength);
	}
}

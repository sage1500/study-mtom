package com.samples.mtom.ws;

import java.util.Locale;
import java.util.Map;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.MethodEndpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.interceptor.EndpointInterceptorAdapter;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpServletConnection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class PayloadRootPathValidationEndpointInterceptor extends EndpointInterceptorAdapter {
	
	private final Map<String, String> payloadRootPathMap;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
		var connection = TransportContextHolder.getTransportContext().getConnection();
		
		if (!(endpoint instanceof MethodEndpoint me) || !(connection instanceof HttpServletConnection sc)) {
			return fault(messageContext, "system error");
		}
		
		var payloadRoot = me.getMethod().getDeclaredAnnotation(PayloadRoot.class);
		if (payloadRoot == null) {
			return fault(messageContext, "missing PayloadRoot annotation");
		}
		
		var localPart = payloadRoot.localPart();
		var expectedPath = payloadRootPathMap.get(localPart);
		if (expectedPath == null) {
			return fault(messageContext, "unknown payload-root: " + localPart);
		}
		
		var servletPath = sc.getHttpServletRequest().getServletPath();
		if (!expectedPath.equals(servletPath)) {
			return fault(messageContext, "unexpected path: payload-root=" + localPart + " path=" + servletPath);
		}
		
		return true;
	}
	
	private boolean fault(MessageContext messageContext, String reason) {
		log.info("★bad request: reason={}", reason);
		
		if (messageContext.getResponse() instanceof SoapMessage response) {
			response.getSoapBody().addClientOrSenderFault(reason, Locale.ENGLISH);
		}
		
		return false;
	}
}

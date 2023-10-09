package com.samples.mtom.ws;

import org.springframework.core.MethodParameter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;

public class MyMethodArgumentResolver implements MethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return Document.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MessageContext messageContext, MethodParameter parameter) throws Exception {
		if (messageContext.getRequest() instanceof SoapMessage soap) {
			return soap.getDocument();
		}
		return null;
	}

}

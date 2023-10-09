package com.samples.mtom.ws;

import java.util.HashMap;

import org.springframework.core.MethodParameter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

public class JaxWsMessageContextMethodArgumentResolver implements MethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return jakarta.xml.ws.handler.MessageContext.class.equals(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MessageContext messageContext, MethodParameter parameter) throws Exception {
		var headers = new HashMap<String, Object>();
		
		if (messageContext.getRequest() instanceof SaajSoapMessage saaj) {
			
			for (var i = saaj.getSaajMessage().getMimeHeaders().getAllHeaders(); i.hasNext();) {
				var h = i.next();
				headers.put(h.getName(), h.getValue());
			}
		}
		
		var ctx = new HashMapJaxWsMessageContext();
		ctx.put( jakarta.xml.ws.handler.MessageContext.HTTP_REQUEST_HEADERS, headers);
		return ctx;
	}

}

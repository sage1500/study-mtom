package org.springframework.ws.samples.mtom.client.sws;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyClientInterceptor extends ClientInterceptorAdapter {

	private static Pattern STATUS_PAT = Pattern.compile("^.*\\[(\\d+)\\]$");

	@Override
	public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
		// HTTP_RESPONSE_CODE
		var status = getResponseCode(ex);
		messageContext.setProperty(jakarta.xml.ws.handler.MessageContext.HTTP_RESPONSE_CODE, status);

		//
		var headers = getResponseHeaders(messageContext);
		messageContext.setProperty(jakarta.xml.ws.handler.MessageContext.HTTP_RESPONSE_HEADERS, status);

		log.info("★status: {}", status);
		log.info("★headers: {}", headers);
	}

	private int getResponseCode(Exception e) {
		if (e != null) {
			var m = STATUS_PAT.matcher(e.getMessage());
			if (m.matches()) {
				return Integer.parseInt(m.group(1));
			}
		}
		return 999;
	}

	private Map<String, Object> getResponseHeaders(MessageContext messageContext) {
		if (messageContext.getResponse() instanceof SaajSoapMessage saaj) {
			var headers = new HashMap<String, Object>();
			for (var i = saaj.getSaajMessage().getMimeHeaders().getAllHeaders(); i.hasNext();) {
				var h = i.next();
				headers.put(h.getName(), h.getValue());
			}
			return headers;
		}
		return Collections.emptyMap();
	}

}

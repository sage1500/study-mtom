package com.samples.mtom.ws;

import java.util.HashMap;

import jakarta.xml.ws.handler.MessageContext;

public class HashMapJaxWsMessageContext extends HashMap<String, Object> implements MessageContext {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScope(String name, Scope scope) {
		// 何もしない
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Scope getScope(String name) {
		// 何もしない
		return null;
	}
}

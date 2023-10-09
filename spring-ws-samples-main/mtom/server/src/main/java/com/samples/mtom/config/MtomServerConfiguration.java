package com.samples.mtom.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurer;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.samples.mtom.schema.StoreContentResponse;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.server.endpoint.adapter.DefaultMethodEndpointAdapter;
import org.springframework.ws.server.endpoint.adapter.method.MarshallingPayloadMethodProcessor;
import org.springframework.ws.server.endpoint.adapter.method.MethodArgumentResolver;
import org.springframework.ws.server.endpoint.interceptor.EndpointInterceptorAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;

import com.samples.mtom.ws.JaxWsMessageContextMethodArgumentResolver;
import com.samples.mtom.ws.MyMethodArgumentResolver;
import com.samples.mtom.ws.PayloadRootPathValidationEndpointInterceptor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arjen Poutsma
 */
@Configuration
@EnableWs
@Slf4j
public class MtomServerConfiguration {

	private static final Map<String, String> PAYLOADROOT_PATH_MAPPINGS = Map.ofEntries(//
			Map.entry("StoreContentRequest", "/services/store"), //
			Map.entry("LoadContentRequest", "/services/load"));
	
	
	@Bean
	WsConfigurer wsConfigure() {
		return new WsConfigurerAdapter() {

			@Override
			public void addInterceptors(List<EndpointInterceptor> interceptors) {
				interceptors.add(new PayloadRootPathValidationEndpointInterceptor(PAYLOADROOT_PATH_MAPPINGS));

				interceptors.add(new EndpointInterceptorAdapter() {
					@Override
					public boolean handleRequest(MessageContext messageContext, Object endpoint) throws Exception {
						messageContext.setProperty("testProp", "test");
						return true;
					}
				});
			}

			@Override
			public void addArgumentResolvers(List<MethodArgumentResolver> argumentResolvers) {
				argumentResolvers.add(new MethodArgumentResolver() {
					
					@Override
					public boolean supportsParameter(MethodParameter parameter) {
						log.info("★ argument resolver: {}", parameter.getParameterType());
						return false;
					}
					
					@Override
					public Object resolveArgument(MessageContext messageContext, MethodParameter parameter) throws Exception {
						return null;
					}
				});
				argumentResolvers.add(new MyMethodArgumentResolver());
				argumentResolvers.add(new JaxWsMessageContextMethodArgumentResolver());
			}
		};
	}

	@Bean
	ServletRegistrationBean<?> webServicesRegistration(ApplicationContext ctx) {
		MessageDispatcherServlet messageDispatcherServlet = new MessageDispatcherServlet();
		messageDispatcherServlet.setApplicationContext(ctx);
		messageDispatcherServlet.setTransformWsdlLocations(true);
		
		return new ServletRegistrationBean<>(messageDispatcherServlet,
				PAYLOADROOT_PATH_MAPPINGS.values().toArray(new String[0]));
	}

	@Bean
	public Jaxb2Marshaller marshaller() {

		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// marshaller.setContextPath("org.springframework.ws.samples.mtom.schema");
		// marshaller.setClassesToBeBound(StoreContentRequest.class,
		// StoreContentResponse.class);
		marshaller.setClassesToBeBound(StoreContentResponse.class);
		marshaller.setMtomEnabled(true);
		return marshaller;
	}

	@Bean
	public MarshallingPayloadMethodProcessor methodProcessor(Jaxb2Marshaller marshaller) {
		return new MarshallingPayloadMethodProcessor(marshaller);
	}

	@Bean
	DefaultMethodEndpointAdapter endpointAdapter(MarshallingPayloadMethodProcessor methodProcessor) {

		DefaultMethodEndpointAdapter adapter = new DefaultMethodEndpointAdapter();
		adapter.setMethodArgumentResolvers(Collections.singletonList(methodProcessor));
		adapter.setMethodReturnValueHandlers(Collections.singletonList(methodProcessor));
		return adapter;
	}

	@Bean
	public SimpleWsdl11Definition contentStore() {

		SimpleWsdl11Definition definition = new SimpleWsdl11Definition();
		definition.setWsdl(new ClassPathResource("/contentStore.wsdl"));
		return definition;
	}

}

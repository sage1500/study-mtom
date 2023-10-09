/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.samples.mtom.client.sws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.client.support.interceptor.ClientInterceptorAdapter;
import org.springframework.ws.context.MessageContext;

import jakarta.activation.DataHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple client that demonstrates MTOM by invoking {@code StoreImage} and {@code LoadImage} using a WebServiceTemplate
 * and SAAJ.
 *
 * @author Tareq Abed Rabbo
 * @author Arjen Poutsma
 */
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class SaajMtomClient {

	public static void main(String[] args) {
		SpringApplication.run(SaajMtomClient.class, args);
	}

	@Bean
	CommandLineRunner invoke(SaajMtomClient saajClient) {
		return args -> {
			saajClient.storeContent();
			saajClient.loadContent();
		};
	}

	private final WebServiceTemplate webServiceTemplate;	
	
	private ObjectFactory objectFactory = new ObjectFactory();

	public void storeContent() {
		StoreContentRequest storeContentRequest = this.objectFactory.createStoreContentRequest();

		storeContentRequest.setName("spring-ws-logo");
		storeContentRequest
				.setContent(new DataHandler(Thread.currentThread().getContextClassLoader().getResource("spring-ws-logo.png")));

		var clientInterceptor = new ClientInterceptorAdapter() {
			@Override
			public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {
				log.info("★afterCompletion: exception", ex);
				log.info("★afterCompletion: response: {}", messageContext.getResponse());
			}
		};
		
		webServiceTemplate.setInterceptors(new ClientInterceptor[] { clientInterceptor, new MyClientInterceptor() });
		try {
//		webServiceTemplate.marshalSendAndReceive("http://localhost:8080/mtom-server/services/store2", storeContentRequest);
			webServiceTemplate.marshalSendAndReceive("https://localhost:8443/mtom-server/services/store", storeContentRequest);
//		webServiceTemplate.marshalSendAndReceive("http://localhost:8080/mtom-server/services/load", storeContentRequest);
		} catch (RuntimeException e) {
			log.info("★send fail.", e);
			throw e;
		}
	}

	public void loadContent() throws IOException {
		LoadContentRequest loadContentRequest = this.objectFactory.createLoadContentRequest();
		loadContentRequest.setName("spring-ws-logo");
		String tmpDir = System.getProperty("java.io.tmpdir");
		File out = new File(tmpDir, "spring_mtom_tmp.bin");
		LoadContentResponse loadContentResponse = (LoadContentResponse) webServiceTemplate
				.marshalSendAndReceive("https://localhost:8443/mtom-server/services/load", loadContentRequest);
		DataHandler content = loadContentResponse.getContent();
		saveContentToFile(content, out);
	}

	private static long saveContentToFile(DataHandler content, File outFile) throws IOException {

		long size = 0;

		byte[] buffer = new byte[1024];
		try (InputStream in = content.getInputStream()) {
			try (OutputStream out = new FileOutputStream(outFile)) {
				for (int readBytes; (readBytes = in.read(buffer, 0, buffer.length)) > 0;) {
					size += readBytes;
					out.write(buffer, 0, readBytes);
				}
			}
		}

		return size;
	}
}

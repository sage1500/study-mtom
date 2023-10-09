package org.springframework.ws.samples.mtom.client.sws;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedTrustManager;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponents5MessageSender;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arjen Poutsma
 */
@Configuration
@Slf4j
public class MtomClientConfig {

	@Bean
	public SaajMtomClient saajClient(WebServiceTemplate mtomWebServiceTemplate) {
		SaajMtomClient client = new SaajMtomClient(mtomWebServiceTemplate);
		return client;
	}

	@Bean
	public WebServiceTemplate mtomWebServiceTemplate(SaajSoapMessageFactory messageFactory,
			Jaxb2Marshaller marshaller, SSLContext sslContext) {
		var template = new WebServiceTemplate();
		template.setDefaultUri("https://localhost:8443/mtom-server/services");
		template.setMarshaller(marshaller);
		template.setUnmarshaller(marshaller);

//		var requestConfig = RequestConfig.custom()
//				.build();

		// @formatter:off
		var connectionConfig = ConnectionConfig.custom()
				.setConnectTimeout(10, TimeUnit.SECONDS)
				.setSocketTimeout(10, TimeUnit.SECONDS)
				.build();
		var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
				.setDefaultConnectionConfig(connectionConfig)
				.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext))
				.build();
		var httpClient = HttpClientBuilder.create()
				// .setBackoffManager(null)
				.setConnectionManager(connectionManager)
				// .setProxy(null)
				.build();
		// @formatter:on

		var sender = new HttpComponents5MessageSender(httpClient);

		template.setMessageSender(sender);

		return template;
	}

	@Bean
	public SaajSoapMessageFactory saajSoapMessageFactory() {
		return new SaajSoapMessageFactory();
	}

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("org.springframework.ws.samples.mtom.client.sws");
		marshaller.setMtomEnabled(true);
		return marshaller;
	}

	@Bean
	public SSLContext sslContext() throws Exception {
		String keystoreFile = "data/mtomTrustStore";
		String password = "password";

		// キーストアファイルをロード
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		try (InputStream in = new FileInputStream(keystoreFile)) {
			keystore.load(in, password.toCharArray());
		}
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keystore, password.toCharArray());

		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keystore);
		var originalTrustManagers = Arrays.asList(trustManagerFactory.getTrustManagers());
//		var newTrustManagers = new ArrayList<>(originalTrustManagers);
		var newTrustManagers = new ArrayList<TrustManager>();
		newTrustManagers.add(new X509ExtendedTrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
			}
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				log.info("★checkServerTrusted 1");
				throw new CertificateException("test");
			}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				this.checkServerTrusted(chain, authType);
			}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				this.checkServerTrusted(chain, authType);
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		});
		newTrustManagers.add(new X509ExtendedTrustManager() {
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			}
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
			}
			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				log.info("★checkServerTrusted 2");
			}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				this.checkServerTrusted(chain, authType);
			}
			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				this.checkServerTrusted(chain, authType);
			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		});
		var nt =  newTrustManagers.toArray(new TrustManager[0]);
		
		// メモ：trustManagers を配列で指定しているが最初の trustManager の結果しか採用していないっぽい。
		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(keyManagerFactory.getKeyManagers(), /*trustManagerFactory.getTrustManagers()*/nt, new SecureRandom());

		return sslContext;
	}
}

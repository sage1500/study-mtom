/*
 * Copyright 2005-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.samples.mtom.ws;

import java.io.File;
import java.io.IOException;

import org.springframework.util.Assert;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.samples.mtom.schema.LoadContentRequest;
import org.springframework.ws.samples.mtom.schema.LoadContentResponse;
import org.springframework.ws.samples.mtom.schema.ObjectFactory;
import org.springframework.ws.samples.mtom.schema.StoreContentRequest;
import org.springframework.ws.samples.mtom.schema.StoreContentResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.samples.mtom.service.ContentRepository;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import lombok.extern.slf4j.Slf4j;

/** @author Arjen Poutsma */
@Endpoint
@Slf4j
public class ContentRepositoryEndpoint {

	private ContentRepository contentRepository;

	private ObjectFactory objectFactory;

	public ContentRepositoryEndpoint(ContentRepository contentRepository) {

		Assert.notNull(contentRepository, "'imageRepository' must not be null");

		this.contentRepository = contentRepository;
		this.objectFactory = new ObjectFactory();
	}

	@PayloadRoot(localPart = "StoreContentRequest", namespace = "http://www.springframework.org/spring-ws/samples/mtom")
	@ResponsePayload
	public StoreContentResponse store(@RequestPayload StoreContentRequest storeContentRequest, MessageContext ctx)
			throws IOException {

		// メモ
		// 通常の unmashller では、
		// MTOMDecoder.startElement() で xop:include を見て特殊処理している。
		/*
    @Override
    public void startElement(TagName tagName) throws SAXException {
        if(tagName.local.equals("Include") && tagName.uri.equals(WellKnownNamespace.XOP)) {
            // found xop:Include
            String href = tagName.atts.getValue("href");
            DataHandler attachment = au.getAttachmentAsDataHandler(href);
            if(attachment==null) {
                // report an error and ignore
                parent.getEventHandler().handleEvent(null);
                // TODO
            }
            base64data.set(attachment);
            next.text(base64data);
            inXopInclude = true;
            followXop = true;
        } else
            next.startElement(tagName);
    }
		 * 
		 */
		// 内部的には以下を利用しているが private クラスのため直接利用できない。
		// org.springframework.oxm.jaxb.Jaxb2Marshaller.Jaxb2AttachmentUnmarshaller.getAttachmentAsDataHandler(String)
		// 
		// そのスーパークラスの AttachmentUnmarshaller は public なクラス（クラスだが基本的に interface と程度の定義しかないが)
		
		log.info("★ctx: {}", ctx);
		var req = (SaajSoapMessage)ctx.getRequest();
		var reqSrc = req.getPayloadSource();
		log.info("★req: {}", req);
		log.info("★reqSrc: {}", reqSrc);
		
		var doc = req.getDocument();
		log.info("★req.doc: {}", doc);
		dumpDocument(doc);
		
		var content = storeContentRequest.getContent().getContent();
		log.info("★req.content: {}", content);
		
		this.contentRepository.storeContent(storeContentRequest.getName(), storeContentRequest.getContent());
		StoreContentResponse response = this.objectFactory.createStoreContentResponse();
		response.setMessage("Success");
		return response;
	}

	@PayloadRoot(localPart = "LoadContentRequest", namespace = "http://www.springframework.org/spring-ws/samples/mtom")
	@ResponsePayload
	public LoadContentResponse load(@RequestPayload LoadContentRequest loadContentRequest, MessageContext ctx) throws IOException {
		log.info("★ctx: {}", ctx);
		var req = (SaajSoapMessage)ctx.getRequest();
		var reqSrc = req.getPayloadSource();
		log.info("★req: {}", req);
		log.info("★reqSrc: {}", reqSrc);
		
		var doc = req.getDocument();
		log.info("★req.doc: {}", doc);
		dumpDocument(doc);

		LoadContentResponse response = this.objectFactory.createLoadContentResponse();

		File contentFile = this.contentRepository.loadContent(loadContentRequest.getName());
		DataHandler dataHandler = new DataHandler(new FileDataSource(contentFile));
		response.setName(loadContentRequest.getName());
		response.setContent(dataHandler);
		return response;
	}
	
	void dumpDocument(Document doc) {
		dumpNode(doc, " ");
	}
	
	void dumpNode(Node node, String index) {
		switch (node.getNodeType()) {
		case Node.TEXT_NODE:
			log.info("{}{}", index, node.getTextContent());
			break;
		case Node.DOCUMENT_NODE:
		case Node.ELEMENT_NODE:
			log.info("{}{}[{}]", index, node.getLocalName(), toStringFromAttr(node.getAttributes()));
			break;
		default:
			log.info("{}node-type={}", index, node.getNodeType());
		}
		
		var children = node.getChildNodes();
		var n = children.getLength();
		for (int i = 0; i < n; i++) {
			var child = children.item(i);
			dumpNode(child, index + " ");
		}
		
	}
	
	String toStringFromAttr(NamedNodeMap attrs) {
		if (attrs == null) {
			return "";
		}
		
		var str = new StringBuilder();
		var n = attrs.getLength();
		for (int i = 0; i < n; i++) {
			var attr = attrs.item(i);
			str.append(attr.getNodeName()).append("=\"").append(attr.getNodeValue()).append("\" ");
		}

		var len = str.length();
		if (len > 0) {
			str.delete(len-1, len);
		}
		return str.toString();
	}

}

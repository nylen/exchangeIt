package com.byarger.exchangeit;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class InboxHasNewMail extends WebDavBase {

	public InboxHasNewMail(String inboxURL, String username, String password) {
		super(inboxURL, username, password);
	}

	public Integer getUnreadMailCount(DefaultHttpClient client)
			throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		NewMailSearch request = new NewMailSearch(getUrl());
		request.setHeader("Depth", "0");

		String content = request.generateRequestBody();
		StringEntity entity = new StringEntity(content);
		entity.setContentType("text/xml;");
		request.setEntity(entity);

		client.getCredentialsProvider().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(getUsername(), getPassword()));

		HttpResponse response = client.execute(request);

		if (response.getStatusLine().getStatusCode() >= 400
				&& response.getStatusLine().getStatusCode() < 500) {
			if (!authenticate(client, getUrl(), getUsername(), getPassword())) {
				throw new RuntimeException("Error authenticating to exchange");
			}
			response = client.execute(request);
		}
		if (response.getStatusLine().getStatusCode() >= 300) {
			throw new RuntimeException("Error - got back status code "
					+ response.getStatusLine().getStatusCode());
		}

		InputStream is = response.getEntity().getContent();
		Document responseDocument = builder.parse(new InputSource(is));
		NodeList list = null;
		if (responseDocument != null) {
			Element multistatus = responseDocument.getDocumentElement();
			list = multistatus.getChildNodes();
		}

		if (list != null) {
			for (int i = 0; i < list.getLength(); i++) {
				Element child = (Element) list.item(i);

				String countString = getSingleValue(child
						.getElementsByTagName("d:unreadcount"));
				try {
					Integer count = Integer.valueOf(countString);
					return count;
				} catch (NumberFormatException nfe) {
					// nothing to do here
				}
			}
		}
		return null;
	}
}

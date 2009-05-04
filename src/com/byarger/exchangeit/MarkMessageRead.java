package com.byarger.exchangeit;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

public class MarkMessageRead extends WebDavBase {

	public MarkMessageRead(String inboxURL, String username, String password) {
		super(inboxURL, username, password);
	}

	public void markRead(DefaultHttpClient client, String uri, boolean read)
			throws IOException, ParserConfigurationException, SAXException {

		MarkMessageReadCommand request = new MarkMessageReadCommand(getUrl(),
				read);
		request.setHeader("If-Match", "*");
		request.setHeader("Brief", "t");

		String content = request.generateRequestBody(uri);
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

	}
}

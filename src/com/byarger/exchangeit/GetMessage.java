package com.byarger.exchangeit;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.android.email.mail.MessagingException;
import com.android.email.mail.internet.MimeMessage;

public class GetMessage extends WebDavBase {

	public GetMessage(String path, String username, String password) {
		super(path, username, password);
	}

	public MimeMessage getMessageContents(DefaultHttpClient client)
			throws MessagingException, ClientProtocolException, IOException {

		String url = getUrl();
		// Apparently OWA doesn't encode [] in it's href, although it does
		// encode other unsafe chars
		if (url.contains("["))
			url = url.replace("[", "%5B");
		if (url.contains("]"))
			url = url.replace("]", "%5D");

		HttpGet request = new HttpGet(url);
		request.setHeader("Translate", "f");

		client.getCredentialsProvider().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(getUsername(), getPassword()));

		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() >= 400
				&& response.getStatusLine().getStatusCode() < 500) {
			if (!authenticate(client, url, getUsername(), getPassword())) {
				throw new RuntimeException("Error authenticating to exchange");
			}
			response = client.execute(request);
		}

		if (response.getStatusLine().getStatusCode() >= 300) {
			return null;
		}
		
		MimeMessage msg = new MimeMessage(response.getEntity().getContent());
		return msg;
	}
}

package com.byarger.exchangeit;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;

import com.android.email.mail.MessagingException;
import com.android.email.mail.internet.MimeMessage;

public class GetMessage extends WebDavBase {

	public GetMessage(String path, String username, String password) {
		super("", "", "", path, username, password);
	}

	public MimeMessage getMessageContents() throws MessagingException,
			ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setBooleanParameter(
				HttpProtocolParams.USE_EXPECT_CONTINUE, false);

		client.getCredentialsProvider().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials(getUsername(), getPassword()));

		HttpGet request = new HttpGet(getPath());
		request.setHeader("Translate", "f");

		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() >= 300) {
			throw new RuntimeException("Error logging in");
		}
		MimeMessage msg = new MimeMessage(response.getEntity().getContent());
		return msg;
	}
}

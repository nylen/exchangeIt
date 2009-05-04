package com.byarger.exchangeit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.android.email.mail.MessagingException;
import com.android.email.mail.internet.BinaryTempFileBody;
import com.android.email.mail.internet.MimeMessage;

public class GetInboxContentsTest {

	private String username = "brian.yarger";
	private String password = "";
	private String inbox = "https://mail.cedarcrestone.com/exchange/Brian.Yarger@cedarcrestone.com/Inbox";

	@Test
	public void getContents() throws IOException, ParserConfigurationException,
			SAXException {
		DefaultHttpClient httpClient = WebDavBase.createHttpClient();

		GetInboxContents subject = new GetInboxContents(inbox, username,
				password);
		ExchangeMessage[] list = subject.getMessages(httpClient);
		assertNotNull("list is null", list);
		assertTrue("list is empty", list.length > 0);
		assertNotNull("href is empty", list[0].getHref());
		assertTrue("href is empty", list[0].getHref().length() > 0);
	}

	@Test
	public void getMessage() throws IOException, ParserConfigurationException,
			SAXException, MessagingException {
		DefaultHttpClient httpClient = WebDavBase.createHttpClient();
		BinaryTempFileBody.setTempDirectory(new File(System
				.getProperty("java.io.tmpdir")));
		GetInboxContents subject = new GetInboxContents(inbox, username,
				password);
		ExchangeMessage[] list = subject.getMessages(httpClient);
		assertNotNull("list is null", list);
		assertTrue("list is empty", list.length > 0);
		assertNotNull("href is empty", list[0].getHref());
		assertTrue("href is empty", list[0].getHref().length() > 0);

		GetMessage msg = new GetMessage(list[0].getHref(), username, password);
		MimeMessage contents = msg.getMessageContents(httpClient);
		assertNotNull("message is null", contents);
	}

	@Test
	public void getUnreadMailCount() throws IOException,
			ParserConfigurationException, SAXException {
		DefaultHttpClient httpClient = WebDavBase.createHttpClient();

		InboxHasNewMail subject = new InboxHasNewMail(inbox, username, password);
		Integer count = subject.getUnreadMailCount(httpClient);
		assertNotNull("count is null", count);
	}

	@Test
	public void selfSignedCertTest() throws Exception {
		DefaultHttpClient httpClient = WebDavBase.createHttpClient();

		httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
				new UsernamePasswordCredentials("byarger", ""));

		HttpGet httpget = new HttpGet("https://svn.e2econsulting.com/");

		System.out.println("executing request" + httpget.getRequestLine());

		HttpResponse response = httpClient.execute(httpget);
		HttpEntity entity = response.getEntity();

		System.out.println("----------------------------------------");
		System.out.println(response.getStatusLine());
		if (entity != null) {
			System.out.println("Response content length: "
					+ entity.getContentLength());
		}
		if (entity != null) {
			entity.consumeContent();
		}

	}
}

package com.byarger.exchangeit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.android.email.mail.internet.BinaryTempFileBody;

public class MarkMessageReadTest {

	private String username = "brian.yarger";
	private String password = "";
	private String inbox = "https://mail.cedarcrestone.com/exchange/Brian.Yarger@cedarcrestone.com/Inbox";

	@Test
	public void markMessageRead() throws IOException,
			ParserConfigurationException, SAXException {
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

		ExchangeMessage msg = list[0];
		MarkMessageRead mmr = new MarkMessageRead(msg.getHref(), username,
				password);
		mmr.markRead(httpClient, msg.getHref(), !msg.isRead());
	}

}

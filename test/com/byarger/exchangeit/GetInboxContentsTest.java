package com.byarger.exchangeit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.android.email.mail.MessagingException;
import com.android.email.mail.internet.BinaryTempFileBody;
import com.android.email.mail.internet.MimeMessage;

public class GetInboxContentsTest {

	@Test
	public void getContents() throws IOException, ParserConfigurationException,
			SAXException {
		GetInboxContents subject = new GetInboxContents(
				"https://mail.cedarcrestone.com/exchange/Brian.Yarger@cedarcrestone.com/Inbox",
				"brian.yarger", "abc123");
		ExchangeMessage[] list = subject.getMessages();
		assertNotNull("list is null", list);
		assertTrue("list is empty", list.length > 0);
		assertNotNull("href is empty", list[0].getHref());
		assertTrue("href is empty", list[0].getHref().length() > 0);
	}

	@Test
	public void getMessage() throws IOException, ParserConfigurationException,
			SAXException, MessagingException {
		BinaryTempFileBody.setTempDirectory(new File(System
				.getProperty("java.io.tmpdir")));
		GetInboxContents subject = new GetInboxContents(
				"https://mail.cedarcrestone.com/exchange/Brian.Yarger@cedarcrestone.com/Inbox",
				"brian.yarger", "abc123");
		ExchangeMessage[] list = subject.getMessages();
		assertNotNull("list is null", list);
		assertTrue("list is empty", list.length > 0);
		assertNotNull("href is empty", list[0].getHref());
		assertTrue("href is empty", list[0].getHref().length() > 0);

		GetMessage msg = new GetMessage(list[0].getHref(), "brian.yarger",
				"abc123");
		MimeMessage contents = msg.getMessageContents();
		assertNotNull("message is null", contents);
	}

}

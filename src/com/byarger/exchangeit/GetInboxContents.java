package com.byarger.exchangeit;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GetInboxContents extends WebDavBase {

	public GetInboxContents(String inboxURL, String username, String password) {
		super(inboxURL, username, password);
	}

	public ExchangeMessage[] getMessages(DefaultHttpClient client)
			throws IOException, ParserConfigurationException, SAXException {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		MessageSearch request = new MessageSearch(getUrl());
		request.setHeader("Depth", "0");

		String content = request.generateRequestBody(getPathURI(getUrl()));
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

		List<ExchangeMessage> emails = new ArrayList<ExchangeMessage>();
		if (list != null) {
			for (int i = 0; i < list.getLength(); i++) {
				Element child = (Element) list.item(i);

				String href = getSingleValue(child
						.getElementsByTagName("a:href"));

				String subject = getSingleValue(child
						.getElementsByTagName("d:subject"));
				String from = getSingleValue(child
						.getElementsByTagName("e:fromname"));
				String dt = getSingleValue(child.getElementsByTagName("d:date"));
				String sz = getSingleValue(child
						.getElementsByTagName("a:getcontentlength"));
				Date date = null;
				try {
					Date utcDate = sdf.parse(dt);
					long utcMiliseconds = utcDate.getTime();
					GregorianCalendar cal = new GregorianCalendar();
					cal.setTimeInMillis(utcMiliseconds);
					date = new Date(utcMiliseconds
							+ cal.get(Calendar.ZONE_OFFSET)
							+ cal.get(Calendar.DST_OFFSET));
				} catch (ParseException e) {

				}
				String to = getSingleValue(child.getElementsByTagName("d:to"));
				String read = getSingleValue(child
						.getElementsByTagName("e:read"));
				if (subject != null && subject.length() > 0) {
					ExchangeMessage msg = new ExchangeMessage();
					msg.setSubject(subject);
					msg.setFrom(from);
					if (date != null) {
						msg.setSent(date);
					}
					msg.setTo(to);
					msg.setRead(read.equals("1"));
					msg.setHref(href);
					if (sz != null && sz.length() > 0) {
						msg.setSize(Long.valueOf(sz).longValue());
					}

					emails.add(msg);
				}
			}
		}
		return emails.toArray(new ExchangeMessage[] {});
	}

	private static String getSingleValue(NodeList nl) {
		if (nl != null && nl.getLength() > 0) {
			Node item = nl.item(0);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				return getSingleValue(item.getChildNodes());
			} else if (item.getNodeType() == Node.TEXT_NODE) {
				return nl.item(0).getNodeValue();
			}
		}
		return "";
	}
}

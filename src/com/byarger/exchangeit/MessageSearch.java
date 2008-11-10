package com.byarger.exchangeit;

import java.net.URI;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.webdav.lib.util.XMLPrinter;

public class MessageSearch extends HttpEntityEnclosingRequestBase implements
		HttpEntityEnclosingRequest {

	public MessageSearch() {
		super();
	}

	public MessageSearch(URI uri) {
		super();
		setURI(uri);
	}

	public MessageSearch(String uri) {
		super();
		setURI(URI.create(uri));
	}

	@Override
	public String getMethod() {
		return "SEARCH";
	}

	public String generateRequestBody(String uri) {

		XMLPrinter printer = new XMLPrinter();

		printer.writeXMLHeader();
		printer.writeElement("a", "DAV:", "searchrequest", XMLPrinter.OPENING);

		printer.writeElement("a", "sql", XMLPrinter.OPENING);
		printer
				.writeText("SELECT \"DAV:contentlength\", \"DAV:href\", \"urn:schemas:mailheader:to\", \"urn:schemas:mailheader:subject\",\"urn:schemas:mailheader:date\", \"urn:schemas:httpmail:read\", \"urn:schemas:httpmail:fromname\" "
						+ "FROM SCOPE('SHALLOW TRAVERSAL OF \""
						+ uri
						+ "\"') "
						+ "WHERE \"DAV:isfolder\" = false AND \"DAV:ishidden\" = false");
		printer.writeElement("a", "sql", XMLPrinter.CLOSING);

		printer.writeElement("a", "searchrequest", XMLPrinter.CLOSING);

		return printer.toString();
	}
}

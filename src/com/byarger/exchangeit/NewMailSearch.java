package com.byarger.exchangeit;

import java.net.URI;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.webdav.lib.util.XMLPrinter;

public class NewMailSearch extends HttpEntityEnclosingRequestBase implements
		HttpEntityEnclosingRequest {

	public NewMailSearch() {
		super();
	}

	public NewMailSearch(URI uri) {
		super();
		setURI(uri);
	}

	public NewMailSearch(String uri) {
		super();
		setURI(URI.create(uri));
	}

	@Override
	public String getMethod() {
		return "PROPFIND";
	}

	public String generateRequestBody() {

		XMLPrinter printer = new XMLPrinter();

		printer.writeXMLHeader();
		printer.writeElement("a", "DAV:", "propfind", XMLPrinter.OPENING);

		printer.writeElement("a", "urn:schemas:httpmail:", "prop",
				XMLPrinter.OPENING, "d");
		printer.writeElement("d", "unreadcount", XMLPrinter.NO_CONTENT);
		printer.writeElement("a", "prop", XMLPrinter.CLOSING);

		printer.writeElement("a", "propfind", XMLPrinter.CLOSING);

		return printer.toString();
	}

}

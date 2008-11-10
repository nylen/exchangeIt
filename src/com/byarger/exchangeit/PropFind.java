package com.byarger.exchangeit;

import java.net.URI;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.webdav.lib.util.XMLPrinter;

public class PropFind extends HttpEntityEnclosingRequestBase implements
		HttpEntityEnclosingRequest {

	public PropFind() {
		super();
	}

	public PropFind(URI uri) {
		super();
		setURI(uri);
	}

	public PropFind(String uri) {
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

		printer.writeElement("a", "prop", XMLPrinter.OPENING);
		printer.writeElement("a", "href", XMLPrinter.NO_CONTENT);
		printer.writeElement("a", "ishidden", XMLPrinter.NO_CONTENT);
		printer.writeElement("a", "isfolder", XMLPrinter.NO_CONTENT);
		printer.writeElement("a", "getcontentlength", XMLPrinter.NO_CONTENT);
		printer.writeElement("a", "prop", XMLPrinter.CLOSING);

		printer.writeElement("a", "urn:schemas:mailheader:", "prop",
				XMLPrinter.OPENING, "d");
		printer.writeElement("d", "to", XMLPrinter.NO_CONTENT);
		printer.writeElement("d", "subject", XMLPrinter.NO_CONTENT);
		printer.writeElement("d", "date", XMLPrinter.NO_CONTENT);
		printer.writeElement("a", "prop", XMLPrinter.CLOSING);

		printer.writeElement("a", "urn:schemas:httpmail:", "prop",
				XMLPrinter.OPENING, "e");
		printer.writeElement("e", "read", XMLPrinter.NO_CONTENT);
		printer.writeElement("e", "fromname", XMLPrinter.NO_CONTENT);
		printer.writeElement("a", "prop", XMLPrinter.CLOSING);

		printer.writeElement("a", "propfind", XMLPrinter.CLOSING);

		return printer.toString();
	}

}

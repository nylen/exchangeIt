package com.byarger.exchangeit;

import java.net.URI;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class MarkMessageReadCommand extends HttpEntityEnclosingRequestBase
		implements HttpEntityEnclosingRequest {

	private boolean read;

	public MarkMessageReadCommand() {
		super();
	}

	public MarkMessageReadCommand(URI uri, boolean read) {
		super();
		setURI(uri);
		this.read = read;
	}

	public MarkMessageReadCommand(String uri, boolean read) {
		super();
		setURI(URI.create(uri));
		this.read = read;
	}

	@Override
	public String getMethod() {
		return "BPROPPATCH";
	}

	public String generateRequestBody(String uri) {
		StringBuilder strBuf = new StringBuilder(300);
		strBuf.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		strBuf
				.append("<D:propertyupdate xmlns:D=\"DAV:\" xmlns:a=\"urn:schemas:httpmail:\" xmlns:T=\"urn:uuid:c2f41010-65b3-11d1-a29f-00aa00c14882/\">\r\n");
		strBuf.append("<D:target>\r\n");
		strBuf.append("<D:href>");
		strBuf.append(uri);
		strBuf.append("</D:href>\r\n");
		strBuf.append("</D:target>\r\n");
		strBuf.append("<D:set><D:prop>\r\n");
		strBuf.append("<a:read>");
		strBuf.append(read ? "1" : "0");
		strBuf.append("</a:read>\r\n");
		strBuf.append("</D:prop></D:set>\r\n");
		strBuf.append("</D:propertyupdate>\r\n");
		return strBuf.toString();
	}
}

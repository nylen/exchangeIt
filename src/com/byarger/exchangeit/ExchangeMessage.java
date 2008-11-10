package com.byarger.exchangeit;

import java.util.Date;

public class ExchangeMessage {

	private String subject;
	private String from;
	private String to;
	private Date sent;
	private boolean read;
	private String href;
	private String textBody;
	private String htmlBody;
	private long size;

	public ExchangeMessage() {

	}

	public ExchangeMessage(String subject, String from, String to, Date sent,
			boolean read, String href) {
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.sent = sent;
		this.read = read;
		this.href = href;
	}

	String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getSent() {
		return sent;
	}

	public void setSent(Date sent) {
		this.sent = sent;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	@Override
	public String toString() {
		return subject;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getTextBody() {
		return textBody;
	}

	public void setTextBody(String textBody) {
		this.textBody = textBody;
	}

	public String getHtmlBody() {
		return htmlBody;
	}

	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getSize() {
		return size;
	}

}

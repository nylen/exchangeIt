package com.byarger.exchangeit;

public class WebDavBase {

	private String url;
	private String contextPath;
	private String mailboxName;
	private String path;
	private String username;
	private String password;

	public WebDavBase(String url, String contextPath, String mailboxName,
			String path, String username, String password) {
		this.url = url;
		this.contextPath = contextPath;
		this.mailboxName = mailboxName;
		this.path = path;
		this.username = username;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public String getMailboxName() {
		return mailboxName;
	}

	public String getPath() {
		return path;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getFullUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append("/");
		}
		sb.append(contextPath);
		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append("/");
		}
		sb.append(mailboxName);
		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append("/");
		}
		sb.append(path);
		return sb.toString();
	}

	public String getContextPath() {
		return contextPath;
	}
}

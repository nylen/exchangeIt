package com.byarger.exchangeit;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebDavBase {

	private String url;
	private String username;
	private String password;

	public WebDavBase(String url, String username, String password) {
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	protected static boolean authenticate(DefaultHttpClient client, String url,
			String username, String password) throws ClientProtocolException,
			IOException {
		HttpGet get = new HttpGet(url);

		// auth problem, lets try forms based auth
		formsBasedAuth2003(client, url, username, password);

		// redo the request, see if the cookies work
		HttpResponse response = client.execute(get);

		int code = response.getStatusLine().getStatusCode();

		if (code == 401) {
			formsBasedAuth2007(client, url, username, password);

			// redo the request, see if the cookies work
			response = client.execute(get);
			code = response.getStatusLine().getStatusCode();
		}
		return code < 300;
	}

	private static int formsBasedAuth2003(HttpClient client, String url,
			String username, String password) throws ClientProtocolException,
			IOException {
		// get the base of the URL

		HttpPost fbaAuth = new HttpPost(getBaseUrl(url)
				+ "/exchweb/bin/auth/owaauth.dll");
		fbaAuth.getParams().setParameter("destination", url);
		fbaAuth.getParams().setParameter("username", username);
		fbaAuth.getParams().setParameter("password", password);
		HttpResponse response = client.execute(fbaAuth);

		return response.getStatusLine().getStatusCode();
	}

	private static int formsBasedAuth2007(HttpClient client, String url,
			String username, String password) throws ClientProtocolException,
			IOException {
		// get the base of the URL

		HttpPost fbaAuth = new HttpPost(getBaseUrl(url)
				+ "/owa/auth/owaauth.dll");
		fbaAuth.getParams().setParameter("destination", url);
		fbaAuth.getParams().setParameter("username", username);
		fbaAuth.getParams().setParameter("password", password);
		HttpResponse response = client.execute(fbaAuth);

		return response.getStatusLine().getStatusCode();
	}

	protected static String getBaseUrl(String fullUrl) {
		int idx = fullUrl.indexOf("//");
		if (idx == -1) {
			return "";
		}
		int nextIdx = fullUrl.indexOf("/", idx + 2);
		if (idx == -1) {
			return "";
		}
		return fullUrl.substring(0, nextIdx);
	}

	protected static String getPathURI(String fullUrl) {
		int idx = fullUrl.indexOf("//");
		if (idx == -1) {
			return "";
		}
		int nextIdx = fullUrl.indexOf("/", idx + 2);
		if (idx == -1) {
			return "";
		}
		return fullUrl.substring(nextIdx);
	}

}

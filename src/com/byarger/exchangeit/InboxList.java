package com.byarger.exchangeit;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Config;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class InboxList extends ListActivity {
	private static final String TAG = "InboxList";

	private static final int DIALOG1_KEY = 0;

	private Handler handler;

	private ExchangeMessageAdapter adapter;

	public static final String EXCHANGE_BASE_URL = "exchangeBaseUrl";
	public static final String EXCHANGE_CONTEXT_PATH = "exchangeContextPath";
	public static final String EXCHANGE_MAILBOX_NAME = "exchangeMailboxName";
	public static final String EXCHANGE_USERNAME = "exchangeUsername";
	public static final String EXCHANGE_PASSWORD = "exchangePassword";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		if (Config.LOGV)
			Log.v(TAG, "creating InboxList");

		setContentView(R.layout.email_list);

		refresh();
	}

	private void refresh() {
		if (Config.LOGV)
			Log.v(TAG, "show progress dialog");
		showDialog(DIALOG1_KEY);

		final Runnable runInUIThread = new Runnable() {
			public void run() {
				if (Config.LOGV)
					Log.v(TAG, "set list adapter");
				setListAdapter(adapter);

				if (Config.LOGV)
					Log.v(TAG, "dismiss dialog");
				dismissDialog(DIALOG1_KEY);
			}
		};

		// Start lengthy operation in a background thread
		new Thread(new Runnable() {
			public void run() {
				try {
					if (Config.LOGV)
						Log.v(TAG, "getting preferences");
					SharedPreferences prefs = PreferenceManager
							.getDefaultSharedPreferences(InboxList.this);

					String baseURL = prefs.getString(EXCHANGE_BASE_URL, "");
					String contextPath = prefs.getString(EXCHANGE_CONTEXT_PATH,
							"exchange");
					String mailboxName = prefs.getString(EXCHANGE_MAILBOX_NAME,
							"");
					String username = prefs.getString(EXCHANGE_USERNAME, "");
					String password = prefs.getString(EXCHANGE_PASSWORD, "");

					if (baseURL.length() > 0 && contextPath.length() > 0
							&& mailboxName.length() > 0
							&& username.length() > 0 && password.length() > 0) {

						if (Config.LOGV)
							Log.v(TAG, "get inbox contents");
						StringBuilder sb = new StringBuilder();
						sb.append(baseURL);
						sb.append("/");
						sb.append(contextPath);
						GetInboxContents getInboxContents = new GetInboxContents(
								sb.toString(), mailboxName, username, password);

						ExchangeMessage[] inbox = getInboxContents
								.getMessages();
						if (Config.LOGV)
							Log.v(TAG, "create list adapter");
						adapter = new ExchangeMessageAdapter(InboxList.this,
								R.layout.email_row, inbox);
					} else {
						if (Config.LOGV)
							Log.v(TAG, "settings not set completely");
					}
				} catch (IOException e) {
					if (Config.LOGV)
						Log.v(TAG, e.getMessage());
					e.printStackTrace();
				} catch (ParserConfigurationException e) {
					if (Config.LOGV)
						Log.v(TAG, e.getMessage());
					e.printStackTrace();
				} catch (SAXException e) {
					if (Config.LOGV)
						Log.v(TAG, e.getMessage());
					e.printStackTrace();
				}

				if (Config.LOGV)
					Log.v(TAG, "update UI");
				handler.post(runInUIThread);
			}
		}).start();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG1_KEY: {
			ProgressDialog dialog = new ProgressDialog(this);
			dialog.setTitle("Loading items from inbox");
			dialog.setMessage("Please wait while loading...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		final String href = adapter.getItem(position).getHref();
		Intent intent = new Intent(this,
				com.byarger.exchangeit.MessageView.class);
		intent.putExtra("com.byarger.exchangeit.href", href);
		startActivity(intent);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refresh:
			refresh();
			return true;
		case R.id.settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
}
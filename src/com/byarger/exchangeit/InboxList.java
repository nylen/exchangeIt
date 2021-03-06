package com.byarger.exchangeit;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

	public static final String EXCHANGE_BASE_URL = "exchangeInboxUrl";
	public static final String EXCHANGE_USERNAME = "exchangeUsername";
	public static final String EXCHANGE_PASSWORD = "exchangePassword";
	public static final String EXCHANGE_CHECK_INTERVAL = "exchangeCheckInterval";
	public static final String EXCHANGE_MARK_AS_READ = "exchangeMarkAsRead";

	private String errorMessage;

	private DefaultHttpClient httpClient;

	private NotificationManager mNM;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			handler = new Handler();
			if (Config.LOGV)
				Log.v(TAG, "creating InboxList");

			httpClient = WebDavBase.createHttpClient();

			setContentView(R.layout.email_list);

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(InboxList.this);

			String url = prefs.getString(EXCHANGE_BASE_URL, "");
			String username = prefs.getString(EXCHANGE_USERNAME, "");
			String password = prefs.getString(EXCHANGE_PASSWORD, "");

			if (url.length() > 0 && username.length() > 0
					&& password.length() > 0) {

				refresh();
			} else {
				startActivity(new Intent(this, SettingsActivity.class));
			}
			Intent i = new Intent();
			i.setClassName("com.byarger.exchangeit",
					"com.byarger.exchangeit.NewMailService");
			i.setAction(NewMailService.ACTION_RESCHEDULE);
			startService(i);
		}
	}

	private void refresh() {
		if (Config.LOGV)
			Log.v(TAG, "show progress dialog");

		mNM.cancelAll();

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

		final Runnable showAlertDialog = new Runnable() {
			public void run() {
				if (errorMessage != null && errorMessage.length() > 0) {
					if (Config.LOGV)
						Log.v(TAG, errorMessage);
					new AlertDialog.Builder(InboxList.this).setMessage(
							errorMessage).setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).show();
				}
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

					String url = prefs.getString(EXCHANGE_BASE_URL, "");
					String username = prefs.getString(EXCHANGE_USERNAME, "");
					String password = prefs.getString(EXCHANGE_PASSWORD, "");

					if (url.length() > 0 && username.length() > 0
							&& password.length() > 0) {

						if (Config.LOGV)
							Log.v(TAG, "get inbox contents");
						GetInboxContents getInboxContents = new GetInboxContents(
								url, username, password);

						ExchangeMessage[] inbox = getInboxContents
								.getMessages(httpClient);
						if (Config.LOGV)
							Log.v(TAG, "create list adapter");
						adapter = new ExchangeMessageAdapter(InboxList.this,
								R.layout.email_row, inbox);
					} else {
						errorMessage = "Settings are not complete";
						handler.post(showAlertDialog);
					}
				} catch (IOException e) {
					errorMessage = "Caught IO Exception: " + e.getMessage();
					handler.post(showAlertDialog);
				} catch (ParserConfigurationException e) {
					errorMessage = "Caught ParserConfiguration Exception: "
							+ e.getMessage();
					handler.post(showAlertDialog);
				} catch (SAXException e) {
					errorMessage = "Caught SAX Exception: " + e.getMessage();
					handler.post(showAlertDialog);
				} catch (RuntimeException e) {
					errorMessage = "Caught unhandled RuntimeException: "
							+ e.getMessage();
					handler.post(showAlertDialog);
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
		final boolean read = adapter.getItem(position).isRead();
		Intent intent = new Intent(this,
				com.byarger.exchangeit.MessageView.class);
		intent.putExtra("com.byarger.exchangeit.href", href);
		intent.putExtra("com.byarger.exchangeit.read", read);
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
		case R.id.help:
			startActivity(new Intent(
					Intent.ACTION_VIEW,
					Uri
							.parse("http://code.google.com/p/exchangeit/wiki/exchangeItHelp")));
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
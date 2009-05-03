package com.byarger.exchangeit;

import java.io.IOException;
import java.text.DateFormat;

import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Config;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.email.Utility;
import com.android.email.mail.Address;
import com.android.email.mail.MessagingException;
import com.android.email.mail.Part;
import com.android.email.mail.Message.RecipientType;
import com.android.email.mail.internet.BinaryTempFileBody;
import com.android.email.mail.internet.MimeMessage;
import com.android.email.mail.internet.MimeUtility;

public class MessageView extends Activity {
	private static final String TAG = "MessageView";

	private static final int DIALOG1_KEY = 0;

	private Handler handler;

	private MimeMessage message;

	private WebView mMessageContentView;
	private TextView mToView;
	private TextView mFromView;
	private TextView mSubjectView;
	private TextView mDateView;

	private DateFormat mDateTimeFormat = DateFormat.getDateTimeInstance(
			DateFormat.SHORT, DateFormat.SHORT);
	private DateFormat mTimeFormat = DateFormat
			.getTimeInstance(DateFormat.SHORT);

	private String errorMessage;

	private DefaultHttpClient httpClient;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BinaryTempFileBody.setTempDirectory(getCacheDir());
		handler = new Handler();

		Bundle extras = getIntent().getExtras();
		final String href = extras.getString("com.byarger.exchangeit.href");

		if (Config.LOGV)
			Log.v(TAG, "creating message view");
		setContentView(R.layout.message_view);

		httpClient = WebDavBase.createHttpClient();

		mMessageContentView = (WebView) findViewById(R.id.message_content);
		mMessageContentView.setVerticalScrollBarEnabled(false);
		mMessageContentView.getSettings().setBlockNetworkImage(true);
		mMessageContentView.getSettings().setSupportZoom(false);

		mFromView = (TextView) findViewById(R.id.from);
		mToView = (TextView) findViewById(R.id.to);
		mSubjectView = (TextView) findViewById(R.id.subject);
		mDateView = (TextView) findViewById(R.id.date);

		showDialog(DIALOG1_KEY);

		final Runnable runInUIThread = new Runnable() {
			public void run() {
				if (Config.LOGV)
					Log.v(TAG, "setting contents to textview");
				onMessageAvaiable();
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
					new AlertDialog.Builder(MessageView.this).setMessage(
							errorMessage).setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).show();
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				try {
					if (Config.LOGV)
						Log.v(TAG, "getting preferences");

					SharedPreferences prefs = PreferenceManager
							.getDefaultSharedPreferences(MessageView.this);

					String username = prefs.getString(
							InboxList.EXCHANGE_USERNAME, "");
					String password = prefs.getString(
							InboxList.EXCHANGE_PASSWORD, "");

					if (username.length() == 0 || password.length() == 0) {
						errorMessage = "Settings are not complete";
						handler.post(showAlertDialog);
						return;
					}

					GetMessage getMessage = new GetMessage(href, username,
							password);

					message = getMessage.getMessageContents(httpClient);

				} catch (IOException e) {
					errorMessage = "Caught IO Exception: " + e.getMessage();
					handler.post(showAlertDialog);
				} catch (MessagingException e) {
					errorMessage = "Caught Messaging Exception: "
							+ e.getMessage();
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
			dialog.setTitle("Loading message contents");
			dialog.setMessage("Please wait while loading...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			return dialog;
		}
		}
		return null;
	}

	private void onMessageAvaiable() {
		try {
			if (message != null) {

				String subjectText = "";
				if (message.getSubject() != null)
					subjectText = message.getSubject();
				String fromText = "";
				if (message.getFrom() != null)
					fromText = Address.toFriendly(message.getFrom());

				String dateText = "";
				if (message.getSentDate() != null) {
					dateText = Utility.isDateToday(message.getSentDate()) ? mTimeFormat
							.format(message.getSentDate())
							: mDateTimeFormat.format(message.getSentDate());
				}

				String toText = "";
				if (message.getRecipients(RecipientType.TO) != null)
					toText = Address.toFriendly(message
							.getRecipients(RecipientType.TO));

				mSubjectView.setText(subjectText);
				mDateView.setText(dateText);
				mToView.setText(toText);
				mFromView.setText(fromText);

				Part part = MimeUtility.findFirstPartByMimeType(message,
						"text/html");
				if (part == null) {
					part = MimeUtility.findFirstPartByMimeType(message,
							"text/plain");
				}
				if (part != null) {
					String text = MimeUtility.getTextFromPart(part);
					if (part.getMimeType().equalsIgnoreCase("text/html")) {
						text = text.replaceAll("cid:", "http://cid/");
					} else {
						text = text.toString().replaceAll("\r?\n", "<br>");
						text = "<html><body>" + text + "</body></html>";
					}

					mMessageContentView.loadDataWithBaseURL("email://", text,
							"text/html", "utf-8", null);
				} else {
					mMessageContentView
							.loadUrl("file:///android_asset/empty.html");
				}
			}
		} catch (Exception e) {
			if (Config.LOGV)
				Log.v(TAG, e.getMessage());
			new AlertDialog.Builder(MessageView.this)
					.setMessage(e.getMessage()).setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							}).show();
		}
	}
}
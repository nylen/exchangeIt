package com.byarger.exchangeit;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Config;
import android.util.Log;

/**
 * This service checks for unread email at an interval specified in the
 * preferences
 * 
 */
public class NewMailService extends Service {

	private static final String TAG = "NewMailService";

	private NotificationManager mNM;

	public static final String ACTION_CHECK_NEW_MAIL = "com.byarger.exchangeit.NEW_MAIL_SERVICE_WAKEUP";

	public static final String ACTION_RESCHEDULE = "com.byarger.exchangeit.RESCHEDULE";

	private DefaultHttpClient httpClient;

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		httpClient = WebDavBase.createHttpClient();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (intent.getAction().equals(ACTION_CHECK_NEW_MAIL)) {
			if (Config.LOGV)
				Log.v(TAG, "Running new mail process");
			Thread notifyingThread = new Thread(null, mTask, "NewMailService");
			notifyingThread.start();
		} else if (intent.getAction().equals(ACTION_RESCHEDULE)) {
			if (Config.LOGV)
				Log.v(TAG, "rescheduling");
			reschedule();
		}
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

	private Runnable mTask = new Runnable() {
		public void run() {
			if (Config.LOGV)
				Log.v(TAG, "Running new mail process");

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(NewMailService.this);

			String url = prefs.getString(InboxList.EXCHANGE_BASE_URL, "");
			String username = prefs.getString(InboxList.EXCHANGE_USERNAME, "");
			String password = prefs.getString(InboxList.EXCHANGE_PASSWORD, "");
			String val = prefs
					.getString(InboxList.EXCHANGE_CHECK_INTERVAL, "0");

			if (url != null && url.length() > 0 && username != null
					&& username.length() > 0 && password != null
					&& password.length() > 0 && val != null && val.length() > 0) {
				InboxHasNewMail newMail = new InboxHasNewMail(url, username,
						password);
				Integer count = null;
				try {
					count = newMail.getUnreadMailCount(httpClient);
				} catch (IOException e) {
					if (Config.LOGV)
						Log.v(TAG, "error getting unread mail count"
								+ e.getMessage());
				} catch (ParserConfigurationException e) {
					if (Config.LOGV)
						Log.v(TAG, "error getting unread mail count"
								+ e.getMessage());
				} catch (SAXException e) {
					if (Config.LOGV)
						Log.v(TAG, "error getting unread mail count"
								+ e.getMessage());
				} catch (RuntimeException e) {
					if (Config.LOGV)
						Log.v(TAG, "error getting unread mail count"
								+ e.getMessage());
				}

				if (count != null && count > 0) {
					showNotification(count);
				} else {
					cancelNotification();
				}

				NewMailService.this.reschedule();
			}
			// Done with our work... stop the service!
			NewMailService.this.stopSelf();
		}
	};

	private void reschedule() {
		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent();
		i.setClassName("com.byarger.exchangeit",
				"com.byarger.exchangeit.NewMailService");
		i.setAction(ACTION_CHECK_NEW_MAIL);
		PendingIntent pi = PendingIntent.getService(this, 0, i, 0);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(NewMailService.this);

		int interval = 0;
		try {
			String val = prefs
					.getString(InboxList.EXCHANGE_CHECK_INTERVAL, "0");
			interval = Integer.parseInt(val);
		} catch (NumberFormatException e) {
			// drop it
		}

		if (Config.LOGV)
			Log.v(TAG, "interval is " + interval);

		if (interval == 0) {
			alarmMgr.cancel(pi);
		} else {
			alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock
					.elapsedRealtime()
					+ (interval * (60 * 1000)), pi);
		}
	}

	private void showNotification(int count) {
		Notification notification = new Notification(R.drawable.send_16, null,
				System.currentTimeMillis());
		notification.number = count;
		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, InboxList.class), 0);

		notification.setLatestEventInfo(this,
				getString(R.string.notificationTitle), getString(
						R.string.notificationText, count), contentIntent);

		// Send the notification.
		mNM.notify(1, notification);
	}

	private void cancelNotification() {
		// cancel any notifications.
		mNM.cancelAll();
	}

}

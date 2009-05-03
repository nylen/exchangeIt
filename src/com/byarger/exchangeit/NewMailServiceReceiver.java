package com.byarger.exchangeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NewMailServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent();
		i.setClassName("com.byarger.exchangeit",
				"com.byarger.exchangeit.NewMailService");
		i.setAction(NewMailService.ACTION_RESCHEDULE);
		context.startService(i);
	}

}

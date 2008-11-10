package com.byarger.exchangeit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ExchangeMessageAdapter extends ArrayAdapter<ExchangeMessage> {

	private LayoutInflater inflator;

	public ExchangeMessageAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		// Cache the LayoutInflate to avoid asking for a new one each time.
		inflator = LayoutInflater.from(context);
	}

	public ExchangeMessageAdapter(Context context, int resource,
			int textViewResourceId) {
		super(context, resource, textViewResourceId);
		// Cache the LayoutInflate to avoid asking for a new one each time.
		inflator = LayoutInflater.from(context);
	}

	public ExchangeMessageAdapter(Context context, int textViewResourceId,
			ExchangeMessage[] objects) {
		super(context, textViewResourceId, objects);
		// Cache the LayoutInflate to avoid asking for a new one each time.
		inflator = LayoutInflater.from(context);
	}

	public ExchangeMessageAdapter(Context context, int resource,
			int textViewResourceId, ExchangeMessage[] objects) {
		super(context, resource, textViewResourceId, objects);
		// Cache the LayoutInflate to avoid asking for a new one each time.
		inflator = LayoutInflater.from(context);
	}

	public ExchangeMessageAdapter(Context context, int textViewResourceId,
			List<ExchangeMessage> objects) {
		super(context, textViewResourceId, objects);
		// Cache the LayoutInflate to avoid asking for a new one each time.
		inflator = LayoutInflater.from(context);
	}

	public ExchangeMessageAdapter(Context context, int resource,
			int textViewResourceId, List<ExchangeMessage> objects) {
		super(context, resource, textViewResourceId, objects);
		// Cache the LayoutInflate to avoid asking for a new one each time.
		inflator = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls
		// to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = inflator.inflate(R.layout.email_row, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.subject = (TextView) convertView.findViewById(R.id.subject);
			holder.from = (TextView) convertView.findViewById(R.id.from);
			holder.received = (TextView) convertView.findViewById(R.id.sent);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		ExchangeMessage msg = getItem(position);
		String received = "";
		Date sent = msg.getSent();
		Date today = new Date();
		SimpleDateFormat mdy = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat hhmm = new SimpleDateFormat("hh:mmaa");
		SimpleDateFormat mmmmd = new SimpleDateFormat("MMM d");
		if (mdy.format(today).equals(mdy.format(sent))) {
			received = hhmm.format(sent);
		} else {
			received = mmmmd.format(sent);
		}
		if (msg.isRead()) {
			holder.subject.setTypeface(Typeface.DEFAULT);
			holder.subject.setTextColor(Color.BLACK);
			holder.from.setTextColor(Color.BLACK);
			holder.from.setTypeface(Typeface.DEFAULT);
			holder.received.setTextColor(Color.BLACK);
			holder.received.setTypeface(Typeface.DEFAULT);
		} else {
			holder.subject.setTypeface(Typeface.DEFAULT_BOLD);
			holder.subject.setTextColor(Color.GRAY);
			holder.from.setTypeface(Typeface.DEFAULT_BOLD);
			holder.from.setTextColor(Color.GRAY);
			holder.received.setTypeface(Typeface.DEFAULT_BOLD);
			holder.received.setTextColor(Color.GRAY);
		}

		// Bind the data efficiently with the holder.
		holder.subject.setText(msg.getSubject());
		holder.from.setText(msg.getFrom());
		holder.received.setText(received);

		return convertView;
	}

	static class ViewHolder {
		TextView subject;
		TextView from;
		TextView received;
	}
}

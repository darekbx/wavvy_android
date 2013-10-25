package com.wavvy.logic.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wavvy.R;
import com.wavvy.model.Message;

public class MessageAdapter extends ArrayAdapter<Message> {

	private Context mContext;
	private int mLayoutResourceId;
	
	public MessageAdapter(Context context, int textViewResourceId) {
		
		super(context, textViewResourceId);

		this.mContext = context;
		this.mLayoutResourceId = textViewResourceId;
	}

	public void addTheirs(List<Message> messages) {
	
		for (Message message : messages)
			this.add(message);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		ItemHolder holder;
		
		final Message item = this.getItem(position);
		
		if (row == null) {
		
			LayoutInflater inflater = ((Activity)this.mContext).getLayoutInflater();
			row = inflater.inflate(this.mLayoutResourceId, parent, false);
			
			holder = new ItemHolder();
			holder.artistTextView = (TextView)row.findViewById(R.id.track_row_artist);
			holder.titleTextView = (TextView)row.findViewById(R.id.track_row_title);
			holder.numberTextView = (TextView)row.findViewById(R.id.track_row_number);
			
			row.setTag(holder);
		}
		else {
			
			holder = (ItemHolder)row.getTag();
		}
		
		holder.artistTextView.setText(item.getDateString(this.mContext));
		holder.titleTextView.setText(item.getMessage());
		holder.numberTextView.setText(this.mContext.getString(R.string.dot_format, 1 + position));

		return row;
	}
	
	private static class ItemHolder {
	
		public TextView artistTextView;
		public TextView titleTextView;
		public TextView numberTextView;
	}
}
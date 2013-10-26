package com.wavvy.logic.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
			holder.messageParent = (LinearLayout)row.findViewById(R.id.message_row_parent);
			holder.messageContainer = (LinearLayout)row.findViewById(R.id.message_row_container);
			holder.dateTextView = (TextView)row.findViewById(R.id.message_row_date);
			holder.textTextView = (TextView)row.findViewById(R.id.message_row_text);
			
			row.setTag(holder);
		}
		else {
			
			holder = (ItemHolder)row.getTag();
		}
		
		holder.dateTextView.setText(item.getDateString(this.mContext));
		holder.textTextView.setText(item.getMessage());
		
		if (item.getIsMine()) {
		
			holder.messageParent.setPadding(14, 14, 100, 14); // TODO: px to dip
			holder.messageContainer.setBackgroundColor(
					this.mContext.getResources().getColor(R.color.light_green));
		}
		else {
			
			holder.messageParent.setPadding(100, 14, 14, 14); // TODO: px to dip
			holder.messageContainer.setBackgroundColor(
					this.mContext.getResources().getColor(R.color.light_grey));
		}

		return row;
	}
	
	private static class ItemHolder {

		public LinearLayout messageParent;
		public LinearLayout messageContainer;
		public TextView dateTextView;
		public TextView textTextView;
	}
}
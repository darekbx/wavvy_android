package com.wavvy.model;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

import android.content.Context;
import android.text.format.DateFormat;

import com.wavvy.R;

public class Message {
	
	private String mMessage;
	private String mDate;
	private boolean mIsMine;
	private int mFromIdUser;

	public Message() {
	
		this.mIsMine = false;
	}
	
	public void fromJsonObject(JSONObject jo, Context context) {

		this.setFromIdUser(jo.optInt(context.getString(R.string.message_from_id_user)));
		this.setMessage(jo.optString(context.getString(R.string.message_message)));
		this.setDate(jo.optString(context.getString(R.string.message_date)));
	}
	
	public String getMessage() {
		return this.mMessage;
	}
	
	public void setMessage(String message) {
		this.mMessage = message;
	}
	
	public String getDate() {
		return this.mDate;
	}
	
	public void setDate(String date) {
		this.mDate = date;
	}
	
	public int getFromIdUser() {
		return this.mFromIdUser;
	}
	
	public void setFromIdUser(int fromIdUser) {
		this.mFromIdUser = fromIdUser;
	}
	
	public void setIsMine() {
		
		this.mIsMine = true;
		this.mDate = String.valueOf(Calendar.getInstance().getTimeInMillis() / 1000);
	}
	
	public boolean getIsMine() {
		return this.mIsMine;
	}
	
	public String getDateString(Context context) {
		
		final long ticks = Long.valueOf(this.mDate) * 1000;
		return DateFormat.getDateFormat(context).format(new Date(ticks));
	}
}
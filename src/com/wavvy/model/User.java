package com.wavvy.model;

import org.json.JSONObject;

import android.content.Context;

import com.wavvy.R;

public class User {
	
	private int mId;
	private String mNick;

	public User() { }
	
	public User(int id, String nick) {
	
		this.mId = id;
		this.mNick = nick;
	}

	public void fromJsonObject(JSONObject jo, Context context) {

		final String field = context.getString(R.string.response_id_user);
		this.setId(jo.optInt(field));
	}

	public int getId() {
		return this.mId;
	}
	
	public void setId(int id) {
		this.mId = id;
	}
	
	public String getNick() {
		return this.mNick;
	}
	
	public void setNick(String nick) {
		this.mNick = nick;
	}
}
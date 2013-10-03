package com.wavvy.model;

public class User {
	
	private int mId;
	private String mNick;

	public User() { }
	
	public User(int id, String nick) {
	
		this.mId = id;
		this.mNick = nick;
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
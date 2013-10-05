package com.wavvy.model;

import com.google.android.gms.maps.model.LatLng;

public class NearestUser {
	
	private int mDistance;
	private double mLatitude;
	private double mLongitude;
	private int mId;
	private String mNick;

	public LatLng getPosition() {
	
		return new LatLng(this.mLatitude, this.mLongitude);
	}
	
	public int getDistance() {
		return this.mDistance;
	}
	
	public void setDistance(int distance) {
		this.mDistance = distance;
	}

	public double getLatitude() {
		return this.mLatitude;
	}

	public void setLatitude(double latitude) {
		this.mLatitude = latitude;
	}

	public double getLongitude() {
		return this.mLongitude;
	}

	public void setLongitude(double longitude) {
		this.mLongitude = longitude;
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
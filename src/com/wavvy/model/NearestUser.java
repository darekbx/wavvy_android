package com.wavvy.model;

import org.json.JSONObject;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.wavvy.R;

public class NearestUser {
	
	private int mDistance;
	private double mLatitude;
	private double mLongitude;
	private int mId;
	private String mNick;
	private String mArtist;
	private String mTitle;

	public LatLng getPosition() {
	
		return new LatLng(this.mLatitude, this.mLongitude);
	}
	
	public void fromJsonObject(JSONObject jo, Context context) {

		final String[] fields = context.getResources().getStringArray(R.array.nearest_fields);
		
		this.setId(jo.optInt(fields[0]));
		this.setNick(jo.optString(fields[1]));
		this.setArtist(jo.optString(fields[2]));
		this.setTitle(jo.optString(fields[3]));
		this.setDistance(jo.optInt(fields[4]));
		this.setLatitude(jo.optDouble(fields[5]));
		this.setLongitude(jo.optDouble(fields[6]));
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
	
	public String getArtist() {
		return this.mArtist;
	}
	
	public void setArtist(String artist) {
		this.mArtist = artist;
	}
	
	public String getTitle() {
		return this.mTitle;
	}
	
	public void setTitle(String title) {
		this.mTitle = title;
	}
	
	@Override
	public String toString() {

		return new StringBuilder()
			.append(this.getArtist())
			.append('-')
			.append(this.getTitle())
			.toString();
	}
}
package com.wavvy.model;

import org.json.JSONObject;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.wavvy.R;

public class SongLocation {
	
	private double mLatitude;
	private double mLongitude;
	private int mId;
	private int mIdUser;
	private int mIdMessage;
	private String mArtist;
	private String mAlbum;
	private String mTitle;
	private String mDate;

	public LatLng getPosition() {
	
		return new LatLng(this.mLatitude, this.mLongitude);
	}
	
	public void fromJsonObject(JSONObject jo, Context context) {

		final String[] fields = context.getResources().getStringArray(R.array.location_fields);

		this.setId(jo.optInt(fields[0]));
		this.setIdUser(jo.optInt(fields[1]));
		this.setArtist(jo.optString(fields[2]));
		this.setTitle(jo.optString(fields[3]));
		this.setAlbum(jo.optString(fields[4]));
		this.setLatitude(jo.optDouble(fields[5]));
		this.setLongitude(jo.optDouble(fields[6]));
		this.setIdMessage(0);//jo.optInt(fields[7]));
		this.setDate(jo.optString(fields[8]));
	}
	
	public boolean hasMessage() {
	
		return this.mIdMessage > 0;
	}
	
	public boolean hasArtist() {
	
		return this.mArtist != null && this.mArtist.length() > 0;
	}
	
	public boolean hasTitle() {
	
		return this.mTitle != null && this.mTitle.length() > 0;
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
	
	public int getIdUser() {
		return this.mIdUser;
	}
	
	public void setIdUser(int id) {
		this.mIdUser = id;
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
	
	public String getAlbum() {
		return this.mAlbum;
	}
	
	public void setAlbum(String album) {
		this.mAlbum = album;
	}

	public int getIdMessage() {
		return this.mIdMessage;
	}
	
	public void setIdMessage(int id) {
		this.mIdMessage = id;
	}

	public String getDate() {
		return this.mDate;
	}

	public void setDate(String date) {
		this.mDate = date;
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
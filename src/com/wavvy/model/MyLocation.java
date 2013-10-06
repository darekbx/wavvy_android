package com.wavvy.model;

import com.google.android.gms.maps.model.LatLng;

public class MyLocation {
	
	private int mIdUser;
	private String mArtist;
	private String mTitle;
	private double mLatitude;
	private double mLongitude;

	public LatLng getPosition() {
	
		return new LatLng(this.mLatitude, this.mLongitude);
	}
	
	public int getIdUser() {
		return this.mIdUser;
	}
	
	public void setIdUser(int idUser) {
		this.mIdUser = idUser;
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
	
	@Override
	public String toString() {

		return new StringBuilder()
			.append(this.getArtist())
			.append('-')
			.append(this.getTitle())
			.toString();
	}
}
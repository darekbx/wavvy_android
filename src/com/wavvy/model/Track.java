package com.wavvy.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.google.android.gms.maps.model.LatLng;
import com.wavvy.logic.LocationManager;

public class Track {

	private int mId;
	private String mArtist;
	private String mTitle;
	private String mAlbum;
	private long mDate;
	
	private int mUserId;

	public Track() { }
	
	public Track(String artist, String title, String album) {

		this.mArtist = artist;
		this.mTitle = title;
		this.mAlbum = album;
	}
	
	public List<BasicNameValuePair> getPostData() {

		// id_user, artist, title, latitude, longitude
		final List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
		data.add(new BasicNameValuePair("id_user", String.valueOf(this.mUserId)));
		data.add(new BasicNameValuePair("artist", this.mArtist));
		data.add(new BasicNameValuePair("title", this.mTitle));
		
		// TODO: latitude & longitude
		final LatLng location = LocationManager.getRandom();

		data.add(new BasicNameValuePair("latitude", String.valueOf(location.latitude)));
		data.add(new BasicNameValuePair("longitude", String.valueOf(location.longitude)));
		
		return data;
	}
	
	public boolean isValid() {
	
		return this.mArtist != null 
				|| this.mTitle != null 
				|| this.mAlbum != null;
	}
	
	public int getId() {
		return this.mId;
	}
	
	public String getArtist() {
		return this.mArtist;
	}
	
	public String getTitle() {
		return this.mTitle;
	}
	
	public String getAlbum() {
		return this.mAlbum;
	}
	
	public long getDate() {
		return this.mDate;
	}
	
	public void setId(int id) {
		this.mId = id;
	}

	public void setArtist(String artist) {
		this.mArtist = artist;
	}
	
	public void setTitle(String title) {
		this.mTitle = title;
	}

	public void setAlbum(String album) {
		this.mAlbum = album;
	}
	
	public void setDate(long date) {
		this.mDate = date;
	}

	public void setDate(String date) {	
		this.mDate = Long.parseLong(date);
	}
	
	public int getUserId() {
		return this.mUserId;
	}

	public void setUserId(int userId) {
		this.mUserId = userId;
	}
}
package com.wavvy.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.wavvy.R;
import com.wavvy.logic.LocationHelper;

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
	
	public List<BasicNameValuePair> getPostData(Context context) {

		String[] fields = context.getResources().getStringArray(R.array.post_fields);
		
		// id_user, artist, title, album, latitude, longitude
		final List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
		data.add(new BasicNameValuePair(fields[0], String.valueOf(this.mUserId)));
		data.add(new BasicNameValuePair(fields[1], this.mArtist));
		data.add(new BasicNameValuePair(fields[2], this.mTitle));
		data.add(new BasicNameValuePair(fields[3], this.mAlbum));
		
		final LatLng location = LocationHelper.getLocation(context);

		data.add(new BasicNameValuePair(fields[4], String.valueOf(location.latitude)));
		data.add(new BasicNameValuePair(fields[5], String.valueOf(location.longitude)));
		
		return data;
	}
	
	public boolean isValid() {
	
		return this.mArtist != null 
				|| this.mTitle != null 
				|| this.mAlbum != null;
	}
	
	public boolean hasArtist() {
	
		return this.mArtist != null && this.mArtist.length() > 0;
	}
	
	public boolean hasTitle() {
	
		return this.mTitle != null && this.mTitle.length() > 0;
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
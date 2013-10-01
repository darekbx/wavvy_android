package com.wavvy.model;

public class Track {

	private int mId;
	private String mArtist;
	private String mTitle;
	private String mAlbum;
	private long mDate;

	public Track() { }
	
	public Track(String artist, String title, String album) {

		this.mArtist = artist;
		this.mTitle = title;
		this.mAlbum = album;
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
	
	@Override
	public String toString() {

		return new StringBuilder()
				.append(this.mArtist).append(" - ")
				.append(this.mTitle).toString();
	}
}
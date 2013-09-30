package com.wavvy.model;

public class Track {

	private int mId;
	private String mArtist;
	private String mTrack;
	private String mAlbum;
	private long mDate;

	public Track() { }
	
	public Track(String artist, String track, String album) {

		this.mArtist = artist;
		this.mTrack = track;
		this.mAlbum = album;
	}
	
	public int getId() {
		return this.mId;
	}
	
	public String getArtist() {
		return this.mArtist;
	}
	
	public String getTrack() {
		return this.mTrack;
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
	
	public void setTrack(String track) {
		this.mTrack = track;
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
				.append(this.mTrack).toString();
	}
}
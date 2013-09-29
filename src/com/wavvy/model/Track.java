package com.wavvy.model;

public class Track {

	private String mArtist;
	private String mTrack;
	private String mAlbum;

	public Track(String artist, String track, String album) {

		this.mArtist = artist;
		this.mTrack = track;
		this.mAlbum = album;
	}

	@Override
	public String toString() {

		return new StringBuilder().append(this.mArtist).append('-')
				.append(this.mTrack).append('-').append(this.mAlbum).toString();
	}
}
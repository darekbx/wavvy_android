package com.wavvy.logic;

import java.util.Locale;

import android.annotation.SuppressLint;
import com.wavvy.model.SongLocation;
import com.wavvy.model.Track;

@SuppressLint("DefaultLocale")
public class SongComprasion {

	public static final int SAME_TITLE = 1;
	public static final int SAME_ARTIST = 2;
	public static final int DEFAULT = 3;
	
	public static int compare(final Track track, final SongLocation song) {
	
		if (track == null || song == null)
			return DEFAULT;
		
		if (track.getTitle() != null 
			&& song.getTitle() != null
			&& track.getTitle().length() > 0
			&& song.getTitle().length() > 0) {
		
			final String title1 = SongComprasion.normalize(track.getTitle());
			final String title2 = SongComprasion.normalize(song.getTitle());
		
			if (title1.contains(title2) || title2.contains(title1)) 
				return SAME_TITLE;
		}

		if (track.getArtist() != null 
			&& song.getArtist() != null
			&& track.getArtist().length() > 0
			&& song.getArtist().length() > 0) {
			
			final String artist1 = SongComprasion.normalize(track.getArtist());
			final String artist2 = SongComprasion.normalize(song.getArtist());
	
			if (artist1.contains(artist2) || artist2.contains(artist1)) 
				return SAME_ARTIST;
		}
		
		return DEFAULT;
	}
	
	private static String normalize(String value) {
	
		return value
				.replaceAll("[^\\x00-\\x7F]", "")
				.replaceAll("[^A-Za-z0-9]", "")
				.toLowerCase(Locale.getDefault());
	}
}
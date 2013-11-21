package com.wavvy.logic;

import java.util.Locale;

import android.annotation.SuppressLint;
import com.wavvy.model.SongLocation;
import com.wavvy.model.Track;

@SuppressLint("DefaultLocale")
public class SongComprasion {

	public static final int SAME_ARTIST_AND_TITLE = 1; // jackpot
	public static final int SAME_ARTIST = 2; // half-jackpot
	public static final int DEFAULT = 3;
	public static final int PERCENT = 60;
	
	public static int compareExtract(final Track track, final SongLocation song) {

		if (track == null || song == null)
			return DEFAULT;

		int artistResult = 0;
		int titleResult = 0;
		
		if (track.getArtist() != null 
			&& song.getArtist() != null
			&& track.getArtist().length() > 0
			&& song.getArtist().length() > 0) {

			final String[] artist1 = SongComprasion.extract(track.getArtist());
			final String[] artist2 = SongComprasion.extract(song.getArtist());
			artistResult = SongComprasion.calculateOccurances(artist1, artist2);
			
			if (track.getTitle() != null 
				&& song.getTitle() != null
				&& track.getTitle().length() > 0
				&& song.getTitle().length() > 0) {

				final String[] title1 = SongComprasion.extract(track.getTitle());
				final String[] title2 = SongComprasion.extract(song.getTitle());
				titleResult = SongComprasion.calculateOccurances(title1, title2);	
			}
		}
		
		if (artistResult > PERCENT) {
		
			if (titleResult > PERCENT) return SAME_ARTIST_AND_TITLE;
			else return SAME_ARTIST;
		}
		
		return DEFAULT;
	}
	
	public static int calculateOccurances(final String[] a, final String[] b) {
	
		int countA = a.length;
		int countB = b.length;
		
		int count = 0;
		
		for (int i = 0; i < countA; i++)
			for (int j = 0; j < countB; j++)
				if (a[i].equals(b[j])) {
				
					count++;
					break;
				}
		
		return (int)((double)count * 100d / (double)countA);
	}
	
	private static String[] extract(String value) {
		
		return SongComprasion.normalize(value).split(" ");
	}
	
	private static String normalize(String value) {
	
		return value
				.replaceAll("[^\\x00-\\x7F]", " ")
				.replaceAll("[^A-Za-z0-9]", " ")
				.toLowerCase(Locale.getDefault());
	}
}
package com.wavvy.logic;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.wavvy.R;
import com.wavvy.model.Track;

public class Scrobbler extends BaseContext {

	private static final int NO_AUDIO_ID = -1;

	public Scrobbler(final Context context) {
		
		super(context);
	}
	
	public Track parseTrack(final Bundle bundle) {
		
		if (bundle == null)
			return null;
		
		final long audioid = this.getAudioId(bundle);
		Track track = null;
		
		if (this.shouldFetchFromMediaStore(audioid)) {
			
			track = this.readTrackFromMediaStore(audioid);
			
			if (track != null)
				return track;
		}

		if (track == null) {
			
			track = this.readTrackFromBundleData(bundle);
			
			if (!track.isValid())
				return null;
		}
		
		return track;
	}
	
	private long getAudioId(final Bundle bundle) {

		final Object idBundle = bundle.get(getString(R.string.receiver_id_key));
		long id = NO_AUDIO_ID;
		
		if (idBundle != null) {
			
			if (idBundle instanceof Long) id = (Long)idBundle;
			else if (idBundle instanceof Integer) id = (Integer)idBundle;
			else if (idBundle instanceof String) id = Long.valueOf((String)idBundle).longValue();
		}

		return id;
	}
	
	private boolean shouldFetchFromMediaStore(final long audioid) {
		
		return audioid > 0;
	}
	
	private Track readTrackFromMediaStore(final long audioid) {

		final String[] columns = new String[] {
			MediaStore.Audio.AudioColumns.ARTIST,
			MediaStore.Audio.AudioColumns.TITLE,
			MediaStore.Audio.AudioColumns.ALBUM };

		final Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioid);
		final Cursor cursor = this.getContext().getContentResolver().query(contentUri, columns, null, null, null);

		if (cursor == null)
			return null;

		try {
			
			if (!cursor.moveToFirst())
				return null;
				
			return new Track(
					cursor.getString(0), 
					cursor.getString(1), 
					cursor.getString(2));
		}
		catch (Exception e) { return null; }
		finally {
			
			cursor.close();
		}
	}

	private Track readTrackFromBundleData(final Bundle bundle) {

		return new Track(
				this.getStringFromBundle(bundle, R.string.receiver_artist_key),  
				this.getStringFromBundle(bundle, R.string.receiver_track_key),
				this.getStringFromBundle(bundle, R.string.receiver_album_key));
	}
	
	private String getStringFromBundle(final Bundle bundle, final int keyResourceId) {
		
		final String key = this.getString(keyResourceId);
		
		if (!bundle.containsKey(key))
			return null;
		
		return bundle.getCharSequence(key).toString();
	}
}
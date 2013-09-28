package com.wavvy;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;

/*
 * TODO:
 * wyswietlic cala tabelke MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
 * 
 * 
 */

public class MainActivity extends Activity {

	private static final int NO_AUDIO_ID = -1;
	private TextView mText;
	
	private class Track {
	
		private String mType;
		private String mArtist;
		private String mTrack;
		private String mAlbum;
		
		private Track(String type, String artist, String track, String album) {
		
			this.mType = type;
			this.mArtist = artist;
			this.mTrack = track;
			this.mAlbum = album;
		}
		
		@Override
		public String toString() {
			
			return this.mType + " - " + this.mArtist + ":" + this.mTrack + ":" + this.mAlbum;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		this.mText = (TextView)this.findViewById(R.id.text);
		
		final IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.htc.music.metachanged");
		iF.addAction("com.andrew.apollo.metachanged");
		
		iF.addAction("com.rdio.android.metachanged");
		iF.addAction("com.nullsoft.winamp.metachanged");

		this.registerReceiver(mReceiver, iF);
	}
	
	private void append(String text){

		String current = this.mText.getText().toString();
		current += text;
		current += "\n";
		
		this.mText.setText(current);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			/*String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			
			String artist = intent.getStringExtra("artist");
			String album = intent.getStringExtra("album");
			String track = intent.getStringExtra("track");
			
			MainActivity.this.append(artist + ":" + album + ":" + track);*/
			
			try {
				
				Track track = MainActivity.this.parseTrack(context, intent.getExtras());
				MainActivity.this.append(track.toString());
			}
			catch (Exception e) { e.printStackTrace(); }
		}
	};
	
	private Track parseTrack(Context context, Bundle bundle) {
		
		long audioid = this.getAudioId(bundle);

		if (this.shouldFetchFromMediaStore(audioid))
			return this.readTrackFromMediaStore(context, audioid);
		else
			return this.readTrackFromBundleData(bundle);
	}
	
	private long getAudioId(Bundle bundle) {
		
		final Object idBundle = bundle.get("id");
		long id = NO_AUDIO_ID;
		
		if (idBundle != null) {
			
			if (idBundle instanceof Long) id = (Long)idBundle;
			else if (idBundle instanceof Integer) id = (Integer)idBundle;
			else if (idBundle instanceof String) id = Long.valueOf((String)idBundle).longValue();
		}
		
		return id;
	}
	
	private boolean shouldFetchFromMediaStore(long audioid) {
		
		if (audioid > 0)
			return true;
		
		return false;
	}
	
	private Track readTrackFromMediaStore(Context context, long audioid) {

		final String[] columns = new String[] {
		    MediaStore.Audio.Media._ID,
			MediaStore.Audio.AudioColumns.ARTIST,
			MediaStore.Audio.AudioColumns.TITLE,
			MediaStore.Audio.AudioColumns.ALBUM };

		Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, audioid);
		final Cursor cur = context.getContentResolver().query(contentUri, columns, null, null, null);

		if (cur == null)
			throw new IllegalArgumentException("could not open cursor to media in media store");

		try {
			
			if (!cur.moveToFirst())
				throw new IllegalArgumentException("no such media in media store");
			
			final String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
			final String track = cur.getString(cur.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
			final String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));

			return new Track("MS", artist, track, album);
		} 
		finally {
			
			cur.close();
		}
	}

	private Track readTrackFromBundleData(Bundle bundle) {

		final CharSequence artist = bundle.getCharSequence("artist");
		final CharSequence album = bundle.getCharSequence("album");
		final CharSequence track = bundle.getCharSequence("track");
		
		return new Track("BD", artist.toString(), track.toString(), album.toString());
	}
	
}
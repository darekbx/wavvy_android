package com.wavvy.db;

import java.util.ArrayList;
import java.util.List;

import com.wavvy.R;
import com.wavvy.logic.BaseContext;
import com.wavvy.model.Track;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class StorageManager extends BaseContext {

	private SQLiteDatabase mDataBase;

	public StorageManager(Context context) {

		super(context);

        final StorageHelper helper = new StorageHelper(context);
        this.mDataBase = helper.getWritableDatabase();
	}
	
	public void close() {
	
		if (this.mDataBase != null)
			this.mDataBase.close();
	}

	public boolean addTrack(Track track) {

		final String sql = String.format(
				this.getString(R.string.db_track_insert), 
				this.trackTableName());

		final SQLiteStatement stmt = this.mDataBase.compileStatement(sql);
		stmt.bindString(1, track.getArtist());
		stmt.bindString(2, track.getTrack());
		stmt.bindString(3, track.getAlbum());
		stmt.bindString(4, String.valueOf(track.getDate()));

		final boolean result = stmt.executeInsert() > 0;
		stmt.close();

		return result;
	}

	public List<Track> getTracks() {

		final Cursor cursor = this.mDataBase.query(
				this.trackTableName(), this.trackColumns(),
				null, null, null, null, null);
		
		final List<Track> items = new ArrayList<Track>();
		
		if (cursor.moveToFirst()) {
		
			do {
				
				items.add(this.getTrackFromCursor(cursor));
			}
			while (cursor.moveToNext());
		}
		
		cursor.close();
		
		return items;
	}

	private Track getTrackFromCursor(Cursor cursor) {

		final Track track = new Track();
		track.setId(cursor.getInt(0));
		track.setArtist(cursor.getString(1));
		track.setTrack(cursor.getString(2));
		track.setAlbum(cursor.getString(3));
		track.setDate(cursor.getString(4));

		return track;
	}

	private String trackTableName() {

		return this.getString(R.string.db_track_tablename);
	}

	private String[] trackColumns() {

		return this.getStringArray(R.array.track_columns);
	}
}
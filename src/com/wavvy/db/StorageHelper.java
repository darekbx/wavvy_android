package com.wavvy.db;

import com.wavvy.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StorageHelper extends SQLiteOpenHelper {

	private Context context;
	
	public StorageHelper(final Context context) {
		
		super(context, context.getString(R.string.db_name), null, 1);
		
		this.context = context;
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {

		db.execSQL(String.format(
				this.context.getString(R.string.db_track_create_syntax), 
				this.context.getString(R.string.db_track_tablename)
		));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
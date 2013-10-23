package com.wavvy.logic.storage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.wavvy.R;
import com.wavvy.logic.BaseContext;

public class LikeStorage extends BaseContext {

	public LikeStorage(Context context) {
		
		super(context);
	}

	public void setLikesCount(int count) {
	
		final SharedPreferences pref = this.getPreferences();
		
		final SharedPreferences.Editor editor = pref.edit();
		editor.putInt(this.getString(R.string.like_count_key), count);
		
		editor.commit();
	}
	
	public int getLikesCount() {

		final SharedPreferences pref = this.getPreferences();
		return pref.getInt(this.getString(R.string.like_count_key), 0);
	}
	
	private SharedPreferences getPreferences() {

		final SharedPreferences pref = this.getContext().getSharedPreferences(
				this.getString(R.string.app_name), Activity.MODE_PRIVATE);
		
		return pref;
	}
}
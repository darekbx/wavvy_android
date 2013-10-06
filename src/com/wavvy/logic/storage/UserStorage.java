package com.wavvy.logic.storage;

import com.wavvy.R;
import com.wavvy.logic.BaseContext;
import com.wavvy.model.User;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class UserStorage extends BaseContext {

	public UserStorage(Context context) {
		
		super(context);
	}

	public void setUser(final User user) {

		final SharedPreferences pref = this.getPreferences();
		
		final SharedPreferences.Editor editor = pref.edit();
		editor.putInt(this.getString(R.string.user_id_key), user.getId());
		editor.putString(this.getString(R.string.user_nick_key), user.getNick());
		
		editor.commit();
	}
	
	public User getUser() {

		final SharedPreferences pref = this.getPreferences();
		
		final User user = new User();
		user.setId(pref.getInt(this.getString(R.string.user_id_key), 0));
		user.setNick(pref.getString(this.getString(R.string.user_nick_key), new String()));
		
		return user;
	}

	public boolean isUserExists() {

		final SharedPreferences pref = this.getPreferences();
		return pref.getInt(this.getString(R.string.user_id_key), -1) > 0;
	}
	
	public void reset() {
	
		final SharedPreferences pref = this.getPreferences();
		final SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	private SharedPreferences getPreferences() {

		final SharedPreferences pref = this.getContext().getSharedPreferences(
				this.getString(R.string.app_name), Activity.MODE_PRIVATE);
		
		return pref;
	}
}
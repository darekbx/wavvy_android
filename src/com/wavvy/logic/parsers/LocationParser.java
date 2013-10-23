package com.wavvy.logic.parsers;

import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.android.gms.maps.model.Marker;
import com.wavvy.logic.BaseContext;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.SongLocation;

public class LocationParser extends BaseContext {
	
	public LocationParser(Context context) {
		
		super(context);
	}

	public LinkedHashMap<SongLocation, Marker> parse(final String content) {

		final LinkedHashMap<SongLocation, Marker> songs = new LinkedHashMap<SongLocation, Marker>();
		
		try {
			
			final JSONArray array = new JSONArray(content);
			final int count = array.length();
			final int currentUser = this.loadUser();
			
			JSONObject jo;
			SongLocation user;
			
			for (int i = 0; i < count; i++) {
			
				jo = array.getJSONObject(i);
				
				user = new SongLocation();
				user.fromJsonObject(jo, this.getContext());
				
				// skip my tracks
				if (currentUser == user.getIdUser())
					continue;
				
				songs.put(user, null);
			}
		} 
		catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return songs;
	}

	private int loadUser() {
	
		final UserStorage storage = new UserStorage(this.getContext());
		
		if (storage.isUserExists())
			return storage.getUser().getId();
		
		return -1;
	}
}
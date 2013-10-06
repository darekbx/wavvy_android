package com.wavvy.receivers;

import java.net.URI;

import com.wavvy.MainActivity.RefreshReceiver;
import com.wavvy.db.StorageManager;
import com.wavvy.listeners.PostListener;
import com.wavvy.logic.Scrobbler;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Post;
import com.wavvy.logic.http.Utils;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.Track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicReceiver extends BroadcastReceiver {
	
	private Context mContext;
	
	@Override
	public void onReceive(final Context context, final Intent intent) {

		this.mContext = context;
		final Track track = new Scrobbler(context).parseTrack(intent.getExtras());
		
		if (track == null)
			return;
		
		if (this.saveTrack(track))
			this.notifyNewTrack();
	}
	
	private boolean saveTrack(final Track track) {
	
		try {
			
			this.saveToDatabase(track);
			this.saveToWeb(track);
			
			return true;
		}
		catch (Exception e) {
		
			e.printStackTrace();
			return false;
		}
	}
	
	private void saveToDatabase(final Track track) {

		final StorageManager manager = new StorageManager(this.mContext);
		manager.addTrack(track);
		manager.close();
	}
	
	private void saveToWeb(final Track track) {
		
		// check internet
		if (Utils.isOnline(this.mContext)) {

			// set user id
			final UserStorage storage = new UserStorage(this.mContext);
			
			if (!storage.isUserExists())
				return;
			
			track.setUserId(storage.getUser().getId());
			
			// save
			final Post post = new Post(track.getPostData(this.mContext));
			post.setOnPostListener(new PostListener() {
				
				@Override
				public void success(String content) { }
				
				@Override
				public void failed(String message) { }
			});

			final URI uri = new AddressBuilder(this.mContext).add();

			if (uri != null)
				post.execute(uri);
		}
	}
	
	private void notifyNewTrack() {
		
		final Intent intent = new Intent(RefreshReceiver.class.getName());
		this.mContext.sendBroadcast(intent);
	}
}
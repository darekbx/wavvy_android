package com.wavvy.receivers;

import com.wavvy.SongsActivity.RefreshReceiver;
import com.wavvy.db.StorageManager;
import com.wavvy.logic.Scrobbler;
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
			
			final StorageManager manager = new StorageManager(this.mContext);
			manager.addTrack(track);
			manager.close();
			
			return true;
		}
		catch (Exception e) {
		
			e.printStackTrace();
			return false;
		}
	}
	
	private void notifyNewTrack() {
		
		final Intent intent = new Intent(RefreshReceiver.class.getName());
		this.mContext.sendBroadcast(intent);
	}
}
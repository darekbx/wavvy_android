package com.wavvy.logic;

import com.wavvy.R;
import com.wavvy.listeners.TrackReceivedListener;
import com.wavvy.model.Track;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class IntentReceiver extends BaseContext {

	private Scrobbler mScrobbler;
	private TrackReceivedListener mTrackReceived;
	
	public IntentReceiver(Context context) {

		super(context);
		
		this.mScrobbler = new Scrobbler(context);
	}

	public void register() {

		final IntentFilter intentFilter = new IntentFilter();
		final String[] filters = this.getStringArray(R.array.intent_filters);

		for (String filter : filters)
			intentFilter.addAction(filter);

		this.getContext().registerReceiver(this.mReceiver, intentFilter);
	}
	
	public void unregister() {
	
		this.getContext().unregisterReceiver(this.mReceiver);
		this.mTrackReceived = null;
	}

	public void setOnTrackReceivedListener(TrackReceivedListener l) {
	
		this.mTrackReceived = l;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			try {

				final Track track = IntentReceiver.this.mScrobbler.parseTrack(intent.getExtras());
				IntentReceiver.this.mTrackReceived.TrackReceived(track);
			} 
			catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	};
}
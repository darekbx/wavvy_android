package com.wavvy;

import java.util.List;

import com.wavvy.db.StorageManager;
import com.wavvy.logic.adapters.TrackAdapter;
import com.wavvy.model.Track;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;

public class SongsActivity extends Activity {

	public class RefreshReceiver extends BroadcastReceiver {
		 
	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	SongsActivity.this.loadSongs();
	    }
	}

	private RefreshReceiver mRefreshReceiver;
	private TrackAdapter mAdapter;
	private ListView mList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_songs);
		
		this.registerReceiver();
		this.loadSongs();
	}
	
	@Override
	protected void onDestroy() {

		if (this.mRefreshReceiver != null)
			this.unregisterReceiver(this.mRefreshReceiver);
		
		super.onDestroy();
	}
	
	private void registerReceiver() {

		this.mRefreshReceiver = new RefreshReceiver();
		final IntentFilter filter = new IntentFilter(RefreshReceiver.class.getName());
		
		this.registerReceiver(this.mRefreshReceiver, filter);
	}
	
	private void loadSongs() {

		if (this.mAdapter == null) {
		
			this.mAdapter = new TrackAdapter(this, R.layout.track_row);
			this.mList = (ListView)this.findViewById(R.id.songs_list);
			this.mList.setAdapter(this.mAdapter);
		}
		
		this.fillAdapter();
	}
	
	private void fillAdapter() {

		final StorageManager manager = new StorageManager(this);
		final List<Track> tracks = manager.getTracks();

		manager.close();
		
		this.mAdapter.clear();
		
		for (final Track track : tracks)
			this.mAdapter.add(track);

		this.mAdapter.notifyDataSetChanged();
	}
}
package com.wavvy;

import java.util.List;

import com.wavvy.db.StorageManager;
import com.wavvy.model.Track;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SongsActivity extends Activity {

	public class RefreshReceiver extends BroadcastReceiver {
		 
	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	SongsActivity.this.loadSongs();
	    }
	}

	private RefreshReceiver mRefreshReceiver;
	private ArrayAdapter<String> mAdapter;
	private ListView mList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_songs);
		
		this.createAdapter();
		this.registerReceiver();
		this.loadSongs();
	}
	
	@Override
	protected void onDestroy() {

		if (this.mRefreshReceiver != null)
			this.unregisterReceiver(this.mRefreshReceiver);
		
		super.onDestroy();
	}
	
	private void createAdapter() {
		
		this.mAdapter = new ArrayAdapter<String>(this, R.layout.list_row);
		this.mList = (ListView)this.findViewById(R.id.songs_list);
		this.mList.setAdapter(this.mAdapter);
	}
	
	private void registerReceiver() {

		this.mRefreshReceiver = new RefreshReceiver();
		final IntentFilter filter = new IntentFilter(RefreshReceiver.class.getName());
		
		this.registerReceiver(this.mRefreshReceiver, filter);
	}
	
	private void loadSongs() {

		final StorageManager manager = new StorageManager(this);
		final List<Track> tracks = manager.getTracks();
		
		manager.close();
		
		this.mAdapter.clear();
		
		for (final Track track : tracks)
			this.mAdapter.add(track.toString());
		
		this.mAdapter.notifyDataSetChanged();
	}
}
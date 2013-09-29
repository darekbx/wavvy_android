package com.wavvy;

import com.wavvy.listeners.TrackReceivedListener;
import com.wavvy.logic.IntentReceiver;
import com.wavvy.model.Track;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
public class MainActivity extends Activity {

	private IntentReceiver mIntentReceiver;
	
	private ArrayAdapter<String> mAdapter;
	private ListView mList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_main);

		this.mList = (ListView)this.findViewById(R.id.list);
		this.mAdapter = new ArrayAdapter<String>(this, R.layout.list_row);
		this.mList.setAdapter(this.mAdapter);
		
		this.mIntentReceiver = new IntentReceiver(this);
		this.mIntentReceiver.setOnTrackReceivedListener(new TrackReceivedListener() {
			
			@Override
			public void TrackReceived(Track track) {

				MainActivity.this.append(String.valueOf(track));
			}
		});
		this.mIntentReceiver.register();
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		
		if (this.mIntentReceiver != null)
			this.mIntentReceiver.unregister();
	}
	
	private void append(String text){

		this.mAdapter.add(text);
		this.mAdapter.notifyDataSetChanged();
	}

}
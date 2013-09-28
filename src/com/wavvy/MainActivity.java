package com.wavvy;

import android.os.Bundle;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MainActivity extends Activity {

	private TextView mText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		this.mText = (TextView)this.findViewById(R.id.text);
		
		final IntentFilter iF = new IntentFilter();
		iF.addAction("com.android.music.metachanged");
		iF.addAction("com.htc.music.metachanged");
		iF.addAction("com.andrew.apollo.metachanged");
		
		iF.addAction("com.rdio.android.metachanged");
		iF.addAction("com.nullsoft.winamp.metachanged");

		registerReceiver(mReceiver, iF);
	}
	
	private void append(String text){

		String current = this.mText.getText().toString();
		current += text;
		current += "\n";
		
		this.mText.setText(current);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			
			String artist = intent.getStringExtra("artist");
			String album = intent.getStringExtra("album");
			String track = intent.getStringExtra("track");
			
			MainActivity.this.append(artist + ":" + album + ":" + track);
		}
	};
	
}
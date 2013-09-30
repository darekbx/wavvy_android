package com.wavvy;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {

	private Button mSongsButton;
	private Button mExitButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_main);
		this.registerListeners();
	}
	
	@Override
	protected void onDestroy() {
		
		this.mExitButton.setOnClickListener(null);
		this.mSongsButton.setOnClickListener(null);
		
		super.onDestroy();
	}

	private void registerListeners() {
		
		this.mSongsButton = (Button)this.findViewById(R.id.button_songs);
		this.mExitButton = (Button)this.findViewById(R.id.button_exit);
		
		this.mSongsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				MainActivity.this.openSongs();
			}
		});
		
		this.mExitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				MainActivity.this.finish();
			}
		});
	}
	
	private void openSongs() {
	
		final Intent songsIntent = new Intent(this, SongsActivity.class);
		this.startActivity(songsIntent);
	}
}
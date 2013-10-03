package com.wavvy;

import com.wavvy.dialog.NickDialog;
import com.wavvy.logic.storage.UserStorage;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;

public class MainActivity extends Activity {

	private Button mSongsButton;
	private Button mExitButton;
	
	private UserStorage mUserStorage;
	private NickDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_main);
		
		this.mUserStorage = new UserStorage(this);
		
		if (!this.mUserStorage.isUserExists()) {
		
			this.mDialog = new NickDialog(this);
			this.mDialog.show();
		}
		
		this.registerListeners();
	}
	
	@Override
	protected void onDestroy() {
		
		this.mExitButton.setOnClickListener(null);
		this.mSongsButton.setOnClickListener(null);

		if (this.mDialog != null) {
		
			this.mDialog.setOnCancelListener(null);
			this.mDialog.setOnDismissListener(null);
		}
		
		super.onDestroy();
	}

	private void registerListeners() {

		if (this.mDialog != null) {
			
			this.mDialog.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
	
					MainActivity.this.finish();
				}
			});
			this.mDialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					
					
				}
			});
		}
		
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
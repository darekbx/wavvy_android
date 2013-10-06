package com.wavvy;

import java.util.List;

import com.wavvy.db.StorageManager;
import com.wavvy.dialog.NickDialog;
import com.wavvy.logic.adapters.TrackAdapter;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.Track;
import com.wavvy.model.User;
import com.wavvy.services.GpsService;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;

public class MainActivity extends Activity {

	public class RefreshReceiver extends BroadcastReceiver {
		 
	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	MainActivity.this.loadSongs();
	    }
	}

	private RefreshReceiver mRefreshReceiver;
	private TrackAdapter mAdapter;
	private ListView mList;
	
	private UserStorage mUserStorage;
	private User mUser;
	private NickDialog mDialog;
	private Button mMapButton;
	private Button mMyLocationsButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_main);
		
		this.mUserStorage = new UserStorage(this);
		
		if (!this.mUserStorage.isUserExists()) {
		
			this.mDialog = new NickDialog(this);
			this.mDialog.show();
		}
		else {
		
			this.loadUser();
		}
		
		this.registerListeners();
		
		this.startService(new Intent(this, GpsService.class));
	}
	
	@Override
	protected void onDestroy() {
		
		if (this.mDialog != null) {
		
			this.mDialog.setOnCancelListener(null);
			this.mDialog.setOnDismissListener(null);
		}

		if (this.mRefreshReceiver != null)
			this.unregisterReceiver(this.mRefreshReceiver);
		
		if (this.mMapButton != null)
			this.mMapButton.setOnClickListener(null);

		if (this.mMyLocationsButton != null)
			this.mMyLocationsButton.setOnClickListener(null);
		
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		final MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
			// TODO: remove reset option
			case R.id.menu_reset:
				
				this.mUserStorage.reset();
				new StorageManager(this).reset();
				
				break;
		}
		
		return super.onOptionsItemSelected(item);
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
					
					MainActivity.this.loadUser();
				}
			});
		}
		
		this.mMapButton = (Button)this.findViewById(R.id.main_map);
		this.mMapButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				final Intent intent = new Intent(MainActivity.this, MapActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});

		this.mMyLocationsButton = (Button)this.findViewById(R.id.main_my_locations);
		this.mMyLocationsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				final Intent intent = new Intent(MainActivity.this, MyLocationsActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
	}
	
	private void loadUser() {

		this.mUser = this.mUserStorage.getUser();
		this.setWelcomeMessage();

		this.registerReceiver();
		this.loadSongs();
	}
	
	private void setWelcomeMessage() {
		
		final String message = this.getString(R.string.main_nick, this.mUser.getNick());
		((TextView)this.findViewById(R.id.main_nick)).setText(message);
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
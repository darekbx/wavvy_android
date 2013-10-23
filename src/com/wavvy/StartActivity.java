package com.wavvy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wavvy.animations.MenuAnimation;
import com.wavvy.listeners.GetListener;
import com.wavvy.logic.LocationHelper;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;
import com.wavvy.logic.http.Utils;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.SongLocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class StartActivity extends FragmentActivity {

	public class RefreshReceiver extends BroadcastReceiver {
		 
	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	StartActivity.this.newSong();
	    }
	}

	private GoogleMap mMap;
	private List<SongLocation> mSongs;
	private LinearLayout mMenu;
	private int mUserId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_start);

		final Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.map);
		this.mMap = ((SupportMapFragment)fragment).getMap();
		this.mMap.setMyLocationEnabled(true);
		this.mMap.setOnMarkerClickListener(this.mMarkerClick);
		this.mMap.setOnMapClickListener(this.mMapClick);
		
		if (!Utils.isOnline(this))
			Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_LONG).show();
		else {
			
			this.collapseMenu();
			this.loadUser();
			this.loadPoints();
		}
	}
	
	@Override
	protected void onDestroy() {

		super.onDestroy();

		if (this.mMap != null) {
			
			this.mMap.setOnMarkerClickListener(null);
			this.mMap.setOnMapClickListener(null);
		}
	}
	
	private OnMarkerClickListener mMarkerClick = new OnMarkerClickListener() {
		
		@Override
		public boolean onMarkerClick(Marker marker) {
			
			StartActivity.this.expandMenu();
			return false;
		}
	};
	
	private OnMapClickListener mMapClick = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng point) {

			StartActivity.this.collapseMenu();
		}
	};
	
	private void collapseMenu() {

		if (this.mMenu == null)
			this.mMenu = (LinearLayout)this.findViewById(R.id.menu_bar);

		final MenuAnimation animation = new MenuAnimation(this.mMenu, 1);
		this.mMenu.startAnimation(animation);
		this.mMenu.setVisibility(View.VISIBLE);
	}
	
	private void expandMenu() {

		if (this.mMenu == null)
			this.mMenu = (LinearLayout)this.findViewById(R.id.menu_bar);

		final MenuAnimation animation = new MenuAnimation(this.mMenu, 0);
		this.mMenu.startAnimation(animation);
	}
	
	private void loadUser() {
	
		final UserStorage storage = new UserStorage(this);
		
		if (storage.isUserExists())
			this.mUserId = storage.getUser().getId();
	}
	
	private void loadPoints() {
	
		final URI address = new AddressBuilder(this).locations();
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				StartActivity.this.parseContent(content);
				
				StartActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						StartActivity.this.zoomToUser();
						StartActivity.this.addPoints();
						StartActivity.this.addMyLocation();
					}
				});
			}
			
			@Override
			public void failed(final String message) { 
				
				StartActivity.this.showError(message);
			}
		});
		
		if (address != null)
			get.execute(address);
	}
	
	private void zoomToUser() {

		final Marker userMarker = this.addMarker(this.mSongs.get(0));
		
		this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 8));
		this.mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
	}
	
	private void addPoints() {
		
		final int count = this.mSongs.size();
		
		if (count == 0)
			return;
		
		for (int i = 0; i < count; i++)
			this.addMarker(this.mSongs.get(i));
	}
	
	private void addMyLocation() {
	
		final LatLng location = LocationHelper.getLoction(this);

		final MarkerOptions markerOptions = new MarkerOptions()
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_flag))
			.position(location);
		this.mMap.addMarker(markerOptions);
	}
	
	private Marker addMarker(final SongLocation nearestUser) {

		final MarkerOptions markerOptions = new MarkerOptions()
			.position(nearestUser.getPosition())
			.title(nearestUser.toString())
			.snippet(nearestUser.getDate()); // TODO: calculate distance
		final Marker marker = this.mMap.addMarker(markerOptions);
		
		return marker;
	}
	
	private void parseContent(final String content) {
		
		try {
			
			final JSONArray array = new JSONArray(content);
			final int count = array.length();
			
			this.mSongs = new ArrayList<SongLocation>();
			
			JSONObject jo;
			SongLocation user;
			
			for (int i = 0; i < count; i++) {
			
				jo = array.getJSONObject(i);
				
				user = new SongLocation();
				user.fromJsonObject(jo, this);
				
				// skip my track
				if (this.mUserId == user.getIdUser())
					continue;
				
				this.mSongs.add(user);
			}
		} 
		catch (JSONException e) {
			
			e.printStackTrace();
		}
	}

	private void showError(final String message) {
	
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {

				Toast.makeText(StartActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void newSong() {
	
		// do nothing?
	}
}

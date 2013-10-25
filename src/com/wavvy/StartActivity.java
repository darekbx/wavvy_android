package com.wavvy;

import java.net.URI;
import java.util.LinkedHashMap;

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
import com.wavvy.dialog.LikeDialog;
import com.wavvy.listeners.ActionListener;
import com.wavvy.listeners.GetListener;
import com.wavvy.listeners.LikesListener;
import com.wavvy.listeners.TickListener;
import com.wavvy.logic.LocationHelper;
import com.wavvy.logic.UpdateTimer;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;
import com.wavvy.logic.http.Utils;
import com.wavvy.logic.managers.LikeManager;
import com.wavvy.logic.parsers.LocationParser;
import com.wavvy.logic.storage.LikeStorage;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.SongLocation;
import com.wavvy.services.GpsService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class StartActivity extends FragmentActivity {

	public class RefreshReceiver extends BroadcastReceiver {
		 
	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	/* received new song */
	    	/* do nothing */
	    }
	}

	private LinkedHashMap<SongLocation, Marker> mSongs;
	private GoogleMap mMap;
	private MenuAnimation mMenuAnimation;
	private ImageButton mMessage;
	private ImageButton mLike;
	private Marker mActiveMarker = null;
	private int mUserId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_start);
		this.startService(new Intent(this, GpsService.class));
		
		final Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.map);
		this.mMap = ((SupportMapFragment)fragment).getMap();
		this.mMap.setMyLocationEnabled(true);
		this.mMap.setOnMarkerClickListener(this.mMarkerClick);
		this.mMap.setOnMapClickListener(this.mMapClick);
		
		if (!Utils.isOnline(this))
			Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_LONG).show();
		else {
			
			UpdateTimer.setTickListener(this.mTick);
			
			this.loadMenuAnimation();
			this.loadEvents();
			this.loadUser();
			this.loadPoints(false);
			
			this.checkLikes();
		}
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		
		UpdateTimer.start();
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		UpdateTimer.stop();
	}
	
	@Override
	protected void onDestroy() {

		super.onDestroy();

		UpdateTimer.setTickListener(null);
		
		if (this.mMap != null) {
			
			this.mMap.setOnMarkerClickListener(null);
			this.mMap.setOnMapClickListener(null);
		}
		
		if (this.mMessage != null)
			this.mMessage.setOnClickListener(null);
		
		if (this.mLike != null)
			this.mLike.setOnClickListener(null);
	}
	
	private void loadEvents() {
	
		this.mMessage = (ImageButton)this.findViewById(R.id.menu_message);
		this.mMessage.setOnClickListener(this.mMessageClick);

		this.mLike = (ImageButton)this.findViewById(R.id.menu_like);
		this.mLike.setOnClickListener(this.mLikeClick);
	}
	
	private void loadUser() {
	
		final UserStorage storage = new UserStorage(this);
		
		if (storage.isUserExists())
			this.mUserId = storage.getUser().getId();
	}
	
	private void loadPoints(final boolean reload) {
	
		final URI address = new AddressBuilder(this).locations();
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				StartActivity.this.mSongs = new LocationParser(StartActivity.this).parse(content);
				
				StartActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						StartActivity.this.addPoints();
						StartActivity.this.addMyLocation();
						
						if (!reload)
							StartActivity.this.zoomToUser();
					}
				});
			}
			
			@Override
			public void failed(final String message) { 
				
				StartActivity.this.showMessage(message);
			}
		});
		
		if (address != null)
			get.execute(address);
	}
	
	private void zoomToUser() {

		final LatLng location = LocationHelper.getLocation(this);
		
		this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 8));
		this.mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
	}
	
	private void addPoints() {
		
		this.mMap.clear();
		
		final int count = this.mSongs.size();
		
		if (count == 0)
			return;
		
		for (SongLocation key : this.mSongs.keySet())
			this.addMarker(key);
	}

	private void addMyLocation() {
	
		final LatLng location = LocationHelper.getLocation(this);

		final MarkerOptions markerOptions = new MarkerOptions()
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_flag))
			.position(location);
		this.mMap.addMarker(markerOptions);
	}
	
	private Marker addMarker(final SongLocation location) {

		final MarkerOptions markerOptions = new MarkerOptions()
			.position(location.getPosition())
			.title(location.toString())
			.snippet(this.getDistance(location));
		final Marker marker = this.mMap.addMarker(markerOptions);
		
		// associate marker to location
		this.mSongs.put(location, marker);
		
		return marker;
	}
	
	private String getDistance(SongLocation target) {

		final Location myLocation = LocationHelper.getFullLocation(this);
		
		if (myLocation == null)
			return new String();
		
		final LatLng position = target.getPosition();
		final Location location = new Location(new String());
		location.setLatitude(position.latitude);
		location.setLongitude(position.longitude);

		final float distance = myLocation.distanceTo(location);
		
		if (distance < 1000) return this.getString(R.string.format_m, (int)distance);
		else return this.getString(R.string.format_km, (int)(distance / 1000));
	}

	private void showMessage(final int messageId) {
	
		this.showMessage(this.getString(messageId));
	}
	
	private void showMessage(final String message) {
	
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {

				Toast.makeText(StartActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void loadMenuAnimation() {

		this.mMenuAnimation = new MenuAnimation(this, 
				(LinearLayout)this.findViewById(R.id.menu_bar));
		
		this.mMenuAnimation.collapse(true);
	}
	
	private void checkLikes() {
	
		if (this.mUserId == -1)
			return;
		
		final LikeStorage storage = new LikeStorage(this);
		final int myLikes = storage.getLikesCount();

		new LikeManager(this).likes(this.mUserId, new LikesListener() {
			
			@Override
			public void onSuccess(int likes) {

				if (likes <= myLikes)
					return; // no new likes
				
				// received like!
				storage.setLikesCount(likes);
				
				StartActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						new LikeDialog(StartActivity.this).show();
					}
				});
			}
			
			@Override
			public void onError() { }
		});
	}
	
	private TickListener mTick = new TickListener() {
		
		@Override
		public void onTick() {

			StartActivity.this.loadPoints(true);
			StartActivity.this.checkLikes();
		}
	};

	private OnClickListener mMessageClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			// TODO:
		}
	};

	private OnClickListener mLikeClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			final StartActivity parent = StartActivity.this;
			
			if (parent.mActiveMarker == null)
				return;
			
			final Marker marker = parent.mActiveMarker;
			final SongLocation location = (SongLocation)com.wavvy.logic.Utils
					.getKeyFromValue(parent.mSongs, marker);
			
			if (location != null) {
				
				new LikeManager(parent).like(location.getIdUser(), new ActionListener() {
					
					@Override
					public void onSuccess() { parent.showMessage(R.string.like_success); }
					
					@Override
					public void onError() { parent.showMessage(R.string.like_error); }
				});
			}
		}
	};
	
	private OnMarkerClickListener mMarkerClick = new OnMarkerClickListener() {
		
		@Override
		public boolean onMarkerClick(Marker marker) {

			if (marker.getTitle() == null)
				return false;
			
			StartActivity.this.mMenuAnimation.expand();
			StartActivity.this.mActiveMarker = marker;
			return false;
		}
	};
	
	private OnMapClickListener mMapClick = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng point) {

			StartActivity.this.mMenuAnimation.collapse();
			StartActivity.this.mActiveMarker = null;
		}
	};
}

package com.wavvy.logic.map;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wavvy.R;
import com.wavvy.StartActivity;
import com.wavvy.db.StorageManager;
import com.wavvy.listeners.GetListener;
import com.wavvy.logic.LocationHelper;
import com.wavvy.logic.SongComprasion;
import com.wavvy.logic.Utils;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;
import com.wavvy.logic.parsers.DistanceParser;
import com.wavvy.logic.parsers.LocationParser;
import com.wavvy.model.SongLocation;
import com.wavvy.model.Track;

public class MapLogic {

	private HashMap<SongLocation, Marker> mSongs;
	private GoogleMap mMap;
	private StorageManager mStorageManager;
	private StartActivity mActivity;
	private Track mLastTrack = null;
	private SongLocation mTempSong = null;
	private DistanceParser mParsers = null;

	public MapLogic(StartActivity activity, GoogleMap map) {
		
		this.mActivity = activity;
		this.mMap = map;
		
		this.mParsers = new DistanceParser(this.getContext());
	}
	
	public void setData(HashMap<SongLocation, Marker> data) {
	
		this.mSongs = data;
	}
	
	public SongLocation getSongLocation(Marker marker) {
	
		SongLocation item = null;
		
		for (SongLocation song : this.mSongs.keySet())
			if (this.mSongs.get(song).getId().equals(marker.getId())) {
				
				item = song;
				break;
			}
		
		if (item == null)
			return this.mTempSong;
		else {
		
			this.mTempSong = item;
			return item;
		}
	}
	
	public void loadPoints(final boolean reload) {
		
		final URI address = new AddressBuilder(this.mActivity).locations();
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				MapLogic.this.setData(
						new LocationParser(MapLogic.this.mActivity).parse(content));
				
				MapLogic.this.mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						MapLogic.this.setLastTrack();
						MapLogic.this.addPoints();
						MapLogic.this.addMyLocation();
						
						if (!reload)
							MapLogic.this.zoomToUser();
					}
				});
			}
			
			@Override
			public void failed(final String message) { 
				
				MapLogic.this.showMessage(message);
			}
		});
		
		if (address != null)
			get.execute(address);
	}
	
	public void zoomToUser() {

		final LatLng location = LocationHelper.getLocation(this.getContext());
		this.mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
		
		// zoom to nearest user
		this.findNearestUser(location);
	}
	
	private void zoomToBounds(final LatLng positionA, final LatLng positionB) {
		
		final LatLngBounds bounds = new LatLngBounds.Builder()
	        .include(new LatLng(positionB.latitude, positionB.longitude))
	        .include(new LatLng(positionA.latitude, positionA.longitude)).build();

		final int padding = Utils.screenWidth(this.getContext()) / 4;
		
		this.mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
	}
	
	public void addPoints() {
		
		this.mMap.clear();
		
		final int count = this.mSongs.size();
		
		if (count == 0)
			return;
		
		for (SongLocation key : this.mSongs.keySet())
			this.addMarker(key);
	}

	public void addMyLocation() {
	
		final LatLng location = LocationHelper.getLocation(this.getContext());

		final MarkerOptions markerOptions = new MarkerOptions()
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_flag))
			.position(location);
		this.mMap.addMarker(markerOptions);
	}
	
	public Marker addMarker(final SongLocation location) {

		final MarkerOptions markerOptions = new MarkerOptions()
			.position(location.getPosition())
			.title(location.toString())
			.snippet(this.getDistance(location));
		
		final Marker marker = this.mMap.addMarker(markerOptions);
		final int comprasion = SongComprasion.compareExtract(this.mLastTrack, location);
		int markerIcon = R.drawable.marker_grey;
		
		switch (comprasion) {
		
			case SongComprasion.SAME_ARTIST_AND_TITLE: markerIcon = R.drawable.marker_star; break;
			case SongComprasion.SAME_ARTIST: markerIcon = R.drawable.marker_blue; break;
		}
		
		marker.setIcon(BitmapDescriptorFactory.fromResource(markerIcon));
		
		// associate marker to location
		this.mSongs.put(location, marker);
		
		return marker;
	}
	
	private void findNearestUser(final LatLng userPosition) {
		
		final URI address = new AddressBuilder(this.mActivity).nearest(userPosition);
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				final LatLng nearest = MapLogic.this.mParsers.parseDistance(content);
				
				if (nearest == null)
					return;
				
				MapLogic.this.mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						MapLogic.this.zoomToBounds(userPosition, nearest);
					}
				});
			}
			
			@Override
			public void failed(final String message) { 
				
				MapLogic.this.showMessage(message);
			}
		});
		
		if (address != null)
			get.execute(address);
	}
	
	private String getDistance(SongLocation target) {

		final Location myLocation = LocationHelper.getFullLocation(this.getContext());
		
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
	
	private void setLastTrack() {
	
		this.mStorageManager = new StorageManager(this.getContext());
		final Track track = this.mStorageManager.getLastTrack();
		this.mStorageManager.close();
		
		if (track == null) {
			
			this.mLastTrack = null;
			return;
		}
		
		// check time
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -5); // 5 minutes
		
		final long timeAdd = calendar.getTimeInMillis();
		
		if (track.getDate() >= timeAdd) this.mLastTrack = track;
		else this.mLastTrack = null;
	}
	
	private void showMessage(final String message) {
	
		this.mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {

				Toast.makeText(MapLogic.this.mActivity, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private String getString(final int resourceId, Object... formatArgs) {
	
		return this.mActivity.getString(resourceId, formatArgs);
	}
	
	private Context getContext() {
	
		return this.mActivity;
	}
}
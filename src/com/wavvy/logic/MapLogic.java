package com.wavvy.logic;

import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

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
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;
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

	public MapLogic(StartActivity activity, GoogleMap map) {
		
		this.mActivity = activity;
		this.mMap = map;
	}
	
	public void setData(HashMap<SongLocation, Marker> data) {
	
		this.mSongs = data;
	}
	
	public SongLocation getSongLocation(Marker marker) {
	
		final SongLocation item = this.getSongLocationFromMarker(marker);
		
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

		//this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 8));
		this.mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
		
		// zoom to nearest user
		this.findNearestUser(location);
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
		final int comprasion = SongComprasion.compare(this.mLastTrack, location);
		int markerIcon = R.drawable.marker_grey;
		
		switch (comprasion) {
		
			case SongComprasion.SAME_TITLE: markerIcon = R.drawable.marker_star; break;
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

				final LatLng nearest = MapLogic.this.parseDistance(content);
				
				if (nearest == null)
					return;
				
				MapLogic.this.mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						MapLogic.this.zoomToNearest(userPosition, nearest);
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
	
	private LatLng parseDistance(final String content) {
		
		try {

			final JSONObject jo = new JSONObject(content);
			final String distanceKey = this.getString(R.string.map_distance);
			
			if (jo.has(distanceKey)) {
				
				final int distance = jo.optInt(distanceKey);
				
				if (distance <= 0)
					return null;
				
				final double lat = jo.optDouble(this.getString(R.string.map_latitude));
				final double lon = jo.optDouble(this.getString(R.string.map_longitude));
				
				return new LatLng(lat, lon);
			}
		} 
		catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void zoomToNearest(final LatLng userPosition, final LatLng nearest) {
		
		final LatLngBounds bounds = new LatLngBounds.Builder()
	        .include(new LatLng(nearest.latitude, nearest.longitude))
	        .include(new LatLng(userPosition.latitude, userPosition.longitude)).build();

		this.mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250, 250, 30));
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

	public SongLocation getSongLocationFromMarker(Marker marker) {
		
		for (SongLocation song : this.mSongs.keySet())
			if (this.mSongs.get(song).getId().equals(marker.getId()))
				return song;
		
		return null;
	}

	private String getString(final int resourceId, Object... formatArgs) {
	
		return this.mActivity.getString(resourceId, formatArgs);
	}
	
	private Context getContext() {
	
		return this.mActivity;
	}
}
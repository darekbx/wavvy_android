package com.wavvy;

import java.net.URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wavvy.listeners.GetListener;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.MyLocation;
import com.wavvy.model.User;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class MyLocationsActivity extends FragmentActivity {

	private GoogleMap mMap;
	private User mUser;
	
	private MyLocation[] mLocations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_map);
		
		final Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.map);
		this.mMap = ((SupportMapFragment)fragment).getMap();
		
		this.loadLocations();
	}
	
	private void loadLocations() {

		this.mUser = new UserStorage(this).getUser();
		
		final URI address = new AddressBuilder(this).userLocations(this.mUser.getId());
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				MyLocationsActivity.this.parseContent(content);
				
				if (MyLocationsActivity.this.containsLocations()) {
				
					MyLocationsActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {

							MyLocationsActivity.this.addMarkers();
						}
					});
				}
			}
			
			@Override
			public void failed(final String message) { 
				
				MyLocationsActivity.this.showError(message);
			}
		});
		
		if (address != null)
			get.execute(address);
	}
	
	private void addMarkers() {
		
		final int count = this.mLocations.length;
		Marker marker = null;
		
		for (int i = 0; i < count; i++) 
			marker = this.addMarker(this.mLocations[i]);
		
		this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 8));
		this.mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
	}
	
	private Marker addMarker(final MyLocation locationUser) {

		final MarkerOptions markerOptions = new MarkerOptions()
			.position(locationUser.getPosition())
			.title(locationUser.toString());
		final Marker marker = this.mMap.addMarker(markerOptions);

		return marker;
	}
	
	private void parseContent(final String content) {
		
		try {
			
			final JSONArray array = new JSONArray(content);
			final int count = array.length();
			final String[] fields = this.getResources().getStringArray(R.array.location_fields);
			
			this.mLocations = new MyLocation[count];
			
			JSONObject jo;
			MyLocation location;
			
			for (int i = 0; i < count; i++) {
			
				jo = array.getJSONObject(i);
				
				location = new MyLocation();
				location.setIdUser(jo.optInt(fields[0]));
				location.setArtist(jo.optString(fields[1]));
				location.setTitle(jo.optString(fields[2]));
				location.setLatitude(jo.optDouble(fields[3]));
				location.setLongitude(jo.optDouble(fields[4]));
				
				this.mLocations[i] = location;
			}
		} 
		catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	
	private boolean containsLocations() {
	
		return this.mLocations != null && this.mLocations.length > 0;
	}
	
	private void showError(final String message) {
	
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {

				Toast.makeText(MyLocationsActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
}
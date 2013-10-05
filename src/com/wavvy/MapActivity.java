package com.wavvy;

import java.net.URI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wavvy.listeners.GetListener;
import com.wavvy.logic.LocationManager;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.NearestUser;
import com.wavvy.model.User;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class MapActivity extends FragmentActivity {

	private GoogleMap mMap;
	private User mUser;
	
	private NearestUser[] mNearestUsers = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.activity_map);
		
		final Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.map);
		this.mMap = ((SupportMapFragment)fragment).getMap();
		
		this.loadNearestUsers();
	}
	
	private void loadNearestUsers() {
	
		this.mUser = new UserStorage(this).getUser();

		// TODO: latitude & longitude
		final LatLng location = LocationManager.getRandom();
		
		final URI address = new AddressBuilder(this).nearest(location, this.mUser.getId());
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				MapActivity.this.parseContent(content);
				
				if (MapActivity.this.containsNearestUsers()) {
					
					MapActivity.this.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {

							MapActivity.this.zoomToUser();
							MapActivity.this.addOtherUsers();
						}
					});
				}
			}
			
			@Override
			public void failed(final String message) { 
				
				MapActivity.this.showError(message);
			}
		});
		
		if (address != null)
			get.execute(address);
	}
	
	private void zoomToUser() {

		final Marker userMarker = this.addMarker(this.mNearestUsers[0]);
				
		this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 8));
		this.mMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
	}
	
	private void addOtherUsers() {
		
		final int count = this.mNearestUsers.length;
		
		if (count <= 1)
			return;
		
		for (int i = 1; i < count; i++)
			this.addMarker(this.mNearestUsers[i]);
	}
	
	private Marker addMarker(final NearestUser nearestUser) {

		final MarkerOptions markerOptions = new MarkerOptions()
			.position(nearestUser.getPosition())
			.title(nearestUser.getNick());
		final Marker marker = this.mMap.addMarker(markerOptions);

		return marker;
	}
	
	private void parseContent(final String content) {
		
		try {
			
			final JSONArray array = new JSONArray(content);
			final int count = array.length();
			
			this.mNearestUsers = new NearestUser[count];
			
			JSONObject jo;
			NearestUser user;
			
			for (int i = 0; i < count; i++) {
			
				jo = array.getJSONObject(i);
				
				user = new NearestUser();
				user.setId(jo.optInt(this.getString(R.string.nearest_user_id)));
				user.setNick(jo.optString(this.getString(R.string.nearest_user_nick)));
				user.setDistance(jo.optInt(this.getString(R.string.nearest_user_distance)));
				user.setLatitude(jo.optDouble(this.getString(R.string.nearest_user_latitude)));
				user.setLongitude(jo.optDouble(this.getString(R.string.nearest_user_longitude)));
				
				this.mNearestUsers[i] = user;
			}
		} 
		catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	
	private boolean containsNearestUsers() {
	
		return this.mNearestUsers != null && this.mNearestUsers.length > 0;
	}
	
	private void showError(final String message) {
	
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {

				Toast.makeText(MapActivity.this, message, Toast.LENGTH_LONG).show();
			}
		});
	}
}
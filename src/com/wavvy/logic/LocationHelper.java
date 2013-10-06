package com.wavvy.logic;

import java.util.Random;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

public class LocationHelper {

	public static LatLng getRandom() {

		final Random r = new Random();
		double latitude = 52 + r.nextDouble();
		double longitude = 21 + r.nextDouble();
		
		return new LatLng(latitude, longitude);
	}
	
	public static LatLng getLoction(Context context) {
	
		final String service = Context.LOCATION_SERVICE;
		final LocationManager manager = (LocationManager)context.getSystemService(service);
		
		final String provider = LocationManager.GPS_PROVIDER;
		final Location location = manager.getLastKnownLocation(provider);
		
		if (location != null)
			return new LatLng(location.getLatitude(), location.getLongitude());
		else 
			return new LatLng(52, 21);
	}
	
	public static Boolean isLocationEnabled(Context context) {

		final String service = Context.LOCATION_SERVICE;
		final LocationManager manager = (LocationManager)context.getSystemService(service);
		
		return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
}
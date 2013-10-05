package com.wavvy.logic;

import java.util.Random;

import com.google.android.gms.maps.model.LatLng;

public class LocationManager {

	public static LatLng getRandom() {

		final Random r = new Random();
		double latitude = 51 + r.nextDouble();
		double longitude = 21 + r.nextDouble();
		
		return new LatLng(latitude, longitude);
	}
}
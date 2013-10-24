package com.wavvy.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class GpsService extends Service {

	private LocationManager mLocationManager;
	private final IBinder mBinder = new LocalBinder();
	
    private static final long TIME_UPDATE = 3000; // 30s
    private static final float DISTANCE_UPDATE = 10; // 10m
    
	private void startGpsUpdates() {

		this.mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

		String provider = LocationManager.NETWORK_PROVIDER;
		
		if (this.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			provider = LocationManager.GPS_PROVIDER;

		this.mLocationManager.requestLocationUpdates(
				provider, 
				TIME_UPDATE,
				DISTANCE_UPDATE,
				this.mListener);
	}
	
	private LocationListener mListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) { }

		@Override
		public void onProviderDisabled(String provider) { }

		@Override
		public void onProviderEnabled(String provider) { }

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) { }
	};
	
	private void stopGpsUpdates() {
	
		if (this.mLocationManager != null)
			this.mLocationManager.removeUpdates(this.mListener);
	}
	
	@Override
	public void onCreate() {

		super.onCreate();

		this.startGpsUpdates();
	}
	
	@Override
	public void onDestroy() {

		super.onDestroy();
		
		this.stopGpsUpdates();
	}
	
	@Override
	public IBinder onBind(Intent intent) {

		return this.mBinder;
	}
	
	public class LocalBinder extends Binder {
		
		GpsService getService() {
        	
			return GpsService.this;
        }
	}
}
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
	
    private static final long TIME_UPDATE = 30000; // 30s
    private static final long DISTANCE_UPDATE = 10; // 10m
    
	private void startGpsUpdates() {
		
		this.mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		this.mLocationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 
				TIME_UPDATE, 
				DISTANCE_UPDATE,
				this.mLocationListener);
	}
	
	private void stopGpsUpdates() {
	
		if (this.mLocationManager != null)
			this.mLocationManager.removeUpdates(this.mLocationListener);
	}
	
	private LocationListener mLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) { }

		@Override
		public void onProviderDisabled(String provider) { }

		@Override
		public void onProviderEnabled(String provider) { }

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) { }
	};
	
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
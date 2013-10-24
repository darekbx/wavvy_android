package com.wavvy.services;

import com.wavvy.StartActivity.LocationReceiver;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

public class GpsService extends Service {

	private LocationManager mLocationManager;
	private PendingIntent mIntent;
	private final IBinder mBinder = new LocalBinder();
	
    private static final long TIME_UPDATE = 0;
    private static final float DISTANCE_UPDATE = 0;
    
	private void startGpsUpdates() {

		final Intent intent = new Intent(this, LocationReceiver.class);
		this.mIntent = PendingIntent.getBroadcast(getApplicationContext(), 
				0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		this.mLocationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

		String provider = LocationManager.NETWORK_PROVIDER;
		
		if (this.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			provider = LocationManager.GPS_PROVIDER;

		this.mLocationManager.requestLocationUpdates(
				provider, 
				TIME_UPDATE,
				DISTANCE_UPDATE,
				this.mIntent);
	}
	
	private void stopGpsUpdates() {
	
		if (this.mLocationManager != null)
			this.mLocationManager.removeUpdates(this.mIntent);
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
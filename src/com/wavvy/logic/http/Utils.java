package com.wavvy.logic.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	
	public static boolean isOnline(Context context) {
		
		final ConnectivityManager cm = (ConnectivityManager)context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = cm.getActiveNetworkInfo();
		
		if (netInfo != null && netInfo.isConnectedOrConnecting())
			return true;
		
		return false;
	}
}
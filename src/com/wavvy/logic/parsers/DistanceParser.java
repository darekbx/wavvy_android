package com.wavvy.logic.parsers;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.wavvy.R;
import com.wavvy.logic.BaseContext;

public class DistanceParser extends BaseContext {

	public DistanceParser(Context context) {
		
		super(context);
	}

	public LatLng parseDistance(final String content) {
		
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
}
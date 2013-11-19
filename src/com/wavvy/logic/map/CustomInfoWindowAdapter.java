package com.wavvy.logic.map;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.wavvy.R;
import com.wavvy.listeners.MarkerDataListener;
import com.wavvy.logic.BaseContext;
import com.wavvy.logic.LocationHelper;
import com.wavvy.model.SongLocation;

@Deprecated
public class CustomInfoWindowAdapter extends BaseContext implements InfoWindowAdapter {

	private final MarkerDataListener mDataListener;
	
	public CustomInfoWindowAdapter(final Context context, final MarkerDataListener listener) {
		
		super(context);
		
		this.mDataListener = listener;
	}
	
	@Override
	public View getInfoContents(Marker marker) {

		final SongLocation song = this.mDataListener.getMarkerData(marker);
		final View view = LayoutInflater.from(this.getContext()).inflate(R.layout.map_info_window, null);
		
		final TextView title = (TextView)view.findViewById(R.id.info_window_title);
		final TextView distance = (TextView)view.findViewById(R.id.info_window_distance);
		
		title.setText(song.toString());
		distance.setText(this.getDistance(song));
		
		this.handleButtons(view);
		
		return view;
	}

	@Override
	public View getInfoWindow(final Marker marker) {
		
		return null;
	}
	
	private void handleButtons(final View view) {
	
		((ImageButton)view.findViewById(R.id.info_window_message)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				view.setLayoutParams(new LayoutParams(300, 600));
			}
		});
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
}
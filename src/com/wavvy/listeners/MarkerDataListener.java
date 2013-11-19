package com.wavvy.listeners;

import com.google.android.gms.maps.model.Marker;
import com.wavvy.model.SongLocation;

public interface MarkerDataListener {

	public SongLocation getMarkerData(final Marker marker);
}
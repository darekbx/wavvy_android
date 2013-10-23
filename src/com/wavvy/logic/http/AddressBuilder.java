package com.wavvy.logic.http;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.wavvy.R;
import com.wavvy.logic.BaseContext;

public class AddressBuilder extends BaseContext {

	public AddressBuilder(Context context) {
		
		super(context);
	}

	@Deprecated
	public URI registerNick(String nick) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_registernick, nick));
		
		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	@Deprecated
	public URI userLocations(int userId) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_userlocations, userId));

		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	@Deprecated
	public URI nearest(LatLng location, int userId) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_nearest, 
				location.latitude, location.longitude, userId));

		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	public URI locations() {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_locations));

		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	public URI add() {

		try {
			
			return new URI(this.getString(R.string.address_base));
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
		
	}
}
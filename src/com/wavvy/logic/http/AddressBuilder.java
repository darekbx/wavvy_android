package com.wavvy.logic.http;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;

import com.wavvy.R;
import com.wavvy.logic.BaseContext;

public class AddressBuilder extends BaseContext {

	public AddressBuilder(Context context) {
		
		super(context);
	}

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

	public URI nearest(Double latitude, Double longitude, int userId) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_nearest, latitude, longitude, userId));

		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}
}
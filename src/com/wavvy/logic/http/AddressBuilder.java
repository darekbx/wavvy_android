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

	public URI nickExists(String nick) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_nickexists, nick));
		
		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	public URI reserveNick(String nick) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_nickexists, nick));
		
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
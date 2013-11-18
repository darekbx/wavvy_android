package com.wavvy.logic.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.wavvy.R;
import com.wavvy.logic.BaseContext;

public class AddressBuilder extends BaseContext {

	public AddressBuilder(Context context) {
		
		super(context);
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
	
	public URI like(int targetUserId) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_like, targetUserId));

		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	public URI likes(int userId) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_likes, userId));

		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	public URI message(int fromIdUser, int targetIdUser, String message64) {

		final StringBuilder builder = new StringBuilder();
		
		builder.append(this.getString(R.string.address_base));
		
		try {
			
			builder.append(this.getString(R.string.address_message, 
					fromIdUser, 
					targetIdUser, 
					URLEncoder.encode(message64, "UTF-8")));
		} 
		catch (UnsupportedEncodingException e1) {

			e1.printStackTrace();
		}

		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}
	
	public URI messages(int userId) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_messages, userId));

		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	public URI nearest(LatLng location) {

		final StringBuilder builder = new StringBuilder();
		builder.append(this.getString(R.string.address_base));
		builder.append(this.getString(R.string.address_nearest, 
				location.latitude, location.longitude));

		try {
			
			return new URI(builder.toString());
		} 
		catch (URISyntaxException e) {
			
			e.printStackTrace();
			return null;
		}
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
}
package com.wavvy.logic.managers;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.wavvy.R;
import com.wavvy.listeners.ActionListener;
import com.wavvy.listeners.GetListener;
import com.wavvy.listeners.LikesListener;
import com.wavvy.logic.BaseContext;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;

public class LikeManager extends BaseContext {

	public LikeManager(Context context) {
		
		super(context);
	}

	public void like(int targetUserId, final ActionListener listener) {
	
		final URI address = new AddressBuilder(this.getContext()).like(targetUserId);
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				if (listener != null)
					listener.onSuccess();
			}
			
			@Override
			public void failed(final String message) { 

				if (listener != null)
					listener.onError();
			}
		});
		
		if (address != null)
			get.execute(address);
	}
	
	public void likes(int userId, final LikesListener listener) {

		final URI address = new AddressBuilder(this.getContext()).likes(userId);
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				if (listener != null)
					listener.onSuccess(LikeManager.this.parseResponse(content));
			}
			
			@Override
			public void failed(final String message) { 

				if (listener != null)
					listener.onError();
			}
		});
		
		if (address != null)
			get.execute(address);
	}
	
	private int parseResponse(final String content) {

		try {
			
			final JSONObject jObject = new JSONObject(content);
			final String count = this.getString(R.string.like_count);
			
			if (jObject.has(count)) 
				return jObject.optInt(count);
			
			return 0;
		} 
		catch (JSONException e) {

			e.printStackTrace();
			return 0;
		}
	}
}
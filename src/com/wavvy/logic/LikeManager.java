package com.wavvy.logic;

import java.net.URI;

import android.content.Context;

import com.wavvy.listeners.ActionListener;
import com.wavvy.listeners.GetListener;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;

public class LikeManager extends BaseContext {

	public LikeManager(Context context) {
		
		super(context);
	}

	public void Like(int targetUserId, final ActionListener listener) {
	
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
}
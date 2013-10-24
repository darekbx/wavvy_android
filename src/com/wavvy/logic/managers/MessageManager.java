package com.wavvy.logic.managers;

import java.net.URI;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.wavvy.R;
import com.wavvy.listeners.ActionListener;
import com.wavvy.listeners.GetListener;
import com.wavvy.listeners.MessagesListener;
import com.wavvy.logic.BaseContext;
import com.wavvy.logic.Utils;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;
import com.wavvy.logic.parsers.MessageParser;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.Message;
import com.wavvy.model.User;

public class MessageManager extends BaseContext {

	private final UserStorage mUserStorage;
	
	public MessageManager(Context context) {
		
		super(context);
		
		this.mUserStorage = new UserStorage(context);
	}

	public void send(int targetUserId, String message, final ActionListener listener) {

		final int userId = this.mUserStorage.getUser().getId();
		final String message64 = Utils.toBase64(message);
		final URI address = new AddressBuilder(this.getContext()).message(userId, targetUserId, message64);
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				try {
					
					if (!MessageManager.this.mUserStorage.isUserExists()) {
					
						// if user is missing then parse json and get created id_user
						final JSONObject jo = new JSONObject(content);
						
						if (jo.has(MessageManager.this.getString(R.string.response_id_user))) {
						
							// created new user
							final User user = new User();
	
							user.fromJsonObject(jo, MessageManager.this.getContext());
							MessageManager.this.mUserStorage.setUser(user);
						}
					}
				}
				catch (JSONException e) {

					e.printStackTrace();
				}
				
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
	
	public void get(final MessagesListener listener) {

		final int userId = this.mUserStorage.getUser().getId();
		final URI address = new AddressBuilder(this.getContext()).messages(userId);
		final Get get = new Get();
		get.setOnGetListener(new GetListener() {
			
			@Override
			public void success(final String content) {

				if (listener != null) {
					
					final Context context = MessageManager.this.getContext();
					final List<Message> messages = new MessageParser(context).parse(content);
					
					listener.onSuccess(messages);
				}
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
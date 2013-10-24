package com.wavvy.logic.parsers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.wavvy.logic.BaseContext;
import com.wavvy.model.Message;

public class MessageParser extends BaseContext {

	public MessageParser(Context context) {
		
		super(context);
	}

	public List<Message> parse(final String content) {

		final List<Message> messages = new ArrayList<Message>();
		
		try {
			
			final JSONArray array = new JSONArray(content);
			final int count = array.length();
			
			JSONObject jo;
			Message message;
			
			for (int i = 0; i < count; i++) {
			
				jo = array.getJSONObject(i);
				
				message = new Message();
				message.fromJsonObject(jo, this.getContext());
				
				messages.add(message);
			}
		} 
		catch (JSONException e) {
			
			e.printStackTrace();
		}
		
		return messages;
	}
}
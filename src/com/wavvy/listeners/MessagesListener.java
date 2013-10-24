package com.wavvy.listeners;

import java.util.List;

import com.wavvy.model.Message;

public interface MessagesListener {

	public void onSuccess(List<Message> messages);
	public void onError();
}
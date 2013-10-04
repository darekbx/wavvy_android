package com.wavvy.listeners;

public interface PostListener {

	public void success(String content);
	public void failed(String message);
}
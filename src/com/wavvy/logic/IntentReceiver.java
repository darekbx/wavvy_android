package com.wavvy.logic;

import com.wavvy.R;
import com.wavvy.receivers.MusicReceiver;

import android.content.Context;
import android.content.IntentFilter;

public class IntentReceiver extends BaseContext {

	private MusicReceiver mReceiver;
	
	public IntentReceiver(Context context) {

		super(context);
		
		this.mReceiver = new MusicReceiver();
	}

	public void register() {

		final IntentFilter intentFilter = new IntentFilter();
		final String[] filters = this.getStringArray(R.array.intent_filters);

		for (String filter : filters)
			intentFilter.addAction(filter);

		this.getContext().registerReceiver(this.mReceiver, intentFilter);
	}
	
	public void unregister() {
	
		this.getContext().unregisterReceiver(this.mReceiver);
	}
}
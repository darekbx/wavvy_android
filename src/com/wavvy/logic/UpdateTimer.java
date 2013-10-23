package com.wavvy.logic;

import java.util.Timer;
import java.util.TimerTask;

import com.wavvy.listeners.TickListener;

public class UpdateTimer {

	private static Timer sTimer = null;
	private static final int INTERVAL = 10000; // 30s
	private static TickListener sListener;
	
	public static void start() {

		UpdateTimer.sTimer = new Timer();
		UpdateTimer.sTimer.scheduleAtFixedRate(new TimerTask() {
					
					@Override
					public void run() {

						UpdateTimer.tick();
					}
				}, INTERVAL, INTERVAL);
	}
	
	public static void stop() {
	
		UpdateTimer.sTimer.cancel();
		UpdateTimer.sTimer = null;
	}
	
	public static void setTickListener(TickListener listener) {
	
		UpdateTimer.sListener = listener;
	}
	
	private static void tick() {

		if (UpdateTimer.sListener != null)
			UpdateTimer.sListener.onTick();	
	}
}
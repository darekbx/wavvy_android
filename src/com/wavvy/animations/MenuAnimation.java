package com.wavvy.animations;

import android.view.View;

public class MenuAnimation extends ExpandCollapseAnimation {

	private static final int DURATION = 100;
	
	public MenuAnimation(View view, int type) {
		
		super(view, DURATION, type);
	}
}
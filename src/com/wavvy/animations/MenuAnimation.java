package com.wavvy.animations;

import com.wavvy.R;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

public class MenuAnimation {

	private Context mContext;
	private LinearLayout mMenu;
	private boolean mExpanded = false;
	
	public MenuAnimation(Context context, LinearLayout menu) {
	
		this.mContext = context;
		this.mMenu = menu;
	}

	public void collapse() {

		if (!this.mExpanded)
			return;
		
		this.collapse(false);
	}
	
	public void collapse(boolean isFirst) {

		if (isFirst) this.mMenu.setVisibility(View.GONE);
		else this.mMenu.setVisibility(View.VISIBLE);
		
		Animation a = AnimationUtils.loadAnimation(this.mContext, R.anim.slide_down);
		a.setInterpolator((new DecelerateInterpolator()));
		a.setAnimationListener(new MyAnimationListener() {
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				
				MenuAnimation.this.mMenu.setVisibility(View.GONE);
			}
		});
		this.mMenu.startAnimation(a);

		this.mExpanded = false;
	}
	
	public void expand() {

		if (this.mExpanded)
			return;
		
		Animation a = AnimationUtils.loadAnimation(this.mContext, R.anim.slide_up);
		a.setInterpolator((new AccelerateDecelerateInterpolator()));
		this.mMenu.startAnimation(a);
		this.mMenu.setVisibility(View.VISIBLE);
		
		this.mExpanded = true;
	}
}
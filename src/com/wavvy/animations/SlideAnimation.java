package com.wavvy.animations;

import com.wavvy.R;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

public class SlideAnimation {

	private long mDuration = 200; 
	private Context mContext;
	private LinearLayout mElement;
	private boolean mExpanded = false;
	
	public SlideAnimation(Context context, LinearLayout element) {
	
		this.mContext = context;
		this.mElement = element;
	}
	
	public boolean isExpanded() {
	
		return this.mExpanded;
	}
	
	public void setDuration(long duration) {
	
		this.mDuration = duration;
	}

	public void collapse() {

		if (!this.mExpanded)
			return;
		
		this.collapse(false);
	}
	
	public void collapse(boolean isFirst) {

		if (isFirst) this.mElement.setVisibility(View.GONE);
		else this.mElement.setVisibility(View.VISIBLE);
		
		Animation a = AnimationUtils.loadAnimation(this.mContext, R.anim.slide_down);
		a.setDuration(this.mDuration);
		a.setInterpolator((new DecelerateInterpolator()));
		a.setAnimationListener(new MyAnimationListener() {
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				
				SlideAnimation.this.mElement.setVisibility(View.GONE);
			}
		});
		this.mElement.startAnimation(a);

		this.mExpanded = false;
	}
	
	public void expand() {

		if (this.mExpanded)
			return;
		
		Animation a = AnimationUtils.loadAnimation(this.mContext, R.anim.slide_up);
		a.setDuration(this.mDuration);
		a.setInterpolator((new AccelerateDecelerateInterpolator()));
		this.mElement.startAnimation(a);
		this.mElement.setVisibility(View.VISIBLE);
		
		this.mExpanded = true;
	}
}
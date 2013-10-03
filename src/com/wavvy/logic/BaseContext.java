package com.wavvy.logic;

import android.content.Context;

public class BaseContext {

	private Context mContext;
	
	public BaseContext(final Context context) {
	
		this.mContext = context;
	}

	protected String getString(final int resourceId, Object... formatArgs) {
	
		return this.mContext.getString(resourceId, formatArgs);
	}
	
	protected String getString(final int resourceId) {
	
		return this.mContext.getString(resourceId);
	}
	
	protected String[] getStringArray(final int resourceId) {
	
		return this.mContext.getResources().getStringArray(resourceId);
	}
	
	protected Context getContext() {
	
		return this.mContext;
	}
}
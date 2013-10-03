package com.wavvy.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

public class BaseDialog  extends Dialog {

	public BaseDialog(Context context) {
		
		super(context);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setCanceledOnTouchOutside(true);
		this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	protected String getString(int resourceId) {
		
		return this.getContext().getString(resourceId);
	}
	
	protected View getView(int viewId) {
	
		return this.findViewById(viewId);
	}
}
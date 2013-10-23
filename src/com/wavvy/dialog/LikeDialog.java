package com.wavvy.dialog;

import com.wavvy.R;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class LikeDialog extends BaseDialog {

	public LikeDialog(Context context) {
		
		super(context);

		this.setContentView(R.layout.like_dialog);
		this.setEvents();
	}

	private void setEvents() {
	
		((Button)this.findViewById(R.id.like_dialog_ok)).setOnClickListener(
				new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				LikeDialog.this.dismiss();
			}
		});
	}
}
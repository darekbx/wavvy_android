package com.wavvy.dialog;

import com.wavvy.R;

import android.content.Context;
import android.view.View;
import android.widget.Button;

public class MessageDialog extends BaseDialog {

	public MessageDialog(Context context) {
		
		super(context);

		this.setContentView(R.layout.message_dialog);
		this.setEvents();
	}

	private void setEvents() {
	
		((Button)this.findViewById(R.id.message_dialog_open)).setOnClickListener(
				new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				MessageDialog.this.dismiss();
			}
		});
	}
}
package com.wavvy.dialog;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import com.wavvy.R;
import com.wavvy.listeners.GetListener;
import com.wavvy.logic.LocationHelper;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Get;
import com.wavvy.logic.http.Utils;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.User;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NickDialog extends BaseDialog {

	private final EditText mNickBox;
	private final TextView mErrorBox;
	private final RelativeLayout mProgress;
	
	public NickDialog(Context context) {
		
		super(context);

		this.setContentView(R.layout.nick_dialog);
		this.setEvents();
		
		this.mNickBox = (EditText)this.getView(R.id.nick_dialog_nick);
		this.mErrorBox = (TextView)this.getView(R.id.nick_dialog_error);
		this.mProgress = (RelativeLayout)this.getView(R.id.nick_progress);
	}
	
	@Override
	public void show() {

		super.show();
		
		// check if gps is enabled
		if (!LocationHelper.isLocationEnabled(this.getContext())) {
		
			this.showError(R.string.error_gps);
			this.mNickBox.setEnabled(false);
			
			return;
		}

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}

	private void checkNick() {

		final String nick = this.mNickBox.getText().toString();
		
		// check internet
		if (!Utils.isOnline(this.getContext())) {

			this.showError(R.string.error_no_internet);
			return;
		}
		
		// validate
		if (this.validate(nick)) {

			this.mErrorBox.setVisibility(View.INVISIBLE);
			
			// check
			final Get get = new Get();
			get.setOnGetListener(new GetListener() {
				
				@Override
				public void success(final String content) {
					
					NickDialog.this.processResponse(content);
				}
				
				@Override
				public void failed(final String message) {

					NickDialog.this.showError(message);
				}
			});
		
			final URI uri = new AddressBuilder(this.getContext()).registerNick(nick);
			
			if (uri != null) {
				
				this.mProgress.setVisibility(View.VISIBLE);
				get.execute(uri);
			}
		}
		else 
			return;
	}
	
	private void processResponse(final String content) {

		int out = this.parseResponse(content);

		if (out > 0) {

			// save user
			final User user = new User(out, this.mNickBox.getText().toString());
			new UserStorage(this.getContext()).setUser(user);

			// finally close the dialog
			this.dismiss();
		}
		else if (out == -1) {
		
			this.showError(R.string.nick_reserved);
		}
		else {

			this.showError(R.string.error_unknown);
		}
	}
	
	private int parseResponse(final String content) {

		try {
			
			final JSONObject jObject = new JSONObject(content);
			final String count = this.getString(R.string.nick_count);
			final String id = this.getString(R.string.nick_id);
			
			if (jObject.has(count)) return -1;
			else if (jObject.has(id)) return jObject.optInt(id);
			
			return -3;
		} 
		catch (JSONException e) {

			e.printStackTrace();
			return -2;
		}
	}
	
	private boolean validate(final String nick) {

		if (nick.length() == 0) {
			
			this.showError(R.string.nick_enter_nick);
			return false;
		}
		else if (nick.length() <= 4) {

			this.showError(R.string.nick_too_short);
			return false;
		}
		
		return true;
	}
	
	private void showError(final int messageId) {

		this.showError(this.getString(messageId));
	}

	private void showError(final String message) {

		final Handler mainHandler = new Handler(this.getContext().getMainLooper());
		final Runnable runnable = new Runnable() {
			
			@Override
			public void run() {

				NickDialog.this.mProgress.setVisibility(View.GONE);
				NickDialog.this.mErrorBox.setVisibility(View.VISIBLE);
				NickDialog.this.mErrorBox.setText(message);
			}
		};
		mainHandler.post(runnable);
	}
	
	private void setEvents() {
	
		((Button)this.findViewById(R.id.nick_dialog_ok)).setOnClickListener(
				new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				NickDialog.this.checkNick();
			}
		});
	}
}
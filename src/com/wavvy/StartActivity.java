package com.wavvy;

import java.util.List;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.wavvy.animations.SlideAnimation;
import com.wavvy.dialog.LikeDialog;
import com.wavvy.dialog.MessageDialog;
import com.wavvy.listeners.ActionListener;
import com.wavvy.listeners.LikesListener;
import com.wavvy.listeners.MessagesListener;
import com.wavvy.listeners.TickListener;
import com.wavvy.logic.MapLogic;
import com.wavvy.logic.MessageLogic;
import com.wavvy.logic.UpdateTimer;
import com.wavvy.logic.http.Utils;
import com.wavvy.logic.managers.LikeManager;
import com.wavvy.logic.managers.MessageManager;
import com.wavvy.logic.storage.LikeStorage;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.Message;
import com.wavvy.model.SongLocation;
import com.wavvy.services.GpsService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class StartActivity extends FragmentActivity {

	public class RefreshReceiver extends BroadcastReceiver {
		 
	    @Override
	    public void onReceive(Context context, Intent intent) {

	    	/* received new song */
	    	/* do nothing */
	    }
	}

	private MapLogic mLogic;
	private GoogleMap mMap;
	
	private MessageLogic mMessageLogic;
	private ListView mMessagesList;
	
	private SlideAnimation mMenuAnimation;
	private SlideAnimation mMessagesAnimation;
	private ImageButton mMessage;
	private ImageButton mSendMessage;
	private ImageButton mLike;
	private EditText mMessageText;
	private Marker mActiveMarker = null;
	private int mUserId = -1;
	private int mNewMessageUserId = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_start);
		this.startService(new Intent(this, GpsService.class));
		
		final Fragment fragment = this.getSupportFragmentManager().findFragmentById(R.id.map);
		this.mMap = ((SupportMapFragment)fragment).getMap();
		this.mMap.setMyLocationEnabled(true);
		this.mMap.setOnMarkerClickListener(this.mMarkerClick);
		this.mMap.setOnMapClickListener(this.mMapClick);
		
		this.mMessagesList = (ListView)this.findViewById(R.id.messages_list);
		
		if (!Utils.isOnline(this))
			Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_LONG).show();
		else {
		
			this.mMessageLogic = new MessageLogic(this, this.mMessagesList);
			this.mLogic = new MapLogic(this, this.mMap);
			
			UpdateTimer.setTickListener(this.mTick);
			
			this.loadMenuAnimation();
			this.loadMessagesAnimation();
			this.loadEvents();
			this.loadUser();
			this.mLogic.loadPoints(false);
			
			this.checkLikes();
			this.loadMessages();
		}
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		
		UpdateTimer.start();
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
		
		UpdateTimer.stop();
	}
	
	@Override
	protected void onDestroy() {

		super.onDestroy();

		UpdateTimer.setTickListener(null);
		
		if (this.mMap != null) {
			
			this.mMap.setOnMarkerClickListener(null);
			this.mMap.setOnMapClickListener(null);
			this.mMessagesAnimation.collapse();
		}
		
		if (this.mMessage != null)
			this.mMessage.setOnClickListener(null);
		
		if (this.mLike != null)
			this.mLike.setOnClickListener(null);
	}
	
	@Override
	public void onBackPressed() {

		if (this.mMessagesAnimation.isExpanded()) {
		
			this.mMessagesAnimation.collapse();
			return;
		}

		if (this.mMenuAnimation.isExpanded()) {
		
			this.mMenuAnimation.collapse();
			return;
		}
		
		super.onBackPressed();
	}
	
	private void loadEvents() {
	
		this.mMessage = (ImageButton)this.findViewById(R.id.menu_message);
		this.mMessage.setOnClickListener(this.mMessageClick);
		
		this.mSendMessage = (ImageButton)this.findViewById(R.id.messages_send);
		this.mSendMessage.setOnClickListener(this.mSendMessageClick);

		this.mMessageText = (EditText)this.findViewById(R.id.message_text);
		
		this.mLike = (ImageButton)this.findViewById(R.id.menu_like);
		this.mLike.setOnClickListener(this.mLikeClick);
	}
	
	private void loadUser() {
	
		final UserStorage storage = new UserStorage(this);
		
		if (storage.isUserExists())
			this.mUserId = storage.getUser().getId();
	}
	
	private void loadMenuAnimation() {

		this.mMenuAnimation = new SlideAnimation(this, 
				(LinearLayout)this.findViewById(R.id.menu_bar));
		
		this.mMenuAnimation.collapse(true);
	}
	
	private void loadMessagesAnimation() {

		this.mMessagesAnimation = new SlideAnimation(this, 
				(LinearLayout)this.findViewById(R.id.messages_form));
		
		this.mMessagesAnimation.setDuration(600);
		this.mMessagesAnimation.collapse(true);
	}
	
	private void checkLikes() {
	
		if (this.mUserId == -1)
			return;
		
		final LikeStorage storage = new LikeStorage(this);
		final int myLikes = storage.getLikesCount();

		new LikeManager(this).likes(this.mUserId, new LikesListener() {
			
			@Override
			public void onSuccess(int likes) {

				if (likes <= myLikes)
					return; // no new likes
				
				// received like!
				storage.setLikesCount(likes);
				
				StartActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						new LikeDialog(StartActivity.this).show();
					}
				});
			}
			
			@Override
			public void onError() { }
		});
	}
	
	private SongLocation getActiveSongLocation() {

		if (this.mActiveMarker == null)
			return null;

		final Marker marker = this.mActiveMarker;
		final SongLocation location = this.mLogic.getSongLocation(marker);
		
		return location;
	}
	
	private void loadMessages() {
		
		new MessageManager(this).get(new MessagesListener() {
			
			@Override
			public void onSuccess(final List<Message> messages) {

				final StartActivity parent = StartActivity.this;
				
				parent.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
				
						parent.mMessageLogic.addTheirs(messages);
						
						if (!parent.mMessagesAnimation.isExpanded()) {
							
							final MessageDialog dialog = new MessageDialog(StartActivity.this);
							dialog.setOnDismissListener(new OnDismissListener() {
								
								@Override
								public void onDismiss(DialogInterface dialog) {

									parent.mNewMessageUserId = messages.get(0).getFromIdUser();
									parent.mMessagesAnimation.expand();
								}
							});
							dialog.show();
						}
					}
				});
			}
			
			@Override
			public void onError() {

				StartActivity.this.showMessage(R.string.messages_get_error);
			}
		});
	}
	
	private TickListener mTick = new TickListener() {
		
		@Override
		public void onTick() {

			StartActivity.this.mLogic.loadPoints(true);
			StartActivity.this.checkLikes();
			StartActivity.this.loadMessages();
		}
	};

	private OnClickListener mMessageClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			StartActivity.this.mMessagesAnimation.expand();
		}
	};

	private OnClickListener mSendMessageClick = new OnClickListener() {
		
		private void setState(boolean enabled) {

			final StartActivity parent = StartActivity.this;
			
			parent.mMessageText.setEnabled(enabled);
			parent.mSendMessage.setEnabled(enabled);
			
			final int resourceId = enabled 
					? R.drawable.icon_message 
					: R.drawable.icon_loading;
			
			parent.mSendMessage.setImageResource(resourceId);
		}
		
		@Override
		public void onClick(View v) {

			final StartActivity parent = StartActivity.this;
			final SongLocation location = parent.getActiveSongLocation();
			int targetUserId = -1;
			
			if (location != null) targetUserId = location.getIdUser();
			else if (parent.mNewMessageUserId != -1) targetUserId = parent.mNewMessageUserId;
			
			if (targetUserId != -1) {
			
				final String message = parent.mMessageText.getText().toString();
				
				new MessageManager(parent).send(targetUserId, message, new ActionListener() {
					
					@Override
					public void onSuccess() {

						StartActivity.this.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {

								setState(true);
								parent.mMessageLogic.add(message);
								parent.mMessageText.setText(new String());
								parent.loadMessages();
							}
						});
					}
					
					@Override
					public void onError() {

						StartActivity.this.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {

								setState(true);
							}
						});
						
						parent.showMessage(R.string.messages_send_error);
					}
				});
				
				setState(false);
			}
		}
	};

	private OnClickListener mLikeClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {

			final StartActivity parent = StartActivity.this;
			final SongLocation location = parent.getActiveSongLocation();
			
			if (location != null) {
				
				new LikeManager(parent).like(location.getIdUser(), new ActionListener() {
					
					@Override
					public void onSuccess() { parent.showMessage(R.string.like_success); }
					
					@Override
					public void onError() { parent.showMessage(R.string.like_error); }
				});
			}
		}
	};
	
	private OnMarkerClickListener mMarkerClick = new OnMarkerClickListener() {
		
		@Override
		public boolean onMarkerClick(Marker marker) {

			if (marker.getTitle() == null)
				return false;
			
			StartActivity.this.mMenuAnimation.expand();
			StartActivity.this.mActiveMarker = marker;
			return false;
		}
	};
	
	private OnMapClickListener mMapClick = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng point) {

			if (StartActivity.this.mMessagesAnimation.isExpanded())
				return;
			
			StartActivity.this.mMenuAnimation.collapse();
			StartActivity.this.mActiveMarker = null;
		}
	};
	
	private void showMessage(final int messageId) {
	
		this.showMessage(this.getString(messageId));
	}
	
	private void showMessage(final String message) {
	
		this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {

				Toast.makeText(StartActivity.this, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
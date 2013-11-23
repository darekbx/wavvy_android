package com.wavvy.receivers;

import java.net.URI;

import org.json.JSONException;
import org.json.JSONObject;

import com.wavvy.R;
import com.wavvy.MainActivity.RefreshReceiver;
import com.wavvy.db.StorageManager;
import com.wavvy.listeners.PostListener;
import com.wavvy.logic.Scrobbler;
import com.wavvy.logic.http.AddressBuilder;
import com.wavvy.logic.http.Post;
import com.wavvy.logic.http.Utils;
import com.wavvy.logic.storage.UserStorage;
import com.wavvy.model.Track;
import com.wavvy.model.User;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicReceiver extends BroadcastReceiver {
	
	private Context mContext;
	
	@Override
	public void onReceive(final Context context, final Intent intent) {

		this.mContext = context;
		final Track track = new Scrobbler(context).parseTrack(intent.getExtras());
		
		if (track == null)
			return;
		
		if (this.saveTrack(track))
			this.notifyNewTrack();
	}
	
	private boolean saveTrack(final Track track) {
	
		try {

			this.saveToWeb(track);
			this.saveToDatabase(track);
			
			return true;
		}
		catch (Exception e) {
		
			e.printStackTrace();
			return false;
		}
	}
	
	private void saveToDatabase(final Track track) {

		final StorageManager manager = new StorageManager(this.mContext);
		manager.addTrack(track);
		manager.close();
	}
	
	private void saveToWeb(final Track track) {
		
		// check internet
		if (Utils.isOnline(this.mContext)) {

			// set user id
			final UserStorage storage = new UserStorage(this.mContext);
			int userId = -1;
			
			if (storage.isUserExists())
				userId = storage.getUser().getId();
			
			track.setUserId(userId);
			
			// save
			final Post post = new Post(track.getPostData(this.mContext));
			post.setOnPostListener(new PostListener() {
				
				@Override
				public void success(String content) { 
					
					MusicReceiver.this.parseResponse(content);
				}
				
				@Override
				public void failed(String message) { }
			});

			final URI uri = new AddressBuilder(this.mContext).add();

			if (uri != null)
				post.execute(uri);
		}
	}
	
	private void parseResponse(final String response) {

		try {

			final UserStorage storage = new UserStorage(this.mContext);

			// do nothing if user exists 
			if (storage.isUserExists())
				return;
			
			// if user is missing then parse json and get created id_user
			final JSONObject jo = new JSONObject(response);
			
			if (jo.has(this.mContext.getString(R.string.response_id_user))) {
			
				// created new user
				final User user = new User();
				
				user.fromJsonObject(jo, this.mContext);
				storage.setUser(user);
			}
			
			/*if (!jo.has(this.mContext.getString(R.string.response_success))) {

				final NearestUser user = new NearestUser();
				user.fromJsonObject(jo, this.mContext);
				
				this.sendNotification(user);
			}*/
		} 
		catch (JSONException e) {
			
			e.printStackTrace();
		}
	}
	
	/*private void sendNotification(final NearestUser user) {

		final String title = this.mContext.getString(
				R.string.notification_title, 
				user.getDistance());

		final String text = this.mContext.getString(
				R.string.notification_text, 
				user.getNick(), 
				user.toString());
		
		final Notification notification = new NotificationCompat.Builder(this.mContext)
			.setContentTitle(title)
			.setContentText(text)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentIntent(PendingIntent.getService(this.mContext, 0, new Intent(), 0))
			.build();
		
		final String service = Context.NOTIFICATION_SERVICE; 
		final NotificationManager notificationManager = 
				  (NotificationManager)this.mContext.getSystemService(service);

		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
	}*/
	
	private void notifyNewTrack() {
		
		final Intent intent = new Intent(RefreshReceiver.class.getName());
		this.mContext.sendBroadcast(intent);
	}
}
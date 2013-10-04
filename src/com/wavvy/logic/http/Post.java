package com.wavvy.logic.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.wavvy.listeners.PostListener;

import android.os.AsyncTask;

public class Post extends AsyncTask<URI, Void, Boolean> {

	private List<BasicNameValuePair> mData;
	private PostListener mListener;
	
	public Post(List<BasicNameValuePair> data) {
		
		this.mData = data;
	}

	public void setOnPostListener(PostListener listener) {
	
		this.mListener = listener;
	}
	
	@Override
	protected Boolean doInBackground(URI... params) {

		final StringBuilder output = new StringBuilder();
		final HttpClient client = new DefaultHttpClient();
		final HttpPost post = new HttpPost(params[0]);
		
		try {
			
			// set data
            post.setEntity(new UrlEncodedFormEntity(this.mData, "UTF-8"));
			
			final HttpResponse response = client.execute(post);
			final StatusLine statusLine = response.getStatusLine();
			final int statusCode = statusLine.getStatusCode();
			
			if (statusCode == HttpURLConnection.HTTP_OK) {
			
				final HttpEntity entity = response.getEntity();
				final InputStream content = entity.getContent();
				final BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				
				String line;
				
				while ((line = reader.readLine()) != null) 
					output.append(line);
				
				try {
				
					reader.close();
					content.close();
				}
				catch (Exception e) {
					
					e.printStackTrace();
				}
				
				this.mListener.success(output.toString());
				return true;
			}
			else {
				
				this.mListener.failed(String.valueOf(statusCode));
				return false;
			}
		} 
		catch (Exception e) {
			
			e.printStackTrace();

			this.mListener.failed(e.getMessage());
			return false;
		}
	}
}
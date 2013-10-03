package com.wavvy.logic.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.wavvy.listeners.GetListener;

import android.os.AsyncTask;

public class Get extends AsyncTask<URI, Void, Boolean> {

	private GetListener mListener;
	
	public void setOnGetListener(GetListener listener) {
	
		this.mListener = listener;
	}
	
	@Override
	protected Boolean doInBackground(URI... params) {
		
		final StringBuilder output = new StringBuilder();
		final HttpClient client = new DefaultHttpClient();
		final HttpGet get = new HttpGet(params[0]);
		
		try {
			
			final HttpResponse response = client.execute(get);
			final StatusLine statusLine = response.getStatusLine();
			final int statusCode = statusLine.getStatusCode();
			
			if (statusCode == 200) {
			
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
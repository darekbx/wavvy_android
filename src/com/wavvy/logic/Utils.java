package com.wavvy.logic;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.content.Context;
import android.util.Base64;

public class Utils {

	public static Object getKeyFromValue(Map<?, ?> hm, Object value) {
		
		for (Object o : hm.keySet())
			if (hm.get(o).equals(value))
				return o;
		
		return null;
	}
	
	public static String toBase64(String text) {

		try {
			
			byte[] data = text.getBytes("UTF-8");
			return Base64.encodeToString(data, Base64.DEFAULT);	
		} 
		catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return text;
		}
	}
	
	public static String fromBase64(String text64) {

		try {

			byte[] data = Base64.decode(text64, Base64.DEFAULT);
			return new String(data, "UTF-8");
		} 
		catch (UnsupportedEncodingException e) {

			e.printStackTrace();
			return text64;
		}
	}
	
	public static int screenWidth(Context context) {
	
		return context.getResources().getDisplayMetrics().widthPixels;
	}
}
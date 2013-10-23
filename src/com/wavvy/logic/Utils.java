package com.wavvy.logic;

import java.util.Map;

public class Utils {

	public static Object getKeyFromValue(Map<?, ?> hm, Object value) {
		
		for (Object o : hm.keySet())
			if (hm.get(o).equals(value))
				return o;
		
		return null;
	}
}
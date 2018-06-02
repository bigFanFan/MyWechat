package com.hufan.config;

import com.sgsl.util.StringUtil;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class AppProps {
	private static final Map<String, String> config = new ConcurrentHashMap<String, String>();

	public static void init() {
		ResourceBundle rb = ResourceBundle.getBundle("custom");
		Enumeration<String> allKey = rb.getKeys();
		while (allKey.hasMoreElements()) {
			String key = allKey.nextElement();
			String value = (String) rb.getString(key);
			config.put(key, value);
		}
	}

	public static String get(String key) {
		return config.get(key);
	}

	public static int getInt(String key) {
		return getInt(key, 0);
	}

	public static int getInt(String key, int def) {
		String value = get(key);
		if (StringUtil.isNotNull(value)) {
			return Integer.valueOf(value);
		}
		return def;
	}

	public static BigDecimal getFloat(String key) {
		String value = get(key);
		if (StringUtil.isNotNull(value)) {
			return new BigDecimal(value);
		}
		return null;

	}
}

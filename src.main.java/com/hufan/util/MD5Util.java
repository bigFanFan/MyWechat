package com.hufan.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	public static String md5(String s) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(s.getBytes());
			return bytes2Hex(md.digest()).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
		}
		return "";
	}

	private static String bytes2Hex(byte[] bts) {
		StringBuilder des = new StringBuilder();
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = Integer.toHexString(bts[i] & 0xFF);
			if (tmp.length() == 1) {
				des.append("0");
			}
			des.append(tmp);
		}
		return des.toString();
	}
}

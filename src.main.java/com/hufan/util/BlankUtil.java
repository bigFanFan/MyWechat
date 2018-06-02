package com.hufan.util;

import com.jfinal.plugin.activerecord.Model;

public class BlankUtil {
	public static boolean isBlankModel(Model M) {
		if ((M == null) || (M.getAttrsEntrySet().size() == 0)) {
			return true;
		}
		return false;
	}
}
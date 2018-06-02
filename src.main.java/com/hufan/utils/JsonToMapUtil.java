package com.hufan.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jfinal.plugin.activerecord.Model;

import java.util.Map;

public class JsonToMapUtil {

	/**
	 * model转换为map
	 * 
	 * @param model
	 * @return
	 * @author: Jerri Liu
	 * @date: 2014年3月30日下午5:17:33
	 */
	public static Map<String, Object> modelToMap(Model model) {
		String jmodel = model.toJson();
		Gson gson = new Gson();
		Map<String, Object> jsonmap = gson.fromJson(jmodel,
				new TypeToken<Map<String, Object>>() {
				}.getType());
		return jsonmap;
	}
}

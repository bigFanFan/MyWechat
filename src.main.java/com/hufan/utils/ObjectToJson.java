package com.hufan.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 *用于将jfinal model数组对象转换成jsonArray
 * @author User
 *
 */
public class ObjectToJson {
	/**
	 * model数组转json数组
	 * @param models
	 * @return
	 */
	public static JSONArray modelListConvert(List models){
		JSONArray resultList = new JSONArray();
		for(int i=0;i<models.size();i++){
			Model model=(Model)models.get(i);
			JSONObject modelJson = new JSONObject();
			Iterator<Entry<String, Object>> iter= model.getAttrsEntrySet().iterator();
			while(iter.hasNext()){
				Entry<String, Object> item=iter.next();
				modelJson.put(item.getKey(), item.getValue());
			}
			resultList.add(modelJson);
		}
		return resultList;
	}
	
	/**
	 * record数组转json数组
	 * @param models
	 * @return
	 */
	public static JSONArray recordListConvert(List<Record> records){
		JSONArray resultList = new JSONArray();
		for(Record record:records){
			JSONObject recordJson = new JSONObject();
			Iterator<Entry<String, Object>> iter= record.getColumns().entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, Object> item=iter.next();
				recordJson.put(item.getKey(), item.getValue());
			}
			resultList.add(recordJson);
		}
		return resultList;
	}
	
	/**
	 * record转json
	 * @param record
	 * @return
	 */
	public static JSONObject recordConvert(Record record) {
		JSONObject recordJson=new JSONObject();
		Iterator<Entry<String, Object>> iter= record.getColumns().entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Object> item=iter.next();
			recordJson.put(item.getKey(), item.getValue());
		}
		return recordJson;
	}
	
	/**
	 * model转json
	 * @param model
	 * @return
	 */
	public static JSONObject modelConvert(Model model) {
		JSONObject modelJson=new JSONObject();
		Iterator<Entry<String, Object>> iter= model.getAttrsEntrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Object> item=iter.next();
			modelJson.put(item.getKey(), item.getValue());
		}
		return modelJson;
	}
}

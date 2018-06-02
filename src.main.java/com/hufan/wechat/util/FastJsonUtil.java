package com.hufan.wechat.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.*;

/**
 * 基于fastjson的JSON工具类
 * @author leon 
 * @version 1.0  2016年11月9日下午3:28:03
 */
public class FastJsonUtil
{
    public static SerializerFeature[] inFeatures = { SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullStringAsEmpty,
            SerializerFeature.QuoteFieldNames, SerializerFeature.WriteDateUseDateFormat,
            SerializerFeature.DisableCircularReferenceDetect // 关闭循环引用支持
    };

    public static Feature[] outFeatures = { Feature.InternFieldNames, Feature.InitStringFieldAsEmpty };

    public static String toJsonString(Object obj)
    {
        return JSON.toJSONString(obj, inFeatures);
    }

    public static JSONObject toJSONObject(String jsonString)
    {
        return (JSONObject) JSON.parse(jsonString, outFeatures);
    }

    public static <T> T toJavaObject(String jsonString, Class<T> type)
    {
        JSON json = toJSONObject(jsonString);
        return JSON.toJavaObject(json, type);
    }

    public static JSONArray toJSONArray(String jsonString)
    {
        return (JSONArray) JSON.parse(jsonString, outFeatures);
    }

    public static List<Map<String, Object>> toList(String jsonString)
    {
        JSONArray array = toJSONArray(jsonString);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(array.size());
        for (int i = 0; i < array.size(); i++)
        {
            JSONObject json = array.getJSONObject(i);
            list.add(new HashMap<String, Object>(json));
        }
        return list;
    }

    public static <T> List<T> toJavaObjectList(String jsonString, Class<T> type)
    {
        JSONArray array = toJSONArray(jsonString);
        List<T> list = new ArrayList<T>(array.size());
        for (int i = 0; i < array.size(); i++)
        {
            JSONObject json = array.getJSONObject(i);
            T instance = JSON.toJavaObject(json, type);
            list.add(instance);
        }
        return list;
    }

    public static Map<String, Object> toMap(JSONObject obj)
    {
        Set<?> set = obj.keySet();
        Map<String, Object> map = new HashMap<String, Object>(set.size());
        for (Object key : obj.keySet())
        {
            Object value = obj.get(key);
            if (value instanceof JSONArray)
            {
                map.put((String) key, toList((JSONArray) value));
            } else if (value instanceof JSONObject)
            {
                map.put((String) key, toMap((JSONObject) value));
            } else
            {
                map.put((String) key, obj.get(key));
            }
            map.put((String) key, obj.get(key));

        }
        return map;
    }

    public static List<Object> toList(JSONArray jsonArr)
    {
        List<Object> list = new ArrayList<Object>();
        for (Object obj : jsonArr)
        {
            if (obj instanceof JSONArray)
            {
                list.add(toList((JSONArray) obj));
            } else if (obj instanceof JSONObject)
            {
                list.add(toMap((JSONObject) obj));
            } else
            {
                list.add(obj);
            }
        }
        return list;
    }
}

package com.hufan.wechat;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class BaiduMapUtil {
	protected final static Logger logger = Logger.getLogger(BaiduMapUtil.class);

	private static final String BAIDU_AK = "puG9vu9hwOZG0kpuQKYZm7OO";

	public static final String BAIDU_ADDRESS_GET = "http://api.map.baidu.com/geocoder/v2/?location=%s&ak=%s&pois=1&output=json";

	public static final String BAIDU_LOCATION_GET = "http://api.map.baidu.com/geocoder/v2/?address=%s&ak=%s&precise=1&output=json";

	private static JSONObject httpRequest(String urlStr, String parameter, String ak) {
		try {
			URL url = new URL(String.format(urlStr, parameter, ak));
			URLConnection httpsConn = (URLConnection) url.openConnection();
			if (httpsConn != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(httpsConn.getInputStream(), "UTF-8"));
				StringBuilder s = new StringBuilder();
				String data = null;
				while ((data = br.readLine()) != null) {
					s.append(data);
				}
				br.close();
				return JSONObject.parseObject(s.toString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new JSONObject();
	}

	/**
	 * 传详细地址获取经纬度信息
	 * 
	 * @param address
	 *            详细地址
	 * @return
	 */
	public static JSONObject getLocation(String address) {
		return httpRequest(BAIDU_LOCATION_GET, address, BAIDU_AK);
	}

	/**
	 * 传经纬度获取详细地址
	 * 
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 * @return
	 */
	public static JSONObject getAddress(Object lng, Object lat) {
		return httpRequest(BAIDU_ADDRESS_GET, lng + "," + lat, BAIDU_AK);
	}

	public static void main(String[] args) throws IOException {
		JSONObject o = getLocation("湖南省长沙市雨花区井湾子街道井莲路红星·紫金国际");
		JSONObject result = (JSONObject) o.get("result");
		JSONObject location = (JSONObject) result.get("location");
		System.out.println(location.get("lng"));
		System.out.println(location.get("lat"));
		JSONObject a = getAddress(location.get("lat"), location.get("lng"));
		System.out.println(a);
	}
}
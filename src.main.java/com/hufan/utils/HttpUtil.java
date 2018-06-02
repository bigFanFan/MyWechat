package com.hufan.utils;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.log.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {
	protected final static Logger logger = Logger.getLogger(HttpUtil.class);
	public static final String CHARSET = "UTF-8",appid="wx8af4eb003406811d",secret="18ce2aab28c182ea61ca7e99dae33f38";

	public static void main(String[] args){

		for (int i=0;i<=1000;i++){
			HttpUtil.get("http://localhost:8081/weixin/activity/getRandomAward");
		}
	}
	/*public static void main(String[] args) {
		Map<String, String> postParams=new HashMap<String,String>();
		postParams.put("body", 
				"{\"button\":[{\"name\":\"我的账户\",\"sub_button\":[{\"type\":\"click\",\"name\":\"账户绑定\",\"key\":\"M1001\"},{\"type\":\"click\",\"name\":\"我的资产\",\"key\":\"M1002\"}]},{\"type\":\"click\",\"name\":\"我的资产\",\"key\":\"M2001\"},{\"type\":\"click\",\"name\":\"其它\",\"key\":\"M3001\"}]}");
		System.out.println(post("https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+getAccessToken(appid,secret).get("access_token"),postParams));
		
	}*/
	public static JSONObject getAccessToken(String appid, String secret){
		String result=get("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret);
		logger.info("getAccessToken返回结果:"+result);
		return JSONObject.parseObject(result);
	}
	public static String post(String url, Map<String, String> postParams) {
		HttpURLConnection con = null;
		OutputStream osw = null;
		InputStream ins = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setDoInput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			if (null != postParams) {
				con.setDoOutput(true);
				String postParam = postParams.get("body");//encodeParameters(postParams);
				byte[] bytes = postParam.getBytes(CHARSET);

				con.setRequestProperty("Content-Length",
						Integer.toString(bytes.length));

				osw = con.getOutputStream();
				osw.write(bytes);
				osw.flush();
			}

			int resCode = con.getResponseCode();
			if (resCode < 400) {
				ins = con.getInputStream();
			} else {
				ins = con.getErrorStream();
			}
			return readContent(ins);
		} catch (IOException e) {

		} finally {

			try {
				if (osw != null) {
					osw.close();
				}
				if (ins != null) {
					ins.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	public static String get(String url) {
		HttpURLConnection con = null;
		OutputStream osw = null;
		InputStream ins = null;
		try {
			con = (HttpURLConnection) new URL(url).openConnection();
			con.setRequestMethod("GET");
			

			int resCode = con.getResponseCode();
			if (resCode < 400) {
				ins = con.getInputStream();
			} else {
				ins = con.getErrorStream();
			}
			return readContent(ins);
		} catch (IOException e) {

		} finally {

			try {
				if (osw != null) {
					osw.close();
				}
				if (ins != null) {
					ins.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	private static final String readContent(InputStream ins) throws IOException {

		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(ins,
				HttpUtil.CHARSET));
		if (ins != null) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		return sb.toString();
	}

	public static String encodeParameters(Map<String, String> postParams) {
		StringBuilder buf = new StringBuilder();
		if (postParams != null && postParams.size() > 0) {

			for (Map.Entry<String, String> tmp : postParams.entrySet()) {
				try {
					buf.append(URLEncoder.encode(tmp.getKey(), CHARSET))
							.append("=")
							.append(URLEncoder.encode(tmp.getValue(), CHARSET))
							.append("&");
				} catch (java.io.UnsupportedEncodingException neverHappen) {
				}
			}

			buf.deleteCharAt(buf.length() - 1);

		}

		return buf.toString();

	}
}

/**
* Copyright © 2016 SGSL
* 湖南绿航恰果果农产品有限公司
* http://www.sgsl.com 
* All rights reserved. 
*/
package com.hufan.utils;

import java.math.BigDecimal;

/**
 * @author User 
 * @version 1.0  2016年11月12日下午2:39:59
 */
public class MapUtil
{
	 private static double EARTH_RADIUS = 6378.137; 
	 
	 private static double rad(double d) { 
	        return d * Math.PI / 180.0; 
	    }

	 /**
	  * 根据经纬度计算距离
	  * @param lat1Str 坐标1纬度
	  * @param lng1Str坐标1经度
	  * @param lat2Str坐标2纬度
	  * @param lng2Str坐标2经度
	  * @return
	  */
	 public static String getDistance(String lat1Str, String lng1Str, String lat2Str, String lng2Str) {
	        Double lat1 = Double.parseDouble(lat1Str);
	        Double lng1 = Double.parseDouble(lng1Str);
	        Double lat2 = Double.parseDouble(lat2Str);
	        Double lng2 = Double.parseDouble(lng2Str);
	         
	        double radLat1 = rad(lat1);
	        double radLat2 = rad(lat2);
	        double difference = radLat1 - radLat2;
	        double mdifference = rad(lng1) - rad(lng2);
	        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2), 2)
	                + Math.cos(radLat1) * Math.cos(radLat2)
	                * Math.pow(Math.sin(mdifference / 2), 2)));
	        distance = distance * EARTH_RADIUS;
	        BigDecimal bd = new BigDecimal(distance);
	        bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
	        
	        return bd.toString();
	    }
    
    public static void main(String[] args) {
    	System.out.println(getDistance("28.236447","112.914096","28.123026","113.021028"));
	}
	
}

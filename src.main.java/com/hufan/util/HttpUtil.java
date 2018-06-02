 package com.hufan.util;

 import java.io.BufferedReader;
 import java.io.IOException;
 import java.io.InputStreamReader;
 import java.io.PrintWriter;
 import java.net.URL;
 import java.net.URLConnection;
 import java.util.Map;

 public class HttpUtil
 {
   public static String sendGet(String url, String param)
   {
    String result = "";
    BufferedReader in = null;
     try {
      String urlName = url + "?" + param;
       URL realUrl = new URL(urlName);
 
      URLConnection conn = realUrl.openConnection();
 
       conn.setRequestProperty("accept", "*/*");
       conn.setRequestProperty("connection", "Keep-Alive");
       conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
 
       conn.setRequestProperty("Accept-Charset", "UTF-8");
 
       conn.connect();
 
       Map map = conn.getHeaderFields();
 
       in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
       String line;
       while ((line = in.readLine()) != null)
        result = result + "" + line;
     }
     catch (Exception e) {
     System.out.println("发送GET请求出现异常！" + e);
      e.printStackTrace();
     }
     finally
     {
       try {
        if (in != null)
           in.close();
       }
       catch (IOException ex) {
         ex.printStackTrace();
       }
     }
    return result;
   }
 
   public static String sendPost(String url, String param)
   {
    PrintWriter out = null;
     BufferedReader in = null;
     String result = "";
     try {
      URL realUrl = new URL(url);
 
       URLConnection conn = realUrl.openConnection();
 
       conn.setRequestProperty("accept", "*/*");
      conn.setRequestProperty("connection", "Keep-Alive");
       conn.setRequestProperty("Accept-Charset", "UTF-8");
       conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
 
      conn.setDoOutput(true);
      conn.setDoInput(true);
 
      out = new PrintWriter(conn.getOutputStream());
 
     out.print(param);
 
       out.flush();
 
       in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
       String line;
       while ((line = in.readLine()) != null)
         result = result + "/n" + line;
     }
     catch (Exception e) {
      System.out.println("发送POST请求出现异常！" + e);
       e.printStackTrace();
     }
     finally
     {
       try {
        if (out != null) {
          out.close();
         }
        if (in != null)
          in.close();
       }
       catch (IOException ex) {
        ex.printStackTrace();
       }
     }
     return result;
   }
 }
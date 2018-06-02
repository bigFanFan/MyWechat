package com.hufan.utils.appUtil;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * httpClient请求工具类 add by huangmk
 */
public class HttpClientUtils {
    protected final static Log logger = LogFactory.getLog(HttpClientUtils.class);
    private static HttpClientBuilder httpBuilder = null;
    private static RequestConfig requestConfig = null;

    public static String GET_REQUEST = "GET";
    public static String POST_REQUEST = "POST";
    public static String PUT_REQUEST = "PUT";
    public static String DELETE_REQUEST = "DELETE";

    /**
     * HEADER_VERSION:网关请求版本号
     */
    public static String HEADER_VERSION = "v1-4-0";
    /**
     * TOKE:网关请求 toke
     */
    public static String TOKE = "";
    /**
     * VERSION_FLAG:版本标识符
     */
    public static boolean VERSION_FLAG = true;

    static {
        // 设置http的状态参数
        requestConfig = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000).build();
        httpBuilder = HttpClients.custom();
    }

    public static CloseableHttpClient getConnection() {
        return httpBuilder.build();
    }

    public static HttpUriRequest getRequestMethod(Map<String, String> map, String url, String method) {
        logger.info("http请求开始：param=" + map + ";url=" + url + ";method=" + method);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (MapUtils.isNotEmpty(map)) {
            Set<Map.Entry<String, String>> entrySet = map.entrySet();
            for (Map.Entry<String, String> e : entrySet) {
                String name = e.getKey();
                String value = e.getValue();
                NameValuePair pair = new BasicNameValuePair(name, value);
                params.add(pair);
            }
        }

        HttpUriRequest reqMethod = null;
        if ("POST".equals(method)) {
            reqMethod = RequestBuilder.post().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()])).setConfig(requestConfig)
                    .build();
        } else if ("GET".equals(method)) {
            reqMethod = RequestBuilder.get().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()])).setConfig(requestConfig)
                    .build();
        } else if ("PUT".equals(method)) {
            reqMethod = RequestBuilder.put().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()])).setConfig(requestConfig)
                    .build();
        } else if ("DELEATE".equals(method)) {
            reqMethod = RequestBuilder.delete().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()])).setConfig(requestConfig)
                    .build();
        }
        return reqMethod;
    }

    public static String responseMessage(Map<String, String> paramMap, String url, String method) throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.getConnection();
        HttpUriRequest getOrPost = HttpClientUtils.getRequestMethod(paramMap, url, method);
        builderHeader(getOrPost);

        HttpResponse httpResponse = null;
        String message = "";
        httpResponse = httpClient.execute(getOrPost); // 请求异常处理IOException
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = httpResponse.getEntity();

            message = EntityUtils.toString(entity, "utf-8");
        }
        return message;
    }

    /**
     * 转bean对象 add by huangmk
     */
    public static <T> T asClass(String responseMessage, Class<T> type) {
        String message = StringUtils.isBlank(responseMessage) ? "" : responseMessage;

        return StringUtils.isBlank(message) ? null : (T) JSON.parseObject(message, type);

    }

    /**
     * Description:post requestBody请求
     * 
     * @param map
     *            参数
     * @param url
     *            url
     * @return
     * @author Limiaojun
     * @date 2018-05-02 17:54
     */
    public static String postResponseText(Map<String, String> map, String url) throws Exception {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClientUtils.getConnection();
        HttpPost method = new HttpPost(url);

        String responseMessage = "";
        if (MapUtils.isNotEmpty(map)) {
            // 解决中文乱码问题
            StringEntity entity = new StringEntity(JSON.toJSONString(map), "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            method.setEntity(entity);
        }
        builderHeader(method);

        HttpResponse result = httpClient.execute(method);

        url = URLDecoder.decode(url, "UTF-8");
        /** 请求发送成功，并得到响应 **/
        if (result.getStatusLine().getStatusCode() == 200) {
            try {
                /** 读取服务器返回过来的json字符串数据 **/
                responseMessage = EntityUtils.toString(result.getEntity());
            } catch (Exception e) {
                logger.error("post请求提交失败:" + e.getMessage());
            }
        }
        return responseMessage;
    }

    /**
     * Description:put requestBody请求
     * 
     * @param map
     *            参数
     * @param url
     *            url
     * @return
     * @author Limiaojun
     * @date 2018-05-02 17:54
     */
    public static String putResponseText(Map<String, String> map, String url) throws Exception {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClientUtils.getConnection();
        HttpPut method = new HttpPut(url);

        String responseMessage = "";
        if (MapUtils.isNotEmpty(map)) {
            // 解决中文乱码问题
            StringEntity entity = new StringEntity(JSON.toJSONString(map), "utf-8");
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json");
            method.setEntity(entity);
        }
        builderHeader(method);

        HttpResponse result = httpClient.execute(method);

        url = URLDecoder.decode(url, "UTF-8");
        /** 请求发送成功，并得到响应 **/
        if (result.getStatusLine().getStatusCode() == 200 || result.getStatusLine().getStatusCode() == 201) {
            try {
                /** 读取服务器返回过来的json字符串数据 **/
                responseMessage = EntityUtils.toString(result.getEntity());
            } catch (Exception e) {
                logger.error("post请求提交失败:" + e.getMessage());
            }
        }
        return responseMessage;
    }

    /**
     * Description:构建header
     * 
     * @return
     * @author Limiaojun
     * @date 2018-05-16 17:56
     */
    private static void builderHeader(HttpUriRequest request) {
        if (VERSION_FLAG) {
            request.addHeader("version", HEADER_VERSION);
            request.addHeader("X-Authorization", TOKE);
        }
    }
}

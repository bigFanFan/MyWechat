package com.hufan.utils.appUtil;

import com.sgsl.config.AppProps;

/**
 * <d>用于数据整合http请求接口的url配置<d/> Created by huangmk on 2018/5/2.
 */
public class AppUrlInfo {

    /* http请求方法 */
    public static final String GET_REQUEST = "GET";
    public static final String POST_REQUEST = "POST";
    public static final String PUT_REQUEST = "PUT";
    public static final String DELETE_REQUEST = "DELETE";
    
    
    
    /** 
     * CURRENCY_OPERATION_ADD:增加鲜果币操作类型
     */  
    public static final String CURRENCY_OPERATION_ADD = "add";
    /** 
     * CURRENCY_OPERATION_ADD:扣除鲜果币操作类型
     */  
    public static final String CURRENCY_OPERATION_SUBTRACT = "subtract";
    /** 
     * CURRENCY_TAG:鲜果币操作类型
     */  
    public static final String CURRENCY_TAG = "wx";
    
    
    

    static {
        AppProps.init();
    }

    /**
     * 用户模块http请求url
     */
    public static final String user_url = AppProps.get("app_wxuser_url");

    public static final String commons_url = AppProps.get("app_commons_url");


}

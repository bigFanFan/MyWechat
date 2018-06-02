package com.hufan.wechat.oauth2;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.sgsl.config.AppConst;
import com.sgsl.config.AppProps;
import com.sgsl.model.appModel.WxUser;
import com.sgsl.wechat.Token;
import com.sgsl.wechat.UserStoreUtil;
import com.sgsl.wechat.WeChatUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信分享好友注册app
 * Created by huangmk on 2018/6/2.
 */
public class OAuth2RegisterInterceptor implements Interceptor {

    public void intercept(ActionInvocation ai){
        Controller ctrl = ai.getController();
        HttpServletRequest request = ctrl.getRequest();
        //忽略非GET请求
        if(!"get".equalsIgnoreCase(request.getMethod())){
            ai.invoke();
            return;
        }
        String code = request.getParameter("code");
        //获取openId
        JSONObject userInfo = WeChatUtil.getInfoByCode(AppProps.get("appid"), AppProps.get("app_secrect"), code);
        String openId = userInfo.getString("openid");
        //获取accessToken
        Token token = WeChatUtil.getToken(AppProps.get("appid"), AppProps.get("app_secrect"));
        String access_token = token.getAccessToken();
        //获取微信个人用户信息（unionid）
        JSONObject userInfoJson  = WeChatUtil.getUserInfo(openId, access_token);
        WxUser wxUser = new WxUser();
        wxUser.setOpenId(openId);
        wxUser.setUnionId(userInfoJson.getString("unionid"));

        //鉴权信息入缓存
        HttpServletResponse response = ctrl.getResponse();
        Cookie cookie = new Cookie(AppConst.WEIXIN_USER, openId);
        response.addCookie(cookie);
        UserStoreUtil.cache(request, response, wxUser);
    }
}

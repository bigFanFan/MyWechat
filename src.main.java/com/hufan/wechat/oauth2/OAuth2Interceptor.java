package com.hufan.wechat.oauth2;

import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.sgsl.model.appModel.WxUser;
import com.sgsl.util.StringUtil;
import com.sgsl.wechat.UserStoreUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;


public class OAuth2Interceptor implements Interceptor {
	protected final static Log logger = LogFactory.getLog(OAuth2Interceptor.class);
	public void intercept(ActionInvocation ai) {
		Controller ctrl = ai.getController();
		HttpServletRequest request = ctrl.getRequest();
		if(!"get".equalsIgnoreCase(request.getMethod())){//忽略非GET请求
			ai.invoke();
			return;
		}
		//获取缓存用户信息 add Limiaojun by 20180504
		WxUser user = UserStoreUtil.get(request);
		logger.info("OAuth2Interceptor-获取wxUser="+(user == null?"":user.toString()));
		if(user != null){
			ai.invoke();
			return;
		}
//		else{
//			//根据用户id获取用户信息 add Limiaojun by 201805002
//			WxUser tUser = UserRemoteClient.findWxUserById(93837);
//			//模拟登陆用户
//			request.getSession().setAttribute(AppConst.WEIXIN_USER, tUser);//13028 12944
//
//			HttpServletResponse response = ctrl.getResponse();
//			Cookie cookie = new Cookie(AppConst.WEIXIN_USER, tUser.getOpenId());
//			response.addCookie(cookie);
//			UserStoreUtil.cache(request, response, tUser);
//			ai.invoke();
//		}

		//开始鉴权
		String qStr = request.getQueryString();
		String url = request.getRequestURL() + (StringUtil.isNull(qStr) ? "" : "?" + qStr );
		ctrl.setSessionAttr("oldUrl", url);
		logger.info("OAuth2Interceptor oldUrl:"+ctrl.getSessionAttr("oldUrl"));
		ctrl.redirect("/wx_oauth2_servlet?step=1");
	}
}

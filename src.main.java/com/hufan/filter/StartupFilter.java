package com.hufan.filter;

import com.revocn.model.SysMenu;
import com.hufan.config.AppConst;
import com.hufan.config.AppProps;
import com.hufan.wechat.JsapiTicket;
import com.hufan.wechat.Token;
import com.hufan.wechat.WeChatUtil;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * 分享过滤器
 * 
 * @author User
 *
 */


public class StartupFilter implements Filter {
	protected final static Logger logger = Logger.getLogger(StartupFilter.class);
	
	private ServletContext servletContext;

	public void init(FilterConfig arg0) throws ServletException {
		this.servletContext = arg0.getServletContext();
		AppProps.init();
		String appid = AppProps.get("appid");
		String appsecrect = AppProps.get("app_secrect");
		Token token = WeChatUtil.getToken(appid, appsecrect);
		JsapiTicket ticket = WeChatUtil.getJsapiTicket(token.getAccessToken());
		
		this.servletContext.setAttribute("_wechat_access_token_", token.getAccessToken());
		this.servletContext.setAttribute("_wechat_jsapi_ticket_", ticket.getTicket());
		logger.info("StartupFilter-init _wechat_access_token_"+this.servletContext.getAttribute("_wechat_access_token_"));
		logger.info("StartupFilter-init _wechat_jsapi_ticket_"+this.servletContext.getAttribute("_wechat_jsapi_ticket_"));
		Timer timer = new Timer();
		timer.schedule(new GetAndCacheToken(appid, appsecrect), 7100 * 1000, 7100 * 1000);
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws IOException, ServletException {
		req.setCharacterEncoding("utf-8");
		String uri = ((HttpServletRequest) req).getRequestURI();
		if (uri.indexOf(".") != -1) {
			filterChain.doFilter(req, res);
			return;
		}
		String appid = AppProps.get("appid");
		String access_token = (String)this.servletContext.getAttribute("_wechat_jsapi_ticket_");
		String jsapi_ticket = (String)this.servletContext.getAttribute("_wechat_jsapi_ticket_");
		req.setAttribute("access_token", access_token);
		req.setAttribute("jsapi_ticket", jsapi_ticket);
		
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		req.setAttribute("timestamp", timestamp);
		String nonce_str = UUID.randomUUID().toString();
		req.setAttribute("nonce_str", nonce_str);
		
		StringBuffer requestUrl = ((HttpServletRequest) req).getRequestURL();
		String queryString = ((HttpServletRequest) req).getQueryString();
		if (StringUtils.hasLength(queryString)) {
			requestUrl.append("?").append(queryString);
		}
		String url = requestUrl.toString();
		if (url.indexOf("#") != -1) {
			url = url.split("[#]")[0];
		}
		url = url.trim();
		String signature = WeChatUtil.getSignature(jsapi_ticket, timestamp, nonce_str, url);
		req.setAttribute("currentUrl", url);
		req.setAttribute("signature", signature);
		req.setAttribute("appid", appid);
		//为了实现定位清除相应的cookie
		Cookie[] cookies = ((HttpServletRequest)req).getCookies();
		if(cookies!=null){
			if("WXMenu".equals(req.getParameter("menuFrom"))){
				for (Cookie cookie : cookies) {
					if("deliverInfo".equals(cookie.getName())||"store_id".equals(cookie.getName())
							||"storeInfo".equals(cookie.getName())||"adrStoreInfo".equals(cookie.getName())){
						cookie.setValue(null);
						cookie.setMaxAge(0);
						cookie.setPath("/");
						//cookie.setDomain(AppProps.get("app_domain"));
						((HttpServletResponse)res).addCookie(cookie);
					}
				}
			}
			
		}
		//设置当前工程地址
		req.setAttribute("app_domain",AppProps.get("app_domain"));
		//如果路径匹配到后台的管理路径
        List<SysMenu> sysMenus= SysMenu.dao.find("select * from sys_menu");
        for(SysMenu item:sysMenus){
            if(((HttpServletRequest) req).getSession().getAttribute(AppConst.KEY_SESSION_USER)==null
                    &&uri.indexOf(item.getStr("href"))!=-1){
                ((HttpServletResponse) res).sendRedirect(AppProps.get("app_domain")+"/m");
                return;
            }
        }
		
		/*if(uri.indexOf("system")!=-1||uri.indexOf("submitMsg")!=-1||uri.indexOf("resourceShow")!=-1
		   ||uri.indexOf("article")!=-1||uri.indexOf("activityManage")!=-1||uri.indexOf("orderManage")!=-1
		   ||uri.indexOf("productManage")!=-1||uri.indexOf("userManage")!=-1||uri.indexOf("storeManage")!=-1
		   ||uri.indexOf("minSys")!=-1||uri.indexOf("feedbackManage")!=-1||uri.indexOf("refererManage")!=-1
		   ||uri.indexOf("recommendManage")!=-1||uri.indexOf("indexSetting")!=-1||uri.indexOf("execute")!=-1
		   ||uri.indexOf("couponManage")!=-1||uri.indexOf("fruitmaster")!=-1||uri.indexOf("masterProductManage")!=-1
		   ||uri.indexOf("masterManage")!=-1||uri.indexOf("masterArticleManage")!=-1||uri.indexOf("masterCarouselManage")!=-1
		   ||uri.indexOf("teamBuyManage")!=-1||uri.indexOf("shareManage")!=-1||uri.indexOf("seedManage")!=-1){
			if(((HttpServletRequest) req).getSession().getAttribute(AppConst.KEY_SESSION_USER)==null){
				((HttpServletResponse) res).sendRedirect(AppProps.get("app_domain")+"/m");
			}
		}*/
		filterChain.doFilter(req, res);
	}

	public void destroy() {

	}

	private class GetAndCacheToken extends TimerTask {
		private String appid;
		private String appsecret;

		public GetAndCacheToken(String appid, String appsecret) {
			this.appid = appid;
			this.appsecret = appsecret;
		}

		public void run() {
			System.out.println("==============refresh access_token start=============");
			Token token = WeChatUtil.getToken(appid, appsecret);
			String access_token = token.getAccessToken();
			JsapiTicket ticket = WeChatUtil.getJsapiTicket(access_token);
			String jsapi_ticket = ticket.getTicket();

			StartupFilter.this.servletContext.setAttribute("_wechat_access_token_", access_token);
			StartupFilter.this.servletContext.setAttribute("_wechat_jsapi_ticket_", jsapi_ticket);
		}
	}
}

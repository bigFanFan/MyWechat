package com.hufan.wechat.oauth2;

import com.alibaba.fastjson.JSONObject;
import com.sgsl.config.AppProps;
import com.sgsl.model.TStock;
import com.sgsl.model.appModel.BaseUser;
import com.sgsl.model.appModel.WxUser;
import com.sgsl.service.app.user.UserRemoteClient;
import com.sgsl.util.StringUtil;
import com.sgsl.wechat.Token;
import com.sgsl.wechat.UserStoreUtil;
import com.sgsl.wechat.WeChatUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//wx_oauth2_servlet
public class WXOAuth2Servlet extends HttpServlet {
	private static final long serialVersionUID = 8752747609578338007L;
	protected final static Log logger = LogFactory.getLog(WXOAuth2Servlet.class);
	public static final String FILTER_EMOJI = "filterEmoji";

	private String filterEmoji;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		this.filterEmoji = config.getInitParameter(FILTER_EMOJI);
		if (StringUtil.isNull(this.filterEmoji)) {
			this.filterEmoji = "byte";
		}
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String step = request.getParameter("step");
		if (step.equals("1")) {
			String re_url = AppProps.get("app_domain") + "/wx_oauth2_servlet?step=2";
			String url = MessageFormat.format(WeChatUtil.OAUTH2_URL, AppProps.get("appid"), re_url, "snsapi_base");
			logger.debug("=======微信授权======step：1 " + url);
			response.sendRedirect(url);
			return;
		}
		if (step.equals("2")) {
			logger.debug("=======微信授权======step：2");
			String code = request.getParameter("code");
			JSONObject userInfo = WeChatUtil.getInfoByCode(AppProps.get("appid"), AppProps.get("app_secrect"), code);
			String openid = (String) userInfo.get("openid");

			//update by huangmk 微商城是否注册
			WxUser wxUser = UserRemoteClient.findWxUserByOpenId(openid);
			if (Objects.isNull(wxUser)) {
				logger.debug("=======微信授权======step：2=======数据库没有此用户，开始网页授权======");
				// 网页授权
				String re_url = AppProps.get("app_domain") + "/wx_oauth2_servlet?step=3";
				String url = MessageFormat.format(WeChatUtil.OAUTH2_URL, AppProps.get("appid"), re_url,
						"snsapi_userinfo");
				response.sendRedirect(url);
				return;
			}else{
				logger.info("=======微信授权======step：2=======数据库已存在此用户，从微信刷新数据======");
				JSONObject userJson = null;
				try {
					Token token = WeChatUtil.getToken(AppProps.get("appid"), AppProps.get("app_secrect"));
					String access_token = token.getAccessToken();
					if(StringUtils.isNotBlank(access_token)){
						userJson = WeChatUtil.getUserInfo(openid, access_token);
						if(userJson.containsKey("subscribe")&&userJson.get("subscribe")!=null){ //是否关注过0未关注1已关注
							int subscribe=userJson.getInteger("subscribe");
							wxUser.setSubscribe(subscribe);
						}
					}
					logger.info("=======微信授权======step：2---微信个人用户信息转wxUser="+wxUser+"--userJson="+userJson.toJSONString());
					wxUser = convert(wxUser, userJson);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				//获取t_base_user的phone update by huangmk
				if(StringUtils.isBlank(wxUser.getPhone())){
					BaseUser baseUser = UserRemoteClient.findBaseUserByUnionId(wxUser.getUnionId());
					if(Objects.nonNull(baseUser)){
						wxUser.setPhone(baseUser.getPhone());
					}
				}
				UserRemoteClient.updateWxUser(wxUser);
				this.createStock(wxUser.getWxId());
				logger.info("=======微信授权======step：2==数据库已存在此用户，重新获取微信个人用户信息成功==wxUser="+wxUser.toString()+"-unionId="+(userJson == null?"":userJson.toJSONString()));
				UserStoreUtil.cache(request, response, wxUser);
				String oldUrl = (String) request.getSession().getAttribute("oldUrl");
				logger.info("WXOAuth2Servlet oldUrl:"+request.getSession().getAttribute("oldUrl"));
				if (oldUrl == null) {
					throw new RuntimeException("oldUrl为空!");
				}
				response.sendRedirect(oldUrl);// 重定向到原页面
				return;
			}
		}
		if (step.equals("3")) {
			logger.debug("=======微信授权======step：3");
			//update by huangmk
			String code = request.getParameter("code");
			JSONObject userInfo = WeChatUtil.getInfoByCode(AppProps.get("appid"), AppProps.get("app_secrect"), code);
			String openId = userInfo.getString("openid");
			//查看用户是否关注了公众号信息
			Token token = WeChatUtil.getToken(AppProps.get("appid"), AppProps.get("app_secrect"));
			String access_token = token.getAccessToken();
			logger.info("=======微信授权======step：3=获取openId和accessToken=openId="+openId+"--access_token="+access_token);
			JSONObject userInfoJson  = WeChatUtil.getUserInfo(openId, access_token);
			logger.info("=======微信授权======step：3==通过openId和access_token获取微信个人用户信息："+userInfoJson.toJSONString());
			WxUser wxUser = new WxUser();
			//是否关注过0未关注1已关注
			if(userInfoJson.containsKey("subscribe")&&userInfoJson.get("subscribe")!=null){
				int subscribe=userInfoJson.getInteger("subscribe");
				wxUser.setSubscribe(subscribe);
			}
			SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
			wxUser.setRegistTime(sdf.format(new Date()));
			wxUser.setOpenId(openId);
			wxUser.setUnionId(userInfoJson.getString("unionid"));
			wxUser = convert(wxUser, userInfoJson);
			// 添加用户同时给用户分配一个仓库
			//update by huangmk
			WxUser existUser = UserRemoteClient.findWxUserByOpenId(openId);
			String oldUrl = (String) request.getSession().getAttribute("oldUrl");
			if (existUser==null) {
				logger.info("=======微信授权======step：3=用户首次关注公众号-通过unionId判断app端是否手机号注册-userInfoByAccessToken="+userInfoJson.toJSONString());
				BaseUser baseUser = UserRemoteClient.findBaseUserByUnionId(userInfoJson.getString("unionid"));
				if(Objects.isNull(baseUser)){
					logger.info("=======微信授权======step：3=用户首次关注公众号-暂时不确定app端是否微信已授权");
					Integer result = UserRemoteClient.wxOAuth(wxUser);
					if(result == 1){
						logger.debug("=======从微信获取用户信息，并成功添加到数据库======");
						wxUser = UserRemoteClient.findWxUserByOpenId(openId);
						this.createStock(wxUser.getWxId());
						UserStoreUtil.cache(request, response, wxUser); //此时没有绑定手机号
						if (oldUrl == null) {
							throw new RuntimeException("oldUrl为空!");
						}
						response.sendRedirect(oldUrl);// 重定向到原页面
					}else {
						throw new RuntimeException("用户首次关注公众号-oauth2授权 - 获取用户信息失败");
					}
				}else{
					logger.info("=======微信授权======step：3=用户首次关注公众号-app端已微信授权");
					//2:app注册过，则注入缓存信息，且标识为subscribe字段为“已关注”，且后续步骤不需要弹出绑定手机号页面
					WxUser newWxUser = getWxUserInfo(baseUser);
					Integer result = UserRemoteClient.addWxUser(newWxUser);
					if(result == 0){
						throw new RuntimeException("oauth2授权 - 新增数据库失败");
					}
					UserStoreUtil.cache(request, response, newWxUser);
					response.sendRedirect(oldUrl);// 重定向到原页面
				}
			}else{
				//获取t_base_user的phone update by huangmk
				BaseUser baseUser = UserRemoteClient.findBaseUserByUnionId(userInfoJson.getString("unionid"));
				if(Objects.nonNull(baseUser)){
					existUser.setPhone(baseUser.getPhone());
				}
				UserRemoteClient.updateWxUser(existUser); //手机号存入数据库
				UserStoreUtil.cache(request, response, existUser);
				response.sendRedirect(oldUrl);// 重定向到原页面
			}
		}
	}

	/**
	 * 获取微商城个人用户信息
	 * @param baseUser
	 * @return
	 * add by huangmk
	 */
	WxUser getWxUserInfo(BaseUser baseUser){
		WxUser wxUser = new WxUser();
		wxUser.setPhone(baseUser.getPhone());
		wxUser.setCurrency(baseUser.getCurrency().intValue());
		wxUser.setNickname(baseUser.getNickname());
		wxUser.setOpenId(baseUser.getOpenId());
		wxUser.setUnionId(baseUser.getUnionId());
		SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		wxUser.setRegistTime(sdf.format(baseUser.getRegistrationAt()));
		wxUser.setBaseUserId(baseUser.getBaseUserId());
		wxUser.setBirthday(baseUser.getBirthday());
		wxUser.setEmail(baseUser.getEmail());
		wxUser.setMemberPoints(baseUser.getMemberPoints());
		wxUser.setUserAddress(baseUser.getAddress());
		wxUser.setSex(baseUser.getSex());
		wxUser.setRealname(baseUser.getRealname());
		wxUser.setUserImgId(baseUser.getAvatar());
		wxUser.setPersonalInfo(baseUser.getDescription());
		wxUser.setQq(baseUser.getQq());
		wxUser.setSubscribe(1);
		return wxUser;
	}

	void createStock(Integer userId) {
		TStock stock = new TStock();
		if (stock.getStockByUser(userId) != null) {
			return;
		}
		UUID uuid = UUID.randomUUID();
		stock.set("stock_id", uuid.toString());
		stock.set("user_id", userId);
		// stock.set("gb", 0);
		stock.save();
		logger.debug("=======成功为用户创建水果仓库======");
	}

	WxUser convert(WxUser wxUser, JSONObject userJson) {
		String nickname = userJson.getString("nickname");
		String openid = userJson.getString("openid");
		String unionid = userJson.getString("unionid");
		if(StringUtil.isNotNull(unionid)){
			wxUser.setUnionId(unionid);
		}else{
			if("oKqRHwch6TKs4vNVwD1wJZARlkqo".equals(openid)){ //hmk oLbmavzxBS7jjjNPY-0jzDKEcWlA
				wxUser.setUnionId("oLbmavzxBS7jjjNPY-0jzDKEcWlA");
			}
			if("oKqRHwewgrqB7WXH2SIC8hfDxwaA".equals(openid)){ //lijian oLbmav4Mexrk136krTpxkr1dHteI
				wxUser.setUnionId("oLbmav4Mexrk136krTpxkr1dHteI");
			}
			if("oIMhevwMWuF2CUJTvFzYV9ntq7gY".equals(openid)){ //wangyulei
				wxUser.setUnionId("oLbmav7ChMjI39Bx_IkemoMAxMnA");
			}
			if("oKqRHwUWwoigeadF-wm6OJ1S-wgA".equals(openid)){ //liumei
				wxUser.setUnionId("oLbmav79RxBNcmQdxVQmkF2V2oBg");
			}
		}
		if (StringUtil.isNull(nickname)) {
			return wxUser;
		}
		if ("byte".equals(this.filterEmoji)) {
			nickname = replaceByte4(nickname);
		}
		if ("regular".equals(this.filterEmoji)) {
			nickname = replaceEmoji(nickname);
		}

		if(StringUtil.isNotNull(nickname)){
			wxUser.setNickname(nickname);
			logger.debug("=======授权用户：" + nickname + "======");
		}
		if(StringUtil.isNotNull(String.valueOf((Integer)userJson.get("sex")))){
			wxUser.setSex(String.valueOf((Integer)userJson.get("sex")));
		}
		if(StringUtil.isNotNull((String)userJson.get("headimgurl"))){
			wxUser.setUserImgId((String)userJson.get("headimgurl"));
		}
		String address = userJson.get("country") + " " + userJson.get("province") + " " + userJson.get("city");
		if(StringUtil.isNotNull(address)){
			wxUser.setUserAddress(address);
		}
		return wxUser;
	}

	static String replaceByte4(String nickname) {
		if (StringUtil.isNull(nickname)) {
			return "";
		}
		try {
			byte[] conbyte = nickname.getBytes();
			for (int i = 0; i < conbyte.length; i++) {
				if ((conbyte[i] & 0xF8) == 0xF0) {// 如果是4字节字符
					for (int j = 0; j < 4; j++) {
						conbyte[i + j] = 0x30;// 将当前字符变为“0000”
					}
					i += 3;
				}
			}
			nickname = new String(conbyte);
			return nickname.replaceAll("0000", "");
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			return "";
		}
	}

	static String replaceEmoji(String nickname) {
		if (StringUtil.isNull(nickname)) {
			return "";
		}
		try {
			Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
					Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
			Matcher emojiMatcher = emoji.matcher(nickname);
			if (emojiMatcher.find()) {
				String temp = nickname.substring(emojiMatcher.start(), emojiMatcher.end());
				nickname = nickname.replaceAll(temp, "");
			}
			return nickname;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			return "";
		}
	}
}

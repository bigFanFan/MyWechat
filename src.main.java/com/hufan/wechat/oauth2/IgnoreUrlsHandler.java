package com.hufan.wechat.oauth2;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class IgnoreUrlsHandler extends Handler {

	private List<String> ignores = new ArrayList<String>();
	
	{
		ignores.add("/wx_oauth2_servlet");
	}
	
	public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
		if(!ignores.contains(target)){
			nextHandler.handle(target, request, response, isHandled);
		}
	}
}

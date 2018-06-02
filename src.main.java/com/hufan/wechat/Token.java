package com.hufan.wechat;

import java.io.Serializable;

public class Token implements Serializable
{
    private static final long serialVersionUID = -4093601016338089860L;
    private String accessToken;
    private int expiresIn;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
    
}

package com.hufan.wechat;

public class JsapiTicket {
	 /** 
     * 有效时长 
     */  
    private int expiresIn;  
    /** 
     * js调用票据 
     */  
    private String ticket;  
    
    public int getExpiresIn() {  
        return expiresIn;  
    }  
    public void setExpiresIn(int expiresIn) {  
        this.expiresIn = expiresIn;  
    }  
    public String getTicket() {  
        return null==ticket?"":ticket;  
    }  
    public void setTicket(String ticket) {  
        this.ticket = ticket;  
    } 
}

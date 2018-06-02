package com.hufan.util;

import redis.clients.jedis.Jedis;

public class RedisUtil {
    private String addr;
    private int port;

    public RedisUtil(String addr,int port) {
        this.addr = addr;
        this.port = port;
    }

    public String getToken() {
        Jedis jedisCli = new Jedis(this.addr, this.port); //新建Jedis对象
        jedisCli.select(0); //切换Redis数据库
        String fengniaoAccessToken = jedisCli.get("\"deliver:fengniao:access-token\"");
        /*fengniaoAccessToken=fengniaoAccessToken.replace("\\", "");
        fengniaoAccessToken=fengniaoAccessToken.replaceFirst("\"", "");
        fengniaoAccessToken=fengniaoAccessToken.substring(0, fengniaoAccessToken.length()-1);*/
        return fengniaoAccessToken;
    }
}

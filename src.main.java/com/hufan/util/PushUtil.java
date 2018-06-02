/**
* Copyright © 2016 SGSL
* 湖南绿航恰果果农产品有限公司
* http://www.sgsl.com 
* All rights reserved. 
*/
package com.hufan.util;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumQueryRequest;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumQueryResponse;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author yj 
 * @version 1.0  2016年11月2日上午9:30:59
 */
public class PushUtil
{
	protected final static Log logger = LogFactory.getLog(PushUtil.class);
    /**
     * 发送短信验证码
     * */
    public static Map<String,String> sendMsgToUser(String mobileNum,String product){
        Map<String,String> resultMap = new HashMap<String,String>();
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23895339",
        		"be016aa428ce418a01c9fddea3aa0bf7");
        String verifyCode = generateCode();
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("123456");
        req.setSmsType("normal");
        req.setSmsFreeSignName("水果熟了微商城");
        //req.setSmsParamString("{\"number\":\""+verifyCode+"\"}");
        req.setSmsParamString("{\"code\":\""+verifyCode+"\",\"product\":\""+product+"\"}");
        req.setRecNum(mobileNum);
        //req.setSmsTemplateCode("SMS_77260065");
        req.setSmsTemplateCode("SMS_70090063");
        AlibabaAliqinFcSmsNumSendResponse rsp;
        
        try
        {
            rsp = client.execute(req);
            if(rsp.isSuccess()){
            	logger.info("验证码短信("+mobileNum+")发送成功"+rsp.isSuccess());
                resultMap.put("status", "success");//成功
                resultMap.put("verifyCode",verifyCode );
            }else{
            	logger.error("验证码短信("+mobileNum+")发送失败："+rsp.getErrorCode());
            	//失败重新发送验证码
            	rsp = client.execute(req);
            	if(rsp.isSuccess()){
                    resultMap.put("status", "success");//成功
                    resultMap.put("verifyCode",verifyCode );
            	}else{ 
            		logger.error("验证码短信("+mobileNum+")发送失败："+rsp.getErrorCode());
            		resultMap.put("status", "fail");
            	}
            }   
        } catch (ApiException e)
        {
            logger.error("验证码短信("+mobileNum+")发送失败："+e.getErrMsg());
            resultMap.put("status", "fail");//失败
        }
        return resultMap;
    }
    
    /**
     * 鲜果师申请短信通知
     */
    public static Map<String,String> sendMsgToMaster(String mobileNum,String masterstatus){
    	 Map<String,String> resultMap = new HashMap<String,String>();
         TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23895339",
         		"be016aa428ce418a01c9fddea3aa0bf7");
         AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
         req.setExtend("123456");
         req.setSmsType("normal");
         req.setSmsFreeSignName("水果熟了微商城");
         req.setSmsParamString("{\"masterstatus\":\""+masterstatus+"\"}");
         req.setRecNum(mobileNum);
         req.setSmsTemplateCode("SMS_91990042");
         AlibabaAliqinFcSmsNumSendResponse rsp;
         try
         {
             rsp = client.execute(req);
             if(rsp.isSuccess()){
                 resultMap.put("status", "success");//成功
             }else{
                 resultMap.put("status", "fail");
             }   
         } catch (ApiException e)
         {
             e.printStackTrace();
             resultMap.put("status", "fail");//失败
         }
         return resultMap;
    }
    
    /**
     * 发送赠送告知短信
     * */
    public static Map<String,String> sendPresentMsgToUser(String recmobileNum,String nickName){
        Map<String,String> resultMap = new HashMap<String,String>();
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23895339",
        		"be016aa428ce418a01c9fddea3aa0bf7");
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("123456");
        req.setSmsType("normal");
        req.setSmsFreeSignName("水果熟了微商城");
        req.setSmsParamString("{\"nickname\":\""+nickName+"\"}");
        req.setRecNum(recmobileNum);
        req.setSmsTemplateCode("SMS_77315077");
        AlibabaAliqinFcSmsNumSendResponse rsp;
        try
        {
            rsp = client.execute(req);
            if(rsp.isSuccess()){
                resultMap.put("status", "success");//成功
            }else{
                resultMap.put("status", "fail");
            }   
        } catch (ApiException e)
        {
            e.printStackTrace();
            resultMap.put("status", "fail");//失败
        }
        return resultMap;
    }
    
    public static void getSendRecord(String mobileNum,String queryDate){
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23513840", "e322bafec9a495214d196c7c2b78f1a4");
        AlibabaAliqinFcSmsNumQueryRequest req = new AlibabaAliqinFcSmsNumQueryRequest();
        req.setRecNum(mobileNum);
        req.setQueryDate(queryDate);
        req.setCurrentPage(1L);
        req.setPageSize(10L);
        AlibabaAliqinFcSmsNumQueryResponse rsp;
        try
        {
            rsp = client.execute(req);
            System.out.println(rsp.getBody());
        } catch (ApiException e)
        {
            e.printStackTrace();
        }
    }
    
    private static String generateCode(){
        Random rad=new Random();  
        return rad.nextInt(1000000)+""; 
    }
    /**
     * 海鼎发送不成功报警
     * @param mobileNum
     * @return
     */
    public static Map<String,String> sendMsgToManager(String mobileNum,String orderId){
        Map<String,String> resultMap = new HashMap<String,String>();
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23895339",
        		"be016aa428ce418a01c9fddea3aa0bf7");
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("123456");
        req.setSmsType("normal");
        req.setSmsFreeSignName("水果熟了微商城");
        orderId="**"+orderId.substring(orderId.length()- 6);
        req.setSmsParamString("{\"orderId\":\""+orderId+"\"}");
        req.setRecNum(mobileNum);
        req.setSmsTemplateCode("SMS_77225070");
        AlibabaAliqinFcSmsNumSendResponse rsp;
        try
        {
            rsp = client.execute(req);
            if(rsp.isSuccess()){
                resultMap.put("status", "success");//成功
            }else{
            	//失败重新发送验证码
            	rsp = client.execute(req);
            	if(rsp.isSuccess()){
                    resultMap.put("status", "success");//成功
            	}else{ 
            		resultMap.put("status", "fail");
            	}
            }   
        } catch (ApiException e)
        {
            e.printStackTrace();
            resultMap.put("status", "fail");//失败
        }
        return resultMap;
    }
    /**
     * 鲜果币转账消息提示
     * @param mobileNum
     * @param friend
     * @param money
     * @return
     */
    public static Map<String,String> sendMsgToFriend(String mobileNum,String friend,String money){
        Map<String,String> resultMap = new HashMap<String,String>();
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23895339",
        		"be016aa428ce418a01c9fddea3aa0bf7");
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("123456");
        req.setSmsType("normal");
        req.setSmsFreeSignName("水果熟了微商城");
        req.setSmsParamString("{\"friend\":\""+friend+"\",\"money\":\""+money+"个\"}");
        req.setRecNum(mobileNum);
        req.setSmsTemplateCode("SMS_77220090");
        AlibabaAliqinFcSmsNumSendResponse rsp;
        try
        {
            rsp = client.execute(req);
            if(rsp.isSuccess()){
                resultMap.put("status", "success");//成功
            }else{
            	//失败重新发送验证码
            	rsp = client.execute(req);
            	if(rsp.isSuccess()){
                    resultMap.put("status", "success");//成功
            	}else{ 
            		resultMap.put("status", "fail");
            	}
            }   
        } catch (ApiException e)
        {
            e.printStackTrace();
            resultMap.put("status", "fail");//失败
        }
        return resultMap;
    }
    
    /**
     * 团购成功通知
     * @param mobileNum
     * @param usercount 团购规模
     * @param product 团购商品名称
     * @return
     */
    public static Map<String,String> sendSuccessMsgToTeamUser(String mobileNum,String usercount,String product){
        Map<String,String> resultMap = new HashMap<String,String>();
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23895339",
        		"be016aa428ce418a01c9fddea3aa0bf7");
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("123456");
        req.setSmsType("normal");
        req.setSmsFreeSignName("水果熟了微商城");
        req.setSmsParamString("{\"usercount\":\""+usercount+"\",\"product\":\""+product+"\"}");
        req.setRecNum(mobileNum);
        req.setSmsTemplateCode("SMS_70130244");
        AlibabaAliqinFcSmsNumSendResponse rsp;
        try
        {
            rsp = client.execute(req);
            if(rsp.isSuccess()){
                resultMap.put("status", "success");//成功
            }else{
            	//失败重新发送验证码
            	rsp = client.execute(req);
            	if(rsp.isSuccess()){
                    resultMap.put("status", "success");//成功
            	}else{ 
            		resultMap.put("status", "fail");
            	}
            }   
        } catch (ApiException e)
        {
            resultMap.put("status", "fail");//失败
        }
        return resultMap;
    }
    /**
     * 团购失败通知
     * @param mobileNum
     * @param usercount 团购规模
     * @param product 团购商品名称
     * @return
     */
    public static Map<String,String> sendFaildMsgToTeamUser(String mobileNum,String usercount,String product){
        Map<String,String> resultMap = new HashMap<String,String>();
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23895339",
        		"be016aa428ce418a01c9fddea3aa0bf7");
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("123456");
        req.setSmsType("normal");
        req.setSmsFreeSignName("水果熟了微商城");
        req.setSmsParamString("{\"usercount\":\""+usercount+"\",\"product\":\""+product+"\"}");
        req.setRecNum(mobileNum);
        req.setSmsTemplateCode("SMS_70000151");
        AlibabaAliqinFcSmsNumSendResponse rsp;
        try
        {
            rsp = client.execute(req);
            if(rsp.isSuccess()){
                resultMap.put("status", "success");//成功
            }else{
            	//失败重新发送验证码
            	rsp = client.execute(req);
            	if(rsp.isSuccess()){
                    resultMap.put("status", "success");//成功
            	}else{ 
            		resultMap.put("status", "fail");
            	}
            }   
        } catch (ApiException e)
        {
            resultMap.put("status", "fail");//失败
        }
        return resultMap;
    }
    
    /**
     * 3.门店提货通知
     * @param mobileNum
     * @param orderId
     * @return
     */
    public static Map<String,String> sendMsgToOrderUser(String mobileNum,String orderId){
        Map<String,String> resultMap = new HashMap<String,String>();
        TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "23895339", "be016aa428ce418a01c9fddea3aa0bf7");
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setExtend("123456");
        req.setSmsType("normal");
        req.setSmsFreeSignName("水果熟了微商城");
        orderId="**"+orderId.substring(orderId.length()- 4);
        req.setSmsParamString("{\"orderid\":\""+orderId+"\"}");
        req.setRecNum(mobileNum);
        req.setSmsTemplateCode("SMS_70035197");
        AlibabaAliqinFcSmsNumSendResponse rsp;
        try
        {
            rsp = client.execute(req);
            if(rsp.isSuccess()){
                resultMap.put("status", "success");//成功
            }else{
            	//失败重新发送验证码
            	rsp = client.execute(req);
            	if(rsp.isSuccess()){
                    resultMap.put("status", "success");//成功
            	}else{ 
            		resultMap.put("status", "fail");
            	}
            }   
        } catch (ApiException e)
        {
            e.printStackTrace();
            resultMap.put("status", "fail");//失败
        }
        return resultMap;
    }
    public static void main(String[] args)
    {
    	//Map<String,String> resultMap = sendMsgToUser("18670787016");
    	//sendPresentMsgToUser("18670787016","乐");
    	/*sendMsgToFriend("18874711213","好像种棵树","2.11");
    	String a = null;
    	System.out.println("agfsa"+a);*/
      //  System.out.println(resultMap.get("verifyCode"));
       // getSendRecord("18670787016","20161102");
          
     //   System.out.println(generateCode());
    	/*sendMsgToUser("18874711213");
    	sendMsgToUser("18670787016");
    			sendMsgToUser("18229848728");
    					sendMsgToUser("13080594761");
    							sendMsgToUser("17093430841");
    									sendMsgToUser("17742618312");
    											sendMsgToUser("18207493756");
    											sendMsgToUser("13786350836");*/
    }
}

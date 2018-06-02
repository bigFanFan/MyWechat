package com.hufan.utils.appUtil;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.sgsl.model.appModel.WxUser;
import com.sgsl.service.app.user.UserRemoteClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Description: 公共方法工具类
 *
 * @author Limiaojun
 * @date: 2018-05-05 15:55:08
 * @version 1.0
 * @since JDK 1.8
 */
public class CommonUtils {

    /**
     * Description:根据返回对象集合获取ids 字符串
     * 
     * @param resList
     *            返回对象集合
     * @param key
     *            id字段名称
     * @return
     * @author Limiaojun
     * @date 2018-05-05 15:57
     */
    public static String getIdsByList(List<?> resList, String key) {
        if (CollectionUtils.isEmpty(resList) || StringUtils.isBlank(key)) {
            return "";
        }

        String ids = "";
        boolean firstFlag = true;

        String id = "";
        for (Object obj : resList) {
            if (obj instanceof Record) {
                if (Objects.isNull(((Record) obj).get(key))) {
                    continue;
                }
                id = ((Record) obj).get(key).toString();
                // id = ((JSONObject)
                // JSON.toJSON(obj)).getJSONObject("columns").getString(key);
            } else if (obj instanceof Model) {
                if (Objects.isNull(((Model<?>) obj).get(key))) {
                    continue;
                }
                id = ((Model<?>) obj).get(key).toString();
                // id = getValueByJsonArrays(key, ((JSONObject)
                // JSON.toJSON(obj)).getJSONArray("attrsEntrySet"));
            }else if(obj instanceof WxUser){
                id = ((WxUser) obj).getWxId().toString();
            }

            if (firstFlag) {
                ids += id;
                firstFlag = false;
            } else {
                // 去重复
                ids += ids.contains(id) ? "" : "," + id;
            }
        }

        return ids;
    }

    /**
     * Description:根据key 获取entrySet对应的value值
     * 
     * @param key
     *            要获取的key
     * @param entrySet
     *            entrySet 数组对象
     * @author Limiaojun
     * @date 2018-05-07 15:38
     */
    public static String getValueByEntrySet(String key, Set<Entry<String, Object>> entrySet) {
        for (Entry<String, Object> entry : entrySet) {
            if (key.equals(entry.getKey()) && !Objects.isNull(entry.getValue())) {
                return entry.getValue().toString();
            }
        }

        return "";

        // JSONObject jsonObject = null;
        // for (Object tempObj : jsonArr) {
        // jsonObject = (JSONObject) tempObj;
        // if (key.equals(jsonObject.getString("key"))) {
        // return jsonObject.getString("value");
        // }
        // }
        //
        // return "";
    }


    /**
     * Description: 合并list
     *
     * @param sourceList        jfinal 原始数据集合
     * @param targetList        用户数据集合
     * @param sourceKey         原始数据匹配key
     * @param targetKey         用户数据匹配key
     * @param keys              要匹配的字段
     * @author Limiaojun
     * @date 2018-05-08 15:15
     */
    public static void mergeList(List<?> sourceList, List<Map<String, Object>> targetList, String sourceKey,
                                 String targetKey, String... keys) {
        if (CollectionUtils.isEmpty(sourceList) || CollectionUtils.isEmpty(targetList)) {
            return;
        }
        Record sourceRecord = null;
        Model<?> sourceModel = null;
        String[] columnNames = null;
        String userId = null;
        for (Object sourceObj : sourceList) {
            if (sourceObj instanceof Record) {
                sourceRecord = (Record) sourceObj;

                columnNames = sourceRecord.getColumnNames();
                userId = null == sourceRecord.get(sourceKey) ? "" : sourceRecord.get(sourceKey).toString();

            } else if (sourceObj instanceof Model) {
                sourceModel = (Model<?>) sourceObj;

                columnNames = sourceModel.getAttrNames();
                userId = null == sourceModel.get(sourceKey) ? "" : sourceModel.get(sourceKey).toString();
            }

            if (keys.length > 0) {
                for (Map<String, Object> targetMap : targetList) {

                    if (userId.equals(MapUtils.getString(targetMap, targetKey))) {
                        for (String key : keys) {
                            
                            
                            //customer_name 为空取 昵称
                            if(key.equals("customer_name")) {
                                if (sourceObj instanceof Record) {
                                    if(StringUtils.isBlank(sourceRecord.getStr(key))) {
                                        sourceRecord.set(key, targetMap.get("nickname"));
                                    }
                                } else if (sourceObj instanceof Model) {
                                    
                                    if(StringUtils.isBlank(sourceRecord.getStr(key))) {
                                        sourceModel.put(key, targetMap.get("nickname"));
                                    }
                                }
                            }
                            
                            
                            
                            if (!Arrays.asList(columnNames).contains(key)) {
                                if (sourceObj instanceof Record) {
                                    sourceRecord.set(key, targetMap.get(key));
                                } else if (sourceObj instanceof Model) {
                                    sourceModel.put(key, targetMap.get(key));
                                }
                            }

                        }
                    }
                }

            } else {

                for (Map<String, Object> targetMap : targetList) {

                    if (userId.equals(MapUtils.getString(targetMap, targetKey))) {

                        for (Entry<String, Object> entry : targetMap.entrySet()) {

                            if (!Arrays.asList(columnNames).contains(entry.getKey())) {
                                if (sourceObj instanceof Record) {
                                    sourceRecord.set(entry.getKey(), entry.getValue());
                                } else if (sourceObj instanceof Model) {
                                    sourceModel.put(entry.getKey(), entry.getValue());
                                }
                            }

                        }

                    }

                }

            }

        }

    }

   
    /** 
     * Description:合并返回记录和微信用户list数据
     *  
     * @param sourceList        返回记录
     * @param sourceKey         返回记录合并关联key
     * @param targetKey         用户数据匹配key
     * @param keys
     * @author Limiaojun
     * @date 2018-05-10 09:59
     */  
    public static void mergeRecordWxUserList(List<?> sourceList,String sourceKey,String targetKey, String... keys) {
        if(CollectionUtils.isEmpty(sourceList)) {
            return;
        }
        // add Limiaojun by 20180505 根据返回对象获取ids
        String userIds = CommonUtils.getIdsByList(sourceList, sourceKey);
        // 根据ids获取用户集合列表
        List<WxUser> wxUserList = UserRemoteClient.findWxUsersByIds(userIds);
        // wxUserList 转成 mapList
        List<Map<String, Object>> mapList = ReflectionUtils.listObj2ListMap(wxUserList);
        
        mergeList(sourceList, mapList, sourceKey, targetKey, keys);
    }
    
    
    /** 
     * Description:构建present_user 和 target_user 数据
     *  
     * @param list  recordList集合
     * @author Limiaojun
     * @date 2018-05-12 16:43
     */  
    public static void buildPresentUserAndTargetUser(List<Record> list) {
        //获取用户ids 并去重复
        Set<Integer> ids = list.stream().map(m -> m.getInt("present_user")).collect(Collectors.toSet());
        ids.addAll(list.stream().map(m -> m.getInt("target_user")).collect(Collectors.toSet()));
        
        //根据ids获取用户集合列表
        List<WxUser> wxUserList =  UserRemoteClient.findWxUsersByIds(Joiner.on(",").join(ids));
        //标志位
        boolean flag = false;
        for (Record record : list) {
            
            for (WxUser wxUser : wxUserList) {
                if(record.getInt("present_user").equals(wxUser.getWxId())) {
                    record.set("present_user", Strings.nullToEmpty(wxUser.getNickname()));
                    flag = true;
                    break;
                }
            }
            //如果没有返回结果对应的用户id present_user 置为空
            if(!flag) {
                record.set("present_user", "");
            }
            
            flag = false;
            for (WxUser wxUser : wxUserList) {
                if(record.getInt("target_user").equals(wxUser.getWxId())) {
                    record.set("target_user", Strings.nullToEmpty(wxUser.getNickname()));
                    flag = true;
                    break;
                }
            }
          //如果没有返回结果对应的用户id present_user 置为空
            if(!flag) {
                record.set("present_user", "");
            }
        }
    }
}

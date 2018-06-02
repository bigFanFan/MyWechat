package com.hufan.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class StringUtil {
	public static boolean isNull(String s) {
		return (s == null) || (s.equals("")) || (s.equals("null") || (s.equals("undefined")));
	}

	public static boolean isNotNull(String s) {
		return (s != null) && (!s.equals("")) && (!s.equals("null")&&(!s.equals("undefined")));
	}

	public static String nextCode(String code) {
		Long num = Long.valueOf(Long.valueOf("1" + code).longValue() + 1L);
		return String.valueOf(num).substring(1, code.length() + 1);
	}

	public static String getPingYin(String src) {  
		  
        char[] t1 = null;  
        t1 = src.toCharArray();  
        String[] t2 = new String[t1.length];  
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
          
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        String t4 = "";  
        int t0 = t1.length;  
        try {  
            for (int i = 0; i < t0; i++) {  
                // 判断是否为汉字字符  
                if (Character.toString(t1[i]).matches(
                        "[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);
                    t4 += t2[0];
                } else
                    t4 += Character.toString(t1[i]);
            }  
            // System.out.println(t4);  
            return t4;  
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();  
        }  
        return t4;  
    }  
}
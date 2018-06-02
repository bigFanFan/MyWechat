package com.hufan.util;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomUtil {
	public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String LETTERCHAR = "abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String NUMBERCHAR = "0123456789";

	public static String generateString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
					.charAt(random.nextInt("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length())));
		}
		return sb.toString();
	}

	public static String generateMixString(int length) {
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			sb.append("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
					.charAt(random.nextInt("abcdefghijkllmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".length())));
		}
		return sb.toString();
	}
	/**
     * 根据传进来的位数产生对应位数的随机码
     *
     * @param length
     * @return
     * @author changxiaolong
     */
    public static String ranDomNo(int length) {
        char[] chars = "23456789abcdefghijkmnpqrstuvwxyz".toCharArray();
        Random rand = new Random();
        StringBuilder randStr = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            int index = rand.nextInt(chars.length - 1);//chars.length-1生成一个伪随机数赋值给index
            randStr.append(chars[index]);//将对应索引的数组与randStr的变量值相连接
        }
        return randStr.toString();
    }

	public static String generateLowerString(int length) {
		return generateMixString(length).toLowerCase();
	}

	public static String generateUpperString(int length) {
		return generateMixString(length).toUpperCase();
	}

	public static String generateZeroString(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append('0');
		}
		return sb.toString();
	}

	public static String toFixdLengthString(long num, int fixdlenth) {
		StringBuffer sb = new StringBuffer();
		String strNum = String.valueOf(num);
		if (fixdlenth - strNum.length() >= 0)
			sb.append(generateZeroString(fixdlenth - strNum.length()));
		else {
			throw new RuntimeException("将数字" + num + "转化为长度为" + fixdlenth + "的字符串发生异常！");
		}

		sb.append(strNum);
		return sb.toString();
	}

	public static int getNotSimple(int[] param, int len) {
		Random rand = new Random();
		for (int i = param.length; i > 1; i--) {
			int index = rand.nextInt(i);
			int tmp = param[index];
			param[index] = param[(i - 1)];
			param[(i - 1)] = tmp;
		}
		int result = 0;
		for (int i = 0; i < len; i++) {
			result = result * 10 + param[i];
		}
		return result;
	}

	/**
	 * 概率随机算法（此随机算法无需权重相加等于100，但需要权重相加>0）
	 * idName 随机奖品对应的key
	 * weightName 权重对应的key
	 *
	 * 如A、B、C、D权重分别为1，2，3，4
	 * 计算各自的权重分段，A的权重分段为(0,1/(1+2+3+4)),B的权重分段为(A分段Max,A分段Max+1/(1+2+3+4))，依此类推
	 * 产生一个[0,1)的随机数，判断随机数属于哪个权重分段之内，则选中的是对应的对象
	 *
	 * @throws Exception
	 * */
	public static String weightRandom(List<Map<String,Object>> weightList,String idName,String weightName) throws Exception{
		double i = 0;
		double randomNum = Math.random();//随机数
		String result = "";
		double weightCount = 0.0;//总权重值
		for(Map<String,Object> weightMap : weightList){
			double weight = Double.parseDouble(String.valueOf(weightMap.get(weightName)));
			weightCount += weight;
		}
		if(weightCount == 0){
			throw new Exception("权重相加必须大于0");
		}
		for(Map<String,Object> weightMap : weightList){
			double weight = Double.parseDouble(String.valueOf(weightMap.get(weightName)));
			double min  = i;//权重分段最小值
			double max = i + weight/weightCount;//权重分段最大值
			if(rangeInDefined(randomNum,min,max)){
				result = String.valueOf(weightMap.get(idName));
				break;
			}
			i = max;
		}
		if("".equals(result)){//若随机到空白区域，则重复执行取值
			result = weightRandom(weightList,idName,weightName);
		}
		return result;
	}

	private static boolean rangeInDefined(double current, double min, double max){
		return current>min && current<max;
	}

	public static void main(String[] args) {
		System.out.println("返回一个定长的随机字符串(只包含大小写字母、数字):" + generateString(10));
		System.out.println("返回一个定长的随机纯字母字符串(只包含大小写字母):" + generateMixString(10));

		System.out.println("返回一个定长的随机纯大写字母字符串(只包含大小写字母):" + generateLowerString(10));

		System.out.println("返回一个定长的随机纯小写字母字符串(只包含大小写字母):" + generateUpperString(10));

		System.out.println("生成一个定长的纯0字符串:" + generateZeroString(10));
		System.out.println("根据数字生成一个定长的字符串，长度不够前面补0:" + toFixdLengthString(123L, 10));

		int[] in = { 1, 2, 3, 4, 5, 6, 7 };
		System.out.println("每次生成的len位数都不相同:" + getNotSimple(in, 3));
	}
}
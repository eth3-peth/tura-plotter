package com.softsec.util;

/**
 * Created with IDEA
 *
 * @Author Chenhh
 * @Date 2019/12/11 10:47
 * @Description 格式化工具类
 * @Version 1.0
 */
public class ChhUtil {

	/**
	 * 将每三个数字（或字符）加上逗号处理（通常使用金额方面的编辑）
	 * 5000000.00 --> 5,000,000.00
	 * 20000000 --> 20,000,000
	 * @param str  无逗号的数字
	 * @return 加上逗号的数字
	 */
	public static String strAddComma(String str) {
		if (str == null) {
			str = "";
		}
		String addCommaStr = ""; // 需要添加逗号的字符串（整数）
		String tmpCommaStr = ""; // 小数，等逗号添加完后，最后在末尾补上
		if (str.contains(".")) {
			addCommaStr = str.substring(0,str.indexOf("."));
			tmpCommaStr = str.substring(str.indexOf("."),str.length());
		}else{
			addCommaStr = str;
		}
		// 将传进数字反转
		String reverseStr = new StringBuilder(addCommaStr).reverse().toString();
		String strTemp = "";
		for (int i = 0; i < reverseStr.length(); i++) {
			if (i * 3 + 3 > reverseStr.length()) {
				strTemp += reverseStr.substring(i * 3, reverseStr.length());
				break;
			}
			strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
		}
		// 将 "5,000,000," 中最后一个","去除
		if (strTemp.endsWith(",")) {
			strTemp = strTemp.substring(0, strTemp.length() - 1);
		}
		// 将数字重新反转,并将小数拼接到末尾
		String resultStr = new StringBuilder(strTemp).reverse().toString() + tmpCommaStr;
		return resultStr;
	}

	/**
	 * 将加上逗号处理的数字（字符）的逗号去掉 （通常使用金额方面的编辑）
	 * 5,000,000.00 --> 5000000.00
	 * 20,000,000 --> 20000000
	 * @param str  加上逗号的数字（字符）
	 * @return 无逗号的数字（字符）
	 */
	public static String strRemoveComma(String str) {
		if (str == null) {
			str = "";
		}
		String resultStr = str.replaceAll(",",""); // 需要去除逗号的字符串（整数）

		return resultStr;
	}

	public static void main(String[] args){
		String result = strAddComma("5000000.00");  //格式化加逗号
		// String result = strRemoveComma("456,415,132.22"); //去除逗号
		System.out.println(result);
	}
}

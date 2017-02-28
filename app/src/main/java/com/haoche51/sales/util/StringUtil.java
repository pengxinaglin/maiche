package com.haoche51.sales.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 */
public class StringUtil {
	private static int formatMoney;

	/**
	 * Native to ascii string. It's same as execut native2ascii.exe.
	 *
	 * @param str native string
	 * @return ascii string
	 */
	public static String native2Ascii(String str) {
		char[] chars = str.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < chars.length; i++) {
			sb.append(char2Ascii(chars[i]));
		}
		return sb.toString();
	}

	/**
	 * Native character to ascii string.
	 *
	 * @param c native character
	 * @return ascii string
	 */
	private static String char2Ascii(char c) {
		if (c > 255) {
			StringBuilder sb = new StringBuilder();
			sb.append("\\u");
			int code = (c >> 8);
			String tmp = Integer.toHexString(code);
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
			code = (c & 0xFF);
			tmp = Integer.toHexString(code);
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
			return sb.toString();
		} else {
			return Character.toString(c);
		}
	}

	/**
	 * Ascii to native string. It's same as execut native2ascii.exe -reverse.
	 *
	 * @param str ascii string
	 * @return native string
	 */
	public static String ascii2Native(String str) {
		StringBuilder sb = new StringBuilder();
		int begin = 0;
		int index = str.indexOf("\\u");
		while (index != -1) {
			sb.append(str.substring(begin, index));
			sb.append(ascii2Char(str.substring(index, index + 6)));
			begin = index + 6;
			index = str.indexOf("\\u", begin);
		}
		sb.append(str.substring(begin));
		return sb.toString();
	}

	/**
	 * Ascii to native character.
	 *
	 * @param str ascii string
	 * @return native character
	 */
	private static char ascii2Char(String str) {
		if (str.length() != 6) {
			throw new IllegalArgumentException("Ascii string of a native character must be 6 character.");
		}
		if (!"\\u".equals(str.substring(0, 2))) {
			throw new IllegalArgumentException("Ascii string of a native character must start with \"\\u\".");
		}
		String tmp = str.substring(2, 4);
		int code = Integer.parseInt(tmp, 16) << 8;
		tmp = str.substring(4, 6);
		code += Integer.parseInt(tmp, 16);
		return (char) code;
	}

	/**
	 * 使用 MD5 算法加密字符串
	 */
	public static String MD5(String data) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(data.getBytes());
			return bytesToHexString(bytes);
		} catch (NoSuchAlgorithmException e) {
		}
		return data;
	}

	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * 将字符串解析成整数
	 *
	 * @param str          要转换的字符串
	 * @param defaultValue 转换失败时的默认整数
	 * @return 转换后的整数，如果转换失败，则返回 defaultValue
	 */
	public static int parseInt(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 将字符串解析成浮点数
	 *
	 * @param str          要转换的字符串
	 * @param defaultValue 转换失败时的默认浮点数
	 * @return 转换后的浮点数，如果转换失败，则返回 defaultValue
	 */
	public static float parseFloat(String str, float defaultValue) {
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 将字符串解析成 double
	 *
	 * @param str          要转换的字符串
	 * @param defaultValue 转换失败时的默认 double
	 * @return 转换后的 double，如果转换失败，则返回 defaultValue
	 */
	public static double parseDouble(String str, float defaultValue) {
		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 将字符串解析成 long
	 *
	 * @param str          要转换的字符串
	 * @param defaultValue 转换失败时的默认 long
	 * @return 转换后的 long，如果转换失败，则返回 defaultValue
	 */
	public static long parseLong(String str, long defaultValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	/**
	 * 对字符串进行 url 编码操作
	 */
	public static String urlEncode(String input) {
		try {
			return URLEncoder.encode(input, "UTF-8");
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 对字符串进行 url 解码操作
	 */
	public static String urlDecode(String input) {
		try {
			return URLDecoder.decode(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return "";
	}

	/**
	 * 获取格式化后的百分比字符串
	 *
	 * @param max     最高值
	 * @param current 当前值
	 * @return 百分比，如：81%
	 */
	public static String getFormattedPercent(long max, long current) {
		double percent = (double) current / max;
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("0.00%");
		return df.format(percent);
	}

	/**
	 * 判断字符串是不是一个有效的 http 资源地址
	 */
	public static boolean isUri(String url) {
		if (!TextUtils.isEmpty(url)) {
			Pattern pat = Pattern.compile("^https?:\\/\\/([\\da-z-]+\\.)+[a-z]{2,6}\\/.+$");
			return pat.matcher(url).find();
		}
		return false;
	}

	/**
	 * 格式化金额/25000 --> 25,000
	 */
	public static String getFormatMoney(int money) {
		String formatMoney = String.valueOf(money);
		if (formatMoney.length() < 3)
			return formatMoney;

		String str1 = formatMoney;
		str1 = new StringBuilder(str1).reverse().toString();//先将字符串颠倒顺序
		String str2 = "";
		for (int i = 0; i < str1.length(); i++) {
			if (i * 3 + 3 > str1.length()) {
				str2 += str1.substring(i * 3, str1.length());
				break;
			}
			str2 += str1.substring(i * 3, i * 3 + 3) + ",";
		}
		if (str2.endsWith(",")) {
			str2 = str2.substring(0, str2.length() - 1);
		}
		//最后再将顺序反转过来
		return new StringBuilder(str2).reverse().toString();
	}

	/**
	 * 截取字符串
	 *
	 * @str 原字符串
	 * @start 开始截取位置
	 * @end 结束截取位置
	 */
	public static String subStirng(String str, String start, String end) {
		String result = null;
		try {
			int s = str.lastIndexOf(start);
			int e = str.lastIndexOf(end);
			if (s != -1 && e != -1) {
				result = str.substring(s + 1, e);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

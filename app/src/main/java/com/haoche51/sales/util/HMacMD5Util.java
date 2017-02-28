package com.haoche51.sales.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by PengXianglin on 16/7/29.
 */
public class HMacMD5Util {

	private static byte[] md5(byte[] str)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str);
		return md.digest();
	}

	public static byte[] getHmacMd5Bytes(byte[] key, byte[] data) throws NoSuchAlgorithmException {

		int length = 64;
		byte[] ipad = new byte[length];
		byte[] opad = new byte[length];
		for (int i = 0; i < 64; i++) {
			ipad[i] = 0x36;
			opad[i] = 0x5C;
		}
		byte[] actualKey = key;      //Actual key.
		byte[] keyArr = new byte[length];   //Key bytes of 64 bytes length

		if (key.length > length) {
			actualKey = md5(key);
		}
		for (int i = 0; i < actualKey.length; i++) {
			keyArr[i] = actualKey[i];
		}

		if (actualKey.length < length) {
			for (int i = actualKey.length; i < keyArr.length; i++)
				keyArr[i] = 0x00;
		}


		byte[] kIpadXorResult = new byte[length];
		for (int i = 0; i < length; i++) {
			kIpadXorResult[i] = (byte) (keyArr[i] ^ ipad[i]);
		}


		byte[] firstAppendResult = new byte[kIpadXorResult.length + data.length];
		for (int i = 0; i < kIpadXorResult.length; i++) {
			firstAppendResult[i] = kIpadXorResult[i];
		}
		for (int i = 0; i < data.length; i++) {
			firstAppendResult[i + keyArr.length] = data[i];
		}


		byte[] firstHashResult = md5(firstAppendResult);


		byte[] kOpadXorResult = new byte[length];
		for (int i = 0; i < length; i++) {
			kOpadXorResult[i] = (byte) (keyArr[i] ^ opad[i]);
		}


		byte[] secondAppendResult = new byte[kOpadXorResult.length + firstHashResult.length];
		for (int i = 0; i < kOpadXorResult.length; i++) {
			secondAppendResult[i] = kOpadXorResult[i];
		}
		for (int i = 0; i < firstHashResult.length; i++) {
			secondAppendResult[i + keyArr.length] = firstHashResult[i];
		}


		byte[] hmacMd5Bytes = md5(secondAppendResult);
		return hmacMd5Bytes;
	}

	public static String getHmacMd5Str(String key, String data) {
		String result = "";
		try {
			byte[] keyByte = key.getBytes("UTF-8");
			byte[] dataByte = data.getBytes("UTF-8");
			byte[] hmacMd5Byte = getHmacMd5Bytes(keyByte, dataByte);
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < hmacMd5Byte.length; i++) {
				if (Integer.toHexString(0xFF & hmacMd5Byte[i]).length() == 1)
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & hmacMd5Byte[i]));
				else
					md5StrBuff.append(Integer.toHexString(0xFF & hmacMd5Byte[i]));
			}
			result = md5StrBuff.toString().toLowerCase();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

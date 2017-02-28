package com.haoche51.settlement.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by yangming on 2015/12/16.
 */
public class DESUtil {

  private static final String TAG = "DESUtil";

  // TODO 初始化向量如何设置，password如何获取
  private static final String iv = "42624351";


  /**
   * 加密
   *
   * @param encryptString
   * @param encryptKey
   * @return
   * @throws Exception
   */
  public static String encryptDES(String encryptString, String encryptKey) throws Exception {

    IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());

//    HCLogUtil.d(TAG,"---encryptKey--->" + encryptKey.substring(0, 8));
//    HCLogUtil.d(TAG,"---encryptKey-String-->" + new String(encryptKey.substring(0, 8).getBytes()));

    SecretKeySpec key = new SecretKeySpec(encryptKey.substring(0, 8).getBytes(), "DES");
    Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
    byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

    return new String(Base64Util.encode(encryptedData));
  }


  /**
   * 解密
   *
   * @param decryptString
   * @param decryptKey
   * @return
   * @throws Exception
   */
  public static String decryptDES(String decryptString, String decryptKey) throws Exception {

    byte[] byteMi = new Base64Util().decode(decryptString.getBytes());
    IvParameterSpec zeroIv = new IvParameterSpec(iv.getBytes());

//    HCLogUtil.d(TAG,"---decryptKey--->" + decryptKey.substring(0, 8));
//    HCLogUtil.d(TAG,"---decryptKey-String-->" + new String(decryptKey.substring(0, 8).getBytes()));

    SecretKeySpec key = new SecretKeySpec(decryptKey.substring(0, 8).getBytes(), "DES");
    Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
    byte decryptedData[] = cipher.doFinal(byteMi);

    return new String(decryptedData);
  }

}

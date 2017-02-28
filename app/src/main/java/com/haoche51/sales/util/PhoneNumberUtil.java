package com.haoche51.sales.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 电话号码工具类
 * Created by wufx on 2016/2/17.
 */
public class PhoneNumberUtil {
  /*
   * 验证号码手机号(固话)是否合法
   * 手机号码：
   * 	13段：130、131、132、133、134、135、136、137、138、139
      14段：145、147
      15段：150、151、152、153、155、156、157、158、159
      17段：170、176、177、178
      18段：180、181、182、183、184、185、186、187、188、189
     固定电话：
       规则->区号3-4位，号码7-8位,可以有分机号，分机号为3-4为，格式如下："0775-85333343-123"
   *
   */
  public static boolean isPhoneNumberValid(String phoneNumber) {
    String expression = "((^(13[0-9]|14[57]|15[012356789]|17[0678]|18[0-9])\\d{8}$)|(^\\d{3,4}-\\d{7,8}(-\\d{3,4})?$))";

    CharSequence inputStr = phoneNumber;

    Pattern pattern = Pattern.compile(expression);

    Matcher matcher = pattern.matcher(inputStr);

    return matcher.matches();

  }
}

package com.haoche51.sales.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.haoche51.sales.HCApplication;
import com.haoche51.sales.R;
import com.haoche51.sales.constants.WeiXinContants;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;


/**
 * Created by yangming on 2015/10/24.
 */
public class WeiXinShareUtils {

  /**
   * 微信分享 分享网页
   *
   * @param flag(0:分享到微信好友，1：分享到微信朋友圈)
   */
  public static void wechatShare(int flag, Context context, String ids, String phone) {
    IWXAPI iwxapi;
    iwxapi = WXAPIFactory.createWXAPI(HCApplication.getContext(), WeiXinContants.WEIXIN_API_ID);
    iwxapi.registerApp(WeiXinContants.WEIXIN_API_ID);
    WXWebpageObject webpage = new WXWebpageObject();
    webpage.webpageUrl = "http://m.haoche51.com/app_tuijian/vehicle_list?ids=" + ids + "&phone=" + phone;
    WXMediaMessage msg = new WXMediaMessage(webpage);
    msg.title = "还在找车吗？好车无忧又有好车来了，不看别后悔哦！";
    msg.description = "好车无忧已经根据您的需求，推荐了一批好车，快来看看吧！";
    //这里替换一张自己工程里的图片资源
    Bitmap thumb = BitmapFactory.decodeResource(HCApplication.getContext().getResources(), R.drawable.share_bargain);
    msg.setThumbImage(thumb);

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = String.valueOf(System.currentTimeMillis());
    req.message = msg;
    req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
    iwxapi.sendReq(req);
  }
}

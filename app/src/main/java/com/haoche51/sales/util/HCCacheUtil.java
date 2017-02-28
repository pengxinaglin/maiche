package com.haoche51.sales.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haoche51.sales.GlobalData;
import com.haoche51.sales.hcmessage.MessageEntity;
import com.haoche51.sales.hctransfer.TransferSimpleEntity;

import java.util.ArrayList;
import java.util.List;

public class HCCacheUtil {

  private static Gson mGson = new Gson();
  private static ACache mCache = ACache.get(GlobalData.mContext);
  /**
   * 缓存过户数据key
   */
  private final static String KEY_CACHED_TRANSDATA = "key_transfer_task";

  /* 缓存推送消息信息 */
  private final static String KEY_CACHED_PUSHMESSAGE = "key_push_messages";


  /***
   * 获取缓存的记录
   */
  public static List<TransferSimpleEntity> getCachedTransferData() {
    List<TransferSimpleEntity> mData = null;
    int checker_id = GlobalData.userDataHelper.getUser().getId();
    String cachedStr = mCache.getAsString(KEY_CACHED_TRANSDATA + checker_id);
    if (!TextUtils.isEmpty(cachedStr)) {
      mData = mGson.fromJson(cachedStr, new TypeToken<List<TransferSimpleEntity>>() {
      }.getType());
    }
    if (mData == null) {
      mData = new ArrayList<TransferSimpleEntity>();
    }
    return mData;
  }

  /***
   * 缓存过户数据
   */
  public static void saveCacheTransferData(final List<TransferSimpleEntity> mData) {
    if (mData == null || mData.isEmpty())
      return;
    Runnable command = new Runnable() {
      @Override
      public void run() {
        int checker_id = GlobalData.userDataHelper.getUser().getId();
        String value = mGson.toJson(mData);
        mCache.remove(KEY_CACHED_TRANSDATA + checker_id);
        mCache.put(KEY_CACHED_TRANSDATA + checker_id, value, Integer.MAX_VALUE);
      }
    };

    HCThreadUtil.execute(command);
  }

  /**
   * 重新设置缓存中的实体状态
   */
  public static void resetCachedTransferStatus(final TransferSimpleEntity entity) {

    Runnable command = new Runnable() {
      @Override
      public void run() {
        List<TransferSimpleEntity> mCacheData = getCachedTransferData();
        if (mCacheData != null && !mCacheData.isEmpty()) {
          int size = mCacheData.size();
          int index = -1;
          for (int i = 0; i < size; i++) {
            TransferSimpleEntity cEntity = mCacheData.get(i);
            if (cEntity.getTransaction_id() == entity.getTransaction_id()) {
              index = i;
              break;
            }
          }
          if (index != -1) {
            mCacheData.remove(index);
            mCacheData.add(index, entity);
            saveCacheTransferData(mCacheData);
          }
        }

      }
    };

    HCThreadUtil.execute(command);
  }

  /**
   * 获取本地缓存的推送消息
   *
   * @return
   */
  public static List<MessageEntity> getCacheMessages() {
    List<MessageEntity> mData = null;
    int checker_id = GlobalData.userDataHelper.getUser().getId();
    String cachedStr = mCache.getAsString(KEY_CACHED_PUSHMESSAGE + checker_id);
    if (!TextUtils.isEmpty(cachedStr)) {
      mData = mGson.fromJson(cachedStr, new TypeToken<List<MessageEntity>>() {
      }.getType());
    }
    if (mData == null) {
      mData = new ArrayList<MessageEntity>();
    }
    return mData;

  }

  /**
   * 存储推送的消息信息
   *
   * @param entities
   */
  public static void saveCacheMessages(final List<MessageEntity> entities) {
    if (entities == null || entities.isEmpty()) return;
    Runnable command = new Runnable() {
      @Override
      public void run() {
        String value = mGson.toJson(entities);
        int checker_id = GlobalData.userDataHelper.getUser().getId();
        mCache.remove(KEY_CACHED_PUSHMESSAGE + checker_id);
        mCache.put(KEY_CACHED_PUSHMESSAGE + checker_id, value, Integer.MAX_VALUE);
      }
    };

    HCThreadUtil.execute(command);
  }

  /**
   * 更新缓存中指定信息
   *
   * @param entity
   */
  public static void updateCacheMessage(final MessageEntity entity) {

    Runnable command = new Runnable() {
      @Override
      public void run() {
        List<MessageEntity> mCacheData = getCacheMessages();
        if (mCacheData != null && !mCacheData.isEmpty()) {
          int size = mCacheData.size();
          int index = -1;
          for (int i = 0; i < size; i++) {
            MessageEntity cEntity = mCacheData.get(i);
            if (cEntity.getId() == entity.getId()) {
              index = i;
              break;
            }
          }
          if (index != -1) {
            mCacheData.remove(index);
            mCacheData.add(index, entity);
            saveCacheMessages(mCacheData);
          }
        }

      }
    };

    HCThreadUtil.execute(command);

  }
}

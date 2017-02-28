package com.haoche51.sales.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.haoche51.sales.hctransaction.BuyerEntity;

import java.util.Map;

public class ContactUtil {

    /**
     * 创建通讯录分组
     */
    public static long addContentGroup(Context context, String groupName) {
        ContentValues valuess = new ContentValues();
        valuess.put(ContactsContract.Groups.TITLE, groupName);
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.Groups.CONTENT_URI, valuess);
        return ContentUris.parseId(rawContactUri);
    }

    /**
     * 获取分组id，如没有会创建
     *
     * @param context
     * @return
     */
    public static int getGroupId(Context context, String groupName) {
        int groupId = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Groups.CONTENT_URI, null, Groups.TITLE + " = ? ", new String[]{groupName}, null);
            if (cursor.moveToNext()) {
                groupId = cursor.getInt(cursor.getColumnIndex(Groups._ID));
            } else {
                groupId = (int) addContentGroup(context, groupName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return groupId;
    }

    /**
     * 判断号码是否存在
     *
     * @param context
     * @param phone
     * @return
     */
    public static boolean isPhoneExists(Context context, String phone) {
        boolean isExists = false;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Contacts.Phones.CONTENT_URI, new String[]{Contacts.Phones.NUMBER}, Contacts.Phones.NUMBER + " = ?", new String[]{phone}, null);
            isExists = cursor.moveToNext();

            //由于某些Android设备原因，会导致查询结果不一致，再次验证一次，是否存在此号码
            if (!isExists) {
                Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + phone);
                ContentResolver resolver = context.getContentResolver();
                cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
                isExists = cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return isExists;
    }

    /**
     * 插入一条通讯录
     *
     * @param context
     * @param groupId
     * @param map
     */
    public static void insertPhoneBook(Context context, int groupId, Map<String, Object> map) {
        try {
            // 首先插入空值，再得到rawContactsId ，用于下面插值
            ContentValues values = new ContentValues();
            // 插入一条空值，以便得到 rawContactsId
            Log.e("ContactUtil", "insertPhoneBook1");
            Uri rawContactUri = context.getContentResolver().insert(RawContacts.CONTENT_URI, values);
            if (rawContactUri != null) {
                long rawContactsId = ContentUris.parseId(rawContactUri);
                Log.e("ContactUtil", "insertPhoneBook2");
                // 添加姓名
                values.clear();
                values.put(StructuredName.RAW_CONTACT_ID, rawContactsId);
                values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
                values.put(StructuredName.DISPLAY_NAME, (String) map.get("name"));
                context.getContentResolver().insert(Data.CONTENT_URI, values);
                Log.e("ContactUtil", "insertPhoneBook3");
                // 添加手机号码
                values.clear();
                values.put(Phone.RAW_CONTACT_ID, rawContactsId);
                values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
                values.put(Phone.NUMBER, (String) map.get("phone"));
                context.getContentResolver().insert(Data.CONTENT_URI, values);
                Log.e("ContactUtil", "insertPhoneBook4");
                // 添加到指定分组
                values.clear();
                values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID, rawContactsId);
                values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId);
                values.put(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);
                context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                Log.e("ContactUtil", "insertPhoneBook5");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入一条通讯录
     *
     * @param context
     * @param groupId
     */
    public static void insertPhoneContactBook(Context context, int groupId, BuyerEntity entity) {
        try {
            // 首先插入空值，再得到rawContactsId ，用于下面插值
            ContentValues values = new ContentValues();
            // 插入一条空值，以便得到 rawContactsId
            Uri rawContactUri = context.getContentResolver().insert(RawContacts.CONTENT_URI, values);
            long rawContactsId = ContentUris.parseId(rawContactUri);
            // 添加姓名
            values.clear();
            values.put(StructuredName.RAW_CONTACT_ID, rawContactsId);
            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
            //默认使用最新的第一个意向车
            String subId = "";
            if (entity.getSubscribe_rule() != null) {
                if (entity.getSubscribe_rule().getSubscribe_series() != null && entity.getSubscribe_rule().getSubscribe_series().size() > 0)
                    subId = entity.getSubscribe_rule().getSubscribe_series().get(0).getId();
            }
            values.put(StructuredName.DISPLAY_NAME, entity.getName() + "-" + subId);//姓名 + 车源
            context.getContentResolver().insert(Data.CONTENT_URI, values);
            // 添加手机号码
            values.clear();
            values.put(Phone.RAW_CONTACT_ID, rawContactsId);
            values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
            values.put(Phone.NUMBER, entity.getPhone());//客户电话
            context.getContentResolver().insert(Data.CONTENT_URI, values);
            // 添加到指定分组
            values.clear();
            values.put(ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID, rawContactsId);
            values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId);
            values.put(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);
            context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

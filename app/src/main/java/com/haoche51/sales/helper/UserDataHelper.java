package com.haoche51.sales.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haoche51.sales.LoginToken;
import com.haoche51.sales.User;
import com.haoche51.sales.activity.UserRightEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserDataHelper {

    public static final String KEY_STORE_TOKEN = "store_token";
    public static final String KEY_STORE_TIMEOUT = "store_timeout";
    public static final String KEY_STORE_CREATE_TIME = "store_create_time";

    private static final String USER_DATA_FILE = "user_data";

    private static final String IS_LOGIN = "is_login";
    private static final String LOGIN_NAME = "login_name";
    private static final String LOGIN_PWD = "login_pwd";
    private static final String BD_HAS_BIND = "baidu_has_bind";
    private static final String BD_USER_ID = "baidu_user_id";
    private static final String BD_CHANNEL_ID = "baidu_channel_id";
    private static final String USER = "checker_json";
    private static final String LAST_CHECKER_ID = "last_checker_id";
    private static final String LAST_CHECKER_NAME = "last_checker_name";
    private static final String LAST_APP_TOKEN = "last_app_token";
    private static final String BIND_PUSH_ID = "bind_baidu_push_id";//修改preference 名字，让客户端重新绑定推送。避免百度升级造成老版本绑定id 不能推送
    private static final String IMPORT_VEHICLE_DATA = "import_vehicle_data";
    public static final String LAST_UPDATE_TIME = "last_update_time";
    public static final String LOGIN_TOKEN = "login_token";
    public static final String USER_RIGHT = "user_right";

    public static final String POS_LOGIN_NAME = "pos_login_name";//pos操作员
    public static final String POS_RSA_YT_PUBLIC = "pos_rsa_yt_public";//银通支付公钥
    public static final String POS_MD5_KEY = "pos_md5_key";//银通支付

    private SharedPreferences reader = null;
    private SharedPreferences.Editor writer = null;
    private Gson gson = null;

    public UserDataHelper(Context mContext) {
        reader = mContext.getSharedPreferences(USER_DATA_FILE, Context.MODE_PRIVATE);
        writer = reader.edit();
        gson = new Gson();
    }

    /**
     * 保存门店Token
     */
    public void saveStoreToken(LoginToken loginToken) {
        writer.putString(KEY_STORE_TOKEN, loginToken.getToken());
        writer.putInt(KEY_STORE_TIMEOUT, loginToken.getTimeOut());
        writer.putInt(KEY_STORE_CREATE_TIME, (int) (System.currentTimeMillis() / 1000));
        writer.commit();
    }

    /**
     * 获得门店Token
     */
    public LoginToken getStoreToken() {
        LoginToken loginToken = new LoginToken();
        String storeToken = reader.getString(KEY_STORE_TOKEN, "");
        int timeOut = reader.getInt(KEY_STORE_TIMEOUT, 0);
        int createTime = reader.getInt(KEY_STORE_CREATE_TIME, 0);
        loginToken.setToken(storeToken);
        loginToken.setTimeOut(timeOut);
        loginToken.setCreate_time(createTime);
        return loginToken;
    }

    /**
     * 清除门店Token
     */
    public void clearStoreToken() {
        writer.putString(KEY_STORE_TOKEN, "");
        writer.putInt(KEY_STORE_TIMEOUT, 0);
        writer.putInt(KEY_STORE_CREATE_TIME, 0);
        writer.commit();
    }

    /***
     * 清除登录和绑定推送状态
     */
    public void clearLoginAndPushStatus() {
        writer.putBoolean(IS_LOGIN, false).putBoolean(BD_HAS_BIND, false).apply();
    }

    public UserDataHelper setLogin() {
        writer.putBoolean(IS_LOGIN, true);
        return this;
    }

    public UserDataHelper clearLogin() {
        writer.remove(IS_LOGIN);
        return this;
    }


    public boolean isLogin() {
        return reader.getBoolean(IS_LOGIN, false);
    }

    /**
     * 登陆token 相关
     *
     * @param token
     * @return
     */
    public UserDataHelper setLoginToken(LoginToken token) {
        writer.putString(LOGIN_TOKEN, gson.toJson(token));
        return this;
    }

    /**
     * 清除登陆token
     *
     * @return
     */
    public UserDataHelper clearLoginToken() {
        writer.remove(LOGIN_TOKEN);
        return this;
    }

    /**
     * 获取登录token
     *
     * @return
     */
    public LoginToken getLoginToken() {
        String token_str = reader.getString(LOGIN_TOKEN, "{}");
        return gson.fromJson(token_str, LoginToken.class);
    }


    public UserDataHelper setLoginName(String username) {
        writer.putString(LOGIN_NAME, username);
        return this;
    }

    public UserDataHelper clearLoginName() {
        writer.remove(LOGIN_NAME);
        return this;
    }

    public String getLoginName() {
        return reader.getString(LOGIN_NAME, "");
    }

    public UserDataHelper setLoginPwd(String password) {
        writer.putString(LOGIN_PWD, password);
        return this;
    }

    public UserDataHelper clearLoginPwd() {
        writer.remove(LOGIN_PWD);
        return this;
    }

    public String getLoginPwd() {
        return reader.getString(LOGIN_PWD, "");
    }

    public UserDataHelper bindBaiduPush() {
        writer.putBoolean(BD_HAS_BIND, true);
        return this;
    }

    public UserDataHelper unbindBaiduPush() {
        writer.putBoolean(BD_HAS_BIND, false);
        return this;
    }

    public boolean hasBindBaiduPush() {
        return reader.getBoolean(BD_HAS_BIND, false);
    }

    public UserDataHelper setPushUserId(String userId) {
        writer.putString(BD_USER_ID, userId);
        return this;
    }

    public UserDataHelper clearPushUserId() {
        writer.remove(BD_USER_ID);
        return this;
    }

    public String getPushUserId() {
        return reader.getString(BD_USER_ID, null);
    }

    public UserDataHelper setPushChannelId(String channelId) {
        writer.putString(BD_CHANNEL_ID, channelId);
        return this;
    }

    public UserDataHelper clearPushChannelId() {
        writer.remove(BD_CHANNEL_ID);
        return this;
    }

    public String getPushChannelId() {
        return reader.getString(BD_CHANNEL_ID, null);
    }

    public UserDataHelper setUser(User user) {
        writer.putString(USER, gson.toJson(user));
        return this;
    }

    public UserDataHelper clearUser() {
        writer.remove(USER);
        return this;
    }

    public User getUser() {
        String checkerJson = reader.getString(USER, "{}");
        return gson.fromJson(checkerJson, User.class);
    }

    public UserDataHelper setLastCheckerId(int checkerId) {
        writer.putInt(LAST_CHECKER_ID, checkerId);
        return this;
    }

    public UserDataHelper setLastCheckerName(String checkerName) {
        writer.putString(LAST_CHECKER_NAME, checkerName);
        return this;
    }

    public UserDataHelper setLastAppToken(String token) {
        writer.putString(LAST_APP_TOKEN, token);
        return this;
    }

    public UserDataHelper clearLastCheckerId() {
        writer.remove(LAST_CHECKER_ID);
        return this;
    }

    public int getLastCheckerId() {
        return reader.getInt(LAST_CHECKER_ID, 0);
    }

    public String getLastCheckerName() {
        return reader.getString(LAST_CHECKER_NAME, "");
    }

    public String getLastAppToken() {
        return reader.getString(LAST_APP_TOKEN, "");
    }

    public UserDataHelper setPushId(int pushId) {
        writer.putInt(BIND_PUSH_ID, pushId);
        return this;
    }

    public UserDataHelper clearPushId() {
        writer.remove(BIND_PUSH_ID);
        return this;
    }

    public int getPushId() {
        return reader.getInt(BIND_PUSH_ID, 0);
    }

    public UserDataHelper setImportVehicleData() {
        writer.putBoolean(IMPORT_VEHICLE_DATA, true);
        return this;
    }

    public UserDataHelper clearImportVehicleData() {
        writer.remove(IMPORT_VEHICLE_DATA);
        return this;
    }

    public boolean getImportVehicleData() {
        return reader.getBoolean(IMPORT_VEHICLE_DATA, false);
    }

    public UserDataHelper setUserRight(String userRight) {
        writer.putString(USER_RIGHT, userRight);
        return this;
    }

    public List<UserRightEntity> getUserRight() {
        List<UserRightEntity> list = new ArrayList<>();
        String userRightJson = reader.getString(USER_RIGHT, "");
        if (TextUtils.isEmpty(userRightJson)) {
            return null;
        }
        Type type = new TypeToken<List<UserRightEntity>>() {
        }.getType();
        try {
            list = (new Gson()).fromJson(userRightJson, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public UserDataHelper clearUserRight() {
        writer.remove(USER_RIGHT);
        return this;
    }

    public UserDataHelper setPosLoginName(String posLoginName) {
        writer.putString(POS_LOGIN_NAME, posLoginName);
        return this;
    }

    public String getPosLoginName() {
        return reader.getString(POS_LOGIN_NAME, "");
    }

    public UserDataHelper setPosRSAYTPublic(String posRSAYTPublic) {
        writer.putString(POS_RSA_YT_PUBLIC, posRSAYTPublic);
        return this;
    }

    public String getPosRSAYTPublic() {
        return reader.getString(POS_RSA_YT_PUBLIC, "");
    }

    public UserDataHelper setPosMD5Key(String posMD5Key) {
        writer.putString(POS_MD5_KEY, posMD5Key);
        return this;
    }

    public String getPosMD5Key() {
        return reader.getString(POS_MD5_KEY, "");
    }

    public boolean commit() {
        return writer.commit();
    }
}

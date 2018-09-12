package com.yunbao.phonelive.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.yunbao.phonelive.AppContext;

/**
 * Created by cxf on 2017/8/3.
 * 保存登录后的uid和token
 */

public class SharedPreferencesUtil {

    private SharedPreferences mSharedPreferences;

    private static SharedPreferencesUtil sInstance;
    private final String UID = "uid";
    private final String TOKEN = "token";
    private final String USER_INFO = "userInfo";
    private final String CONFIG = "config";
    private final String IGNORE_MESSAGE = "ignore";
    private final String EM_LOGIN = "em_login";

    private SharedPreferencesUtil() {
        mSharedPreferences = AppContext.mApplication.getSharedPreferences("token", Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance() {
        if (sInstance == null) {
            synchronized (SharedPreferencesUtil.class) {
                if (sInstance == null) {
                    sInstance = new SharedPreferencesUtil();
                }
            }
        }
        return sInstance;
    }

    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear().commit();
    }

    /**
     * 在登录成功之后返回uid和token
     *
     * @param uid
     * @param token
     */
    public void saveUidAndToken(String uid, String token) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(UID, uid);
        editor.putString(TOKEN, token);
        editor.commit();
    }

    /**
     * 返回保存在本地的uid和token
     *
     * @return 以字符串数组形式返回uid和token
     */
    public String[] readUidAndToken() {
        String uid = mSharedPreferences.getString(UID, "");
        if ("".equals(uid)) {
            return null;
        }
        String token = mSharedPreferences.getString(TOKEN, "");
        if ("".equals(token)) {
            return null;
        }
        return new String[]{uid, token};
    }

    public void saveUserInfo(String userInfo) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(USER_INFO, userInfo);
        editor.commit();
    }

    public String readUserInfo() {
        return mSharedPreferences.getString(USER_INFO, "");
    }

    //保存config信息
    public void saveConfig(String config) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(CONFIG, config);
        editor.commit();
    }

    //读取config信息
    public String readConfig() {
        return mSharedPreferences.getString(CONFIG, "");
    }

    //保存忽略未读消息状态
    public void saveIgnoreMessage(boolean ignore) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(IGNORE_MESSAGE, ignore);
        editor.commit();
    }

    //读取忽略未读消息状态
    public boolean readIgnoreMessage() {
        return mSharedPreferences.getBoolean(IGNORE_MESSAGE, false);
    }

    /**
     * 读取环信登录状态
     */
    public boolean readEMLoginStatus() {
        return mSharedPreferences.getBoolean(EM_LOGIN, false);
    }

    //保存环信登录状态
    public void saveEMLoginStatus(boolean login) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(EM_LOGIN, login);
        editor.commit();
    }

}

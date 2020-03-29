package com.proton.carepatchtemp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Created by Api on 2016/9/21.
 */

//SharedPreferences操作
public class SpUtils {

    public static final String USER_OBJ = "user_obj";
    private final static String SP_NAME = "carepatch_temp_config";
    public static String cToken = null;
    /**
     * 默认sp保存目录
     */
    private static SharedPreferences sp;
    private static int mode = Context.MODE_PRIVATE;

    /**
     * 保存布尔值
     */
    public static void saveBoolean(String key, boolean value) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        sp.edit().putBoolean(key, value).apply();
    }

    /**
     * 保存字符串
     */
    public static void saveString(String key, String value) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        sp.edit().putString(key, value).apply();

    }

    /**
     * 保存在指定保存名的sharedpreference
     *
     * @param fileName 文件名
     * @param key
     * @param value
     */
  /*  public static void saveFileString(String fileName, String key, String value) {
        SharedPreferences sp = App.get().getSharedPreferences(fileName, mode);
        sp.edit().putString(key, value).apply();
    }*/

    /**
     * 读取指定文件里的sp值
     */
  /*  public static String readFileString(String fileName, String key, String value) {
        SharedPreferences sp = App.get().getSharedPreferences(fileName, mode);
        return sp.getString(key, value);
    }*/
    public static void remove(String key) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        sp.edit().remove(key).apply();
    }

    public static void clear() {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        sp.edit().clear().apply();
    }

    /**
     * 保存long型
     */
    public static void saveLong(String key, long value) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        sp.edit().putLong(key, value).apply();
    }

    /**
     * 保存int型
     */
    public static void saveInt(String key, int value) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        sp.edit().putInt(key, value).apply();
    }

    /**
     * 保存float型
     */
    public static void saveFloat(String key, float value) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        sp.edit().putFloat(key, value).apply();
    }

    /**
     * 获取字符值
     */
    public static String getString(String key, String defValue) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        return sp.getString(key, defValue);
    }

    /**
     * 获取int值
     */
    public static int getInt(String key, int defValue) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        return sp.getInt(key, defValue);
    }

    /**
     * 获取long值
     */
    public static long getLong(String key, long defValue) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        return sp.getLong(key, defValue);
    }

    /**
     * 获取float值
     */
    public static float getFloat(String key, float defValue) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        return sp.getFloat(key, defValue);
    }

    /**
     * 获取布尔值
     */
    public static boolean getBoolean(String key,
                                     boolean defValue) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        return sp.getBoolean(key, defValue);
    }

    /**
     * 将对象进行base64编码后保存到SharePref中
     */
    public static void saveObj(String key, Object object) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            // 将对象的转为base64码
            String objBase64 = new String(Base64.encode(baos
                    .toByteArray(), Base64.DEFAULT));

            sp.edit().putString(key, objBase64).apply();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将SharePref中经过base64编码的对象读取出来
     */
    public static Object getObj(String key) {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        String objBase64 = sp.getString(key, null);
        if (TextUtils.isEmpty(objBase64))
            return null;

        // 对Base64格式的字符串进行解码
        byte[] base64Bytes = Base64.decode(objBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);

        ObjectInputStream ois;
        Object obj = null;
        try {
            ois = new ObjectInputStream(bais);
            obj = ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static SharedPreferences getSp() {
        if (sp == null)
            sp = App.get().getSharedPreferences(SP_NAME, mode);
        return sp;
    }

    public static void setToken(String cToken) {
        String tToken = cToken == null ? "" : cToken;
        SpUtils.cToken = tToken;
        SpUtils.saveString(AppConfigs.SHARED_PREFERENCE_TOKEN, tToken);
    }
}

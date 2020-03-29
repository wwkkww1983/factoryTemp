package com.proton.carepatchtemp.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wms.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by wangmengsi on 2018/2/27.
 */

public class JSONUtils {

    public static String getString(String jsonStr, String key) {
        JSONObject jObj = getJOSNObj(jsonStr);
        if (jObj == null) {
            return "";
        }
        try {
            return jObj.getString(key);
        } catch (JSONException e) {
            Logger.w(e.toString());
        }
        return "";
    }

    public static Boolean getBooon(String jsonStr, String key) {
        JSONObject jObj = getJOSNObj(jsonStr);
        if (jObj == null) {
            return false;
        }
        try {
            return jObj.getBoolean(key);
        } catch (JSONException e) {
            Logger.w(e.toString());
        }
        return false;
    }

    public static JSONObject getJOSNObj(String jsonStr) {
        try {
            return new JSONObject(jsonStr);
        } catch (JSONException e) {
            Logger.w(e.toString());
        }
        return null;
    }

    public static <T> T getObj(String jsonStr, Class<T> classType) {
        try {
            return new Gson().fromJson(jsonStr, classType);
        } catch (JsonSyntaxException e) {
            Logger.w(e.toString());
        }
        return null;
    }

    public static <T> List<T> getObj(String jsonStr, Type type) {
        try {
            return new Gson().fromJson(jsonStr, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> String toJson(T t) {
        try {
            return new Gson().toJson(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.proton.temp.connector.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.proton.temp.connector.bean.ConnectorListener;
import com.proton.temp.connector.bean.DeviceType;
import com.proton.temp.connector.bean.DockerDataBean;
import com.proton.temp.connector.bluetooth.utils.BleUtils;
import com.wms.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

/**
 * Created by wangmengsi on 2018/3/15.
 */
public class Utils {

    public static final String PREFIX = "esp/";
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    public static String getTopicByMacAddress(String macaddress) {
        return PREFIX + macaddress.toUpperCase().replace(":", "");
    }

    public static String getWillTopicByMacAddress(String macaddress) {
        return PREFIX + macaddress.toUpperCase().replace(":", "") + "/lastwill";
    }

    public static String getPatchDisconnectTopicByMacAddress(String macaddress) {
        return PREFIX + macaddress.toUpperCase().replace(":", "") + "/state";
    }

    public static String getMacAddressByTopic(String topic) {
        if (!TextUtils.isEmpty(topic)) {
            return parseBssid2Mac(topic.substring(PREFIX.length(), 12 + PREFIX.length()));
        }
        return "";
    }

    /**
     * 解析收到的数据
     */
    public static void parseData(final String macaddress, final DockerDataBean dockerDataBean, final Map<String, ConnectorListener> connectorListenerMap) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (connectorListenerMap.containsKey(macaddress)) {

                    if (!TextUtils.isEmpty(dockerDataBean.getRawTemp())) {
                        //当前温度
                        connectorListenerMap.get(macaddress).getDataListener().receiveCurrentTemp(BleUtils.parseMqttTemp(dockerDataBean));
                    }

                    connectorListenerMap.get(macaddress).getDataListener().receiveBattery(dockerDataBean.getBattery());
                    connectorListenerMap.get(macaddress).getDataListener().receiveCharge(dockerDataBean.isCharge());
                    connectorListenerMap.get(macaddress).getDataListener().receivePackageNumber(dockerDataBean.getPackageNumber());
                    connectorListenerMap.get(macaddress).getDataListener().receiveBleAndWifiRssi(dockerDataBean.getBleRssi(), dockerDataBean.getWifiRssi());

                    if (!TextUtils.isEmpty(dockerDataBean.getHardVersion())) {
                        //硬件版本
                        connectorListenerMap.get(macaddress).getDataListener().receiveHardVersion(dockerDataBean.getHardVersion());
                    }
                }
            }
        });
    }

    /**
     * check whether the network is connected
     *
     * @param context conetxt
     * @return true is connected , false is not
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * check whether the list is empty
     *
     * @param list list
     * @return true is empty , false is not
     */
    public static boolean listIsEmpty(List list) {
        return !(list != null && !list.isEmpty());
    }

    public static String parseBssid2Mac(String bssid) {
        StringBuilder macbuilder = new StringBuilder();
        for (int i = 0; i < bssid.length() / 2; i++) {
            macbuilder.append(bssid, i * 2, i * 2 + 2).append(":");
        }
        macbuilder.delete(macbuilder.length() - 1, macbuilder.length());
        return macbuilder.toString();
    }

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

    public static JSONObject getJOSNObj(String jsonStr) {
        try {
            return new JSONObject(jsonStr);
        } catch (JSONException e) {
            Logger.w(e.toString());
        }
        return null;
    }

    public static DockerDataBean parseDockerData(String json) {
        return JSON.parseObject(json, DockerDataBean.class);
    }

    public static boolean getJSONBoolean(String json, String key) {
        try {
            JSONObject object = new JSONObject(json);
            return object.getBoolean(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }
}

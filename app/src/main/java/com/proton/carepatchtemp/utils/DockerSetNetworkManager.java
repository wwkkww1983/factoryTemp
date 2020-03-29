package com.proton.carepatchtemp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.wms.logger.Logger;

import java.util.List;

/**
 * Created by wangmengsi on 2018/2/28.
 * 充电器配网
 */
public class DockerSetNetworkManager {
    private static DockerSetNetworkManager mInstance = new DockerSetNetworkManager();
    private static Context mContext;
    private EsptouchTask mEsptouchTask;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private DockerSetNetworkManager() {
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static DockerSetNetworkManager getInstance() {
        if (mContext == null) {
            throw new IllegalStateException("You should initialize TempConnectorManager before using,You can initialize in your Application class");
        }
        return mInstance;
    }

    public void start(String ssid, String password, OnSetNetworkListener listener) {
        mHandler.post(() -> {
            if (listener != null) {
                listener.onStart();
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    String apSsid = Utils.getWifiConnectedSsidAscii(mContext, ssid);
                    String apBssid = Utils.getWifiConnectedBssid(mContext);
                    mEsptouchTask = new EsptouchTask(apSsid, apBssid, password, null, mContext);
                    mEsptouchTask.setEsptouchListener(result -> Logger.w("add result..."));
                    List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(1);
                    if (resultList == null || resultList.size() <= 0) return;
                    IEsptouchResult firstResult = resultList.get(0);
                    if (!firstResult.isCancelled()) {
                        int count = 0;
                        final int maxDisplayCount = 5;

                        if (firstResult.isSuc()) {
                            StringBuilder sb = new StringBuilder();
                            for (IEsptouchResult resultInList : resultList) {
                                sb.append("Esptouch success, bssid = ").append(com.proton.temp.connector.utils.Utils.parseBssid2Mac(resultInList.getBssid())).append(",InetAddress = ").append(resultInList.getInetAddress().getHostAddress()).append("\n");
                                count++;
                                if (count >= maxDisplayCount) {
                                    break;
                                }

                                mHandler.post(() -> {
                                    if (listener != null) {
                                        listener.onSuccess(com.proton.temp.connector.utils.Utils.parseBssid2Mac(resultInList.getBssid()).toUpperCase(), resultInList.getBssid());
                                    }
                                });
                                Logger.w("配网成功:" + sb.toString());
                            }
                            if (count < resultList.size()) {
                                mHandler.post(() -> {
                                    if (listener != null) {
                                        listener.onNotFound();
                                    }
                                });
                            }
                        } else {
                            Logger.w("配网失败");
                            mHandler.post(() -> {
                                if (listener != null) {
                                    listener.onFail();
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }

    public void stop() {
        if (mEsptouchTask != null) {
            mEsptouchTask.interrupt();
        }
    }

    public interface OnSetNetworkListener {
        /**
         * 开始配网
         */
        void onStart();

        /**
         * 配网成功
         */
        void onSuccess(String macaddress, String bssid);

        /**
         * 配网失败
         */
        void onFail();

        /**
         * 没搜索到设备
         */
        void onNotFound();
    }
}

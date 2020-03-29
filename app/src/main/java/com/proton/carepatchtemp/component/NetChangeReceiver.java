package com.proton.carepatchtemp.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.wms.logger.Logger;

/**
 * Created by wangmengsi on 2017/11/24.
 */
public class NetChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                ConnectivityManager manager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                if (manager == null) return;
                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.NET_CHANGE));
                if (activeNetwork == null) return;
                if (activeNetwork.isConnected()) {
                    Logger.i("网络连接成功，上传离线报告");
                    IntentUtils.startOffLineReportUploadService(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

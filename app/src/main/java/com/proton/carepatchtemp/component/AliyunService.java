package com.proton.carepatchtemp.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.proton.carepatchtemp.net.center.MeasureReportCenter;
import com.wms.logger.Logger;

/**
 * Created by wangmengsi on 2017/11/24.
 */

public class AliyunService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.w("阿里云服务开启成功");
        new Thread(() -> {
            while (true) {
                MeasureReportCenter.getAliyunToken();
                try {
                    Thread.sleep(10 * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.w("阿里云服务销毁");
    }
}

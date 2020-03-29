package com.proton.carepatchtemp.utils;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by MoonlightSW on 2017/1/12.
 */

public class NotificationUtils {

    public static void initNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) return;
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup("a", "a"));
            NotificationChannel channel = new NotificationChannel("null",
                    "Channel1", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 创建通知，  * 请在调用此方法时开启子线程
     *
     * @param context    上下文
     * @param tickerText 通知未拉开的内容
     * @param title      通知标题
     * @param content    通知主内容
     * @param intent     意图
     */
    public static void createNotification(Context context, String tickerText, String title, String content, Intent intent, int id) {
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, "null")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setTicker(tickerText)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        if (mNotifyMgr != null) {
            mNotifyMgr.notify(id, mBuilder.build());
        }
    }

    //删除通知
    public static void clearNotification(int ntfCode) {
        NotificationManager notificationManager = (NotificationManager)
                App.get().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(ntfCode);
    }

}

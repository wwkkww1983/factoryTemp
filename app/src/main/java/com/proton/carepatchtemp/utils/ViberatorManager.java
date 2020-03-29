package com.proton.carepatchtemp.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * Created by wangmengsi on 2018/3/28.
 * 震动工具类
 */
public class ViberatorManager {
    private static ViberatorManager mInstance = new ViberatorManager();
    private static Context mContext;
    private static Vibrator vibrator;

    private ViberatorManager() {
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static ViberatorManager getInstance() {
        if (mContext == null) {
            throw new IllegalStateException("You should initialize ViberatorManager before using,You can initialize in your Application class");
        }
        if (vibrator == null) {
            vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        }
        return mInstance;
    }

    public void vibrate(long[] patter) {
        vibrator.vibrate(patter, 0);
    }

    public void vibrate() {
        vibrate(new long[]{200, 500, 200, 500});
    }

    public void cancel() {
        vibrator.cancel();
    }
}

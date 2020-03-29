package com.proton.carepatchtemp.utils;

import android.os.Handler;
import android.os.Looper;

import com.proton.carepatchtemp.component.App;

import java.util.concurrent.ExecutorService;

public class ThreadUtils {

    public static final long DELAY_CHART_ECG = 50;

    public static void runOnOtherThread(Runnable runnable) {
        final ExecutorService cachePool = App.get().getCachePool();
        cachePool.execute(runnable);
    }

    public static void runOnOtherThread(final Runnable runnable, long delay) {
        final ExecutorService cachePool = App.get().getCachePool();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> cachePool.execute(runnable), delay);
    }


}

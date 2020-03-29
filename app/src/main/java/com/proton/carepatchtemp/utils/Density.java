package com.proton.carepatchtemp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import com.wms.logger.Logger;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class Density {

    private static float appDensity;
    private static float appScaledDensity;
    private static DisplayMetrics appDisplayMetrics;
    private static int barHeight;
    private static float designWidth, designHeight;

    public static void setDensity(@NonNull Application application, int designWidth, int designHeight) {
        //获取application的DisplayMetrics
        appDisplayMetrics = application.getResources().getDisplayMetrics();
        //获取状态栏高度
        barHeight = getStatusBarHeight(application);
        Density.designWidth = designWidth;
        Density.designHeight = designHeight;

        if (appDensity == 0) {
            //初始化的时候赋值
            appDensity = appDisplayMetrics.density;
            appScaledDensity = appDisplayMetrics.scaledDensity;

            //添加字体变化的监听
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    //字体改变后,将appScaledDensity重新赋值
                    if (newConfig != null && newConfig.fontScale > 0) {
                        appScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {
                }
            });
        }
    }

    //此方法在BaseActivity中做初始化(如果不封装BaseActivity的话,直接用下面那个方法就好)
    public static void setDefault(Activity activity) {
        setAppOrientation(activity, Orientation.WIDTH);
    }

    //此方法用于在某一个Activity里面更改适配的方向
    public static void setOrientation(Activity activity, Orientation orientation) {
        setAppOrientation(activity, orientation);
    }

    /**
     * targetDensity
     * targetScaledDensity
     * targetDensityDpi
     * 这三个参数是统一修改过后的值
     * <p>
     * orientation:方向值,传入width或height
     */
    private static void setAppOrientation(@Nullable Activity activity, Orientation orientation) {

        if (activity == null || orientation == null) return;
        float targetDensity;

        if (activity.getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT) {
            if (orientation == Orientation.HEIGHT) {
                targetDensity = (appDisplayMetrics.heightPixels - barHeight) / designHeight;
            } else {
                targetDensity = appDisplayMetrics.widthPixels / designWidth;
            }
        } else {
            if (orientation == Orientation.HEIGHT) {
                targetDensity = (appDisplayMetrics.widthPixels - barHeight) / designHeight;
            } else {
                targetDensity = appDisplayMetrics.heightPixels / designWidth;
            }
        }

        Logger.w("dpiW:", appDisplayMetrics.widthPixels / designWidth
                , ",dpiH:", appDisplayMetrics.heightPixels / designHeight, ",dpi:", appDensity);

        float targetScaledDensity = targetDensity * (appScaledDensity / appDensity);
        int targetDensityDpi = (int) (160 * targetDensity);

        DisplayMetrics activityDisplayMetrics = activity.getResources().getDisplayMetrics();
        activityDisplayMetrics.density = targetDensity;
        activityDisplayMetrics.scaledDensity = targetScaledDensity;
        activityDisplayMetrics.densityDpi = targetDensityDpi;
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public enum Orientation {
        WIDTH, HEIGHT
    }
}
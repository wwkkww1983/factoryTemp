package com.proton.carepatchtemp.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 王梦思 on 2017/12/6.
 */

public class ActivityManager {

    /**
     * 维护Activity 的list
     */
    private static List<Activity> mActivitys = Collections.synchronizedList(new LinkedList<Activity>());
    private static List<AppStatusListener> appStatusListeners = new ArrayList<>(1);
    private static int onResultConut;

    public static void registerActivityListener(Application application) {
        registerActivityListener(application, null);
    }

    public static void registerActivityListener(Application application, final AppStatusListener statusListener) {
        addAppStatusListener(statusListener);
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //监听到 Activity创建事件 将该 Activity 加入list
                //只加入该应用的activity
                if (activity instanceof BaseActivity) {
                    pushActivity(activity);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {
                onResultConut++;
                if (onResultConut == 1) {
                    for (AppStatusListener listener : appStatusListeners) {
                        if (listener != null) {
                            listener.isAppBackground(false);
                        }
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                onResultConut--;
                if (onResultConut <= 0) {
                    for (AppStatusListener listener : appStatusListeners) {
                        if (listener != null) {
                            listener.isAppBackground(true);
                        }
                    }
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                if (null == mActivitys || mActivitys.isEmpty()) {
                    return;
                }
                if (mActivitys.contains(activity)) {
                    //监听到 Activity销毁事件 将该Activity 从list中移除
                    popActivity(activity);
                }
            }
        });
    }

    public static void addAppStatusListener(AppStatusListener listener) {
        if (listener == null) return;
        if (appStatusListeners == null) {
            appStatusListeners = new ArrayList<>();
        }
        if (!appStatusListeners.contains(listener)) {
            appStatusListeners.add(listener);
        }
    }

    public static void removeAppStatusListener(AppStatusListener listener) {
        if (listener != null) {
            appStatusListeners.remove(listener);
        }
    }

    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    public static void pushActivity(Activity activity) {
        mActivitys.add(activity);
        Logger.w("activity = " + activity.getClass().getSimpleName()
                + ",size = " + mActivitys.size()
                + ",topactivity:" + currentActivity().getClass().getSimpleName());
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    public static void popActivity(Activity activity) {
        if (!CommonUtils.listIsEmpty(mActivitys)) {
            mActivitys.remove(activity);
        }
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     * 去掉正在finish的activity，防止一些奔溃问题
     */
    public static Activity currentActivity() {
        for (int i = mActivitys.size() - 1; i >= 0; i--) {
            if (!mActivitys.get(i).isFinishing()) {
                return mActivitys.get(i);
            }
        }
        return null;
    }

    /**
     * 获取根activity
     */
    public static Activity getRootActivity() {
        if (!CommonUtils.listIsEmpty(mActivitys)) {
            return mActivitys.get(0);
        }
        return null;
    }

    /**
     * 结束所有activity除了根activity
     */
    public static void finishAllExcept(Class rootActivity) {
        if (mActivitys == null || rootActivity == null) {
            return;
        }
        Iterator<Activity> iterator = mActivitys.iterator();
        Activity activity;
        while (iterator.hasNext()) {
            activity = iterator.next();
            if (!activity.getClass().getName().equals(rootActivity.getName())) {
                activity.finish();
                iterator.remove();
            }
        }
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        if (!CommonUtils.listIsEmpty(mActivitys)) {
            Activity activity = mActivitys.get(mActivitys.size() - 1);
            finishActivity(activity);
        }
    }

    /**
     * 结束指定的Activity
     */
    public static void finishActivity(Activity activity) {
        if (activity != null) {
            mActivitys.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public static void finishActivity(Class<?> cls) {
        if (mActivitys == null || mActivitys.isEmpty()) {
            return;
        }
        Iterator<Activity> iterable = mActivitys.iterator();
        Activity activity;
        while (iterable.hasNext()) {
            activity = iterable.next();
            if (activity.getClass().getSimpleName().equals(cls.getSimpleName())) {
                activity.finish();
                iterable.remove();
            }
        }
    }

    public static void finishOthersActivity(Class<?> cls) {
        if (mActivitys != null && mActivitys.size() > 0) {
            Iterator<Activity> iterator = new ArrayList<>(mActivitys).iterator();
            Activity activity;
            while (iterator.hasNext()) {
                activity = iterator.next();
                if (!activity.getClass().getName().equalsIgnoreCase(cls.getName())) {
                    activity.finish();
                    iterator.remove();
                }
            }
        }
    }

    public static boolean hasActivity(Class<?> clazz) {
        for (int i = 0; i < mActivitys.size(); i++) {
            if (mActivitys.get(i).getClass().getName().equalsIgnoreCase(clazz.getName())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 按照指定类名找到activity
     */
    public static <T extends Activity> T findActivity(Class<T> cls) {
        Activity targetActivity = null;
        if (mActivitys != null) {
            for (Activity activity : mActivitys) {
                if (activity.getClass().equals(cls)) {
                    targetActivity = activity;
                    break;
                }
            }
        }
        return (T) targetActivity;
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        if (mActivitys == null) {
            return;
        }
        for (Activity activity : mActivitys) {
            activity.finish();
        }
        mActivitys.clear();
    }

    /**
     * 退出应用程序
     */
    public static void appExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 只保留哪些activity
     */
    public static void keepActivity(Class<?>... cls) {
        List<Class> keepClazzs = new ArrayList<>();
        Collections.addAll(keepClazzs, cls);

        List<Class> allClazzs = new ArrayList<>();
        for (Activity activity : mActivitys) {
            allClazzs.add(activity.getClass());
        }

        allClazzs.removeAll(keepClazzs);

        Iterator<Activity> iterator = mActivitys.iterator();
        Activity activity;
        while (iterator.hasNext()) {
            activity = iterator.next();
            for (Class clazz : allClazzs) {
                if (clazz.getSimpleName().equalsIgnoreCase(activity.getClass().getSimpleName())) {
                    activity.finish();
                    iterator.remove();
                }
            }
        }
    }

    /**
     * @return 作用说明 ：获取当前最顶部activity的实例
     */
    public static Activity getTopActivity() {
        Activity mBaseActivity;
        synchronized (mActivitys) {
            final int size = mActivitys.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = mActivitys.get(size);
        }
        return mBaseActivity;

    }

    /**
     * @return 作用说明 ：获取当前最顶部的acitivity 名字
     */
    public static String getTopActivityName() {
        Activity mBaseActivity;
        final int size = mActivitys.size() - 1;
        if (size < 0) {
            return null;
        }
        mBaseActivity = mActivitys.get(size);
        return mBaseActivity.getClass().getName();
    }

    /**
     * app是否处于后台
     */
    public static boolean isAppBackground() {
        return onResultConut <= 0;
    }

    public static int size() {
        if (CommonUtils.listIsEmpty(mActivitys)) return 0;
        return mActivitys.size();
    }
}

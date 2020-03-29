package com.proton.carepatchtemp.component;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Keep;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixApplication;
import com.taobao.sophix.SophixEntry;
import com.taobao.sophix.SophixManager;

public class SophixStubApplication extends SophixApplication {
    private static final String TAG = "SophixStubApplication";
    public static boolean needRestart;
    private static SophixStubApplication mInstance;

    public static SophixStubApplication getInstance() {
        return mInstance;
    }

    /**
     * get version code
     *
     * @param context context
     * @return version code
     */
    public static String getAppVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * get app version code
     */
    public static String getAppVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode + "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        initHotFix();
    }

    private void initHotFix() {
        SophixManager.getInstance().setContext(this)
                .setAppVersion(getAppVersion(this) + "." + getAppVersionCode(this))
                .setEnableDebug(true)
                .setPatchLoadStatusStub((mode, code, info, handlePatchVersion) -> {
                    // 补丁加载回调通知
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 表明补丁加载成功
                        Log.e(TAG, "load success:" + handlePatchVersion);
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                        // 建议: 用户可以监听进入后台事件, 然后应用自杀
                        Log.e(TAG, "load success restart");
                        needRestart = true;
                    } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                        // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                        Log.e(TAG, "load fail");
                        SophixManager.getInstance().cleanPatches();
                    } else {
                        // 其它错误信息, 查看PatchStatus类说明
                        Log.e(TAG, "load fail---code = " + code);
                    }
                }).initialize();
    }

    /**
     * 此处SophixEntry应指定真正的Application，并且保证RealApplicationStub类名不被混淆。
     */
    @Keep
    @SophixEntry(App.class)
    static class RealApplicationStub {
    }
}
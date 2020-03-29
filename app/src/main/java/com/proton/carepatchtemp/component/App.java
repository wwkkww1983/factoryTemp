package com.proton.carepatchtemp.component;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.TextUtils;

import com.aliyun.sls.android.sdk.SLSDatabaseManager;
import com.baidu.mobstat.StatService;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.AliyunToken;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.database.ProfileManager;
import com.proton.carepatchtemp.enums.InstructionConstant;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.socailauth.PlatformConfig;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.BuglyUtils;
import com.proton.carepatchtemp.utils.ClassicsFooter;
import com.proton.carepatchtemp.utils.ClassicsHeader;
import com.proton.carepatchtemp.utils.Constants;
import com.proton.carepatchtemp.utils.Density;
import com.proton.carepatchtemp.utils.DockerSetNetworkManager;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.ImagePipelineConfigFactory;
import com.proton.carepatchtemp.utils.MQTTShareManager;
import com.proton.carepatchtemp.utils.NotificationUtils;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.UmengUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.utils.ViberatorManager;
import com.proton.carepatchtemp.utils.net.OSSUtils;
import com.proton.temp.connector.TempConnectorManager;
import com.proton.temp.connector.bean.MQTTConfig;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.taobao.sophix.SophixManager;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import blufi.espressif.apputil.utils.BlufiApp;

/**
 * Created by wangmengsi on 2018/2/26.b
 */

@Keep
public class App extends BlufiApp {

    private static App mInstance;
    public AliyunToken aliyunToken;//阿里云token
    public ExecutorService mCachePool = null;
    //    public IWXAPI wxApi;
    public boolean hasShowBackgroundTip;
    private String version;
    private String systemInfo;
    private long outTime = 0;
    /**
     * 是否已经显示gps警告
     */
    private boolean hasShowGpsWarm;
    /**
     * 上次扫描的设备的id
     */
    private String lastScanDeviceId;
    /**
     * 是否扫描了二维码
     */
    private List<Long> hasScanQRCode = new ArrayList<>();

    /**
     * 定义算法时长，及姿势（2 3 4 ）的占比
     */
    private long duration = 600;

    /**
     * 算法姿势（2 3 4 ）在duration内占比阈值
     */
    private float percentage = 70;

    /**
     * 第三方登录或者验证码登录的时候需要判断是否新用户,密码登录可肯定是老用户
     */
    private boolean isNewUser = false;

    /**
     * 测量页面的指令，默认是aa
     */
    private InstructionConstant instructionConstant = InstructionConstant.aa;

    /**
     * 当前Acitity个数
     */
    private int activityAount = 0;
    /**
     * app是否在前台
     */
    private boolean isForeground;

    public static App get() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
        UmengUtils.initUmeng();
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private void init() {
        String processName = Utils.getProcessName(this);
        Logger.w("进程名称:" + processName + ",packageName = " + getPackageName());
        if (processName != null && !processName.equals(getPackageName())) {
            return;
        }
        ActivityManager.registerActivityListener(this);
        //Fresco初始化
        if (!Fresco.hasBeenInitialized()) {
            Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
        }
        Density.setDensity(this, 375, 667);
        NotificationUtils.initNotificationChannel(this);
        //初始化日志
        Logger.newBuilder()
                .tag("proton_temp")
                .showThreadInfo(false)
                .methodCount(1)
                .saveLogCount(7)
                .context(this)
                .deleteOnLaunch(false)
                .saveFile(BuildConfig.DEBUG)
                .isDebug(BuildConfig.DEBUG)
                .build();
        //连接器初始化
        TempConnectorManager.init(this, new MQTTConfig(BuildConfig.MQTT_SERVER, BuildConfig.MQTT_USERNAME, BuildConfig.MQTT_PWD));
        //下拉刷新初始化
        initRefresh();
        //数据库初始化
        LitePal.initialize(this);
        //阿里云token初始化
        OSSUtils.initOss();
        ViberatorManager.init(this);
        //注册微信
//        wxApi = WXAPIFactory.createWXAPI(App.get(), AppConfigs.WX_APP_ID, true);
//        wxApi.registerApp(AppConfigs.WX_APP_ID);
        // 初始化微信  将该app注册到微信
        PlatformConfig.setWeixin(AppConfigs.WX_APP_ID);

        DockerSetNetworkManager.init(this);
        MQTTShareManager.init(this);
        BuglyUtils.init(this);
        //百度统计
        if (!BuildConfig.IS_INTERNAL) {
            StatService.setAppKey("f5f6af6091");
        }
//        else {
//            StatService.setAppKey("0cd528d30d");
//        }
        StatService.setAppChannel(this, "新版", true);
        StatService.setDebugOn(BuildConfig.DEBUG);
//        if (Utils.isMyTestPhone()) {
//            LeakCanary.install(this);
//        }
        ZXingLibrary.initDisplayOpinion(this);
        Logger.w("手机型号:", Build.MANUFACTURER, ":", Build.MODEL, "当前版本:", getVersion());

//        SLSDatabaseManager.getInstance().setupDB(getApplicationContext());
    }

    public void initRefresh() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater((context, layout) -> new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate));
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater((context, layout) -> new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate));
    }

    public String getApiUid() {
        return SpUtils.getString(Constants.APIUID, "");
    }

    public String getToken() {
        return SpUtils.getString(Constants.APITOKEN, "");
    }

    public String getVersion() {
        if (TextUtils.isEmpty(version)) {
            version = CommonUtils.getAppVersion(this) + "&" + CommonUtils.getAppVersionCode(this);
        }
        return version;
    }

    public int getVersionCode() {
        int appVersionCode;
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            appVersionCode = info.versionCode; //版本名
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
        return appVersionCode;
    }

    public ExecutorService getCachePool() {
        if (null == mCachePool) {
            mCachePool = Executors.newCachedThreadPool();
        }
        return mCachePool;
    }

    public String getSystemInfo() {
        if (TextUtils.isEmpty(systemInfo)) {
            systemInfo = android.os.Build.MODEL + "&" + android.os.Build.VERSION.RELEASE;
        }
        return systemInfo;
    }

    public void logout() {
        clearCache();
        if (!BuildConfig.DEBUG) {
            SophixManager.getInstance().queryAndLoadNewPatch();
        }
        //停用服务
        stopService(new Intent(this, AliyunService.class));
        //关闭共享
        MQTTShareManager.getInstance().close();
        //断开所有连接
        TempConnectorManager.close();
        //取消震动，防止一些意外情况导致震动一直存在
        Utils.cancelVibrateAndSound();
        ActivityManager.appExit();
        restartApplication();
    }

    /**
     * 清除缓存
     */
    private void clearCache() {
        ProfileManager.deleteAll();
        SpUtils.saveString(Constants.APITOKEN, "");
        SpUtils.saveString(Constants.APIUID, "");
        SpUtils.saveString(Constants.ACCOUNT, "");
        //绑定设备类型清空
        SpUtils.saveString(AppConfigs.SP_KEY_EXPERIENCE_BIND_DEVICE, "");
        hasScanQRCode.clear();
        hasShowBackgroundTip = false;
    }

    public void restartApplication() {
        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void showLoginOut() {
        if (System.currentTimeMillis() - outTime > 5 * 1000) {
            outTime = System.currentTimeMillis();
            BlackToast.show(R.string.string_please_login_again);
        }
    }

    /**
     * 是否已经登录
     */
    public boolean isLogined() {
        return !TextUtils.isEmpty(getToken()) && !TextUtils.isEmpty(getApiUid());
    }

    public boolean hasShowGpsWarm() {
        return hasShowGpsWarm;
    }

    public void setHasShowGpsWarm(boolean hasShowGpsWarm) {
        this.hasShowGpsWarm = hasShowGpsWarm;
    }

    public String getLastScanDeviceId() {
        return lastScanDeviceId;
    }

    public void setLastScanDeviceId(String lastScanDeviceId) {
        this.lastScanDeviceId = lastScanDeviceId;
    }

    public InstructionConstant getInstructionConstant() {
        Logger.w("指令 is : ", instructionConstant.getInstruction());
        return instructionConstant;
    }

    public void setInstructionConstant(InstructionConstant instructionConstant) {
        this.instructionConstant = instructionConstant;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public List<Long> getHasScanQRCode() {
        return hasScanQRCode;
    }

    public void kickOff() {
        showLoginOut();
        logout();
    }

    public boolean isNewUser() {
        return isNewUser;
    }

//    public boolean isNewUser() {
//        return true;
//    }

    public void setNewUser(boolean newUser) {
        isNewUser = newUser;
    }

    /**
     * 退出应用时销毁所有的Activity
     */
    public static void clear() {
        try {
            ActivityManager.finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.exit(0);
        }
    }

    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (activityAount == 0) {
                //app回到前台
                isForeground = true;
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.APP_ISFOREGROUND, isForeground));
            }
            activityAount++;
            Logger.w("app isForeground : ", isForeground, " ,activityAount : ", activityAount);
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityAount--;
            if (activityAount == 0) {
                //app切换到后台
                isForeground = false;
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.APP_ISFOREGROUND, isForeground));
            }
            Logger.w("app isForeground : ", isForeground, " ,activityAount : ", activityAount);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    public boolean isForeground() {
        return isForeground;
    }

}

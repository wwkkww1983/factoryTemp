package com.proton.carepatchtemp.constant;

import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.component.App;

/**
 * Created by luochune on 2018/3/15.
 * 经常变动的配置
 */
public class AppConfigs {
    public static final int SP_VALUE_TEMP_C = 1;//使用c温度单位
    public static final int SP_VALUE_TEMP_F = 2;//使用f温度单位
    public static final String SP_KEY_TEMP_UNIT = "tempUnit";
    public static final String QQ_GROUP = "0Xt2-CdDomqPGIE6fYr_IzYo5aqe0fDJ";
    public static final String SHARED_PREFERENCE_TOKEN = "token";
    public static final String SP_KEY_NOTIFY_DURATION = "timeDur";//提醒时间间隔
    public static final String SP_KEY_VIBRATOR = "is_open_vibrator";//震动开关
    public static final String SP_KEY_HIGH_TEMP_WARNING = "high_temp_low_temp_warningwarning";//是否需要高温提醒
    public static final String SP_KEY_LOW_TEMP_WARNING = "";//是否需要低温提醒
    public static final String SP_KEY_LOW_POWER = "device_lowPower_warning";//是否需要低电量提醒
    public static final String SP_KEY_CONNECT_INTERRUPT = "device_connect_interrupt";//是否需要设备连接中断提醒
    public static final String SP_KEY_IS_FIRST_INSTALL = "first_install";//是否首次安装app
    public static final String WX_APP_ID = "wxa6133c49581203d1"; //微信APP_ID
    public static final String DEFAULT_AVATOR_URL = "https://vdpics.oss-cn-hangzhou.aliyuncs.com/default/vcare-default.png";
    //  温度读取时间间隔(单位: 毫秒)
    public static final long TEMP_LOAD_TIME_DIV_MISECONDS = 4000;
    /**
     * 体验模式绑定的设备
     */
    public static final String SP_KEY_EXPERIENCE_BIND_DEVICE = "SP_KEY_EXPERIENCE_BIND_DEVICE";
    public static final String SP_KEY_SHOW_GUIDE = "SP_KEY_SHOW_GUIDE";
    public static final String SP_KEY_SHOW_TUTORIAL = "SP_KEY_SHOW_TUTORIAL";
    /**
     * 是否国际版本
     */
    public static boolean isInternal = BuildConfig.IS_INTERNAL;
    public static final int TEMP_UNIT_DEFAULT = getTempUnit();

    public static int getTempUnit() {
        if (AppConfigs.isInternal) {
            return SP_VALUE_TEMP_F;
        } else {
            return SP_VALUE_TEMP_C;
        }
    }

    public static String getSpKeyVibrator() {
        return SP_KEY_VIBRATOR + ":" + App.get().getApiUid();
    }

    public static String getSpKeyHighTempWarning() {
        return SP_KEY_HIGH_TEMP_WARNING + ":" + App.get().getApiUid();
    }

    public static String getSpKeyLowTempWarning() {
        return SP_KEY_LOW_TEMP_WARNING + ":" + App.get().getApiUid();
    }

    public static String getSpKeyLowPower() {
        return SP_KEY_LOW_POWER + ":" + App.get().getApiUid();
    }

    public static String getSpKeyConnectInterrupt() {
        return SP_KEY_CONNECT_INTERRUPT + ":" + App.get().getApiUid();
    }

}

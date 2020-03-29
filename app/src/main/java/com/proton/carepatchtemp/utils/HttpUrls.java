package com.proton.carepatchtemp.utils;

import com.proton.carepatchtemp.BuildConfig;

/**
 * Created by luochune on 2018/3/15.
 */

public class HttpUrls {
    public static final String URL_SCAN_HELP = BuildConfig.IS_INTERNAL ? "http://www.protontek.com/app/helpEN/instruction.html" : "http://www.protontek.com/vcare/guide/guide.html";
    /**
     * 隐私
     */
    public static final String URL_PRIVICY = "file:///android_asset/agreement/privacy.html";
    public static final String URL_AGREEMENT = "file:///android_asset/agreement/service_agreement.html";
    public static String URL_HELP_CENTER_URL = BuildConfig.IS_INTERNAL ? "http://www.protontek.com/app/help/Page/home.html" : "http://www.protontek.com/app/help/Page/home.html";
    public static String URL_CONTACT = "http://www.protontek.com/app/help/Ultimate/customer.html";
    /**
     * 充电器电源指示灯未闪烁帮助页面
     */
    public static String URL_POWER_LIGHT_NOT_SHINING = BuildConfig.IS_INTERNAL ? "http://www.protontek.com/app/helpEN/nolight.html" : "http://www.protontek.com/app/help/Ultimate/nolight.html";
    /**
     * 底座没搜索到
     */
    public static String URL_NO_DEVICE_SEARCH = "http://www.protontek.com/app/help/Ultimate/nodata.html";
    public static String URL_ATTENTION = "http://www.protontek.com/attention/android.html";
}

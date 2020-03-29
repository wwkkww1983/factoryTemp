package com.proton.carepatchtemp.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageUtils {
    public static Context attachBaseContext(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createConfigurationResources(context, language);
        } else {
            applyLanguage(context, language);
            return context;
        }
    }

    /**
     * 7.1.1以下设置语言的方式
     */
    private static void applyLanguage(Context context, String newLanguage) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        switch (newLanguage) {
            case "en_US":
                configuration.locale = Locale.ENGLISH;
                break;
            case "zh_CN":
                configuration.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            default:
                configuration.locale = Locale.ENGLISH;
                break;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }

    /**
     * 7.1.1以上设置语言的方式
     */

    @TargetApi(Build.VERSION_CODES.N)
    private static Context createConfigurationResources(Context context, String language) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        switch (language) {
            case "en_US":
                configuration.setLocale(Locale.ENGLISH);
                break;
            case "zh_CN":
                configuration.setLocale(Locale.SIMPLIFIED_CHINESE);
                break;
            default:
                configuration.setLocale(Locale.ENGLISH);
                break;
        }
        return context.createConfigurationContext(configuration);
    }
}

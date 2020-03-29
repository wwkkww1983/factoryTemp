package com.proton.carepatchtemp.utils.net;

import android.support.annotation.IntDef;

import com.proton.carepatchtemp.BuildConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Api on 2016/9/30.
 */

public class InitOssUtils {

    public static final int DATA_AVATAR = 0;
    public static final int DATA_BEAT = 1;
    public static final int DATA_TEMP = 2;

    private static String getEndPoint() {
        if (BuildConfig.IS_INTERNAL) {
            return "oss-us-west-1.aliyuncs.com";
        } else {
            return "oss-cn-hangzhou.aliyuncs.com";
        }
    }

    @IntDef({DATA_AVATAR, DATA_BEAT, DATA_TEMP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DATA_TYPE {
    }

}

package com.proton.carepatchtemp.socailauth.listener;

import com.proton.carepatchtemp.socailauth.PlatformType;

import java.util.Map;

/**
 * 登录回调
 * Created by yuxiongfeng.
 * Date: 2019/8/22
 */
public interface AuthListener {
    void onComplete(PlatformType platform_type, Map<String, String> map);

    void onError(PlatformType platform_type, String err_msg);

    void onCancel(PlatformType platform_type);
}

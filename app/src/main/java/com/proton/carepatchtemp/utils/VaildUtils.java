package com.proton.carepatchtemp.utils;

import android.text.TextUtils;

/**
 * Created by luochune on 2018/3/19.
 * 校验工具
 */

public class VaildUtils {

    public static boolean valiMobile(String mobiles) {
        /*

         */
        // String telRegex = "[19][0-9]\\d{9}";
        String telRegex = "^1[0-9][0-9]\\d{8}$";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

    public static boolean valiPwd(String pwd) {
        String pwdRegx = "[a-zA-Z\\d]{6,100}";
        return pwd != null && !TextUtils.isEmpty(pwd) && pwd.length() >= 6 && pwd.length() < 100 && pwd.matches(pwdRegx);
    }
}

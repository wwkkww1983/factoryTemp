package com.proton.carepatchtemp.utils;

import android.text.TextUtils;

import com.wms.logger.Logger;

/**
 * 密码校验工具
 * Created by yuxiongfeng.
 * Date: 2019/8/23
 */
public class PwdCheckUtil {

    public static String SPECIAL_CHAR = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    /**
     * @return 包含数字 返回true
     * @brief 检测密码中是否包含数字
     * @param[in] password            密码字符串
     */
    public static boolean checkContainDigit(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int num_count = 0;

        for (int i = 0; i < chPass.length; i++) {
            if (Character.isDigit(chPass[i])) {
                num_count++;
            }
        }
        if (num_count >= 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * @return 包含字母 返回true
     * @brief 检测密码中是否包含字母（不区分大小写）
     * @param[in] password            密码字符串
     */
    public static boolean checkContainCase(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int char_count = 0;

        for (int i = 0; i < chPass.length; i++) {
            if (Character.isLetter(chPass[i])) {
                char_count++;
            }
        }
        if (char_count >= 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * @return 包含小写字母 返回true
     * @brief 检测密码中是否包含小写字母
     * @param[in] password            密码字符串
     */
    public static boolean checkContainLowerCase(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int char_count = 0;

        for (int i = 0; i < chPass.length; i++) {
            if (Character.isLowerCase(chPass[i])) {
                char_count++;
            }
        }
        if (char_count >= 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * @return 包含大写字母 返回true
     * @brief 检测密码中是否包含大写字母
     * @param[in] password            密码字符串
     */
    public static boolean checkContainUpperCase(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int char_count = 0;

        for (int i = 0; i < chPass.length; i++) {
            if (Character.isUpperCase(chPass[i])) {
                char_count++;
            }
        }
        if (char_count >= 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * @return 包含特殊符号 返回true
     * @brief 检测密码中是否包含特殊符号
     * @param[in] password            密码字符串
     */
    public static boolean checkContainSpecialChar(String password) {
        char[] chPass = password.toCharArray();
        boolean flag = false;
        int special_count = 0;

        for (int i = 0; i < chPass.length; i++) {
            if (SPECIAL_CHAR.indexOf(chPass[i]) != -1) {
                special_count++;
            }
        }

        if (special_count >= 1) {
            flag = true;
        }
        return flag;
    }

    /**
     * 字符 字母 数字必须有两种
     *
     * @param pwd
     * @return
     */
    public static boolean checkContainTwo(String pwd) {
        if (TextUtils.isEmpty(pwd) || pwd.trim().length() < 6) {
            return false;
        }
        boolean digit = checkContainDigit(pwd);
        boolean zimu = checkContainCase(pwd);
        boolean zifu = checkContainSpecialChar(pwd);
        Logger.w("密码校验: 是否包含数字 ",digit," ,是否包含字母 ",zimu," ,是否包含字符 ",zifu);

        if (!digit && !zifu && !zimu) {
            return false;
        }

        if (!digit && !zimu) {
            return false;
        }

        if (!digit && !zifu) {
            return false;
        }

        if (!zifu && !zimu) {
            return false;
        }
        return true;
    }

}

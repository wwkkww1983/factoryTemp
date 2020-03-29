package com.proton.carepatchtemp.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * Created by luochune on 2018/3/13.
 */

public class StringUtils {

    /**
     * 比较带v的固件版本大小
     *
     * @return 0: v1和v2相等
     * 1: v1>v2
     * -1: v1<v2
     */
    public static int compareVersion(String version1, String version2) {

        if (TextUtils.isEmpty(version1) || TextUtils.isEmpty(version2)) {
            return 0;
        }

        if (!version1.contains(".") || !version2.contains(".")) {
            return 0;
        }

        if (!version1.contains("V") || !version2.contains("V")) {
            return 0;
        }

        if (version1.equalsIgnoreCase(version2)) {
            return 0;
        }

        version1 = version1.replaceAll("V", "");
        version2 = version2.replaceAll("V", "");

        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");

        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;

        while (index < minLen && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }

        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }

            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    public static String join(List<String> list, String splitStr) {
        StringBuffer resultStrBuffer = new StringBuffer();
        if (list == null || list.size() == 0 || splitStr == null) {
            return resultStrBuffer.toString();
        }
        Iterator iterator = list.iterator();
        //先加入第一个id，防止拼接出来的字符串最后一个字符是分隔符
        if (iterator.hasNext()) {
            resultStrBuffer.append(iterator.next());
        }
        for (; iterator.hasNext(); ) {
            resultStrBuffer.append(splitStr).append(iterator.next());
        }
        return resultStrBuffer.toString();
    }

    /**
     * 提供精确的加法运算。
     *
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 截取某个字符串之前的所有字符
     *
     * @param totalStr 总的字符串
     * @param splitStr 截取标志字符串
     */
    public static String substringBefore(String totalStr, String splitStr) {
        int splitTagIndex = totalStr.indexOf(splitStr);
        return totalStr.substring(0, splitTagIndex);
    }
}

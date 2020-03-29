package com.proton.carepatchtemp.utils;

import android.support.annotation.StringRes;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.widget.TextView;

import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.view.CustomClickableSpan;

public class UIUtils {
    public static float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, App.get().getResources().getDisplayMetrics());
    }

    public static float sp2px(float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, App.get().getResources().getDisplayMetrics());
    }

    public static String getString(@StringRes int stringRes) {
        if (ActivityManager.currentActivity() == null) {
            return App.get().getString(stringRes);
        }
        return ActivityManager.currentActivity().getString(stringRes);
    }

    public static String getQuantityString(int stringRes, int count) {
        if (ActivityManager.currentActivity() == null) {
            return App.get().getResources().getQuantityString(stringRes, count, count);
        }
        return ActivityManager.currentActivity().getResources().getQuantityString(stringRes, count, count);
    }

    /**
     * 根据最高体温值获取最高体温显示数字
     *
     * @param maxTemp 最高体温值(单位 ℃)
     * @return 备注：
     * 显示范围：[25.00℃-45℃]
     * 小于25：<25
     * 大于45: >45
     */
    public static String currMaxTemp(float maxTemp) {
        //摄氏温度的判断范围
        Float[] tempRange = new Float[]{25.00F, 45.00F};
        //单位若是华氏温度则温度范围值需要转换
        if (!Utils.isSelsiusUnit()) {
            tempRange = new Float[]{FormatUtils.c2F(25.00F), FormatUtils.c2F(45.00F)};
        }
        //最大体温值根据实际单位转换
        maxTemp = Utils.getTemp(maxTemp);
        //温度范围内返回温度值
        if (maxTemp >= tempRange[0] && maxTemp <= tempRange[tempRange.length - 1]) {
            return Utils.formatTempToStr(maxTemp);
        } else if (maxTemp < tempRange[0]) {
            //小于显示范围
            return "<" + Utils.formatTempToStr(tempRange[0]);
        } else if (maxTemp > tempRange[tempRange.length - 1]) {
            //高于显示范围
            return ">" + Utils.formatTempToStr(tempRange[tempRange.length - 1]);
        }
        return String.valueOf(maxTemp);
    }

    /**
     * 获取温度范围
     * 正常体温：[36.00℃-37.09℃]
     * 低热：[37.10℃-37.99℃]
     * 中热：[38.00℃-38.99℃]
     * 高热：[39.00℃-42.99℃]
     *
     * @return -1: 非正常体温
     * 0:  正常体温
     * 1: 低热
     * 2: 中热
     * 3: 高热
     */
    public static int tempLevel(float maxTemp) {
        maxTemp = Utils.formatTemp(maxTemp);
        //摄氏温度的判断范围
        Float[] tempRange = new Float[]{36.00F, 37.09F, 37.10F, 37.99F, 38.00F, 38.99F, 39.00F, 42.99F};
        //单位若是华氏温度则温度范围值需要转换
        if (AppConfigs.SP_VALUE_TEMP_F == SpUtils.getInt(AppConfigs.SP_KEY_TEMP_UNIT, AppConfigs.TEMP_UNIT_DEFAULT)) {
            tempRange = new Float[]{FormatUtils.c2F(36.00F), FormatUtils.c2F(37.09F), FormatUtils.c2F(37.10F), FormatUtils.c2F(37.99F), FormatUtils.c2F(38.00F), FormatUtils.c2F(38.99F), FormatUtils.c2F(39.00F), FormatUtils.c2F(42.99F)};
            //最大体温值单位转换
            maxTemp = FormatUtils.c2F(maxTemp);
        }
        if (maxTemp >= tempRange[0] && maxTemp <= tempRange[1]) {
            //正常体温
            return 0;
        } else if (maxTemp >= tempRange[2] && maxTemp <= tempRange[3]) {
            //低热
            return 1;
        } else if (maxTemp >= tempRange[4] && maxTemp <= tempRange[5]) {
            //中热
            return 2;
        } else if (maxTemp >= tempRange[6] && maxTemp <= tempRange[7]) {
            //高热
            return 3;
        } else {
            //非正常体温
            return -1;
        }
    }

    public static void spanStr(TextView textView, String totalStr, String[] clickStr, int highLightColor, boolean isNeedUnderLine, CustomClickableSpan.SpanClickListener onClickListener) {
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(totalStr);
            if (clickStr != null && clickStr.length > 0) {
                //开始截取点击文字的下标位置
                for (int i = 0; i < clickStr.length; i++) {
                    //点击文字
                    String str = clickStr[i];
                    if (TextUtils.isEmpty(str)) continue;
                    //点击文字起始坐标
                    int startIndex = totalStr.indexOf(str);
                    CustomClickableSpan customClickableSpan = new CustomClickableSpan(highLightColor, isNeedUnderLine, i, onClickListener);
                    spannableStringBuilder.setSpan(customClickableSpan, startIndex, startIndex + str.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(spannableStringBuilder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

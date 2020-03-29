package com.proton.carepatchtemp.utils;

import com.proton.carepatchtemp.R;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MoonlightSW on 2016/12/7.
 */

public class FormatUtils {

    private static final long seconds_of_1minute = 60;
    private static final long seconds_of_1hour = 60 * 60;

    /**
     * @return timtPoint距离现在经过的时间，分为
     * 刚刚，1-29分钟前，半小时前，1-23小时前，1-14天前，半个月前，1-5个月前，半年前，1-xxx年前
     */
    public static String getTimeElapse(long createTime) {

        long nowTime = System.currentTimeMillis() / 1000;

        //createTime是发表文章的时间

        long oldTime = createTime / 1000;

        //elapsedTime是发表和现在的间隔时间

        long elapsedTime = nowTime - oldTime;

        Calendar calendar0 = Calendar.getInstance();
        calendar0.setTimeInMillis(System.currentTimeMillis());

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(createTime);

        if (elapsedTime < seconds_of_1minute) {
            return UIUtils.getString(R.string.string_just_now);
        }
        if (elapsedTime < seconds_of_1hour) {
            return elapsedTime / seconds_of_1minute + UIUtils.getString(R.string.string_minutes_ago);
        }
        if (calendar0.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)
                && calendar0.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH)
                && calendar0.get(Calendar.DAY_OF_MONTH) == calendar1.get(Calendar.DAY_OF_MONTH)) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");//初始化Formatter的转换格式。
            return UIUtils.getString(R.string.string_today) + " " + formatter.format(createTime).split(" ")[1];
        }
        if (calendar0.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)
                && calendar0.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH)
                && calendar0.get(Calendar.DAY_OF_MONTH) - calendar1.get(Calendar.DAY_OF_MONTH) == 1) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");//初始化Formatter的转换格式。
            return UIUtils.getString(R.string.string_yestoday) + " " + formatter.format(createTime).split(" ")[1];
        }
        //同一年的只返回几月几号几点几分
        if (calendar0.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR)) {
            return transferLongToDate(UIUtils.getString(R.string.string_mdhm), createTime);
        }
        if (calendar0.get(Calendar.YEAR) != calendar1.get(Calendar.YEAR)) {
            return transferLongToDate(UIUtils.getString(R.string.string_ymdhm), createTime);
        }
        return "";
    }


    /*
     * 毫秒转化天时分秒毫秒
     * 格式1天12小时12分12秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + UIUtils.getString(R.string.string_day));
        }
        if (hour > 0) {
            sb.append(hour + UIUtils.getString(R.string.string_hour));
        }
        if (minute > 0) {
            sb.append(minute + UIUtils.getString(R.string.string_minutes));
        }
        if (second > 0) {
            sb.append(second + UIUtils.getString(R.string.string_seconds));
        }
        return sb.toString();
    }

    /**
     * 把毫秒转化成日期
     *
     * @param dateFormat(日期格式，例如：yyyy年MM月dd日 HH:mm:ss)
     */
    public static String transferLongToDate(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }

    public static String getTime(long time, String format) {
        return new SimpleDateFormat(format).format(time);
    }

    public static String getTimeYMDHM(long time) {
        return getTime(time, UIUtils.getString(R.string.string_ymdhm));
    }

    /**
     * 温度转换C -> F
     */
    public static float c2F(float f) {
        BigDecimal b1 = new BigDecimal(f * 1.8f + 32F);
        return b1.setScale(2, BigDecimal.ROUND_DOWN).floatValue();
    }
}

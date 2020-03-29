package com.proton.carepatchtemp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by luochune on 2017/3/31.
 * Introduction:时间日期工具类
 */

public class DateUtils {
    /**
     * 将时间撮转成时间年月日
     */
    public static String dateStrToYMD(long dateStr) {
        SimpleDateFormat ymdSimpleDate = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return ymdSimpleDate.format(new Date(dateStr));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将时间撮转成时分
     */
    public static String dateStrToTime(long dateStr) {
        SimpleDateFormat ymdSimpleDate = new SimpleDateFormat("HH:mm");
        try {
            return ymdSimpleDate.format(new Date(dateStr));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将时间转换成年月日 时分秒
     */
    public static String dateStrToYMDHMS(long dateStr) {
        SimpleDateFormat ymdSimpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return ymdSimpleDate.format(new Date(dateStr));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将时间转换成年月日 时分
     */
    public static String dateStrToYMDHM(long dateStr) {
        SimpleDateFormat ymdSimpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return ymdSimpleDate.format(new Date(dateStr));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * 字符串的日期格式的计算
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * 获取时间戳的年 、月、日
     *
     * @param currentTime 当前的时间戳
     * @param formatType  yyyy为年，MM为月，dd为日
     */
    public static int longToDate(long currentTime, String formatType) {
        try {
            Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
            String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
            int iDataTime = Integer.parseInt(sDateTime);
            return iDataTime;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}

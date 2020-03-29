package com.proton.temp.algorithm.utils;

import com.proton.temp.algorithm.BuildConfig;
import com.proton.temp.algorithm.bean.TempImg;
import com.proton.temp.algorithm.bean.TempResult;

/**
 * Created by wangmengsi on 2017/7/21.
 * 算法处理
 */

public class AlgorithmHelper {
    static {
        //防止7.0下修复失败
        System.loadLibrary("tempalg1_0");
        System.loadLibrary("tempalg1_5");
        System.loadLibrary("tempalgorithm");
        init(BuildConfig.DEBUG);
    }

    public static native void init(boolean isDebug);

    public static native String getVersion();

    public static native TempImg getTempOld(float[] temps, TempImg tempImg);

    /**
     * 算法处理v1.5版本算法
     *
     * @param temp   实时温度数据信息
     * @param flag   连接的设备数量信息，主要时针对APP这边多设备连接
     * @param state  体温贴测量状态信息，有三个状态，0---开始测量，1—测量中，2—结束测量
     * @param isPill 小孩发烧后，用户反馈的降温处理情况，若isPill = 0,代表没有进行任何处理或是正常温度，isPill = 1,代表进行了物理降温，isPill > 1代表吃药
     * @param sample 体温贴实际的采样频率。嵌入式（暂定，还需要进行更改）
     */
    public static native TempResult getTemp(float temp, long time, int flag, int state, int isPill, int sample, int connectionType);
}

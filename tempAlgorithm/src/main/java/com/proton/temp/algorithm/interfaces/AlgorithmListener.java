package com.proton.temp.algorithm.interfaces;

import com.proton.temp.algorithm.bean.AlgorithmData;

/**
 * Created by wangmengsi on 2018/3/23.
 * 算法处理监听器
 */

public abstract class AlgorithmListener {
    /**
     * 算法处理结果
     */
    public void onComplete(AlgorithmData data) {
    }
}

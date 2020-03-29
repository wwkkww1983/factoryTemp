package com.proton.temp.connector.interfaces;

/**
 * Created by wangmengsi on 2018/3/15.
 * 算法回调
 */
public class AlgorithmStatusListener {
    /**
     * 测量状态int值
     */
    public void receiveMeasureStatus(int status) {
    }

    /**
     * 手势状态值
     */
    public void receiveGesture(int gesture) {
    }

    public void receiveMeasureStatusAndGesture(int status, int gesture) {

    }

    /**
     * 获取算法版本号类型 0：表示本地算法版本号  1：表示底座算法版本号
     *
     * @param type
     */
    public void receiveAlgorithmVersionType(int type) {
    }

}
package com.proton.temp.algorithm.bean;

import java.io.Serializable;

/**
 * Created by wangmengsi on 2017/10/24.
 * /**
 * 算法对每次的实时温度进行处理
 * data:体温贴采集的数据
 * preVal:前一次的算法处理得到的温度
 * predictedTemp:预测的结果
 * startInd:预测开始点；
 * predInd:预测结束点
 * stickCount:贴上的点
 * decline:下降开始点
 * handle：是否需要校准
 * 1.
 * 算法中除data外，传入的值为我上一次算法处理返回的结果，其中preVal为返回的TempImg中的stabT
 * 2.
 * 第一执行时，创建相应的变量传入，变量的值可任意
 * 3.当stickN >= 0时，表示贴上，界面跳转到预测第二个界面
 * 4.当predTemp > 0时，表示预测成功，界面跳转（若三分钟还是没有跳转，将由你们自动设置跳转）
 */

public class TempImg implements Serializable {
    private boolean handle;
    private int startInd = -1;
    /**
     * 是否贴上 stickCount > 0 贴上
     */
    private int stickCount = -1;
    private int decline = -1;
    private int predInd = -1;
    /**
     * 实时温度
     */
    private float currentTemp = -1;
    /**
     * 预测温度
     * predictedTemp > 0时，表示预测成功，界面跳转
     */
    private float predictedTemp = -1;
    private int firstStart = -1;
    /**
     * 0--取下，1--贴上未夹紧，2--首次升温，3--稳定阶段，1.0没有二次升温
     */
    private int status;

    public boolean isHandle() {
        return handle;
    }

    public void setHandle(boolean handle) {
        this.handle = handle;
    }

    public int getStartInd() {
        return startInd;
    }

    public void setStartInd(int startInd) {
        this.startInd = startInd;
    }

    public int getStickCount() {
        return stickCount;
    }

    public void setStickCount(int stickCount) {
        this.stickCount = stickCount;
    }

    public int getDecline() {
        return decline;
    }

    public void setDecline(int decline) {
        this.decline = decline;
    }

    public int getPredInd() {
        return predInd;
    }

    public void setPredInd(int predInd) {
        this.predInd = predInd;
    }

    public float getPredictedTemp() {
        return predictedTemp;
    }

    public void setPredictedTemp(float predictedTemp) {
        this.predictedTemp = predictedTemp;
    }

    public float getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(float currentTemp) {
        this.currentTemp = currentTemp;
    }

    @Override
    public String toString() {
        return "TempImg{" +
                "handle=" + handle +
                ", startInd=" + startInd +
                ", stickCount=" + stickCount +
                ", decline=" + decline +
                ", predInd=" + predInd +
                ", currentTemp=" + currentTemp +
                ", predictedTemp=" + predictedTemp +
                '}';
    }

    public int getFirstStart() {
        return firstStart;
    }

    public void setFirstStart(int firstStart) {
        this.firstStart = firstStart;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

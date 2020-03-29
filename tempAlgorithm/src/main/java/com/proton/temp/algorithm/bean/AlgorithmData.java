package com.proton.temp.algorithm.bean;

/**
 * Created by 王梦思 on 2019-05-31.
 * <p/>
 */
public class AlgorithmData {
    /**
     * 算法处理温度
     */
    private float processTemp;
    /**
     * 测量状态：0取下,1贴上未夹紧,2首次升温（1min倒计时开始),3稳定(倒计时结束),4降到异常温(弹窗,然后2min倒计时)
     */
    private int measureStatus;
    /**
     * 采样频率 单位秒
     */
    private int sample;
    /**
     * 首次升温百分比
     */
    private int percent;
    /**
     * 手势状态
     */
    private int gesture;

    public AlgorithmData() {
    }

    public AlgorithmData(float processTemp, int measureStatus, int sample, int percent, int gesture) {
        this.processTemp = processTemp;
        this.measureStatus = measureStatus;
        this.sample = sample;
        this.percent = percent;
        this.gesture = gesture;
    }

    public float getProcessTemp() {
        return processTemp;
    }

    public void setProcessTemp(float processTemp) {
        this.processTemp = processTemp;
    }

    public int getMeasureStatus() {
        return measureStatus;
    }

    public void setMeasureStatus(int measureStatus) {
        this.measureStatus = measureStatus;
    }

    public int getSample() {
        return sample;
    }

    public void setSample(int sample) {
        this.sample = sample;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getGesture() {
        return gesture;
    }

    public void setGesture(int gesture) {
        this.gesture = gesture;
    }
}

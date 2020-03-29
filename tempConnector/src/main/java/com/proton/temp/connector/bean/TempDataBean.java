package com.proton.temp.connector.bean;

/**
 * Created by wangmengsi on 2018/3/28.
 */

public class TempDataBean {
    /**
     * 当前温度的时间
     */
    private long time;
    /**
     * 温度值
     */
    private float temp;
    /**
     * 算法温度，网络传输专用
     */
    private float algorithmTemp;
    /**
     * 测量阶段，网络传输专用
     */
    private int measureStatus;
    private int gesture = -1;
    /**
     * 算法版本号类型 0：表示本地算法版本号  1：表示底座算法版本号
     */
    private int algorithmVerType;
    /**
     * 当前温度所用的采样
     */
    private int sample;
    /**
     * 首次升温百分比
     */
    private int percent;
    private int packageNumber;

    public TempDataBean() {
    }

    public TempDataBean(long time, float temp) {
        this.time = time;
        this.temp = temp;
    }

    public TempDataBean(float temp, float algorithmTemp, int sample, int packageNumber, int measureStatus, int gesture, int percent) {
        this.temp = temp;
        this.algorithmTemp = algorithmTemp;
        this.sample = sample;
        this.measureStatus = measureStatus;
        this.gesture = gesture;
        this.percent = percent;
        this.packageNumber = packageNumber;
    }

    public TempDataBean(float temp) {
        this.temp = temp;
    }

    public TempDataBean(float temp, int sample) {
        this.temp = temp;
        this.sample = sample;
    }

    public TempDataBean(float temp, int sample, int packageNumber) {
        this.temp = temp;
        this.sample = sample;
        this.packageNumber = packageNumber;
    }

    public TempDataBean(long time, float temp, int sample) {
        this.time = time;
        this.temp = temp;
        this.sample = sample;
    }

    public TempDataBean(long time, float temp, float algorithmTemp) {
        this.time = time;
        this.temp = temp;
        this.algorithmTemp = algorithmTemp;
    }

    public TempDataBean(long time, float temp, float algorithmTemp, int sample) {
        this.time = time;
        this.temp = temp;
        this.algorithmTemp = algorithmTemp;
        this.sample = sample;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public int getSample() {
        return sample;
    }

    public void setSample(int sample) {
        this.sample = sample;
    }

    public float getAlgorithmTemp() {
        return algorithmTemp;
    }

    public void setAlgorithmTemp(float algorithmTemp) {
        this.algorithmTemp = algorithmTemp;
    }

    public int getMeasureStatus() {
        return measureStatus;
    }

    public void setMeasureStatus(int measureStatus) {
        this.measureStatus = measureStatus;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(int packageNumber) {
        this.packageNumber = packageNumber;
    }

    public int getGesture() {
        return gesture;
    }

    public void setGesture(int gesture) {
        this.gesture = gesture;
    }

    public int getAlgorithmVerType() {
        return algorithmVerType;
    }

    public void setAlgorithmVerType(int algorithmVerType) {
        this.algorithmVerType = algorithmVerType;
    }
}

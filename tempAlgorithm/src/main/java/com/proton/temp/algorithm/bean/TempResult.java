package com.proton.temp.algorithm.bean;

/**
 * 1.5版本算法结果
 */
public class TempResult {
    /**
     * 算法处理后温度结果
     */
    private float currentTemp;
    /**
     * 需求的采样频率，如1s/个的频率返回1，5s/个的频率返回5
     */
    private int sample;
    /**
     * 测量状态：0取下,1贴上未夹紧,2首次升温（1min倒计时开始),3稳定(倒计时结束),4降到异常温(弹窗,然后2min倒计时)
     * <p>
     * 2代表贴上转圈 3预测成功
     */
    private int status;
    /**
     * 首次升温百分比
     */
    private int percent;
    private int gesture;

    public float getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(float currentTemp) {
        this.currentTemp = currentTemp;
    }

    public int getSample() {
        return sample;
    }

    public void setSample(int sample) {
        this.sample = sample;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

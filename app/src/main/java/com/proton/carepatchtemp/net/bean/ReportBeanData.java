package com.proton.carepatchtemp.net.bean;

import java.util.List;

/**
 * Created by luochune on 2018/5/10.
 * 报告温度点数据
 */

public class ReportBeanData {

    /**
     * deviceId : 310318
     * endTime : 1525948864292
     * devVersion : V1.0.0
     * max : 0.0
     * maxTmpr : 35.47
     * min : 0.0
     * minTmpr : 30.71
     * floats : [31.42,31.42,31.45,31.49,31.51,31.52,31.52,31.52,31.52,31.52,31.52,31.52,31.52,31.52,31.52,31.52,31.52,31.52,31.12,30.71,30.83,31,31.32,31.65,31.95,32.23,32.46,32.68,32.96,33.25,33.55,33.83,34.09,34.29,34.38,34.48,34.48,34.57,34.59,34.65,34.7,34.75,34.8,34.84,34.88,34.91,34.96,34.98,35,35.04,35.07,35.09,35.12,35.14,35.16,35.18,35.18,35.21,35.23,35.25,35.25,35.28,35.29,35.3,35.32,35.33,35.34,35.34,35.36,35.37,35.38,35.39,35.4,35.4,35.41,35.41,35.43,35.43,35.43,35.45,35.45,35.46,35.46,35.46,35.46,35.46,35.46,35.47,35.47,35.47]
     * creator : 1059
     * name : nana
     * reportId : 44247
     * rawData : [31.42,31.45,31.49,31.51,31.52,30.07,30.05,30.04,30.05,30.05,30.05,30.06,30.06,30.06,30.07,30.17,30.32,30.53,30.71,30.83,31,31.32,31.65,31.95,32.23,32.46,32.68,32.86,33.03,33.18,33.31,33.43,33.54,33.63,33.72,33.79,33.87,33.94,33.99,34.04,34.09,34.14,34.19,34.22,34.26,34.3,34.32,34.35,34.38,34.41,34.43,34.46,34.48,34.5,34.52,34.53,34.55,34.57,34.59,34.6,34.62,34.63,34.64,34.66,34.67,34.68,34.69,34.7,34.71,34.72,34.73,34.74,34.74,34.75,34.76,34.77,34.77,34.78,34.79,34.79,34.8,34.8,34.81,34.82,34.82,34.82,34.83,34.83,34.84,34.84]
     * startTime : 1525948754782
     * times : [1525948754,1525948755,1525948756,1525948757,1525948758,1525948759,1525948760,1525948761,1525948762,1525948763,1525948764,1525948765,1525948766,1525948767,1525948768,1525948769,1525948770,1525948771,1525948772,1525948773,1525948774,1525948775,1525948776,1525948777,1525948778,1525948779,1525948780,1525948781,1525948782,1525948783,1525948784,1525948785,1525948786,1525948787,1525948788,1525948789,1525948790,1525948791,1525948792,1525948793,1525948794,1525948795,1525948796,1525948797,1525948798,1525948799,1525948800,1525948801,1525948802,1525948803,1525948804,1525948805,1525948806,1525948807,1525948808,1525948809,1525948810,1525948811,1525948812,1525948813,1525948814,1525948815,1525948816,1525948817,1525948818,1525948819,1525948820,1525948821,1525948822,1525948823,1525948824,1525948825,1525948826,1525948827,1525948828,1525948829,1525948830,1525948831,1525948832,1525948833,1525948834,1525948835,1525948836,1525948837,1525948838,1525948839,1525948840,1525948841,1525948842,1525948843]
     * baseObjId : 0
     */

    private String deviceId;
    private long endTime;
    private String devVersion;
    private double max;
    private double maxTmpr;
    private double min;
    private double minTmpr;
    private String creator;
    private String name;
    private String reportId;
    private long startTime;
    private int baseObjId;
    private List<Float> floats;
    private List<Double> rawData;
    private List<Long> times;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDevVersion() {
        return devVersion;
    }

    public void setDevVersion(String devVersion) {
        this.devVersion = devVersion;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMaxTmpr() {
        return maxTmpr;
    }

    public void setMaxTmpr(double maxTmpr) {
        this.maxTmpr = maxTmpr;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMinTmpr() {
        return minTmpr;
    }

    public void setMinTmpr(double minTmpr) {
        this.minTmpr = minTmpr;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getBaseObjId() {
        return baseObjId;
    }

    public void setBaseObjId(int baseObjId) {
        this.baseObjId = baseObjId;
    }

    public List<Float> getFloats() {
        return floats;
    }

    public void setFloats(List<Float> floats) {
        this.floats = floats;
    }

    public List<Double> getRawData() {
        return rawData;
    }

    public void setRawData(List<Double> rawData) {
        this.rawData = rawData;
    }

    public List<Long> getTimes() {
        return times;
    }

    public void setTimes(List<Long> times) {
        this.times = times;
    }
}

package com.proton.carepatchtemp.factory.bean;

public class CalibrateDetailBean {

    /**
     * 体温贴mac地址
     */
    private String mac;

    /**
     * 设备序列号
     */
    private String sn;

    /**
     * 设备版本号
     */
    private String version;

    /**
     * 设备类型 2 - 10
     */
    private int type;

    /**
     * 测温点1 ： 0 未检查 1：稳定 2：不稳定
     */
    private int firstStableState;

    /**
     * 测温点1稳定温度，以0.01为基准，如稳定温度为37.13，则firstStableTemp=3713
     */
    private int firstStableTemp;

    /**
     * 测温点1：水槽温度，以0.01为基准，如稳定温度为37.13，则firstSinkTemp=3713
     */
    private int firstSinkTemp;

    /**
     * 第一次测温允许误差
     */
    private int firstAllowableError;

    /**
     * 测温点2:   0 未检查  1 稳定  2 不稳定
     */
    private int secondStableState;
    /**
     * 温度点2稳定温度，以0.01为基准，如稳定温度为37.13，则secondStableTemp=3713
     */
    private int secondStableTemp;
    /**
     * 测温点2 ：水槽温度，以0.01为基准，如稳定温度为37.13，则secondSinkTemp=3713
     */
    private int secondSinkTemp;
    /**
     * 第二次温度允许误差
     */
    private int secondAllowableError;

    /**
     * 校准状态： 0 未校准   1 已校准  2 校准错误
     */
    private int calibrationStatus;

    /**
     * 校准值 ，以0.01为基准，如：+0.02 摄氏度，则校准值为calibration=2
     */
    private int calibration;

    /**
     * 合格状态  0 没有进入合格状态  1 已合格  2 未合格
     */
    private int qualificationStatus;

    /**
     * 报告id
     */
    private String reportId;


    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFirstStableState() {
        return firstStableState;
    }

    public void setFirstStableState(int firstStableState) {
        this.firstStableState = firstStableState;
    }

    public int getFirstStableTemp() {
        return firstStableTemp;
    }

    public void setFirstStableTemp(int firstStableTemp) {
        this.firstStableTemp = firstStableTemp;
    }

    public int getFirstSinkTemp() {
        return firstSinkTemp;
    }

    public void setFirstSinkTemp(int firstSinkTemp) {
        this.firstSinkTemp = firstSinkTemp;
    }

    public int getFirstAllowableError() {
        return firstAllowableError;
    }

    public void setFirstAllowableError(int firstAllowableError) {
        this.firstAllowableError = firstAllowableError;
    }

    public int getSecondStableState() {
        return secondStableState;
    }

    public void setSecondStableState(int secondStableState) {
        this.secondStableState = secondStableState;
    }

    public int getSecondStableTemp() {
        return secondStableTemp;
    }

    public void setSecondStableTemp(int secondStableTemp) {
        this.secondStableTemp = secondStableTemp;
    }

    public int getSecondSinkTemp() {
        return secondSinkTemp;
    }

    public void setSecondSinkTemp(int secondSinkTemp) {
        this.secondSinkTemp = secondSinkTemp;
    }

    public int getSecondAllowableError() {
        return secondAllowableError;
    }

    public void setSecondAllowableError(int secondAllowableError) {
        this.secondAllowableError = secondAllowableError;
    }

    public int getCalibrationStatus() {
        return calibrationStatus;
    }

    public void setCalibrationStatus(int calibrationStatus) {
        this.calibrationStatus = calibrationStatus;
    }

    public int getCalibration() {
        return calibration;
    }

    public void setCalibration(int calibration) {
        this.calibration = calibration;
    }

    public int getQualificationStatus() {
        return qualificationStatus;
    }

    public void setQualificationStatus(int qualificationStatus) {
        this.qualificationStatus = qualificationStatus;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

}

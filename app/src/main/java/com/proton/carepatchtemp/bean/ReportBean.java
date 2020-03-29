package com.proton.carepatchtemp.bean;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * Created by wangmengsi on 2017/10/17.
 */
public class ReportBean extends LitePalSupport {
    /**
     * 算法数据（显示用）
     */
    @SerializedName("floats")
    private List<Float> processTemps;
    /**
     * 真实数据
     */
    @SerializedName("rawData")
    private List<Float> sourceTemps;
    /**
     * 采样率
     */
    private List<Integer> sample;
    /**
     * 温度对应的时间，单位秒
     */
    @SerializedName("times")
    private List<Long> tempTimes;

    /**
     * 设备版本号
     */
    @SerializedName("devVersion")
    private String hardVersion;
    /**
     * 用户设置的高温报警值
     */
    @SerializedName("max")
    private float maxAlarmTemp;
    /**
     * 用户设置的低温报警值
     */
    @SerializedName("min")
    private float minAlarmTemp;
    /**
     * 测量最大值
     */
    @SerializedName("maxTmpr")
    private float maxTemp;
    /**
     * 测量最小值
     */
    @SerializedName("minTmpr")
    private float minTemp;
    private long startTime;
    private long endTime;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * creator
     */
    @SerializedName("creator")
    private String profileId;
    /**
     * 档案名称
     */
    @SerializedName("name")
    private String profileName;
    /**
     * 报告id
     */
    private String reportId;
    private transient String filePath;
    /**
     * 用户id
     */
    private transient String userId;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 体温贴的版本号
     */
    private transient String patchVersion;
    /**
     * 体温贴的序列号
     */
    private transient String patchSerialNum;
    /**
     * /**
     * 体温贴的mac地址
     */
    private transient String patchMacaddress;
    /**
     * 设备类型
     */
    private transient String patchDeviceType;
    @Column(ignore = true)
    @SerializedName("type")
    private String deviceType;
    @SerializedName("stage")
    private int type;
    private List<Integer> connectType;

    /**
     * 算法姿势状态
     */
    private List<Integer> states;
    /**
     * 算法阶段状态
     */
    private List<Integer> gestures;
    /**
     * 算法版本号
     */
    private List<String> algSdkVer;

    public List<Float> getProcessTemps() {
        return processTemps;
    }

    public void setProcessTemps(List<Float> processTemps) {
        this.processTemps = processTemps;
    }

    public List<Float> getSourceTemps() {
        return sourceTemps;
    }

    public void setSourceTemps(List<Float> sourceTemps) {
        this.sourceTemps = sourceTemps;
    }

    public String getHardVersion() {
        return hardVersion;
    }

    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion;
    }

    public float getMaxAlarmTemp() {
        return maxAlarmTemp;
    }

    public void setMaxAlarmTemp(float maxAlarmTemp) {
        this.maxAlarmTemp = maxAlarmTemp;
    }

    public float getMinAlarmTemp() {
        return minAlarmTemp;
    }

    public void setMinAlarmTemp(float minAlarmTemp) {
        this.minAlarmTemp = minAlarmTemp;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPatchVersion() {
        return patchVersion;
    }

    public void setPatchVersion(String patchVersion) {
        this.patchVersion = patchVersion;
    }

    public String getPatchSerialNum() {
        return patchSerialNum;
    }

    public void setPatchSerialNum(String patchSerialNum) {
        this.patchSerialNum = patchSerialNum;
    }

    public String getPatchMacaddress() {
        return patchMacaddress;
    }

    public void setPatchMacaddress(String patchMacaddress) {
        this.patchMacaddress = patchMacaddress;
    }

    public String getPatchDeviceType() {
        return patchDeviceType;
    }

    public void setPatchDeviceType(String patchDeviceType) {
        this.patchDeviceType = patchDeviceType;
    }

    @Override
    public String toString() {
        return "ReportBean{" +
                "  states=" + states +
                ", gestures=" + gestures +
                ", algSdkVer=" + algSdkVer +
                ", processTemps=" + processTemps +
                ", sourceTemps=" + sourceTemps +
                ", hardVersion='" + hardVersion + '\'' +
                ", maxAlarmTemp=" + maxAlarmTemp +
                ", minAlarmTemp=" + minAlarmTemp +
                ", maxTemp=" + maxTemp +
                ", minTemp=" + minTemp +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", profileId='" + deviceId + '\'' +
                ", profileId='" + profileId + '\'' +
                ", profileName='" + profileName + '\'' +
                ", reportId='" + reportId + '\'' +
                ", filePath='" + filePath + '\'' +
                ", userId='" + userId + '\'' +
                ", patchVersion='" + patchVersion + '\'' +
                ", patchSerialNum='" + patchSerialNum + '\'' +
                ", patchMacaddress='" + patchMacaddress + '\'' +
                ", patchDeviceType=" + patchDeviceType +
                '}';
    }

    public List<Long> getTempTimes() {
        return tempTimes;
    }

    public void setTempTimes(List<Long> tempTimes) {
        this.tempTimes = tempTimes;
    }

    public List<Integer> getSample() {
        return sample;
    }

    public void setSample(List<Integer> sample) {
        this.sample = sample;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setConnectType(List<Integer> connectType) {
        this.connectType = connectType;
    }

    public List<Integer> getConnectType() {
        return connectType;
    }

    public List<Integer> getGestures() {
        return gestures;
    }

    public void setGestures(List<Integer> gestures) {
        this.gestures = gestures;
    }

    public List<Integer> getStates() {
        return states;
    }

    public void setStates(List<Integer> states) {
        this.states = states;
    }

    public List<String> getAlgSdkVer() {
        return algSdkVer;
    }

    public void setAlgSdkVer(List<String> algSdkVer) {
        this.algSdkVer = algSdkVer;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
}

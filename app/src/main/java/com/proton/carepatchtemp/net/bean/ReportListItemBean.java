package com.proton.carepatchtemp.net.bean;

import com.proton.carepatchtemp.utils.net.OSSUtils;

import java.io.Serializable;

/**
 * Created by MoonlightSW on 2016/11/17.
 */

public class ReportListItemBean implements Serializable {

    /**
     * id : 81
     * deviceid : 15
     * profileid : 4
     * type : 1
     * profilename : hunjji
     * profileavatar : http://vdpics.oss-cn-hangzhou.aliyuncs.com/2016/12/vu1481194392703680.jpg
     * devicetype : 1
     * starttime : 1481263873000
     * endtime : 1481263976000
     * filepath : http://rawtemp.oss-cn-hangzhou.aliyuncs.com/2016/12/i_5_1481263882_4.json
     * vcareadvice :
     * vcareresult :
     * doctoradvice :
     * doctoravatar :
     * healthtipid : 4
     * healthtip : Treatments vary depending on the cause of the fever. For example, antibiotics would be used for a bacterial infection such as strep throat.
     * data : {"time":94,"heart_rate_avg":0,"temp_max":42.39,"conception_rate":0,"temp_bbt":0}
     * creator : 5
     * paystatus : 0
     * reqdoctorid : null
     * reqpriority : null
     * collect : false
     * timestr : null
     */

    private String id;
    private String deviceid;
    /**
     * 档案id
     */
    private long profileid;
    private int type;
    private String profilename;
    private String profileavatar;
    private String devicetype;
    private long starttime;
    private long endtime;
    private String filepath;
    private String vcareadvice;
    private String vcareresult;
    private String doctoradvice;
    private String doctoravatar;
    private int healthtipid;
    private String healthtip;
    private DataBean data;
    private boolean collect;
    private boolean checked = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public long getProfileid() {
        return profileid;
    }

    public void setProfileid(long profileid) {
        this.profileid = profileid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProfilename() {
        return profilename;
    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }

    public String getProfileavatar() {
        return profileavatar;
    }

    public void setProfileavatar(String profileavatar) {
        this.profileavatar = profileavatar;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public String getFilepath() {
        filepath = OSSUtils.getRealUrl(filepath);
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getVcareadvice() {
        return vcareadvice;
    }

    public void setVcareadvice(String vcareadvice) {
        this.vcareadvice = vcareadvice;
    }

    public String getVcareresult() {
        return vcareresult;
    }

    public void setVcareresult(String vcareresult) {
        this.vcareresult = vcareresult;
    }

    public String getDoctoradvice() {
        return doctoradvice;
    }

    public void setDoctoradvice(String doctoradvice) {
        this.doctoradvice = doctoradvice;
    }

    public String getDoctoravatar() {
        return doctoravatar;
    }

    public void setDoctoravatar(String doctoravatar) {
        this.doctoravatar = doctoravatar;
    }

    public int getHealthtipid() {
        return healthtipid;
    }

    public void setHealthtipid(int healthtipid) {
        this.healthtipid = healthtipid;
    }

    public String getHealthtip() {
        return healthtip;
    }

    public void setHealthtip(String healthtip) {
        this.healthtip = healthtip;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }


    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }


    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public static class DataBean implements Serializable {
        /**
         * time : 94
         * heart_rate_avg : 0       获取体温数据时此项为高温报警值
         * temp_max : 42.39
         * conception_rate : 0      获取体温数据时此项为低温报警值
         * temp_bbt : 0
         */

        private long time;
        private float heart_rate_avg;
        private String temp_max;
        private float conception_rate;
        private int temp_bbt;

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public float getHeart_rate_avg() {
            return heart_rate_avg;
        }

        public void setHeart_rate_avg(float heart_rate_avg) {
            this.heart_rate_avg = heart_rate_avg;
        }

        public String getTemp_max() {
            return temp_max;
        }

        public void setTemp_max(String temp_max) {
            this.temp_max = temp_max;
        }

        public float getConception_rate() {
            return conception_rate;
        }

        public void setConception_rate(float conception_rate) {
            this.conception_rate = conception_rate;
        }

        public int getTemp_bbt() {
            return temp_bbt;
        }

        public void setTemp_bbt(int temp_bbt) {
            this.temp_bbt = temp_bbt;
        }
    }
}

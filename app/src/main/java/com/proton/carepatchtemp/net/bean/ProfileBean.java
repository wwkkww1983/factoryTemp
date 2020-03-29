package com.proton.carepatchtemp.net.bean;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.utils.UIUtils;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by luochune on 2018/3/16.
 * 档案管理实体对象
 */

public class ProfileBean extends LitePalSupport implements Serializable {
    /**
     * id : 1524
     * title : 宝宝
     * realname : 宝宝
     * gender : 2
     * avatar : http://vdpics.oss-cn-hangzhou.aliyuncs.com/2017/10/vu1508494452807435.jpg
     * birthday : 2017-01-02
     * height : null
     * weight : null
     * isdefault : false
     * lastreport : null
     * shareto : null
     * creator : 811
     * created : 1496360214000
     * status : 1
     * deleted : false
     * profiletype : 1
     * openId : oKkYFwv0MH4dQZ55XUFyl9bLBexQ
     */

    @SerializedName("id")
    private long profileId = -1;
    private String realname;
    private int gender;
    private String avatar;
    private String birthday;
    private long created;
    @SerializedName("patchMac")
    private String macaddress;

    /**
     * 当前activity是否依附在AddNewDeviceActivity  1:是依附在addNewDeviceActivity   0：依附在homeActivity
     */
    private int isAttachAddNew;

    public int getIsAttachAddNew() {
        return isAttachAddNew;
    }

    public void setIsAttachAddNew(int isAttachAddNew) {
        this.isAttachAddNew = isAttachAddNew;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getAge() {
        if (!TextUtils.isEmpty(birthday)) {
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date birthDate = dateformat.parse(birthday);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(birthDate);
                int birthYear = calendar.get(Calendar.YEAR);
                //获得当前年份
                calendar.setTime(new Date());
                int currentYear = calendar.get(Calendar.YEAR);
                int age = currentYear - birthYear;
                if (age >= 0) {
                    return UIUtils.getQuantityString(R.plurals.string_sui, age);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }
}

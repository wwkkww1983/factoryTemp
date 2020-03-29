package com.proton.carepatchtemp.database;

import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.utils.EventBusManager;

import org.litepal.LitePal;

import java.util.List;

public class ProfileManager {

    /**
     * 获取所有档案
     */
    public static List<ProfileBean> getAllProfile() {
        return LitePal.order("created asc").find(ProfileBean.class);
    }

    /**
     * 获取默认档案
     */
    public static ProfileBean getDefaultProfile() {
        List<ProfileBean> profileBeanList = ProfileManager.getAllProfile();
        if (profileBeanList != null && profileBeanList.size() > 0) {
            return profileBeanList.get(0);
        }
        return null;
    }

    /**
     * 保存档案
     */
    public static void saveAll(List<ProfileBean> profiles) {
        LitePal.deleteAll(ProfileBean.class);
        LitePal.saveAll(profiles);
    }

    public static ProfileBean getProfileBean(long profileId) {
        List<ProfileBean> mProfileBeanList = LitePal.where("profileId = ?", profileId + "").find(ProfileBean.class);
        if (mProfileBeanList == null || mProfileBeanList.size() == 0) {
            return null;
        }
        return mProfileBeanList.get(0);
    }

    public static void deleteAll() {
        LitePal.deleteAll(ProfileBean.class);
    }

    public static void update(ProfileBean profileBean) {
        if (profileBean == null) return;
        profileBean.saveOrUpdate("profileId = ?", String.valueOf(profileBean.getProfileId()));
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.PROFILE_CHANGE));
    }

    public static void save(ProfileBean profileBean) {
        if (profileBean != null) {
            profileBean.setCreated(System.currentTimeMillis());
            profileBean.save();
        }
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.PROFILE_CHANGE));
    }

    public static void delete(long profileId) {
        ProfileBean profile = LitePal.where("profileId = ?", String.valueOf(profileId)).findFirst(ProfileBean.class);
        if (profile != null) {
            profile.delete();
        }
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.PROFILE_CHANGE));
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.PROFILE_DELETE));
    }

    public static ProfileBean getById(long profileId) {
        return LitePal.where("profileId = ?", String.valueOf(profileId)).findFirst(ProfileBean.class);
    }

    public static int getCount() {
        return LitePal.count(ProfileBean.class);
    }
}

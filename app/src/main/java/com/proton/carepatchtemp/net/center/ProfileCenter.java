package com.proton.carepatchtemp.net.center;

import com.google.gson.reflect.TypeToken;
import com.proton.carepatchtemp.database.ProfileManager;
import com.proton.carepatchtemp.net.RetrofitHelper;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.NetSubscriber;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.wms.logger.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangmengsi on 2018/02/03.
 */

public class ProfileCenter extends DataCenter {

    /**
     * 获取档案列表
     */
    public static void getProfileList(NetCallBack<List<ProfileBean>> netCallBack) {
        RetrofitHelper
                .getProfileApi()
                .getProfileFileList()
                .map(json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        Type type = new TypeToken<List<ProfileBean>>() {
                        }.getType();
                        List<ProfileBean> profiles = JSONUtils.getObj(resultPair.getData(), type);
                        ProfileManager.saveAll(profiles);
                        return profiles;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<List<ProfileBean>>(netCallBack) {
                    @Override
                    public void onNext(List<ProfileBean> profileMangeItemList) {
                        netCallBack.onSucceed(profileMangeItemList);
                    }
                });
    }

    public static void addProfile(NetCallBack<ProfileBean> resultPairNetCallBack, Map<String, String> map) {
        RetrofitHelper.getProfileApi().addProfile(map).map(
                json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair != null && resultPair.isSuccess()) {
                        ProfileBean profileBean = JSONUtils.getObj(resultPair.getData(), ProfileBean.class);
                        ProfileManager.save(profileBean);
                        return profileBean;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }
        ).compose(threadTrans()).subscribe(new NetSubscriber<ProfileBean>(resultPairNetCallBack) {
            @Override
            public void onNext(ProfileBean profileBean) {
                resultPairNetCallBack.onSucceed(profileBean);
            }
        });
    }

    /**
     * 删除档案
     */
    public static void deleteProfile(long profileId, NetCallBack<ResultPair> resultPairNetCallBack) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("profileid", profileId);
        RetrofitHelper.getProfileApi().deleteProfile(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                ProfileManager.delete(profileId);
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(resultPairNetCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                resultPairNetCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 编辑档案
     */
    public static void editProfile(HashMap<String, String> map, NetCallBack<ProfileBean> netCallBack) {
        RetrofitHelper.getProfileApi().editProfile(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                ProfileBean profileBean = JSONUtils.getObj(resultPair.getData(), ProfileBean.class);
                profileBean.setCreated(Long.parseLong(map.get("created")));
                ProfileManager.update(profileBean);
                return profileBean;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ProfileBean>(netCallBack) {
            @Override
            public void onNext(ProfileBean profileBean) {
                netCallBack.onSucceed(profileBean);
            }
        });
    }
}

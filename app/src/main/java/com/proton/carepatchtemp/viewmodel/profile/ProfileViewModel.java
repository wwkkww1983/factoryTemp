package com.proton.carepatchtemp.viewmodel.profile;

import android.databinding.ObservableField;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.ShareBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.database.ProfileManager;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.DeviceCenter;
import com.proton.carepatchtemp.net.center.ProfileCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luochune on 2018/3/16.
 */

public class ProfileViewModel extends BaseViewModel {

    public ObservableField<List<ProfileBean>> profileList = new ObservableField<>();
    public ObservableField<List<ShareBean>> shareList = new ObservableField<>();

    public void getProfileList() {
        getProfileList(false);
    }

    /**
     * 获取档案列表
     */
    public void getProfileList(boolean refresh) {
        if (!refresh) {
            List<ProfileBean> profileBeans = ProfileManager.getAllProfile();
            if (!CommonUtils.listIsEmpty(profileBeans)) {
                status.set(Status.Success);
                profileList.set(profileBeans);
                return;
            }
        }
        ProfileCenter.getProfileList(new NetCallBack<List<ProfileBean>>() {
            @Override
            public void noNet() {
                super.noNet();
                //刷新无网
                List<ProfileBean> profileBeansList = ProfileManager.getAllProfile();
                if (profileBeansList != null && profileBeansList.size() > 0) {
                    profileList.set(profileBeansList);
                    status.set(Status.Success);
                } else {
                    status.set(Status.NO_NET);
                }
            }

            @Override
            public void onSucceed(List<ProfileBean> data) {
                if (CommonUtils.listIsEmpty(data)) {
                    profileList.set(data);
                    status.set(Status.Empty);
                } else {
                    profileList.set(data);
                    status.set(Status.Success);
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                status.set(Status.Fail);
            }
        });
    }

    /**
     * 删除档案
     */
    public void deleteProfile(long id) {
        ProfileCenter.deleteProfile(id, new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                //无网提示
                BlackToast.show(App.get().getResources().getString(R.string.string_no_net));
            }

            @Override
            public void onSucceed(ResultPair data) {
                //档案删除成功
                BlackToast.show(R.string.string_delete_success);
                //刷新列表
                getProfileList();
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DELETE_PROFILE));
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                } else {
                    BlackToast.show(R.string.string_delete_failed);
                }
            }
        });
    }

    public void getSharedList() {
        DeviceCenter.getSharedList(new NetCallBack<List<ShareBean>>() {
            @Override
            public void onSucceed(List<ShareBean> data) {
                if (!CommonUtils.listIsEmpty(data)) {
                    shareList.set(data);
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                shareList.set(new ArrayList<>());
            }
        });
    }
}

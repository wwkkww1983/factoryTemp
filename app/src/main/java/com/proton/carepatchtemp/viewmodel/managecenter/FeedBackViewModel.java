package com.proton.carepatchtemp.viewmodel.managecenter;

import android.databinding.ObservableField;
import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.managecenter.FeedBackActivity;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.ManageCenter;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;

/**
 * Created by luochune on 2018/3/20.
 */

public class FeedBackViewModel extends BaseViewModel {
    public ObservableField<Boolean> isBeautyChecked = new ObservableField<>(false);//界面美观是否选中
    public ObservableField<Boolean> isHaveAppBug = new ObservableField<>(false);//软件是否有bug
    public ObservableField<Boolean> isSatisfyProduct = new ObservableField<>(false);//对产品是否满意
    public ObservableField<String> feedBackStr = new ObservableField<>("");//意见反馈字符串
    public ObservableField<String> feedNoBeautyStr = new ObservableField<>(UIUtils.getString(R.string.string_feedback_nobeauty));//界面不够美观
    public ObservableField<String> feedAppHaveBug = new ObservableField<>(UIUtils.getString(R.string.string_app_bugs));
    public ObservableField<String> feedUnsatisfyProduct = new ObservableField<>(UIUtils.getString(R.string.string_unsatisfy_product));

    /**
     * 发送点击方法
     */
    public void sendFeedBackStr() {
        StringBuffer sendStrBuffer = new StringBuffer();
        if (isBeautyChecked.get()) {
            sendStrBuffer.append(feedNoBeautyStr.get()).append(",");
        }
        if (isHaveAppBug.get()) {
            sendStrBuffer.append(feedAppHaveBug.get()).append(",");
        }
        if (isSatisfyProduct.get()) {
            sendStrBuffer.append(feedUnsatisfyProduct.get()).append(",");
        }
        String feedStr = feedBackStr.get().trim();
        if (!TextUtils.isEmpty(feedStr)) {
            sendStrBuffer.append(feedStr);
        }
        if (!TextUtils.isEmpty(sendStrBuffer.toString())) {
            ManageCenter.sendFeedBack(sendStrBuffer.toString(), new FeedBackCallBack());
        } else {
            BlackToast.show(R.string.string_say_something);
        }
    }

    private class FeedBackCallBack extends NetCallBack<ResultPair> {
        @Override
        public void onSubscribe() {
            super.onSubscribe();
            showDialog();
        }

        @Override
        public void noNet() {
            super.noNet();
            dismissDialog();
            BlackToast.show(R.string.string_no_net);
        }

        @Override
        public void onSucceed(ResultPair data) {
            dismissDialog();
            BlackToast.show(R.string.string_feedback_success);
            ActivityManager.finishActivity(FeedBackActivity.class);
        }

        @Override
        public void onFailed(ResultPair resultPair) {
            super.onFailed(resultPair);
            dismissDialog();
            if (resultPair != null && resultPair.getData() != null) {
                BlackToast.show(resultPair.getData());
            }
        }
    }

}

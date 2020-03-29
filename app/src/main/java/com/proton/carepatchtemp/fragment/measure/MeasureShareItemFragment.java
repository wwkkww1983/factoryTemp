package com.proton.carepatchtemp.fragment.measure;

import android.databinding.Observable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.bean.PushBean;
import com.proton.carepatchtemp.databinding.FragmentMeasureShareItemBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.MeasureStatusView;
import com.proton.carepatchtemp.view.WarmDialog;
import com.proton.carepatchtemp.viewmodel.measure.ShareMeasureViewModel;
import com.wms.utils.NetUtils;

/**
 * Created by wangmengsi on 2018/2/28.
 * 共享显示
 */

public class MeasureShareItemFragment extends BaseMeasureFragment<FragmentMeasureShareItemBinding, ShareMeasureViewModel> {

    private OnMeasureItemListener onMeasureItemListener;
    private WarmDialog mNetDisconnectDialog;
    private WarmDialog failDialog;
    private WarmDialog warmDialog;

    public static MeasureShareItemFragment newInstance(MeasureBean measureBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("measureInfo", measureBean);
        MeasureShareItemFragment fragment = new MeasureShareItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_share_item;
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();
        binding.setViewmodel(viewmodel);
        //设置状态为连接
        viewmodel.connectStatus.set(2);
        viewmodel.currentTemp.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                updateView();
            }
        });

        viewmodel.isCancelShare.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                //取消共享
                doCloseWarm(String.format(UIUtils.getString(R.string.string_cancel_share), viewmodel.measureInfo.get().getProfile().getRealname()));
            }
        });
        viewmodel.isEndMeasure.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                //结束测量
                doCloseWarm(String.format(UIUtils.getString(R.string.string_share_finish), viewmodel.measureInfo.get().getProfile().getRealname()));
            }
        });
        viewmodel.canNotGetData.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                showFailDialog();
            }
        });
    }

    private void showFailDialog() {
        if (failDialog == null) {
            failDialog = new WarmDialog(ActivityManager.currentActivity())
                    .hideCancelBtn()
                    .setTopText(R.string.string_warm_tips)
                    .setConfirmListener(v -> doCardClose())
                    .setContent(String.format(UIUtils.getString(R.string.string_current_device_is_offline), viewmodel.measureInfo.get().getProfile().getRealname()));
            failDialog.setCancelable(false);
        }
        if (!failDialog.isShowing()) {
            failDialog.show();
        }
    }

    private void doCloseWarm(String content) {
        if (warmDialog == null) {
            warmDialog = new WarmDialog(ActivityManager.currentActivity())
                    .hideCancelBtn()
                    .setTopText(R.string.string_warm_tips)
                    .setConfirmListener(v -> doCardClose());
            warmDialog.setCancelable(false);
        }
        warmDialog.setContent(content);
        if (!warmDialog.isShowing()) {
            warmDialog.show();
        }
    }

    /**
     * 更新界面
     */
    @Override
    protected void updateView() {
        float currentTemp = viewmodel.currentTemp.get();
        if (currentTemp <= 0) {
            binding.idCurrentTemp.setText("--.--");
            binding.idHighestTemp.setText("--.--");
            return;
        }
        if (mLastTemp == 0) {
            //上次温度为0
            mLastTemp = currentTemp;
        }
        int color;
        if (currentTemp >= mWarmHighestTemp) {
            binding.idStatusCircle.setStatus(MeasureStatusView.Status.High);
            color = R.color.color_temp_high;
           /* if (mLastTemp < mWarmHighestTemp) {
                //上一次温度比最高报警温度低，说明是从低温或者正常温度切换到高温
                isGoToHighestTemp = true;
                //显示提醒
            }*/
            showHighestTempWarmDialog(currentTemp, mMeasureInfo.getProfile().getRealname());
        } else if (currentTemp <= mWarmLowestTemp) {
            binding.idStatusCircle.setStatus(MeasureStatusView.Status.Low);
            color = R.color.color_temp_low;
            /*if (mLastTemp > mWarmLowestTemp) {
                //上一次温度比最低报警温度高，说明是从高温或者正常温度切换到低温
                isGoToLowestTemp = true;
                //显示提醒
            }*/
            showLowestTempWarmDialog(currentTemp, mMeasureInfo.getProfile().getRealname());
        } else {
            binding.idStatusCircle.setStatus(MeasureStatusView.Status.Normal);
            color = R.color.color_temp_normal;
            dismissAllTempWarmDialog();
        }
        color = ContextCompat.getColor(mContext, color);
        binding.idCurrentTempUnit.setTextColor(color);
        binding.idCurrentTemp.setTextColor(color);
        mLastTemp = currentTemp;
        binding.setHighestWarmTemp(mWarmHighestTemp);
        binding.setLowestWarmTemp(mWarmLowestTemp);
    }

    @Override
    protected void initView() {
        super.initView();

        binding.idClose.setOnClickListener(v -> new WarmDialog(ActivityManager.currentActivity())
                .setTopText(R.string.string_end_view)
                .setConfirmListener(v1 -> doCardClose())
                .setContent(String.format(UIUtils.getString(R.string.string_confirm_end_view), mMeasureInfo.getProfile().getRealname()))
                .show());
        binding.idHighestTempSetting.setOnClickListener(v -> initWarmTempPicker(37, 42.1f, mWarmHighestTemp, true));
        binding.idLowestTempSetting.setOnClickListener(v -> initWarmTempPicker(30, 36.1f, mWarmLowestTemp, false));
        updateView();
    }

    @Override
    protected ShareMeasureViewModel getViewModel() {
        //加个share前缀防止共享测量和实时测量mac地址相同
        return Utils.getShareViewmodel(mMeasureInfo.getMacaddress(), mMeasureInfo.getProfile().getProfileId());
    }

    public void setOnMeasureItemListener(OnMeasureItemListener onMeasureItemListener) {
        this.onMeasureItemListener = onMeasureItemListener;
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.SWITCH_UNIT) {
            //显示单位更新下
            binding.idCurrentTempUnit.setText(Utils.getTempUnit());
            binding.idHighestTempUnit.setText(Utils.getTempUnit());
            binding.idCurrentTemp.setText(Utils.getTempStr(viewmodel.currentTemp.get()));
            binding.idHighestTemp.setText(Utils.getTempStr(viewmodel.highestTemp.get()));
        } else if (event.getEventType() == MessageEvent.EventType.NET_CHANGE) {
            //网络变化
            showNetDisconnectWarmDialog();
        } else if (event.getEventType() == MessageEvent.EventType.PUSH_SHARE_CANCEL) {
            if (event.getObject() != null && event.getObject() instanceof PushBean) {
                PushBean pushBean = (PushBean) event.getObject();
                if (pushBean.getProfileId() == mMeasureInfo.getProfile().getProfileId()) {
                    viewmodel.isCancelShare.set(true);
                }
            }
        }
    }

    /**
     * 显示网络断开连接对话框
     */
    private void showNetDisconnectWarmDialog() {
        if (NetUtils.isConnected(mContext)) {
            if (mNetDisconnectDialog != null) {
                mNetDisconnectDialog.dismiss();
            }
            viewmodel.reconnect();
            return;
        }
        if (mNetDisconnectDialog == null) {
            mNetDisconnectDialog = new WarmDialog(ActivityManager.currentActivity())
                    .setContent(getString(R.string.string_network_is_disconnect_can_not_share))
                    .setTopText(R.string.string_warm_tips)
                    .hideCancelBtn()
                    .setConfirmText(R.string.string_confirm);
        }
        mNetDisconnectDialog.show();
    }

    @Override
    protected void doCardClose() {
        Utils.clearMeasureViewModel(Utils.getShareViewModelKey(mMeasureInfo.getMacaddress()), mMeasureInfo.getProfile().getProfileId());
        viewmodel.connectStatus.set(-1);
        viewmodel.disConnect();
        if (onMeasureItemListener != null) {
            onMeasureItemListener.closeCard(this);
        }
    }

    public interface OnMeasureItemListener {
        /**
         * 停止当前测量
         */
        void closeCard(MeasureShareItemFragment fragment);
    }

    @Override
    protected boolean isShare() {
        return true;
    }
}

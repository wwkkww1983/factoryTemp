package com.proton.carepatchtemp.fragment.measure;

import android.content.Context;
import android.content.Intent;
import android.databinding.Observable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.measure.DeviceShareActivity;
import com.proton.carepatchtemp.activity.measure.DrugRecordActivity;
import com.proton.carepatchtemp.activity.measure.NurseSuggestBaseInfoActivity;
import com.proton.carepatchtemp.activity.report.ReportDetailActivity;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.bean.ReportBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.database.ProfileManager;
import com.proton.carepatchtemp.databinding.FragmentMeasureItemBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.MeasureCenter;
import com.proton.carepatchtemp.socailauth.PlatformType;
import com.proton.carepatchtemp.socailauth.SocialApi;
import com.proton.carepatchtemp.socailauth.listener.ShareListener;
import com.proton.carepatchtemp.socailauth.share_media.IShareMedia;
import com.proton.carepatchtemp.socailauth.share_media.ShareWebMedia;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.HttpUrls;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.MeasureStatusView;
import com.proton.carepatchtemp.view.OnDoubleClickListener;
import com.proton.carepatchtemp.view.WarmDialog;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.wms.logger.Logger;
import com.wms.utils.NetUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.Type;

/**
 * Created by wangmengsi on 2018/2/28.
 */

public class MeasureItemFragment extends BaseMeasureFragment<FragmentMeasureItemBinding, MeasureViewModel> {
    /**
     * 是否显示信号强度
     */
    private boolean isShowRssi = false;

    private OnMeasureItemListener onMeasureItemListener;
    private WarmDialog mNetDisconnectDialog;

    /**
     * 微信分享，登录工具类
     */
    private SocialApi mSocialApi;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    /**
     * 是否能开始进行报警，首次进入的时候需要先延时2秒，因为可能因为测量准备界面调用onDestoryed()方法导致报警关掉
     */
    private boolean isCanVibrateAndSound = false;

    public static MeasureItemFragment newInstance(MeasureBean measureBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("measureInfo", measureBean);
        MeasureItemFragment fragment = new MeasureItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_item;
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();
        binding.setViewmodel(viewmodel);
        viewmodel.currentTemp.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                updateView();
            }
        });

        viewmodel.deviceId.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                binding.idShare.setEnabled(!TextUtils.isEmpty(viewmodel.deviceId.get()) || !App.get().isLogined());
            }
        });
        viewmodel.reportId.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                binding.idNurseSuggest.setEnabled(!TextUtils.isEmpty(viewmodel.reportId.get()) || !App.get().isLogined());
            }
        });

        updateView();
        showBackgroundTips();

        mSocialApi = SocialApi.get(mContext);

        mMainHandler.postDelayed(() -> isCanVibrateAndSound = true, 2000);

    }

    private void showBackgroundTips() {
        if (App.get().hasShowBackgroundTip
                || SpUtils.getBoolean("hasShowBackgroundTip", false)
                || BuildConfig.IS_INTERNAL) {
            return;
        }
        PowerManager manager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && manager != null) {
            if (!manager.isIgnoringBatteryOptimizations(mContext.getPackageName())) {
                SweetAlertDialog dialog = new SweetAlertDialog(mContext, Type.NORMAL_TYPE);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(true);
                dialog.showCancelButton(true)
                        .setTitleText(getString(R.string.string_warm_tips))
                        .setCancelText(getString(R.string.string_remind_next_time))
                        .setConfirmText(getString(R.string.string_view_help))
                        .setContentText(getString(R.string.string_background_tips))
                        .setConfirmClickListener(alertDialog -> {
                            alertDialog.dismiss();
                            IntentUtils.goToWeb(mContext, HttpUrls.URL_ATTENTION);
                            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                            try {
                                startActivity(intent);
                            } finally {
                                SpUtils.saveBoolean("hasShowBackgroundTip", true);
                            }
                        })
                        .setCancelClickListener(alertDialog -> {
                            App.get().hasShowBackgroundTip = true;
                            alertDialog.dismiss();
                        })
                        .show();
            }
        }
    }

    /**
     * 更新界面
     */
    @Override
    protected void updateView() {
        super.updateView();

        float currentTemp = viewmodel.currentTemp.get();

        //过滤掉数据为0的温度
        if (currentTemp == 0) {
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

          /*  if (mLastTemp < mWarmHighestTemp) {
                //上一次温度比最高报警温度低，说明是从低温或者正常温度切换到高温
                isGoToHighestTemp = true;
                //显示提醒
            }*/
            if (isCanVibrateAndSound) {
                showHighestTempWarmDialog(currentTemp, mMeasureInfo.getProfile().getRealname());
            }

        } else if (currentTemp <= mWarmLowestTemp) {
            binding.idStatusCircle.setStatus(MeasureStatusView.Status.Low);
            color = R.color.color_temp_low;
         /*   if (mLastTemp > mWarmLowestTemp) {
                //上一次温度比最低报警温度高，说明是从高温或者正常温度切换到低温
                isGoToLowestTemp = true;
                //显示提醒
            }*/

            if (isCanVibrateAndSound) {
                showLowestTempWarmDialog(currentTemp, mMeasureInfo.getProfile().getRealname());
            }

        } else {
            binding.idStatusCircle.setStatus(MeasureStatusView.Status.Normal);
            color = R.color.color_temp_normal;
            dismissAllTempWarmDialog();
        }
        color = ContextCompat.getColor(mContext, color);
        binding.idCurrentTempUnit.setTextColor(color);
        binding.idCurrentTemp.setTextColor(color);
        mLastTemp = currentTemp;

        initSettingView();
    }

    @Override
    protected void initView() {
        super.initView();

        binding.idClose.setOnClickListener(v -> closeCard());

        binding.idBatteryLayout.idBatteryRoot.setOnClickListener(new OnDoubleClickListener(() -> {
            isShowRssi = !isShowRssi;
            binding.idBatteryLayout.idRssi.setVisibility(isShowRssi ? View.VISIBLE : View.GONE);
        }));

        binding.idEndMeasureLayout.setOnClickListener(v -> {
            isNeedCloseCard = false;
            if (!viewmodel.isManualDisconnect()) {
                new WarmDialog(ActivityManager.currentActivity())
                        .setTopText(R.string.string_end_measure)
                        .setContent(R.string.string_warn_finish_measure_content)
                        .setConfirmListener(view -> saveReport()).show();
            } else {
                //重连，mqtt连接则检查下有没有网络
                if (viewmodel.getConnectorManager().isMQTTConnect()) {
                    //不是蓝牙连接
                    if (NetUtils.isConnected(mContext)) {
                        reMeasure();
                    } else {
                        if (mNetDisconnectDialog == null) {
                            mNetDisconnectDialog = new WarmDialog(ActivityManager.currentActivity())
                                    .setContent(getString(R.string.string_network_is_disconnect_can_not_measure))
                                    .setTopText(R.string.string_warm_tips)
                                    .hideCancelBtn()
                                    .setConfirmText(R.string.string_confirm);
                        }
                        mNetDisconnectDialog.show();
                    }
                } else {
                    reMeasure();
                }
            }
        });
        //护理建议
        binding.idNurseSuggest.setOnClickListener(v -> {
            String[] stringItems = {getString(R.string.string_suggest), getString(R.string.string_drug_record)};
            ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
            dialog.title(getString(R.string.string_device_share));
            dialog.titleTextSize_SP(14F);
            dialog.show();
            dialog.setOnOperItemClickL((parent, view1, position, id) -> {
                switch (position) {
                    case 0:
                        startActivity(new Intent(getActivity(), NurseSuggestBaseInfoActivity.class).putExtra("currentTemp", viewmodel.currentTemp.get()));
                        break;
                    case 1:
                        if (App.get().isLogined()) {
                            String reportId = viewmodel.reportId.get();
                            if (!TextUtils.isEmpty(reportId)) {
                                startActivity(new Intent(getActivity(), DrugRecordActivity.class).putExtra("reportId", reportId));
                            } else {
                                if (NetUtils.isConnected(mContext)) {
                                    BlackToast.show(getResString(R.string.string_report_add_fail));
                                } else {
                                    BlackToast.show(getResString(R.string.string_no_net));
                                }
                            }
                        } else {
                            //体验模式
                            startActivity(new Intent(getActivity(), DrugRecordActivity.class));
                        }
                        break;
                }
                dialog.dismiss();
            });
        });
        //温度曲线
        binding.idCurve.setOnClickListener(v -> IntentUtils.goToTempCurve(mContext, mMeasureInfo.getMacaddress(), mWarmHighestTemp, mWarmLowestTemp));
        //设备共享
        binding.idShare.setOnClickListener(v -> {
            //未登录体验模式下直接跳转至远程分享页
            if (!App.get().isLogined() || BuildConfig.IS_INTERNAL) {
                startActivity(new Intent(getActivity(), DeviceShareActivity.class)
                        .putExtra("profileId", mMeasureInfo.getProfile().getProfileId() + "")
                        .putExtra("deviceId", viewmodel.deviceId.get())
                        .putExtra("macaddress", mMeasureInfo.getMacaddress()));
                return;
            }
            final String[] stringItems = {getString(R.string.string_shareBy_wx), getString(R.string.string_shareBy_app)};
            final ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
            dialog.title(getString(R.string.string_device_share));
            dialog.titleTextSize_SP(14F);
            dialog.show();
            dialog.setOnOperItemClickL((parent, view1, position, id) -> {
                switch (position) {
                    case 0:
                        //通过微信分享
                        shareWeChat();
                        break;
                    case 1:
                        //通过App分享
                        startActivity(new Intent(getActivity(), DeviceShareActivity.class)
                                .putExtra("profileId", mMeasureInfo.getProfile().getProfileId() + "")
                                .putExtra("deviceId", viewmodel.deviceId.get())
                                .putExtra("macaddress", mMeasureInfo.getMacaddress()));
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            });
        });

        binding.idDockerSetNet.setOnClickListener(v -> new WarmDialog(ActivityManager.currentActivity())
                .setContent(getString(R.string.string_only_p03_set_net))
                .setTopText(R.string.string_warm_tips)
                .setConfirmText(R.string.string_confirm)
                .setConfirmListener(dialog -> IntentUtils.goToDockerSetNetwork(mContext))
                .show());

        binding.idShare.setEnabled(!TextUtils.isEmpty(viewmodel.deviceId.get()) || !App.get().isLogined());
        binding.idNurseSuggest.setEnabled(!TextUtils.isEmpty(viewmodel.reportId.get()) || !App.get().isLogined());
        initSettingView();
    }

    /**
     * 重新测量
     */
    private void reMeasure() {
        if (onMeasureItemListener != null) {
            Utils.clearMeasureViewModel(mMeasureInfo.getMacaddress(), mMeasureInfo.getProfile().getProfileId());
            onMeasureItemListener.remeasure(mMeasureInfo);
        }
    }

    private void shareWeChat() {
        showDialog();
        MeasureCenter.getShareWechatUrl(mMeasureInfo.getProfile().getProfileId(), new NetCallBack<String>() {

            @Override
            public void noNet() {
                BlackToast.show(R.string.string_please_check_your_network);
                dismissDialog();
            }

            @Override
            public void onSucceed(String data) {

                IShareMedia shareMedia = new ShareWebMedia();
                ((ShareWebMedia) shareMedia).setWebPageUrl(data);
                ((ShareWebMedia) shareMedia).setDescription(getResString(R.string.string_invite_to_checkMeasureReport) + mMeasureInfo.getProfile().getRealname() + getResString(R.string.string_one_temp) + "\n此链接24小时内有效");
                ((ShareWebMedia) shareMedia).setTitle(getResString(R.string.stirng_carePatch_shrare));
                mSocialApi.doShare(getActivity(), PlatformType.WEIXIN, shareMedia, new ShareListener() {
                    @Override
                    public void onComplete(PlatformType platform_type) {
                        Logger.w("微信分享onComplete");
//                        BlackToast.show(R.string.string_share_success);
                    }

                    @Override
                    public void onError(PlatformType platform_type, String err_msg) {
                        Logger.w("微信分享onError", err_msg);
                        BlackToast.show(err_msg);

                    }

                    @Override
                    public void onCancel(PlatformType platform_type) {
                        Logger.w("微信分享onCancel");
                        BlackToast.show(R.string.string_share_cancel);
                    }
                });

                dismissDialog();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                BlackToast.show(resultPair.getData());
                dismissDialog();
            }
        });
    }

    /**
     * 显示温度设置
     */
    private void initSettingView() {
        binding.idHighestTempSetting.setText(Utils.getFormartTempAndUnitStr(mWarmHighestTemp));
        binding.idLowestTempSetting.setText(Utils.getFormartTempAndUnitStr(mWarmLowestTemp));
        binding.idHighestTempSetting.setOnClickListener(v -> initWarmTempPicker(37, 42.1f, mWarmHighestTemp, true));
        binding.idLowestTempSetting.setOnClickListener(v -> initWarmTempPicker(30, 36.1f, mWarmLowestTemp, false));
    }

    private void initBottomButton() {
        binding.idNurseSuggest.setEnabled(!viewmodel.isManualDisconnect());
        binding.idCurve.setEnabled(!viewmodel.isManualDisconnect());
        if (App.get().isLogined() && viewmodel.isManualDisconnect()) {
            binding.idDockerSetNet.setEnabled(false);
            binding.idShare.setEnabled(false);
        }
    }

    @Override
    protected void doConnectStatus(int status) {
        super.doConnectStatus(status);
        initBottomButton();
    }

    @Override
    protected MeasureViewModel getViewModel() {
        return Utils.getMeasureViewmodel(mMeasureInfo.getMacaddress(), mMeasureInfo.getProfile().getProfileId());
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
        MessageEvent.EventType type = event.getEventType();
        if (type == MessageEvent.EventType.SWITCH_UNIT) {
            //显示单位更新下
            binding.idCurrentTempUnit.setText(Utils.getTempUnit());
            binding.idHighestTempUnit.setText(Utils.getTempUnit());
            binding.idCurrentTemp.setText(Utils.getTempStr(viewmodel.currentTemp.get()));
            binding.idHighestTemp.setText(Utils.getTempStr(viewmodel.highestTemp.get()));
        } else if (type == MessageEvent.EventType.NET_CHANGE) {
            if (mNetDisconnectDialog != null) {
                mNetDisconnectDialog.dismiss();
            }
            //以防开始测量的时候没有网络，这时候deviceId和reportId会没有，导致保存报告的时候失败
            viewmodel.addDeviceToServer();
        } else if (type == MessageEvent.EventType.LOGIN || type == MessageEvent.EventType.PROFILE_CHANGE) {
            updateProfile();
            viewmodel.addDeviceToServer();
        }
    }

    private void updateProfile() {
        //从游客模式登录，更新当前档案
        ProfileBean profile;
        long profileId = viewmodel.measureInfo.get().getProfile().getProfileId();
        if (profileId == -1) {
            //游客模式取默认档案
            profile = ProfileManager.getDefaultProfile();
        } else {
            profile = ProfileManager.getById(profileId);
        }
        if (profile != null) {
            viewmodel.measureInfo.get().setProfile(profile);
            viewmodel.measureInfo.notifyChange();
        }
    }

    @Override
    protected void doSaveReportSuccessAndGotoReportDetail(ReportBean reportBean) {
        super.doSaveReportSuccessAndGotoReportDetail(reportBean);
        Intent mIntent = new Intent(ActivityManager.currentActivity(), ReportDetailActivity.class);
        mIntent.putExtra("reportId", reportBean.getReportId());
        mIntent.putExtra("maxTemp", String.valueOf(reportBean.getMaxTemp()));
        mIntent.putExtra("reportUrlPath", reportBean.getFilePath());
        mIntent.putExtra("starttime", reportBean.getStartTime());
        mIntent.putExtra("endtime", reportBean.getEndTime());
        mIntent.putExtra("profileId", mMeasureInfo.getProfile().getProfileId());
        mIntent.putExtra("profileName", mMeasureInfo.getProfile().getRealname());
        ActivityManager.currentActivity().startActivity(mIntent);
    }

    @Override
    protected void doCardClose() {
        super.doCardClose();
        if (onMeasureItemListener != null) {
            onMeasureItemListener.closeCard(this);
        }
    }

    public interface OnMeasureItemListener {
        /**
         * 停止当前测量
         */
        void closeCard(MeasureItemFragment fragment);

        /**
         * 重新测量
         */
        void remeasure(MeasureBean measureBean);
    }

}

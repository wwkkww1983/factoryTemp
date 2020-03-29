package com.proton.carepatchtemp.fragment.measure;

import android.app.Activity;
import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.measure.AddNewDeviceActivity;
import com.proton.carepatchtemp.activity.user.LoginActivity;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.bean.ReportBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.database.ProfileManager;
import com.proton.carepatchtemp.fragment.base.BaseViewModelFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.LogTopicConstant;
import com.proton.carepatchtemp.utils.Settings;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.DisconnectDialog;
import com.proton.carepatchtemp.enums.InstructionConstant;
import com.proton.carepatchtemp.view.WarmDialog;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.ConnectionType;
import com.wms.logger.Logger;

import java.lang.ref.WeakReference;

import cn.qqtheme.framework.picker.NumberPicker;

/**
 * Created by wangmengsi on 2018/3/28.
 */

public abstract class BaseMeasureFragment<DB extends ViewDataBinding, VM extends MeasureViewModel> extends BaseViewModelFragment<DB, VM> {

    /**
     * 高温报警最大温度
     */
    protected float mWarmHighestTemp = Settings.DEFAULT_HIGHTEST_TEMP;
    /**
     * 高温报警最小温度
     */
    protected float mWarmLowestTemp = Settings.DEFAULT_LOWEST_TEMP;
    protected MeasureBean mMeasureInfo;
    /**
     * 是否需要关闭卡片
     */
    protected boolean isNeedCloseCard;
    /**
     * 是否是从正常或者低温到高温的
     */
    protected boolean isGoToHighestTemp;
    /**
     * 是否是从正常或者高温到低温的
     */
    protected boolean isGoToLowestTemp;
    /**
     * 上次显示的温度
     */
    protected float mLastTemp;
    private WarmDialog mHighestWarmDialog;
    private WarmDialog mLowestWarmDialog;
    private WarmDialog mBatteryLowDialog;
    private DisconnectDialog mDisconnectDialog;
    private WarmDialog mEndMeasureDialog;
    private WarmDialog mChargeDialog;


    /**
     * 上次设置的报警时间间隔
     */
    private long lastWarmDuration;
    /**
     * 是否按照上次报警时间显示高温报警的标志(主要是为了规避如：之前设置报警时间为2分钟，超过一分钟后设置报警时间为1min中这个时候就会立马报警的问题)
     */
    private boolean lastHightWarmFlag = true;
    /**
     * 是否按照上次报警时间显示低温报警的标志
     */
    private boolean lastLowWarmFlag = true;

    /**
     * 断开连接对话框的类型(0 蓝牙连接断开   1是wifi连接断开)
     */
    private int disconnectType = 0;
    /**
     * 是否是游客模式
     */
    private boolean isVisitorMode;
    private TextView mCurrentTempTextView, mCurrentTempUnit;
    private Observable.OnPropertyChangedCallback mBatteryCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            showBatteryWarmDialog(viewmodel.battery.get());
        }
    };
    private Observable.OnPropertyChangedCallback mStickCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            doStick(viewmodel.hasStick.get());
        }
    };
    private Observable.OnPropertyChangedCallback mConnectStatusCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (viewmodel.isDisconnect()) {
                if (!viewmodel.isShare()) {
                    showDisconnectDialog();
                }
            } else if (viewmodel.isConnected()) {
                if (mDisconnectDialog != null) {
                    mDisconnectDialog.dismiss();
                }
            }
            doConnectStatus(viewmodel.connectStatus.get());
        }
    };
    private Observable.OnPropertyChangedCallback mSaveReportCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (isHidden()) return;
            if (isNeedCloseCard) {
                doCardClose();
            } else {
                ReportBean report = viewmodel.saveReport.get();
                if (report != null) {
                    //不关闭卡片保存成功，直接跳转报告详情
                    doSaveReportSuccessAndGotoReportDetail(report);
                }
            }
        }
    };
    private Observable.OnPropertyChangedCallback mDisconnectCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (viewmodel.needShowDisconnectDialog.get()) {
                if (!viewmodel.isShare()) {
                    if (isBeforeMeasure()) {
                        binding.getRoot().postDelayed(() -> showDisconnectDialog(), viewmodel.getConnectorManager().isMQTTConnect() ? 4000 : 0);
                    } else {
                        showDisconnectDialog();
                    }
                }
            } else {
                if (mDisconnectDialog != null) {
                    mDisconnectDialog.dismiss();
                }
            }
        }
    };
    private Observable.OnPropertyChangedCallback mChargeCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            if (viewmodel.isCharge.get()) {
                showChargeDialog(isBeforeMeasure());
            } else {
                if (mChargeDialog != null && mChargeDialog.isShowing()) {
                    mChargeDialog.dismiss();
                }
            }
        }
    };

    private Observable.OnPropertyChangedCallback mShowPreheatingCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            runOnUiThread(() -> {
                if (mCurrentTempTextView != null) {
                    if (viewmodel.needShowPreheating.get() && viewmodel.connectStatus.get() == 2 || App.get().getInstructionConstant() != InstructionConstant.aa) {
                        mCurrentTempTextView.setTypeface(null);
                    } else {
                        mCurrentTempTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/demo.ttf"));
                    }
                }
            });
        }
    };


    /**
     * 暂时作废，有App里面监听是否处于后台替代
     */
/*
    private BroadcastReceiver mLockScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                Logger.w("屏幕亮屏");
//                viewmodel.isScreenLocked.set(false);
                viewmodel.doScreenLockStatus(false);
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Logger.w("屏幕关闭");
//                viewmodel.isScreenLocked.set(true);
                viewmodel.doScreenLockStatus(true);
            } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                Logger.w("屏幕解锁");
            }
        }
    };
*/
    @Override
    protected void fragmentInit() {
        mMeasureInfo = (MeasureBean) getArguments().getSerializable("measureInfo");
        if (mMeasureInfo == null) return;
        if (mMeasureInfo.getProfile().getProfileId() == -1) {
            isVisitorMode = true;
        }
        setViewModel();
//        if (!isShare()) {
//            registScreenLockBroadcast();
//        }
    }

    @Override
    protected void initView() {
        super.initView();
        mCurrentTempTextView = binding.getRoot().findViewById(R.id.id_current_temp);
        mCurrentTempUnit = binding.getRoot().findViewById(R.id.id_current_temp_unit);

        if (mCurrentTempTextView != null) {
            if (App.get().getInstructionConstant() == InstructionConstant.aa) {
                mCurrentTempTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/demo.ttf"));
            } else {
                mCurrentTempTextView.setTypeface(null);
            }
        }
    }

/*    private void registScreenLockBroadcast() {
        IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        getHostActivity().registerReceiver(mLockScreenReceiver, filter);
    }*/

    /**
     * 关闭卡片
     */
    protected void closeCard() {
        isNeedCloseCard = true;
        if (mEndMeasureDialog == null) {
            mEndMeasureDialog = new WarmDialog(getHostActivity())
                    .setTopText(R.string.string_close_card)
                    .setConfirmText(R.string.string_confirm)
                    .setConfirmListener(v -> saveReport());
        }

        mEndMeasureDialog.setContent(isBeforeMeasure()
                ? R.string.string_measure_time_not_enough_and_not_save_report
                : (viewmodel.isConnected() ? R.string.string_end_measure_tips : R.string.string_ensure_close_card));

        if (!isBeforeMeasure() && viewmodel.isConnected()) {
            mEndMeasureDialog.setConfirmText(R.string.string_end_measure);
            mEndMeasureDialog.setConfirmTextColor(Color.parseColor("#ef6a58"));
        } else {
            mEndMeasureDialog.setConfirmText(R.string.string_confirm);
        }
        if (!mEndMeasureDialog.isShowing()) {
            mEndMeasureDialog.show();
        }
    }

    /**
     * 仅仅关闭卡片不保存数据
     */
    protected void closeCardOnly() {
        viewmodel.disConnect();
        doCardClose();
    }

    /**
     * 保存报告
     */
    protected void saveReport() {
        if (!App.get().isLogined() && viewmodel.isConnected() && !isBeforeMeasure()) {
            //弹出未登录对话框
            new WarmDialog(getHostActivity())
                    .setTopText(R.string.string_end_measure)
                    .setContent(R.string.string_not_login_can_not_save_report)
                    .showFirstBtn()
                    .setFirstBtnListener(v -> startActivity(new Intent(mContext, LoginActivity.class).putExtra("from", "measureSave")))
                    .setConfirmText(getString(R.string.string_not_save))
                    .setConfirmListener(v -> {
                        viewmodel.doSaveReportFail();
                        viewmodel.disConnect();
                    }).show();
            return;
        }

        if (viewmodel.isManualDisconnect()) {
            //手动断开连接状态，则直接关闭
            viewmodel.disConnect();
            viewmodel.saveReport.notifyChange();
            return;
        }

        if (isBeforeMeasure()) {
            viewmodel.saveReport2Json();
            viewmodel.disConnect();
            viewmodel.saveReport.notifyChange();
            return;
        }

        viewmodel.saveReport();
    }

    protected void setViewModel() {
        viewmodel = getViewModel();
        viewmodel.hasStick.addOnPropertyChangedCallback(mStickCallback);
        viewmodel.battery.addOnPropertyChangedCallback(mBatteryCallback);
        viewmodel.connectStatus.addOnPropertyChangedCallback(mConnectStatusCallback);
        viewmodel.saveReport.addOnPropertyChangedCallback(mSaveReportCallback);
        viewmodel.needShowDisconnectDialog.addOnPropertyChangedCallback(mDisconnectCallback);
        viewmodel.isCharge.addOnPropertyChangedCallback(mChargeCallback);
        viewmodel.needShowPreheating.addOnPropertyChangedCallback(mShowPreheatingCallback);
        viewmodel.measureInfo.set(mMeasureInfo);
        viewmodel.setIsBeforeMeasure(isBeforeMeasure());
    }

    /**
     * 显示断开连接对话框
     */
    protected void showDisconnectDialog() {
        if (!SpUtils.getBoolean(AppConfigs.getSpKeyConnectInterrupt(), true)) {
            return;
        }
        if (Utils.needRecreateDialog(mDisconnectDialog)) {
            mDisconnectDialog = new DisconnectDialog(ActivityManager.currentActivity(), viewmodel.patchMacaddress.get());
        }
        if (!viewmodel.getConnectorManager().isMQTTConnect() && viewmodel.lastConnectType != ConnectionType.NET) {
            //蓝牙连接
            mDisconnectDialog.setType(0);
        } else {
            //网络连接
            mDisconnectDialog.setType(1);
        }

        if (!mDisconnectDialog.isShowing()) {
            //震动
            Utils.vibrateAndSound();
            mDisconnectDialog.show();
        }
    }

    /**
     * 显示电量警告
     */
    protected void showBatteryWarmDialog(int battery) {
        if (isHidden()) return;
        boolean hasOpenLowerWarm = SpUtils.getBoolean(AppConfigs.getSpKeyLowPower(), true);
        long lowPowerDuration = SpUtils.getLong(Utils.getLowPowerSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);
        if (!hasOpenLowerWarm || lowPowerDuration == -1) return;
        if (battery <= Settings.MIN_BATTERY) {
            if (Utils.needRecreateDialog(mBatteryLowDialog)) {
                mBatteryLowDialog = new WarmDialog(ActivityManager.currentActivity())
                        .setTopText(R.string.string_battery_low)
                        .setContent(R.string.string_battery_low_tips)
                        .setConfirmText(R.string.string_i_konw)
                        .hideCancelBtn()
                        .setConfirmListener(v -> {
                            //-1代表不再提醒
                            Utils.cancelVibrateAndSound();
                            SpUtils.saveLong(Utils.getLowPowerSharedPreferencesKey(mMeasureInfo.getMacaddress()), -1);
                        });
            }
            if (!mBatteryLowDialog.isShowing()) {
                //震动
                Utils.vibrateAndSound();
                mBatteryLowDialog.show();
            }
        } else {
            if (mBatteryLowDialog != null && mBatteryLowDialog.isShowing()) {
                Utils.cancelVibrateAndSound();
                mBatteryLowDialog.dismiss();
            }
        }
    }

    /**
     * 显示充电对话框
     */
    protected void showChargeDialog(boolean isFromBeforeMeasure) {
        if (mChargeDialog == null) {
            mChargeDialog = new WarmDialog(getHostActivity())
                    .setTopText(R.string.string_close_card)
                    .setContent(R.string.string_is_charge_end_measure)
                    .setConfirmText(R.string.string_confirm)
                    .setConfirmListener(v -> {
                        if (isFromBeforeMeasure) {
                            isNeedCloseCard = true;
                            viewmodel.doSaveReportFail();
                            viewmodel.disConnect();
                        } else {
                            saveReport();
                        }
                    });
        }

        mChargeDialog.setConfirmText(R.string.string_end_measure);
        mChargeDialog.setConfirmTextColor(Color.parseColor("#ef6a58"));
        mChargeDialog.show();
    }

    /**
     * 显示高温报警对话框
     */
    protected void showHighestTempWarmDialog(float currentTemp, String profileName) {
        if (getActivity().getClass().getSimpleName().equals(AddNewDeviceActivity.class.getSimpleName())) {
            return;
        }
        boolean needWarm = isNeedWarm(currentTemp, true);
        if (!needWarm) return;

        //重置低温提醒时间
//        SpUtils.saveLong(Utils.getLowTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);
        if (Utils.needRecreateDialog(mHighestWarmDialog)) {
            mHighestWarmDialog = new WarmDialog(getActivity())
                    .setTopColor(Color.parseColor("#f65d5d"))
                    .setTopText(R.string.string_high_temp_warm)
                    .setConfirmText(R.string.string_i_konw)
                    .setCancelListener(v -> {
                        Utils.cancelVibrateAndSound();
                        isGoToHighestTemp = false;
                        SpUtils.saveLong(Utils.getHighTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), System.currentTimeMillis());

                    });
        }

        mHighestWarmDialog.setCancelText(Utils.getWarmDurationStr() + getString(R.string.string_not_warm_in));
        mHighestWarmDialog.setContent(profileName + getString(R.string.string_high_temp_warm_tips, Utils.getTempAndUnit(currentTemp)));

        if (mLowestWarmDialog != null && mLowestWarmDialog.isShowing()) {
            Utils.cancelVibrateAndSound();
            mLowestWarmDialog.dismiss();
        }

        mHighestWarmDialog.hideConfirmBtn();//高温报警去掉确定按钮

        if (!mHighestWarmDialog.isShowing()) {
            Utils.vibrateAndSound();
            mHighestWarmDialog.show();
        }
    }

    /**
     * 显示低温报警对话框
     */
    protected void showLowestTempWarmDialog(float currentTemp, String profileName) {
        if (getActivity().getClass().getSimpleName().equals(AddNewDeviceActivity.class.getSimpleName())) {
            return;
        }
        boolean needWarm = isNeedWarm(currentTemp, false);
        if (!needWarm) return;

        //重置高温提醒时间
//        SpUtils.saveLong(Utils.getHighTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);

        if (Utils.needRecreateDialog(mLowestWarmDialog)) {
            mLowestWarmDialog = new WarmDialog(getActivity())
                    .setTopText(R.string.string_low_temp_warm)
                    .setConfirmText(R.string.string_i_konw)
                    .setCancelListener(v -> {
                        Utils.cancelVibrateAndSound();
                        isGoToLowestTemp = false;
                        SpUtils.saveLong(Utils.getLowTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), System.currentTimeMillis());
                    });
        }

        mLowestWarmDialog.setCancelText(Utils.getWarmDurationStr() + UIUtils.getString(R.string.string_not_warm_in));
        mLowestWarmDialog.setContent(profileName + String.format(getString(R.string.string_high_temp_warm_tips), Utils.getTempAndUnit(currentTemp)));

        if (mHighestWarmDialog != null && mHighestWarmDialog.isShowing()) {
            Utils.cancelVibrateAndSound();
            mHighestWarmDialog.dismiss();
        }
        mLowestWarmDialog.hideConfirmBtn();//低温报警去掉确定按钮

        if (!mLowestWarmDialog.isShowing()) {
            Utils.vibrateAndSound();
            mLowestWarmDialog.show();
        }

        /*if (isHidden() || !viewmodel.isConnected()) return;
        //重置高温提醒时间
        SpUtils.saveLong(Utils.getHighTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);
        boolean hasOpenLowTempWarm = SpUtils.getBoolean(AppConfigs.SP_KEY_LOW_TEMP_WARNING, true);
        if (!hasOpenLowTempWarm) return;
        long lowTempDuration = SpUtils.getLong(Utils.getLowTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);
        boolean isTimeOut = System.currentTimeMillis() - lowTempDuration > Utils.getWarmDuration();
        if ((isTimeOut && lowTempDuration != -1) || (isGoToLowestTemp && isTimeOut)) {
            if (Utils.needRecreateDialog(mLowestWarmDialog)) {
                mLowestWarmDialog = new WarmDialog(ActivityManager.currentActivity())
                        .setTopText(R.string.string_low_temp_warm)
                        .setConfirmText(R.string.string_i_konw)
                        .setCancelListener(v -> {//xx时间内不再提示
                            Utils.cancelVibrateAndSound();
                            SpUtils.saveLong(Utils.getLowTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), System.currentTimeMillis());
                            isGoToLowestTemp = false;
                        })
                        .setConfirmListener(v -> {//知道了
                            Utils.cancelVibrateAndSound();
                            SpUtils.saveLong(Utils.getLowTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), -1);
                            isGoToLowestTemp = false;
                        });
            }
            mLowestWarmDialog.setCancelText(Utils.getWarmDurationStr() + UIUtils.getString(R.string.string_not_warm_in));
            mLowestWarmDialog.setContent(profileName + String.format(getString(R.string.string_high_temp_warm_tips), Utils.getTempAndUnit(currentTemp)));

            if (mHighestWarmDialog != null && mHighestWarmDialog.isShowing()) {
                Utils.cancelVibrateAndSound();
                mHighestWarmDialog.dismiss();
            }
            mLowestWarmDialog.hideConfirmBtn();//低温报警去掉确定按钮

            if (!mLowestWarmDialog.isShowing()) {
                Utils.vibrateAndSound();
                mLowestWarmDialog.show();
            }
        }
*/
    }


    /**
     * 判断当前温度是否需要报警逻辑（2019.4.23 版）false:不报警  true：报警
     *
     * @param currentTemp 当前温度
     * @param isHighWram  false：低温报警逻辑   true：高温报警逻辑
     */
    protected boolean isNeedWarm(float currentTemp, boolean isHighWram) {

        if (isHidden() || !viewmodel.isConnected()) return false;//fragment隐藏或者设备未连接，不报警

        /**
         * 体温贴不可用
         */
        if (!viewmodel.carePatchEnable.get()) return false;

        if (isHighWram) { //高温报警
            if (currentTemp < mWarmHighestTemp) return false;

            boolean hasOpenHighTempWarm = SpUtils.getBoolean(AppConfigs.getSpKeyHighTempWarning(), true);
            if (!hasOpenHighTempWarm) return false;

            long hightTempDuration = SpUtils.getLong(Utils.getHighTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);
            boolean isTimeOut;
            if (lastHightWarmFlag) {
                isTimeOut = System.currentTimeMillis() - hightTempDuration > lastWarmDuration;
            } else {
                isTimeOut = System.currentTimeMillis() - hightTempDuration > Utils.getWarmDuration();
            }

            if (isTimeOut) {
                lastHightWarmFlag = false;
                return true;
            } else {
                return false;
            }
        } else { //低温报警
            if (currentTemp > mWarmLowestTemp) return false;
            boolean hasOpenLowTempWarm = SpUtils.getBoolean(AppConfigs.getSpKeyLowTempWarning(), true);//设置页面是否打开了需要低温报警
            if (!hasOpenLowTempWarm) return false;
            long lowTempDuration = SpUtils.getLong(Utils.getLowTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);
            boolean isTimeOut;
            if (lastLowWarmFlag) {
                isTimeOut = System.currentTimeMillis() - lowTempDuration > lastWarmDuration;
            } else {
                isTimeOut = System.currentTimeMillis() - lowTempDuration > Utils.getWarmDuration();
            }
            Logger.w("当前系统时间戳:", System.currentTimeMillis(), " 上次报警时间戳: ", lowTempDuration, "当前减上次报警时间戳:", System.currentTimeMillis() - lowTempDuration);

            if (isTimeOut) {
                lastLowWarmFlag = false;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 隐藏高温和低温提醒对话框
     */
    protected void dismissAllTempWarmDialog() {
        Utils.cancelVibrateAndSound();
        if (mLowestWarmDialog != null && mLowestWarmDialog.isShowing()) {
            mLowestWarmDialog.dismiss();
        }
        if (mHighestWarmDialog != null && mHighestWarmDialog.isShowing()) {
            mHighestWarmDialog.dismiss();
        }
    }

    /**
     * 取消所有提醒对话框
     */
    protected void dismissAllDialog() {
        dismissAllTempWarmDialog();
        if (mBatteryLowDialog != null && mBatteryLowDialog.isShowing()) {
            mBatteryLowDialog.dismiss();
        }
        if (mEndMeasureDialog != null && mEndMeasureDialog.isShowing()) {
            mEndMeasureDialog.dismiss();
        }
        if (mDisconnectDialog != null && mDisconnectDialog.isShowing()) {
            mDisconnectDialog.dismiss();
        }
    }

    /**
     * 选择温度
     *
     * @param isHighestWarm 是否是高温提醒
     */
    protected void initWarmTempPicker(float min, float max, float current, boolean isHighestWarm) {
        WeakReference<Activity> weakSelf = new WeakReference<>(getHostActivity());
        NumberPicker warmTempPicker = new NumberPicker(weakSelf.get());
        warmTempPicker.setOffset(2);//偏移量
        warmTempPicker.setTitleText(isHighestWarm ? R.string.string_high_temp_warm_setting : R.string.string_low_temp_warm_setting);
        warmTempPicker.setAnimationStyle(R.style.animate_dialog);
        warmTempPicker.setRange(Utils.getTemp(min), Utils.getTemp(max), 0.1f);//数字范围
        warmTempPicker.setSelectedItem(Utils.getTemp(current));
        warmTempPicker.setLabel(Utils.getTempUnit());

        warmTempPicker.setOnItemPickListener((index, item) -> {
            if (isHighestWarm) {
                //高温报警设置
                mWarmHighestTemp = Utils.getTemp(item.floatValue(), Utils.isSelsiusUnit());
                mLastTemp = -1;
                SpUtils.saveLong(Utils.getHighTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);
            } else {
                //低温报警设置
                mWarmLowestTemp = Utils.getTemp(item.floatValue(), Utils.isSelsiusUnit());
                mLastTemp = Integer.MAX_VALUE;
                SpUtils.saveLong(Utils.getLowTempWarmSharedPreferencesKey(mMeasureInfo.getMacaddress()), 0);
            }
            updateView();
        });

        warmTempPicker.show();
    }

    protected void updateView() {
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.PROFILE_CHANGE) {
            //更改档案信息，重新查询当前档案
            ProfileBean profile = ProfileManager.getById(mMeasureInfo.getProfile().getProfileId());
            if (profile != null) {
                viewmodel.measureInfo.get().setProfile(profile);
                viewmodel.measureInfo.notifyChange();
            }
        } else if (event.getEventType() == MessageEvent.EventType.INPUT_INSTRUCTION) {
            String msg = event.getMsg();
            InstructionConstant instructionConstant = InstructionConstant.getInstructionConstant(msg);
            switch (instructionConstant) {
                case ab:
                    App.get().setInstructionConstant(InstructionConstant.ab);
                    break;
                case aa:
                    App.get().setInstructionConstant(InstructionConstant.aa);
                    break;
                case bb:
                    App.get().setInstructionConstant(InstructionConstant.bb);
                    break;
            }

            runOnUiThread(() -> {
                if (mCurrentTempTextView != null) {
                    if (App.get().getInstructionConstant() == InstructionConstant.aa) {
                        boolean tempUnitVisibility = Utils.getTempUnitVisibility(viewmodel.connectStatus.get(), viewmodel.needShowPreheating.get(), viewmodel.needShowTempLow.get());
                        if (tempUnitVisibility) mCurrentTempUnit.setVisibility(View.VISIBLE);
                        mCurrentTempTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/demo.ttf"));
                    } else {
                        mCurrentTempTextView.setTypeface(null);
                        mCurrentTempUnit.setVisibility(View.GONE);
                    }
                }
            });
            viewmodel.currentTemp.notifyChange();
        } else if (event.getEventType() == MessageEvent.EventType.APP_ISFOREGROUND) {//监听app是否在前台,和锁屏的逻辑一样
            boolean isForeground = (boolean) event.getObject();
            if (isForeground) {
                viewmodel.doScreenLockStatus(false);
            } else {
                viewmodel.doScreenLockStatus(true);
            }
        } else if (event.getEventType() == MessageEvent.EventType.MODIFIY_WARM_DURATION) {
            lastWarmDuration = (long) event.getObject();
            lastHightWarmFlag = true;
            lastLowWarmFlag = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewmodel != null) {
            viewmodel.hasStick.removeOnPropertyChangedCallback(mStickCallback);
            viewmodel.battery.removeOnPropertyChangedCallback(mBatteryCallback);
            viewmodel.connectStatus.removeOnPropertyChangedCallback(mConnectStatusCallback);
            viewmodel.saveReport.removeOnPropertyChangedCallback(mSaveReportCallback);
            viewmodel.needShowDisconnectDialog.removeOnPropertyChangedCallback(mDisconnectCallback);
            viewmodel.isCharge.removeOnPropertyChangedCallback(mChargeCallback);
            viewmodel.needShowPreheating.removeOnPropertyChangedCallback(mShowPreheatingCallback);
        }
   /*     if (mLockScreenReceiver != null && !isShare()) {
            getHostActivity().unregisterReceiver(mLockScreenReceiver);
        }*/

        dismissAllDialog();
    }

    private FragmentActivity getHostActivity() {
        return ActivityManager.findActivity(Settings.MEASURE_ACTIVITY);
    }


    //判断Activity是否Destroy
/*    protected boolean isDestroy(Activity activity) {
        return activity == null || activity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed());
    }*/


    @Override
    public void onPause() {
        super.onPause();
        Utils.cancelVibrateAndSound();
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isHighestWarmDialogShowing = mHighestWarmDialog != null && mHighestWarmDialog.isShowing();
        boolean isLowestWarmDialogShowing = mLowestWarmDialog != null && mLowestWarmDialog.isShowing();
        boolean isBatteryWarmDialogShowing = mBatteryLowDialog != null && mBatteryLowDialog.isShowing();
        if (isHighestWarmDialogShowing
                || isLowestWarmDialogShowing
                || isBatteryWarmDialogShowing) {
            Utils.vibrateAndSound();
        }
    }

    /**
     * 关闭卡片
     */
    protected void doCardClose() {
        long profileId = mMeasureInfo.getProfile().getProfileId();
        if (isVisitorMode) {
            profileId = -1;
        }
        Utils.clearMeasureViewModel(mMeasureInfo.getMacaddress(), profileId);
    }

    /**
     * 不关闭卡片保存成功，直接跳转报告详情
     */
    protected void doSaveReportSuccessAndGotoReportDetail(ReportBean reportBean) {
    }

    /**
     * 连接状态
     */
    protected void doConnectStatus(int status) {
    }

    /**
     * 设备是否贴上
     */
    protected void doStick(boolean isStick) {
    }

    protected boolean isBeforeMeasure() {
        return false;
    }

    protected boolean isShare() {
        return false;
    }

}

package com.proton.carepatchtemp.fragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;

import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.common.GlobalWebActivity;
import com.proton.carepatchtemp.activity.managecenter.AboutProtonActivity;
import com.proton.carepatchtemp.activity.managecenter.FeedBackActivity;
import com.proton.carepatchtemp.activity.managecenter.MsgCenterActivity;
import com.proton.carepatchtemp.activity.user.AccountAndSafeActivity;
import com.proton.carepatchtemp.activity.user.LoginActivity;
import com.proton.carepatchtemp.activity.user.RemindBellSelectActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.databinding.FragmentManagerCenterBinding;
import com.proton.carepatchtemp.databinding.ViewstubMsgSetOpenBinding;
import com.proton.carepatchtemp.fragment.base.BaseViewModelFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.Constants;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.utils.HttpUrls;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.Settings;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.SwitchView;
import com.proton.carepatchtemp.view.WarmDialog;
import com.proton.carepatchtemp.viewmodel.managecenter.ManageCenterViewModel;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.wms.logger.Logger;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.Type;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SettingFragment extends BaseViewModelFragment<FragmentManagerCenterBinding, ManageCenterViewModel> implements View.OnClickListener {

    /**
     * 消息设置打开view
     */
    private View msgSetOpenView;
    private ViewstubMsgSetOpenBinding viewstubMsgSetOpenBinding;

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_manager_center;
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();
        //点击监听
        binding.setClickListener(this);
        binding.setViewModel(getViewModel());
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.LOGIN) {
            initData();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idTopLayout.idTitle.setText(mContext.getResources().getString(R.string.string_setting_title));
        Utils.notLoginViewHide(binding.layFeedback);
        //监听温度单位选择变化
        viewmodel.addTempChangeChoose();
        getCacheSize();
        setListener();
    }

    @SuppressLint("CheckResult")
    private void getCacheSize() {
        //初始化缓存数量
        Observable.just(1.0d)
                .map(integer -> {
                    //初始化缓存数量
                    return FileUtils.readCacheData();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cacheSize -> binding.idTvCachesize.setText(FileUtils.getFormatSize(cacheSize)));
    }

    private void setListener() {
        binding.idTopLayout.idToogleDrawer.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity instanceof HomeActivity) {
                ((HomeActivity) activity).toogleDrawer();
            }
        });
    }

    /**
     * 设置提醒监听
     */
    private void setNotifyListener() {
        //震动开关
        viewstubMsgSetOpenBinding.idVibratorNotify.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true);
                SpUtils.saveBoolean(AppConfigs.getSpKeyVibrator(), true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                SpUtils.saveBoolean(AppConfigs.getSpKeyVibrator(), false);
            }
        });

        viewstubMsgSetOpenBinding.svHighTempNotify.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true);
                SpUtils.saveBoolean(AppConfigs.getSpKeyHighTempWarning(), true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                SpUtils.saveBoolean(AppConfigs.getSpKeyHighTempWarning(), false);
            }
        });
        //低温提醒
        viewstubMsgSetOpenBinding.svLowTempNotify.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true);
                SpUtils.saveBoolean(AppConfigs.getSpKeyLowTempWarning(), true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                SpUtils.saveBoolean(AppConfigs.getSpKeyLowTempWarning(), false);
            }
        });
        //低电量提醒
        viewstubMsgSetOpenBinding.svLowpowerNotify.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true);
                SpUtils.saveBoolean(AppConfigs.getSpKeyLowPower(), true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                SpUtils.saveBoolean(AppConfigs.getSpKeyLowPower(), false);
            }
        });
        //连接中断提醒
        viewstubMsgSetOpenBinding.svConnectInterrupt.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(SwitchView view) {
                view.toggleSwitch(true);
                SpUtils.saveBoolean(AppConfigs.getSpKeyConnectInterrupt(), true);
            }

            @Override
            public void toggleToOff(SwitchView view) {
                view.toggleSwitch(false);
                SpUtils.saveBoolean(AppConfigs.getSpKeyConnectInterrupt(), false);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        //用户登录状态
        String token = SpUtils.getString(Constants.APITOKEN, "");
        if (TextUtils.isEmpty(token)) {
            binding.idTvLoginUserName.setText(getString(R.string.string_unlogin_userName, ""));
            binding.idTvLoginOperation.setText(getString(R.string.string_go_to_login));
            binding.idTvLoginOperation.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_radius5dp_blue30));
            binding.idTvLoginOperation.setTextColor(getResources().getColor(R.color.color_blue_30));
        } else {
            //已登录
//            String phoneNum = PreferencesUtils.getString(App.get(), Constants.ACCOUNT, "");
            String phoneNum = SpUtils.getString(Constants.ACCOUNT, "");
            if (!BuildConfig.IS_INTERNAL) {
                binding.idTvLoginUserName.setText(getString(R.string.string_login_userName, phoneNum));
            } else {
                binding.idTvLoginUserName.setText(phoneNum);
            }
            binding.idTvLoginOperation.setText(getString(R.string.string_to_loginOut));
            binding.idTvLoginOperation.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_radius5dp_redff6));
            binding.idTvLoginOperation.setTextColor(getResources().getColor(R.color.color_orange_ff6));
        }
        //读取本地温度单位设置
        int tempUnit = SpUtils.getInt(AppConfigs.SP_KEY_TEMP_UNIT, AppConfigs.TEMP_UNIT_DEFAULT);//默认使用c温度
        if (tempUnit == AppConfigs.SP_VALUE_TEMP_C) {
            viewmodel.isCTempChoose.set(true);
        } else if (tempUnit == AppConfigs.SP_VALUE_TEMP_F) {
            viewmodel.isFTempChoose.set(true);
        }
    }

    @Override
    protected ManageCenterViewModel getViewModel() {
        return ViewModelProviders.of(getActivity()).get(ManageCenterViewModel.class);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_msg_set:
                openMsgSet();
                break;
            case R.id.iv_msgset_down:
                //消息设置
                openMsgSet();
                break;
            case R.id.lay_clear_cache:
                //清除缓存
                new SweetAlertDialog(getActivity(), Type.WARNING_TYPE)
                        .setContentText(getString(R.string.string_confirm_delete))
                        .setCancelText(getString(R.string.string_cancel))
                        .setConfirmText(getString(R.string.string_delete))
                        .setTitleText(getString(R.string.string_warm_tips))
                        .showCancelButton(true)
                        .setCancelClickListener(Dialog::dismiss)
                        .setConfirmClickListener(sDialog -> new AsyncTask<Void, Void, String>() {
                            @Override
                            protected void onPreExecute() {
                                sDialog.setTitleText(getResources().getString(R.string.string_deleting))
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(Type.PROGRESS_TYPE);
                                super.onPreExecute();
                            }

                            @Override
                            protected String doInBackground(Void... params) {
                                //清除本应用内部缓存
                                FileUtils.deleteFolderFile(App.get().getCacheDir().getPath(), false);
                                //清除本应用外部缓存
                                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                    FileUtils.deleteFolderFile(App.get().getExternalCacheDir().getPath(), false);
                                }
                                double cacheSize = FileUtils.readCacheData();
                                Logger.i("cacheSize: " + cacheSize);
                                String cacheSizeStr = FileUtils.getFormatSize(cacheSize);
                                return cacheSizeStr;
                            }

                            @Override
                            protected void onPostExecute(String cacheSizeStr) {
                                super.onPostExecute(cacheSizeStr);
                                if (!TextUtils.isEmpty(cacheSizeStr)) {
                                    binding.idTvCachesize.setText(cacheSizeStr);
                                }
                                sDialog.setTitleText(getString(R.string.string_delete))
                                        .setContentText(getString(R.string.string_cache_file_deleted))
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(Type.SUCCESS_TYPE);

                            }
                        }.execute())

                        .show();
                break;
            case R.id.lay_set_help:
                //帮助
                IntentUtils.goToWeb(getActivity(), HttpUrls.URL_HELP_CENTER_URL);
                break;
            case R.id.lay_about_proton:
                //关于质子
                startActivity(new Intent(getActivity(), AboutProtonActivity.class));
                break;
            case R.id.lay_userqq_group:
                //用户qq群
                IntentUtils.goToWeb(getActivity(), HttpUrls.URL_CONTACT);
                break;
            case R.id.lay_feedback:
                //意见反馈
                startActivity(new Intent(getActivity(), FeedBackActivity.class));
                break;
            case R.id.id_tv_login_operation:
                if (binding.idTvLoginOperation.getText().toString().equals(getResources().getString(R.string.string_to_loginOut))) {
                    //退出登录
                    final String[] stringItems = {getString(R.string.string_confirm)};
                    final ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
                    dialog.title(getString(R.string.string_login_out));
                    dialog.titleTextSize_SP(13F);
                    dialog.cancelText(getString(R.string.string_cancel));
                    dialog.show();
                    dialog.setOnOperItemClickL((parent, view1, position, id) -> {
                        switch (position) {
                            case 0:
                                //退出登录
                                if (Utils.hasMeasureItem()) {
                                    new WarmDialog(ActivityManager.currentActivity())
                                            .setTopText(R.string.string_warm_tips)
                                            .setContent(R.string.string_can_not_logout_because_has_device_is_measuring)
                                            .hideCancelBtn()
                                            .show();
                                } else {
                                    App.get().logout();
                                }
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    });
                } else if (binding.idTvLoginOperation.getText().toString().equals(getResources().getString(R.string.string_go_to_login))) {
                    //登录
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.id_lay_timeDuration:
                //提醒时间间隔
                int[] timeDurationAry = new int[]{15, 30, 45, 60};
                String timeDurUnit = getResources().getString(R.string.string_minutes_unit);
                long lastWarmDuration = Utils.getWarmDuration();
                final String[] stringItems = {timeDurationAry[0] + timeDurUnit, timeDurationAry[1] + timeDurUnit, timeDurationAry[2] + timeDurUnit, timeDurationAry[3] + timeDurUnit};
                final ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
                dialog.title(getString(R.string.string_notifyDuration_tip));
                dialog.titleTextSize_SP(14F);
                dialog.cancelText(getString(R.string.string_cancel));
                dialog.show();
                dialog.setOnOperItemClickL((parent, view1, position, id) -> {
                    Utils.setWarmDuration((long) timeDurationAry[position] * 60 * 1000);
                    viewstubMsgSetOpenBinding.idTvTimeDuration.setText(stringItems[position]);
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.MODIFIY_WARM_DURATION,lastWarmDuration));
                    dialog.dismiss();
                });
                break;
            case R.id.id_lay_msg_center:
                //消息中心
                startActivity(new Intent(getActivity(), MsgCenterActivity.class));
                break;
            case R.id.lay_intention:
                //注意事项
                startActivity(new Intent(getActivity(), GlobalWebActivity.class).putExtra("url", HttpUrls.URL_ATTENTION).putExtra("title", getResString(R.string.string_intention)));
                break;
            case R.id.id_check_update:
                Utils.checkUpdate(getActivity(), true);
                break;
            case R.id.id_account_safe:
                //账号与安全
                startActivity(new Intent(getActivity(), AccountAndSafeActivity.class));
                break;

        }
    }

    /****************
     * 发起添加群流程。群号：卡帕奇体温贴用户群1(517778251) 的 key 为： 0Xt2-CdDomqPGIE6fYr_IzYo5aqe0fDJ
     * 调用 joinQQGroup(0Xt2-CdDomqPGIE6fYr_IzYo5aqe0fDJ) 即可发起手Q客户端申请加群 卡帕奇体温贴用户群1(517778251)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    /**
     * 消息设置打开
     */
    private void openMsgSet() {
        //消息设置
        if (!binding.idVsMsgSet.isInflated() || msgSetOpenView.getVisibility() == View.GONE) {
            //展开
            if (msgSetOpenView == null) {
                ViewStub msgSetVs = binding.idVsMsgSet.getViewStub();
                msgSetOpenView = msgSetVs.inflate();
                viewstubMsgSetOpenBinding = DataBindingUtil.bind(msgSetOpenView);
                viewstubMsgSetOpenBinding.setClickListener(SettingFragment.this::onClick);
                setNotifyListener();
            } else {
                msgSetOpenView.setVisibility(View.VISIBLE);
            }
            binding.ivMsgsetDown.setImageResource(R.drawable.icon_setarrow_on);
            //初始化提醒
            //提醒事件间隔
            long notifyTimeDuration = Utils.getWarmDuration();
            if (notifyTimeDuration == 0) {
                Utils.setWarmDuration(Settings.DEFAULT_WARM_DURATION);
            } else {
                int minuteNum = (int) (notifyTimeDuration / 60 / 1000);
                viewstubMsgSetOpenBinding.idTvTimeDuration.setText(minuteNum + (mContext.getResources().getString(R.string.string_minutes_unit)));
            }

            //震动开关
            boolean isOpenVibrator = SpUtils.getBoolean(AppConfigs.getSpKeyVibrator(), true);
            if (isOpenVibrator) {
                viewstubMsgSetOpenBinding.idVibratorNotify.setOpened(true);
            } else {
                viewstubMsgSetOpenBinding.idVibratorNotify.setOpened(false);
            }

            //高温提醒
            boolean isHighTempNotify = SpUtils.getBoolean(AppConfigs.getSpKeyHighTempWarning(), true);
            if (isHighTempNotify) {
                viewstubMsgSetOpenBinding.svHighTempNotify.setOpened(true);
            } else {
                viewstubMsgSetOpenBinding.svHighTempNotify.setOpened(false);
            }
            //低温提醒
            boolean isLowTempNotify = SpUtils.getBoolean(AppConfigs.getSpKeyLowTempWarning(), true);
            if (isLowTempNotify) {
                viewstubMsgSetOpenBinding.svLowTempNotify.setOpened(true);
            } else {
                viewstubMsgSetOpenBinding.svLowTempNotify.setOpened(false);
            }
            //设备低电量提醒
            boolean lowPowerNotify = SpUtils.getBoolean(AppConfigs.getSpKeyLowPower(), true);
            if (lowPowerNotify) {
                viewstubMsgSetOpenBinding.svLowpowerNotify.setOpened(true);
            } else {
                viewstubMsgSetOpenBinding.svLowpowerNotify.setOpened(false);
            }
            //连接中断提醒
            boolean deviceInterrupt = SpUtils.getBoolean(AppConfigs.getSpKeyConnectInterrupt(), true);
            if (deviceInterrupt) {
                viewstubMsgSetOpenBinding.svConnectInterrupt.setOpened(true);
            } else {
                viewstubMsgSetOpenBinding.svConnectInterrupt.setOpened(false);
            }

            /**
             * 铃声提醒
             */
            viewstubMsgSetOpenBinding.idBellRemind.setOnClickListener(v -> startActivity(new Intent(mContext, RemindBellSelectActivity.class)));

        } else {
            //关闭
            msgSetOpenView.setVisibility(View.GONE);
            binding.ivMsgsetDown.setImageResource(R.drawable.icon_setarrow_off);
        }
    }
}

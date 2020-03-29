package com.proton.carepatchtemp.fragment.measure;

import android.content.Intent;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.common.GlobalWebActivity;
import com.proton.carepatchtemp.activity.device.FirewareUpdatingActivity;
import com.proton.carepatchtemp.bean.DeviceOnlineBean;
import com.proton.carepatchtemp.bean.LastUseBean;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.databinding.FragmentMeasureScanDeviceBinding;
import com.proton.carepatchtemp.databinding.LayoutEmptyDeviceListBinding;
import com.proton.carepatchtemp.databinding.LayoutScanDeviceHeaderBinding;
import com.proton.carepatchtemp.fragment.base.BaseFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.bean.UpdateFirmwareBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.DeviceCenter;
import com.proton.carepatchtemp.net.center.MeasureCenter;
import com.proton.carepatchtemp.utils.HttpUrls;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.WarmDialog;
import com.proton.carepatchtemp.view.recyclerheader.HeaderAndFooterWrapper;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.ConnectionType;
import com.proton.temp.connector.bean.DeviceBean;
import com.proton.temp.connector.bean.DeviceType;
import com.proton.temp.connector.bluetooth.BleConnector;
import com.proton.temp.connector.bluetooth.callback.OnScanListener;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;
import com.wms.ble.utils.BluetoothUtils;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangmengsi on 2018/2/28.
 * 测量搜索设备
 */

public class MeasureScanDeviceFragment extends BaseFragment<FragmentMeasureScanDeviceBinding> {

    private List<DeviceBean> mDeviceList = new ArrayList<>();
    private LayoutScanDeviceHeaderBinding headerBinding;
    private HeaderAndFooterWrapper mAdapter;
    private LayoutEmptyDeviceListBinding emptyDeviceBinding;
    private OnScanDeviceListener onScanDeviceListener;
    private WarmDialog mConnectFailDialog;
    /**
     * 上次使用贴的mac地址
     */
    private String lastUsePatchMac;
    private ProfileBean mProfile;

    private boolean isReBind;
    /**
     * 由重新绑定界面进入点击没有二维码进入，连接前需要校验体温贴是否在线
     */
    private boolean isNeedCheck;


    /**
     * 是否正在扫描设备
     */
    private boolean isScanDevice;
    private OnScanListener mScanListener = new OnScanListener() {
        @Override
        public void onDeviceFound(DeviceBean device) {
            //看看当前设备是否已经添加或者已经连接了
            if (!CommonUtils.listIsEmpty(mDeviceList)) {
                for (DeviceBean tempDevice : mDeviceList) {
                    if (tempDevice.getMacaddress().equalsIgnoreCase(device.getMacaddress())) {
                        return;
                    }
                }
            }

            if (!TextUtils.isEmpty(lastUsePatchMac) && !isReBind) {
                if (device.getMacaddress().equalsIgnoreCase(lastUsePatchMac)) {
                    if (!isNeedUpdate(device)) {
                        connectDevice(device);
                    } else {
                        //需要更新则加入设备列表
                        mDeviceList.add(device);
                        mAdapter.notifyItemInserted(mDeviceList.size());
                        BleConnector.stopScan();
                    }
                }
            } else {
                //没绑定贴则添加到设备列表
                mDeviceList.add(device);
                headerBinding.setHasDevice(true);
                mAdapter.notifyItemInserted(mDeviceList.size());
            }
        }

        @Override
        public void onScanStopped() {
            Logger.w("搜索设备结束");
            doSearchStoped();
            stopSearch(true);
            isReBind = false;
        }

        @Override
        public void onScanCanceled() {
            Logger.w("搜索设备取消");
            stopSearch(false);
        }
    };

    public static MeasureScanDeviceFragment newInstance(ProfileBean profile) {
        Bundle bundle = new Bundle();
        MeasureScanDeviceFragment fragment = new MeasureScanDeviceFragment();
        bundle.putSerializable("profile", profile);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_scan_device;
    }

    @Override
    protected void fragmentInit() {
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new HeaderAndFooterWrapper(new CommonAdapter<DeviceBean>(mContext, mDeviceList, R.layout.item_device) {

            @Override
            public void convert(CommonViewHolder holder, DeviceBean device) {
                holder.setText(R.id.id_device_mac, Utils.getShowMac(device.getMacaddress()));

                boolean isNeedUpdate = isNeedUpdate(device);
                holder.getView(R.id.id_connect).setOnClickListener(v -> {
                    if (isNeedUpdate) {
                        //升级固件
                        startActivity(new Intent(mContext, FirewareUpdatingActivity.class)
                                .putExtra("macaddress", device.getMacaddress())
                                .putExtra("deviceType", device.getDeviceType())
                        );
                    } else {
                        connectDevice(device);
                    }
                });

                if (isNeedUpdate) {
                    holder.setText(R.id.id_connect, getString(R.string.string_click_update));
                } else {
                    holder.setText(R.id.id_connect, getString(R.string.string_click_use));
                }
            }
        });

        mProfile = (ProfileBean) getArguments().getSerializable("profile");
        headerBinding = LayoutScanDeviceHeaderBinding.inflate(getLayoutInflater());
        headerBinding.setProfile(mProfile);
        headerBinding.idSwitchProfile.setOnClickListener(v -> {
            if (onScanDeviceListener != null) {
                onScanDeviceListener.onSwitchProfile();
            }
        });

        /**
         * 点击头像进入修改档案页面---去掉
         */
//        headerBinding.idAvatar.setOnClickListener(v -> IntentUtils.goToEditProfile(mContext, mProfile));
        headerBinding.idSwitchAvatar.setVisibility(App.get().isLogined() ? View.VISIBLE : View.GONE);
        headerBinding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mAdapter.addHeaderView(headerBinding.getRoot());
        //设置动画
        Utils.setRecyclerViewDeleteAnimation(binding.idRecyclerview);
        binding.idRecyclerview.setAdapter(mAdapter);
    }

    private boolean isNeedUpdate(DeviceBean device) {
        UpdateFirmwareBean lastFireware = LitePal.where("deviceType = ?", String.valueOf(device.getDeviceType().getValue())).findFirst(UpdateFirmwareBean.class);
        if (lastFireware == null) return device.isNeedUpdate();

        boolean isVersionLower = false;
        if ((!TextUtils.isEmpty(device.getHardVersion())
                && !TextUtils.isEmpty(lastFireware.getVersion())
                && Utils.compareVersion(device.getHardVersion(), lastFireware.getVersion()) == -1)
                || (TextUtils.isEmpty(device.getHardVersion()) && device.getDeviceType() != DeviceType.P02)) {
            isVersionLower = true;
        }
        return device.isNeedUpdate() || isVersionLower;
    }

    public void setProfile(ProfileBean profile) {
        this.mProfile = profile;
        if (headerBinding != null) {
            headerBinding.setProfile(profile);
        }
    }

    public void setIsUnbindInto(boolean isNeedCheck) {
        this.isNeedCheck = isNeedCheck;
    }

    @Override
    protected void initData() {
        super.initData();
        if (!App.get().isLogined()) return;
        getLastUseDevice();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idScanDevice.setOnClickListener(v -> getLastUseDevice());
    }

    /**
     * 获取上次使用的设备
     */
    private void getLastUseDevice() {
        if (isScanDevice) return;
        if (!App.get().isLogined()) {
            lastUsePatchMac = SpUtils.getString(AppConfigs.SP_KEY_EXPERIENCE_BIND_DEVICE, "");
            if (TextUtils.isEmpty(lastUsePatchMac)
                    && !App.get().getHasScanQRCode().contains(mProfile.getProfileId())) {
                IntentUtils.goToScanQRCode(mContext, mProfile);
                return;
            }
            LastUseBean lastUse = new LastUseBean();
            lastUse.setMacaddress(lastUsePatchMac);
            checkPatchIsMeasuring(lastUse);
            return;
        }

        if (!TextUtils.isEmpty(mProfile.getMacaddress())) {
            lastUsePatchMac = mProfile.getMacaddress();
            LastUseBean lastUseBean = new LastUseBean();
            lastUseBean.setMacaddress(lastUsePatchMac);
            checkPatchIsMeasuring(lastUseBean);
            return;
        }

        DeviceCenter.getLastUseDevice(mProfile.getProfileId(), new NetCallBack<LastUseBean>() {

            @Override
            public void noNet() {
                super.noNet();
                scanDevice();
            }

            @Override
            public void onSucceed(LastUseBean lastUse) {
                if (lastUse.isExist()) {
                    //蓝牙和服务器同时查询该贴
                    lastUsePatchMac = lastUse.getMacaddress();
                    checkPatchIsMeasuring(lastUse);
                } else {
                    binding.getRoot().postDelayed(() -> {
                        if (App.get().getHasScanQRCode().contains(mProfile.getProfileId())) {
                            scanDevice();
                            return;
                        }
                        IntentUtils.goToScanQRCode(mContext, mProfile);
                    }, 200);
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                scanDevice();
            }
        });
    }

    /**
     * 检查设备是否在线,不在线用蓝牙
     */
    private void checkPatchIsMeasuring(final LastUseBean lastUseBean) {
        MeasureCenter.checkPatchIsMeasuring(lastUseBean.getMacaddress(), new NetCallBack<DeviceOnlineBean>() {

            @Override
            public void noNet() {
                scanDevice();
            }

            @Override
            public void onSucceed(DeviceOnlineBean deviceOnline) {
                Logger.w("贴是否在线:", deviceOnline.isOnline(), ",mac:", lastUseBean.getMacaddress());
                if (deviceOnline.isOnline()) {
                    connectDevice(new DeviceBean(lastUsePatchMac, deviceOnline.getDockerMac())
                            , lastUseBean.getHardVersion()
                            , lastUseBean.getSerialNumber()
                            , lastUseBean.getMacaddress());
                } else {
                    scanDevice();
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                scanDevice();
            }
        });
    }

    private void connectDevice(DeviceBean device) {
        if (isNeedCheck) {
            MeasureCenter.checkPatchIsMeasuring(device.getMacaddress(), new NetCallBack<DeviceOnlineBean>() {

                @Override
                public void noNet() {
                    super.noNet();
                    connectDevice(device, "", "", device.getMacaddress());
                }

                @Override
                public void onSucceed(DeviceOnlineBean deviceOnline) {
                    Logger.w("贴是否在线:", deviceOnline.isOnline(), ",mac:", device.getMacaddress());
                    if (deviceOnline.isOnline()) {
                        connectDevice(new DeviceBean(device.getMacaddress(), deviceOnline.getDockerMac())
                                , device.getHardVersion()
                                , null
                                , device.getMacaddress());
                    } else {
                        connectDevice(device, "", "", device.getMacaddress());
                    }
                }

                @Override
                public void onFailed(ResultPair resultPair) {
                    super.onFailed(resultPair);
                    connectDevice(device, "", "", device.getMacaddress());
                }
            });
        } else {
            connectDevice(device, "", "", device.getMacaddress());
        }
    }

    private void connectDevice(DeviceBean device, String hardVersion, String serialNum, String patchMacaddress) {
        //停止搜索
        BleConnector.stopScan();
        MeasureBean measureBean = new MeasureBean(mProfile, device);
        measureBean.setPatchMac(patchMacaddress);
        measureBean.setHardVersion(hardVersion);
        measureBean.setSerialNum(serialNum);
        MeasureViewModel viewModel = Utils.getMeasureViewmodel(device.getMacaddress(), measureBean.getProfile().getProfileId());
        viewModel.measureInfo.set(measureBean);
        Observable.OnPropertyChangedCallback connectStatusCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.isConnected()) {
                    //连接成功
                    dismissDialog();
                    viewModel.connectStatus.removeOnPropertyChangedCallback(this);
                    if (onScanDeviceListener != null) {
                        onScanDeviceListener.onShowBeforeMeasure(measureBean);
                    }
                    if (!App.get().isLogined()) {
                        SpUtils.saveString(AppConfigs.SP_KEY_EXPERIENCE_BIND_DEVICE, patchMacaddress);
                    }
                } else if (viewModel.isConnecting()) {
                    showDialog(R.string.string_connecting, true);
                } else {
                    dismissDialog();
                    if (device.getDeviceType() == DeviceType.P03 && (device.getConnectionType() == ConnectionType.NET)) {
                        Logger.w("mqtt连接失败，蓝牙搜索p03设备");
                        scanDevice();
                    } else {
                        showConnectFailDialog();
                    }
                    Utils.clearMeasureViewModel(device.getMacaddress(), measureBean.getProfile().getProfileId());
                }
            }
        };
        viewModel.connectStatus.addOnPropertyChangedCallback(connectStatusCallback);
        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                viewModel.connectStatus.removeOnPropertyChangedCallback(connectStatusCallback);
                viewModel.cancelConnect();
                Utils.clearMeasureViewModel(device.getMacaddress(), measureBean.getProfile().getProfileId());
            }
            return false;
        });
        viewModel.connectStatus.set(1);
        viewModel.connectDevice(0);
    }

    /**
     * 显示连接失败对话框
     */
    private void showConnectFailDialog() {
        if (mConnectFailDialog == null) {
            mConnectFailDialog = new WarmDialog(getActivity())
                    .setContent(R.string.string_connect_fail)
                    .setConfirmText(getString(R.string.string_reboot_bluetooth))
                    .setCancelText(R.string.string_i_konw)
                    .setConfirmListener(v -> {
                        BluetoothUtils.closeBluetooth();
                        binding.getRoot().postDelayed(BluetoothUtils::openBluetooth, 3000);
                    });
        }
        if (!mConnectFailDialog.isShowing()) {
            mConnectFailDialog.show();
        }
    }

    /**
     * 扫描设备
     */
    private void scanDevice() {
        if (binding == null) return;
        if (!BluetoothUtils.isBluetoothOpened()) {
            BluetoothUtils.openBluetooth();
            return;
        }
        if (emptyDeviceBinding != null) {
            mAdapter.removeHeader(emptyDeviceBinding.getRoot());
        }
        headerBinding.setHasDevice(false);
        mDeviceList.clear();
        binding.idRecyclerview.getAdapter().notifyDataSetChanged();
        binding.idWave.start();
        isScanDevice = true;
        binding.idScanDevice.setText(R.string.string_searching);
        BleConnector.scanDevice(mScanListener);
    }

    private void stopSearch(boolean showEmpty) {
        isScanDevice = false;
        binding.idWave.stop();
        binding.idScanDevice.setText(R.string.string_rescan);
        if (showEmpty) {
            if (CommonUtils.listIsEmpty(mDeviceList)) {
                showEmptyTips();
                headerBinding.setHasDevice(false);
            }
        }
    }

    private void doSearchStoped() {
        if (!CommonUtils.listIsEmpty(mDeviceList)) {
            headerBinding.setHasDevice(true);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 显示无设备提示
     */
    private void showEmptyTips() {
        if (emptyDeviceBinding == null) {
            emptyDeviceBinding = LayoutEmptyDeviceListBinding.inflate(LayoutInflater.from(App.get()));
            emptyDeviceBinding.getRoot().setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            String[] clickAryStr = new String[]{getResString(R.string.string_rebind), getResString(R.string.string_not_show_green), getResString(R.string.string_click_here)};
            UIUtils.spanStr(emptyDeviceBinding.idTvP03empty, getResString(R.string.string_p03device_empty2), clickAryStr, R.color.color_blue_005c, true, position -> {
                switch (position) {
                    case 0:
                        //重新绑定
                        isReBind = true;
                        IntentUtils.goToScanQRCode(mContext, mProfile);
                        break;
                    case 1:
                        //重新配网
                        IntentUtils.goToDockerSetNetwork(mContext, true);
                        break;
                    case 2:
                        //点击这里
                        startActivity(new Intent(getActivity(), GlobalWebActivity.class).putExtra("url", HttpUrls.URL_NO_DEVICE_SEARCH));
                        break;
                }
            });
        }
//        emptyDeviceBinding.idTitle.setText(String.format(getString(R.string.string_can_not_get_data_please_confirm), Utils.getShowMac(lastUsePatchMac)));
        emptyDeviceBinding.idTitle.setText(String.format(getString(R.string.string_can_not_get_data_please_confirm), "体温贴"));
        mAdapter.removeHeader(emptyDeviceBinding.getRoot());
        mAdapter.addHeaderView(emptyDeviceBinding.getRoot());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            lastUsePatchMac = "";
            headerBinding.setHasDevice(false);
            binding.idWave.stop();
            mDeviceList.clear();
            if (emptyDeviceBinding != null) {
                mAdapter.removeHeader(emptyDeviceBinding.getRoot());
            }
            mAdapter.notifyDataSetChanged();
            BleConnector.stopScan();
        } else {
            if (App.get().isLogined()) {
                getLastUseDevice();
            }
        }
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.LOGIN) {
            if (headerBinding != null) {
                headerBinding.idSwitchAvatar.setVisibility(View.VISIBLE);
            }
        } else if (event.getEventType() == MessageEvent.EventType.SCAN_COMPLETE) {
            if (isHidden()) return;
            getLastUseDevice();
        } else if (event.getEventType() == MessageEvent.EventType.FIREWARE_UPDATE_SUCCESS) {
            if (isHidden()) return;
            scanDevice();
        } else if (event.getEventType() == MessageEvent.EventType.PROFILE_CHANGE && !TextUtils.isEmpty(event.getMsg()) && event.getMsg().equals("isEdit")) {
            //档案编辑了
            ProfileBean profileBean = (ProfileBean) event.getObject();
            mProfile = profileBean;
            headerBinding.setProfile(profileBean);
        } else if (event.getEventType() == MessageEvent.EventType.UNBIND_DEVICE_SUCCESS) {
            isNeedCheck = true;
        }
    }

    public void setOnScanDeviceListener(OnScanDeviceListener onScanDeviceListener) {
        this.onScanDeviceListener = onScanDeviceListener;
    }

    public interface OnScanDeviceListener {
        /**
         * 连接设备
         */
        void onShowBeforeMeasure(MeasureBean measureBean);

        /**
         * 切换档案
         */
        void onSwitchProfile();
    }
}

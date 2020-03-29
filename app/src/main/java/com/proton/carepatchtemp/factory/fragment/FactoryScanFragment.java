package com.proton.carepatchtemp.factory.fragment;

import android.content.Intent;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.common.GlobalWebActivity;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.database.ProfileManager;
import com.proton.carepatchtemp.databinding.FragmentMeasureScanDeviceBinding;
import com.proton.carepatchtemp.databinding.LayoutEmptyDeviceListBinding;
import com.proton.carepatchtemp.factory.bean.CalibrateBean;
import com.proton.carepatchtemp.fragment.base.BaseFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
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
 * 扫描页面
 */
public class FactoryScanFragment extends BaseFragment<FragmentMeasureScanDeviceBinding> {
    private List<DeviceBean> mDeviceList = new ArrayList<>();
    private HeaderAndFooterWrapper mAdapter;
    private LayoutEmptyDeviceListBinding emptyDeviceBinding;

    private WarmDialog mConnectFailDialog;

    /**
     * 是否正在扫描设备
     */
    private boolean isScanDevice;

    /**
     * 最大同时连接体温贴的个数
     */
    public static final int CONNECT_NUMBER_MAX = 20;
    /**
     * 当前已连接个数
     */
    private int currentConnectNum = 0;

    private boolean isNewSearch = false;

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

            //没绑定贴则添加到设备列表
            mDeviceList.add(device);
            mAdapter.notifyItemInserted(mDeviceList.size());
        }

        @Override
        public void onScanStopped() {
            Logger.w("搜索设备结束");
            doSearchStoped();
            stopSearch(true);
        }

        @Override
        public void onScanCanceled() {
            Logger.w("搜索设备取消");
            stopSearch(false);
        }
    };


    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_scan_device;
    }

    public static FactoryScanFragment newInstance(boolean isNewSearch) {

        Bundle bundle = new Bundle();
        FactoryScanFragment fragment = new FactoryScanFragment();
        bundle.putBoolean("isNewSearch", isNewSearch);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void fragmentInit() {
        isNewSearch = getArguments().getBoolean("isNewSearch", false);
        Logger.w("isNewSearch is :", isNewSearch);

        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new HeaderAndFooterWrapper(new CommonAdapter<DeviceBean>(mContext, mDeviceList, R.layout.item_device) {
            @Override
            public void convert(CommonViewHolder holder, DeviceBean device) {
                holder.setText(R.id.id_signal, device.getBluetoothRssi() + "");
                holder.setText(R.id.id_device_mac, device.getMacaddress());
                holder.getView(R.id.id_connect).setOnClickListener(v -> {
                    connectDevice(device, device.getHardVersion(), null, device.getMacaddress(), false);
                });
            }
        });

        //设置动画
        Utils.setRecyclerViewDeleteAnimation(binding.idRecyclerview);
        binding.idRecyclerview.setAdapter(mAdapter);
        scanDevice();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idScanDevice.setOnClickListener(v -> scanDevice());
    }

    /**
     * 扫描设备
     */
    private void scanDevice() {
        if (binding == null) return;
        if (isScanDevice) return;

        if (!BluetoothUtils.isBluetoothOpened()) {
            BluetoothUtils.openBluetooth();
            return;
        }

        if (emptyDeviceBinding != null) {
            mAdapter.removeHeader(emptyDeviceBinding.getRoot());
        }

        mDeviceList.clear();
        if (!isNewSearch) {
            Utils.clearAllMeasureViewModel();
            LitePal.deleteAll(CalibrateBean.class);
        }

        binding.idRecyclerview.getAdapter().notifyDataSetChanged();
        binding.idWave.start();
        isScanDevice = true;
        binding.idScanDevice.setText(R.string.string_searching);
        BleConnector.scanDevice(mScanListener);
    }

    private void connectDevice(DeviceBean device, String hardVersion, String serialNum, String patchMacaddress, boolean isAll) {
        if (isAll) {
            currentConnectNum++;
        }
        //停止搜索
        BleConnector.stopScan();
        MeasureBean measureBean = new MeasureBean(ProfileManager.getDefaultProfile(), device);
        measureBean.setPatchMac(patchMacaddress);
        measureBean.setHardVersion(hardVersion);
        measureBean.setSerialNum(serialNum);
        MeasureViewModel viewModel = Utils.getMeasureViewModel(device.getMacaddress());
        viewModel.measureInfo.set(measureBean);
        Observable.OnPropertyChangedCallback connectStatusCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (viewModel.isConnected()) {
                    viewModel.connectStatus.removeOnPropertyChangedCallback(this);
                    if (!App.get().isLogined()) {
                        SpUtils.saveString(AppConfigs.SP_KEY_EXPERIENCE_BIND_DEVICE, patchMacaddress);
                    }
                    if (isAll && currentConnectNum < mDeviceList.size() && currentConnectNum < CONNECT_NUMBER_MAX) {
                        DeviceBean deviceBean = mDeviceList.get(currentConnectNum);
                        connectDevice(deviceBean, device.getHardVersion(), null, deviceBean.getMacaddress(), true);
                    } else {
                        //连接成功
                        dismissDialog();
                        if (newDeviceConnectCallBack != null) {
                            newDeviceConnectCallBack.connectSuccess();
                        } else {
                            EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_measure1"));
                        }
                    }
                } else if (viewModel.isConnecting()) {
                    showDialog(R.string.string_connecting, true);
                } else {
                    if (!isAll) {
                        dismissDialog();
                        if (device.getDeviceType() == DeviceType.P03 && (device.getConnectionType() == ConnectionType.NET)) {
                            Logger.w("mqtt连接失败，蓝牙搜索p03设备");
                            scanDevice();
                        } else {
                            showConnectFailDialog();
                        }
                        Utils.clearMeasureViewModel(device.getMacaddress());
                    } else {
                        if (isAll && currentConnectNum < mDeviceList.size() && currentConnectNum < CONNECT_NUMBER_MAX) {
                            DeviceBean deviceBean = mDeviceList.get(currentConnectNum);
                            connectDevice(deviceBean, device.getHardVersion(), null, deviceBean.getMacaddress(), true);
                        } else {
                            //连接成功
                            dismissDialog();
                            if (newDeviceConnectCallBack == null) {
                                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_measure1"));
                            }
                        }
                    }

                }
            }
        };
        viewModel.connectStatus.addOnPropertyChangedCallback(connectStatusCallback);
        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                viewModel.connectStatus.removeOnPropertyChangedCallback(connectStatusCallback);
                viewModel.cancelConnect();
                Utils.clearMeasureViewModel(device.getMacaddress());
            }
            return false;
        });
        viewModel.connectStatus.set(1);
        viewModel.connectDevice();
    }

    /**
     * 连接信号最好的20个贴
     */
    public void connectAllDevice() {
        currentConnectNum = 0;
        if (mDeviceList == null || mDeviceList.size() == 0) {
            BlackToast.show("请先搜索体温贴");
            return;
        }
        DeviceBean deviceBean = mDeviceList.get(currentConnectNum);
        connectDevice(deviceBean, deviceBean.getHardVersion(), null, deviceBean.getMacaddress(), true);
    }

    private void stopSearch(boolean showEmpty) {
        isScanDevice = false;
        binding.idWave.stop();
        binding.idScanDevice.setText(R.string.string_rescan);
        if (showEmpty) {
            if (CommonUtils.listIsEmpty(mDeviceList)) {
                showEmptyTips();
            }
        }
    }

    private void doSearchStoped() {
        if (!CommonUtils.listIsEmpty(mDeviceList)) {
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            binding.idWave.stop();
            mDeviceList.clear();
            mAdapter.notifyDataSetChanged();
            BleConnector.stopScan();
        } else {
            if (App.get().isLogined()) {
                scanDevice();
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
                        IntentUtils.goToScanQRCode(mContext);
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
        try {//防止正在搜索的时候点击了返回键，出现IllegalStateException异常
            emptyDeviceBinding.idTitle.setText(String.format(getString(R.string.string_can_not_get_data_please_confirm), "体温贴"));
            mAdapter.removeHeader(emptyDeviceBinding.getRoot());
            mAdapter.addHeaderView(emptyDeviceBinding.getRoot());
            mAdapter.notifyDataSetChanged();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新设备连接的回调
     */
    private ConnectCallBack newDeviceConnectCallBack;

    public void setNewDeviceConnectCallBack(ConnectCallBack newDeviceConnectCallBack) {
        this.newDeviceConnectCallBack = newDeviceConnectCallBack;
    }

    public interface ConnectCallBack {
        void connectSuccess();
    }

}

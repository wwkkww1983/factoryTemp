package com.proton.carepatchtemp.factory.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.databinding.FragmentCalibrateLayoutBinding;
import com.proton.carepatchtemp.factory.CalibrateTableEnums;
import com.proton.carepatchtemp.factory.utils.CalibrateUtil;
import com.proton.carepatchtemp.factory.adapter.CalibrateAdapter;
import com.proton.carepatchtemp.fragment.base.BaseFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bluetooth.BleConnector;
import com.wms.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 校准页面
 */
public class CalibrateFragment extends BaseFragment<FragmentCalibrateLayoutBinding> {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private CalibrateAdapter calibrateAdapter;
    private List<MeasureViewModel> datum = new ArrayList<>();

    public static CalibrateFragment newInstance() {

        Bundle args = new Bundle();
        CalibrateFragment fragment = new CalibrateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_calibrate_layout;
    }

    @Override
    protected void fragmentInit() {

    }

    @Override
    protected void initView() {
        super.initView();
        calibrateAdapter = new CalibrateAdapter(mContext, datum);
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        binding.idRecyclerview.setAdapter(calibrateAdapter);
        refresh();
    }

    public void refresh() {
        if (datum != null) {
            datum.clear();
        }
        datum.addAll(Utils.getAllMeasureViewModelList());
        if (calibrateAdapter != null) {
            calibrateAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.CALIBRATE_ALL_PATCH) {//校准所有未校准的贴
            calibrateAllPatch();
        }
    }

    /**
     * 当前下标
     */
    private int currentIndex = 0;

    /**
     * 校准所有未校准的贴
     */
    private void calibrateAllPatch() {
        currentIndex = 0;
        Logger.w("开始校准所有未校准且存在校准值的体温贴");
        calibratePatch(datum.get(currentIndex));
    }

    /**
     * 单个体温贴校准方法
     */
    private void calibratePatch(MeasureViewModel measureViewModel) {
        if (datum == null || currentIndex >= datum.size()) {
            if (currentIndex >= datum.size()) {
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_qualification"));
            }
            return;
        }
        currentIndex++;

        //判断是否未校准
        if (measureViewModel.calibrationStatus.get() != 1) {
            if (currentIndex < datum.size()) {
                calibratePatch(datum.get(currentIndex));
            } else {
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_qualification"));
            }
        }
        //判断是否存在校准值
        if (measureViewModel.calibration.get() == CalibrateUtil.INVALID_T) {
            if (currentIndex < datum.size()) {
                calibratePatch(datum.get(currentIndex));
            } else {
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_qualification"));
            }
            return;
        }
        BleConnector bleConnector = (BleConnector) measureViewModel.getConnectorManager().getmConnector();
        Logger.w("校准值的十六进制：", CalibrateTableEnums.getCalibrateTByT(measureViewModel.calibration.get()).toString());
        String tHex = CalibrateTableEnums.getCalibrateTByT(measureViewModel.calibration.get()).gettHex();
        bleConnector.calibrateTemp(tHex);
        mHandler.postDelayed(new Runnable() {//写入的时候延时100ms刷新
            @Override
            public void run() {
                measureViewModel.calibrationStatus.set(1);//设置为已校准
                if (currentIndex < datum.size()) {
                    calibratePatch(datum.get(currentIndex));
                } else {
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_qualification"));
                }
            }
        }, 100);

    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }
}

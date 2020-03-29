package com.proton.carepatchtemp.factory.fragment;

import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.databinding.FragmentFactoryMeasureLayoutBinding;
import com.proton.carepatchtemp.factory.utils.CalibrateBeanDao;
import com.proton.carepatchtemp.factory.activity.NewSearchActivity;
import com.proton.carepatchtemp.factory.adapter.FactoryMeasureAdapter;
import com.proton.carepatchtemp.fragment.base.BaseFragment;
import com.proton.carepatchtemp.fragment.measure.MeasureContainerFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bluetooth.BleConnector;
import com.wms.ble.utils.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 1.测温页面
 * 2.记录温度页面
 */
public class FactoryMeasureFragment extends BaseFragment<FragmentFactoryMeasureLayoutBinding> {

    private Handler mHandler=new Handler(Looper.getMainLooper());
    private FactoryMeasureAdapter factoryMeasureAdapter;
    private List<MeasureViewModel> datum = new ArrayList<>();

    /**
     * fragment的标识，默认为第一次测温
     */
    private String fragment_tag = "factory_measure1";

    public static FactoryMeasureFragment newInstance() {

        Bundle args = new Bundle();
        FactoryMeasureFragment fragment = new FactoryMeasureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setFragment_tag(String fragment_tag) {
        this.fragment_tag = fragment_tag;
        if (fragment_tag.equalsIgnoreCase("factory_measure1")) {
            factoryMeasureAdapter.setIsSecondMeasure(false);
        } else {
            factoryMeasureAdapter.setIsSecondMeasure(true);
        }
    }

    /**
     * 进入（测温点2 ）入口
     */
    public void enterTemp2Measure() {
        binding.txtAddNewDevice.setVisibility(View.GONE);
        fragment_tag = "factory_measure2";
        factoryMeasureAdapter.notifyDataSetChanged();
        factoryMeasureAdapter.setIsSecondMeasure(true);
    }


    @Override
    protected int inflateContentView() {
        return R.layout.fragment_factory_measure_layout;
    }

    @Override
    protected void fragmentInit() {
        refresh();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        factoryMeasureAdapter = new FactoryMeasureAdapter(mContext, datum);
        binding.idRecyclerview.setAdapter(factoryMeasureAdapter);

        //点击item稳定按钮，更新右上角“下一步”的点击状态，保存相关数据
        factoryMeasureAdapter.setCallBack(new FactoryMeasureAdapter.ItemClickCallBack() {
            @Override
            public void statusClickCallBack(int position) {
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.STABLE_CHANGE));
                saveMonitorData(datum.get(position));
            }

            @Override
            public void disconnectClickCallBack(int position) {
                disconnect(datum.get(position).patchMacaddress.get());
            }
        });

        /**
         * 点击全部稳点按钮
         */
        binding.txtAllStable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAllMeasureStabled();
                factoryMeasureAdapter.notifyDataSetChanged();
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.STABLE_CHANGE));
            }
        });

        /**
         * 添加新设备
         */
        binding.txtAddNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, NewSearchActivity.class));
            }
        });
    }

    public void refresh() {
        if (datum != null) {
            datum.clear();
        }
        datum.addAll(Utils.getAllMeasureViewModelList());
        if (factoryMeasureAdapter != null) {
            factoryMeasureAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.TEMP_CHANGE) {//温度刷新
            factoryMeasureAdapter.notifyDataSetChanged();
        } else if (event.getEventType() == MessageEvent.EventType.DEVICE_ADD) {
            refresh();
        }
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    /**
     * 设置温度为稳定状态
     */
    private void setAllMeasureStabled() {
        Map<String, ViewModel> allMeasureViewModel = Utils.getAllMeasureViewModel();
        Set<Map.Entry<String, ViewModel>> entries = allMeasureViewModel.entrySet();
        Iterator<Map.Entry<String, ViewModel>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            MeasureViewModel measureViewModel = (MeasureViewModel) iterator.next().getValue();
            saveMonitorData(measureViewModel);
        }
    }

    /**
     * 温度稳定，保存单个检测点数据
     */
    private void saveMonitorData(MeasureViewModel measureViewModel) {
        if (fragment_tag.equalsIgnoreCase("factory_measure1")) {
            measureViewModel.firstStableState.set(1);
            measureViewModel.firstStableTemp.set(measureViewModel.originalTemp.get());
            measureViewModel.firstSinkTemp.set(((MeasureContainerFragment) getParentFragment()).getFirstSinkTemp());
            CalibrateBeanDao.saveMonitor1Data();//保存到本地数据库
        } else {
            measureViewModel.secondStableState.set(1);
            measureViewModel.secondStableTemp.set(measureViewModel.originalTemp.get());
            measureViewModel.secondSinkTemp.set(((MeasureContainerFragment) getParentFragment()).getSecondSinkTemp());
            CalibrateBeanDao.saveMonitor2Data();//保存到本地数据库
        }
    }

    /**
     * 断开体温贴,并关机
     *
     * @param mac
     */
    public void disconnect(String mac) {
        Logger.w("断开前measureViewModel size is : ", Utils.getAllMeasureViewModel().size());
        MeasureViewModel measureViewModel = Utils.getMeasureViewmodel(mac);
        BleConnector bleConnector = (BleConnector) Utils.getMeasureViewmodel(mac).getConnectorManager().getmConnector();
        if (bleConnector == null) {
            return;
        }
        //写入ff  自动关机体温贴
        bleConnector.calibrateTemp("ff");
        mHandler.postDelayed(() -> {
            measureViewModel.disConnect();
            Utils.clearMeasureViewModel(measureViewModel.patchMacaddress.get());
            //删除本地数据库数据
            CalibrateBeanDao.deleteMonitorLitepalData(measureViewModel.patchMacaddress.get());
            Logger.w("断开后measureViewModel size is : ", Utils.getAllMeasureViewModel().size());

            if (Utils.getAllMeasureViewModel().size() == 0) {//当全部断开的时候回到搜索页面
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_scan"));
            } else {
                refresh();
            }

        }, 200);
    }


}

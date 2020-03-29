package com.proton.carepatchtemp.factory.fragment;

import android.arch.lifecycle.ViewModel;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.databinding.FragmentQualificationLayoutBinding;
import com.proton.carepatchtemp.factory.adapter.QualificationAdapter;
import com.proton.carepatchtemp.factory.bean.CalibrateBean;
import com.proton.carepatchtemp.factory.bean.CalibrateDetailBean;
import com.proton.carepatchtemp.factory.bean.CalibrateRequest;
import com.proton.carepatchtemp.factory.utils.CalibrateBeanDao;
import com.proton.carepatchtemp.factory.utils.CalibrateUtil;
import com.proton.carepatchtemp.fragment.base.BaseFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.CalibrateCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bluetooth.BleConnector;
import com.wms.ble.utils.Logger;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QualificationFragment extends BaseFragment<FragmentQualificationLayoutBinding> {
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private QualificationAdapter qualificationAdapter;
    private List<MeasureViewModel> datum = new ArrayList<>();
    /**
     * 当前下标
     */
    private int currentIndex = 0;


    public static QualificationFragment newInstance() {

        Bundle args = new Bundle();

        QualificationFragment fragment = new QualificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_qualification_layout;
    }

    @Override
    protected void fragmentInit() {

    }

    @Override
    protected void initView() {
        super.initView();
        qualificationAdapter = new QualificationAdapter(mContext, datum);
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        binding.idRecyclerview.setAdapter(qualificationAdapter);
        refresh();

        binding.btnCloseQualification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calibrateCheckAll();
            }
        });

        qualificationAdapter.setCloseCallBack(new QualificationAdapter.ItemClickCallBack() {
            @Override
            public void closeCard(String mac) {
                calibrateCheck(mac);
            }
        });

    }

    public void refresh() {
        if (datum != null) {
            datum.clear();
        }
        datum.addAll(Utils.getAllMeasureViewModelList());
        if (qualificationAdapter != null) {
            qualificationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType()==MessageEvent.EventType.QUALIFICATION_REFRESH) {
            refresh();
        }
    }

    /**
     * 上传单个体温贴数据
     *
     * @param mac
     */
    private void calibrateCheck(String mac) {
        CalibrateBean calibrateBean = LitePal.where("mac = ?", mac).findFirst(CalibrateBean.class);
        List<CalibrateDetailBean> list = new ArrayList<>();
        list.add(CalibrateUtil.getCalibrateDetail(calibrateBean));
        CalibrateRequest request = new CalibrateRequest();
        request.setList(list);
        CalibrateCenter.calibrateCheck(request, new NetCallBack<String>() {

            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showDialog(R.string.string_uploading, false);
            }

            @Override
            public void onSucceed(String data) {
                Logger.w("校准相关信息上传成功");
                dismissDialog();
                BlackToast.show("上传成功");
                disconnect(mac,false);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("校准相关信息上传失败 : ", resultPair.getData());
                dismissDialog();
                BlackToast.show(resultPair.getData());
            }
        });
    }

    private void calibrateCheckAll() {
        List<CalibrateBean> calibrateBeanList = LitePal.findAll(CalibrateBean.class);
        List<CalibrateDetailBean> list = new ArrayList<>();
        for (int i = 0; i < calibrateBeanList.size(); i++) {
            if (CalibrateUtil.judgeQualification(calibrateBeanList.get(i).getMac())) {
                list.add(CalibrateUtil.getCalibrateDetail(calibrateBeanList.get(i)));
            }
        }
        if (list == null || list.size() == 0) {
            Logger.w("没有合格的体温贴可以关闭");
            BlackToast.show("没有合格的体温贴可以关闭");
            return;
        }
        CalibrateRequest request = new CalibrateRequest();
        request.setList(list);
        CalibrateCenter.calibrateCheck(request, new NetCallBack<String>() {

            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showDialog(R.string.string_uploading, false);
            }

            @Override
            public void onSucceed(String data) {
                Logger.w("校准相关信息上传成功");
                dismissDialog();
                BlackToast.show("上传成功");
                closeAllQualificationCard();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("校准相关信息上传失败 : ", resultPair.getData());
                dismissDialog();
                BlackToast.show(resultPair.getData());
            }
        });
    }

    /**
     * 关掉所有合格的贴
     */
    private void closeAllQualificationCard() {
        Map<String, ViewModel> allMeasureViewModel = Utils.getAllMeasureViewModel();
        Set<Map.Entry<String, ViewModel>> entries = allMeasureViewModel.entrySet();
        Iterator<Map.Entry<String, ViewModel>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            MeasureViewModel measureViewModel = (MeasureViewModel) iterator.next().getValue();
            boolean isQualification = CalibrateUtil.judgeQualification(measureViewModel.patchMacaddress.get());
            if (!isQualification) {
                disconnect(measureViewModel.patchMacaddress.get(),iterator.hasNext());
            }else {
                if (!iterator.hasNext()) {
                    refresh();
                }
            }
        }
    }

    /**
     * 断开体温贴,并关机
     *
     * @param mac
     */
    private void disconnect(String mac,boolean hasNext) {
        MeasureViewModel measureViewModel = Utils.getMeasureViewmodel(mac);
        BleConnector bleConnector = (BleConnector) Utils.getMeasureViewmodel(mac).getConnectorManager().getmConnector();
        //写入ff  自动关机体温贴
        bleConnector.calibrateTemp("ff");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                measureViewModel.disConnect();
                //删除measureViewModel
                Utils.clearMeasureViewModel(measureViewModel.patchMacaddress.get());
                //删除本地数据库数据
                CalibrateBeanDao.deleteMonitorLitepalData(measureViewModel.patchMacaddress.get());
                if (Utils.getAllMeasureViewModel().size() == 0) {
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_FACTORY_FRAGMENT, "factory_scan"));
                }
                if (!hasNext) {
                    refresh();
                }
            }
        }, 100);
    }

}

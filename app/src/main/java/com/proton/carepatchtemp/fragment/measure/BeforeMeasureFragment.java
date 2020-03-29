package com.proton.carepatchtemp.fragment.measure;

import android.databinding.Observable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.databinding.FragmentBeforeMeasureBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.OnDoubleClickListener;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.wms.logger.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by wangmengsi on 2018/3/23.
 * 测量准备
 */
public class BeforeMeasureFragment extends BaseMeasureFragment<FragmentBeforeMeasureBinding, MeasureViewModel> {
    private OnBeforeMeasureListener onBeforeMeasureListener;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    /**
     * 是否显示信号强度
     */
    private boolean isShowRssi = false;

    private Observable.OnPropertyChangedCallback mGoToMeasureCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable sender, int propertyId) {
            goToMeasuring();
        }
    };
    private Disposable mGoToMeasureDisposed;

    public static BeforeMeasureFragment newInstance(MeasureBean measureBean) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("measureInfo", measureBean);
        BeforeMeasureFragment fragment = new BeforeMeasureFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_before_measure;
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();
        binding.setViewmodel(viewmodel);
        viewmodel.needGoToMeasure.addOnPropertyChangedCallback(mGoToMeasureCallback);
        Logger.w("isConnect is :", viewmodel.getConnectorManager().isConnected());

        if (viewmodel.getConnectorManager().isConnected()) {
            viewmodel.getConnectorManager().setReconnectCount(Integer.MAX_VALUE);
        } else {
            viewmodel.connectDevice();
        }

        /**
         * 首次进入前10秒不显示电量
         */
        binding.idBatteryLayout.idBatteryLayout.setVisibility(View.GONE);
        mHandler.postDelayed(() -> binding.idBatteryLayout.idBatteryLayout.setVisibility(View.VISIBLE), 10000);
        test();
    }

    private void test() {
        if (Utils.isMyTestPhone()) {
            mGoToMeasureDisposed = io.reactivex.Observable
                    .just(1)
                    .delay(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(integer -> goToMeasuring());
        }
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idClose.setOnClickListener(v -> {
            if (mGoToMeasureDisposed != null && !mGoToMeasureDisposed.isDisposed()) {
                mGoToMeasureDisposed.dispose();
            }
//            closeCard();
        });

        binding.idBatteryLayout.idBatteryRoot.setOnClickListener(new OnDoubleClickListener(() -> {
            isShowRssi = !isShowRssi;
            binding.idBatteryLayout.idRssi.setVisibility(isShowRssi ? View.VISIBLE : View.GONE);
        }));
    }

    @Override
    protected MeasureViewModel getViewModel() {
        return Utils.getMeasureViewmodel(mMeasureInfo.getMacaddress(), mMeasureInfo.getProfile().getProfileId());
    }

    /**
     * 测量界面
     */
    private void goToMeasuring() {
        if (onBeforeMeasureListener != null) {
            onBeforeMeasureListener.onGoToMeasure(viewmodel.measureInfo.get());
        }
    }

    public void setMeasureInfo(MeasureBean measureBean) {
        this.mMeasureInfo = measureBean;
    }

    @Override
    protected void doCardClose() {
        super.doCardClose();
        if (onBeforeMeasureListener != null) {
//            onBeforeMeasureListener.closeCard();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewmodel != null) {
            viewmodel.needGoToMeasure.removeOnPropertyChangedCallback(mGoToMeasureCallback);
        }
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    protected boolean isBeforeMeasure() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.SWITCH_UNIT) {
            binding.idCurrentTempUnit.setText(Utils.getTempUnit());
        }
    }

    public void setOnBeforeMeasureListener(OnBeforeMeasureListener onBeforeMeasureListener) {
        this.onBeforeMeasureListener = onBeforeMeasureListener;
    }

    public interface OnBeforeMeasureListener {
        /**
         * 跳转到测量界面
         */
        void onGoToMeasure(MeasureBean measureBean);

        /**
         * 停止当前测量
         */
        void closeCard();
    }
}

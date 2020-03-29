package com.proton.carepatchtemp.activity.measure;

import android.content.Intent;
import android.databinding.Observable;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivityTempCurveBinding;
import com.proton.carepatchtemp.utils.Density;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.TempDataBean;

import java.util.List;

/**
 * Created by wangmengsi on 2018/3/28.
 * 温度实时曲线
 */

public class TempCurveActivity extends BaseViewModelActivity<ActivityTempCurveBinding, MeasureViewModel> {

    private String macaddress;
    /**
     * 高温报警温度
     */
    private float warmHighTemp;
    /**
     * 低温报警温度
     */
    private float warmLowTemp;
    private Observable.OnPropertyChangedCallback mCurrentTempCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            binding.idCurveView.addData(viewmodel.currentTemp.get(),viewmodel.algorithmTemp.get(),viewmodel.algorithStatus,viewmodel.algorithGesture);
        }
    };
    private boolean flag = true;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_temp_curve;
    }

    @Override
    protected void init() {
        Intent intent = getIntent();
        macaddress = intent.getStringExtra("macaddress");
        super.init();
        warmHighTemp = intent.getFloatExtra("warmHighTemp", 37.50f);
        warmLowTemp = intent.getFloatExtra("warmLowTemp", 35.00f);
        viewmodel.currentTemp.addOnPropertyChangedCallback(mCurrentTempCallback);
        binding.setViewmodel(viewmodel);
    }

    @Override
    protected void initData() {
        super.initData();
        List<TempDataBean> allTemps = viewmodel.getAllTemps();
//        Utils.secondTransferToMinute(allTemps);
//        Utils.fillEmpthData(allTemps);
        binding.idCurveView.setChartType(App.get().getInstructionConstant());
        binding.idCurveView.addDatas(allTemps, warmHighTemp, warmLowTemp);

        binding.idHighestTemp.setText(String.valueOf(viewmodel.highestTemp.get()));

//        binding.idHighestTempLayout.setOnClickListener(v -> {
//            if (flag) {
//                ObjectAnimator animator = new ObjectAnimator().ofFloat(binding.idHighestTempLayout, "translationY",
//                        binding.idHighestTempLayout.getTranslationY(), binding.idHighestTempLayout.getTranslationY() - 140);
//                animator.setDuration(500);
//                animator.setRepeatCount(0);
//                animator.start();
//            } else {
//                ObjectAnimator animator = new ObjectAnimator().ofFloat(binding.idHighestTempLayout, "translationY",
//                        binding.idHighestTempLayout.getTranslationY(), binding.idHighestTempLayout.getTranslationY() + 140);
//                animator.setDuration(500);
//                animator.setRepeatCount(0);
//                animator.start();
//            }
//            flag = !flag;
//        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewmodel.currentTemp.removeOnPropertyChangedCallback(mCurrentTempCallback);
    }

    @Override
    protected Density.Orientation getOrientation() {
        return Density.Orientation.HEIGHT;
    }

    @Override
    protected MeasureViewModel getViewModel() {
        return Utils.getMeasureViewmodel(macaddress);
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_real_curve);
    }

}

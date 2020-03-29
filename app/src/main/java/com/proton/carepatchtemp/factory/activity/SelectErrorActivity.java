package com.proton.carepatchtemp.factory.activity;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivitySelectErrorBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.zyyoona7.wheel.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择校准值
 */
public class SelectErrorActivity extends BaseActivity<ActivitySelectErrorBinding> {

    private List<Float> errors1 = new ArrayList<>();
    private List<String> errors1StrList = new ArrayList<>();
    private List<Float> errors2 = new ArrayList<>();
    private List<String> errors2StrList = new ArrayList<>();

    private int errorFirstPosition;
    private int errorSecondPosition;

    /**
     * 第一次测温允许误差
     */
    private float firstAllowableError;
    /**
     * 第二次温度允许误差
     */
    private float secondAllowableError;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_select_error;
    }

    @Override
    protected void init() {
        super.init();
        firstAllowableError = getIntent().getFloatExtra("firstAllowableError", 0);
        secondAllowableError = getIntent().getFloatExtra("secondAllowableError", 0);
        initError();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.wheelview1.setData(errors1StrList);
        binding.wheelview1.setOnWheelChangedListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {

            }

            @Override
            public void onWheelItemChanged(int oldPosition, int newPosition) {

            }

            @Override
            public void onWheelSelected(int position) {
                errorFirstPosition = position;
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.ERROR_CHANGE, errors1.get(errorFirstPosition), errors2.get(errorSecondPosition)));
            }

            @Override
            public void onWheelScrollStateChanged(int state) {

            }
        });
        binding.wheelview1.setSelectedItemPosition(errors1.indexOf(firstAllowableError));


        binding.wheelview2.setData(errors2StrList);
        binding.wheelview2.setOnWheelChangedListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {

            }

            @Override
            public void onWheelItemChanged(int oldPosition, int newPosition) {

            }

            @Override
            public void onWheelSelected(int position) {
                errorSecondPosition = position;
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.ERROR_CHANGE, errors1.get(errorFirstPosition), errors2.get(errorSecondPosition)));
            }

            @Override
            public void onWheelScrollStateChanged(int state) {

            }
        });
        binding.wheelview2.setSelectedItemPosition(errors2.indexOf(secondAllowableError));

    }

    private void initError() {
        if (errors1 != null) {
            errors1.clear();
        }

        if (errors2 != null) {
            errors2.clear();
        }

        for (int i = 0; i <= 30; i++) {
            float error = (float) (0.01 * i);
            errors1.add(error);
            errors1StrList.add("±" + error);
            errors2.add(error);
            errors2StrList.add("±" + error);
        }

    }

    @Override
    protected boolean showBackBtn() {
        return true;
    }

}

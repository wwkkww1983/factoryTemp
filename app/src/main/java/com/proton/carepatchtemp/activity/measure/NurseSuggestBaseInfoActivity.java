package com.proton.carepatchtemp.activity.measure;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivityNurseSuggestBaseInfoBinding;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.Utils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cn.qqtheme.framework.picker.NumberPicker;

/**
 * 发烧护理建议基本信息
 * <extra>
 * currentTemp String 当前体温
 * </extra>
 */
public class NurseSuggestBaseInfoActivity extends BaseActivity<ActivityNurseSuggestBaseInfoBinding> {
    private TagAdapter<String> symptomAdapter;
    private String[] symptom;
    private NumberPicker warmTempPicker;
    /**
     * 当前体温
     */
    private float currentTemp;
    /**
     * 体温护理的最小体温值(默认摄氏温度)
     */
    private float minTemp = Utils.getTemp(36.0f);
    /**
     * 体温护理的最大体温值(默认摄氏温度)
     * 备注: 实际包含最大体温 42.9
     */
    private float maxTemp = Utils.getTemp(43.0f);

    @Override
    protected void init() {
        super.init();
        symptom = new String[]{
                getResources().getString(R.string.string_fx),
                getResources().getString(R.string.string_tt),
                getString(R.string.string_hxjc),
                getResources().getString(R.string.string_cough),
                getResources().getString(R.string.string_hc),
                getResources().getString(R.string.string_jswm),
                getResources().getString(R.string.string_pz),
                getResources().getString(R.string.string_szmsbl),
                getResources().getString(R.string.string_ot),
                getString(R.string.string_szcc),
                getResources().getString(R.string.string_yht),
        };
        currentTemp = getIntent().getFloatExtra("currentTemp", 0);
        initBodyTempPicker(minTemp, maxTemp, currentTemp);
    }

    @Override
    protected void initView() {
        super.initView();

        binding.idIncludeTop.idTvRightOperate.setText(R.string.string_next);
        binding.idIncludeTop.idTvRightOperate.setVisibility(View.VISIBLE);
        Utils.notLoginViewHide(binding.idIncludeTop.idTvRightOperate);
        //温度值
        if (Utils.getTemp(currentTemp) >= minTemp && Utils.getTemp(currentTemp) <= maxTemp) {
            binding.idTvBodyTemp.setText(Utils.getFormartTempAndUnitStr(currentTemp));
        } else {
            binding.idTvBodyTemp.setText(Utils.formatTempToStr(minTemp) + Utils.getTempUnit());
            if (!Utils.isSelsiusUnit()) {
                currentTemp = Utils.getTemp(minTemp, false);
            } else {
                currentTemp = minTemp;
            }
        }
        symptomAdapter = new TagAdapter<String>(symptom) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(NurseSuggestBaseInfoActivity.this).inflate(R.layout.item_layout_tab_tv, binding.idTagflowBabycondition, false);
                tv.setText(s);
                return tv;
            }
        };
        //宝宝症状适配
        binding.idTagflowBabycondition.setAdapter(symptomAdapter);
    }

    /**
     * 初始化温度范围选择
     */
    protected void initBodyTempPicker(float min, float max, double current) {
        WeakReference<Activity> weakSelf = new WeakReference<>(this);
        warmTempPicker = new NumberPicker(weakSelf.get());
        warmTempPicker.setOffset(2);//偏移量
        warmTempPicker.setAnimationStyle(R.style.animate_dialog);
        warmTempPicker.setRange(min, max, 0.1);//数字范围
        if (current != 0) {
            warmTempPicker.setSelectedItem(current);
        }
        warmTempPicker.setLabel(Utils.getTempUnit());
        warmTempPicker.setOnItemPickListener((index, item) -> {
            if (!Utils.isSelsiusUnit()) {
                currentTemp = Utils.getTemp(item.floatValue(), false);
            } else {
                currentTemp = item.floatValue();
            }
            binding.idTvBodyTemp.setText(Utils.formatTempToStr(item.floatValue()) + Utils.getTempUnit());
        });
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idIncludeTop.idTvRightOperate.setOnClickListener(view -> {
            //下一步
            toTempDescribe();
        });
        //温度选择
        binding.idTvBodyTemp.setOnClickListener(view -> {
            if (warmTempPicker != null) {
                warmTempPicker.show();
            }
        });
    }

    /**
     * 下一步
     */
    private void toTempDescribe() {
        //是否有发热史
        boolean isFever = false;
        int feverCheckedRbId = binding.idRgIsHaveFever.getCheckedRadioButtonId();
        if (feverCheckedRbId == binding.idRbIsFever.getId()) {
            //有发热史
            isFever = true;
        } else if (feverCheckedRbId == binding.idRbNoFever.getId()) {
            //无发热史
            isFever = false;
        } else {
            BlackToast.show(R.string.string_please_chooseFever);
            return;
        }
        //是否有疫苗接种史
        boolean isVacine = false;
        int vacineCheckedId = binding.idRgIsHaveVaccin.getCheckedRadioButtonId();
        if (vacineCheckedId == binding.idRbIsVaccin.getId()) {
            isVacine = true;
        } else if (vacineCheckedId == binding.idRbNoVaccin.getId()) {
            isVacine = false;
        } else {
            BlackToast.show(R.string.string_please_choose_isHaveVacin);
            return;
        }
        //宝宝状态选中位置
        Set<Integer> selectedSet = binding.idTagflowBabycondition.getSelectedList();
        //宝宝状态选中文字集合
        List<String> conditionSelectedList = new ArrayList<>();
        if (selectedSet.size() > 0) {
            Iterator<Integer> mIterator = selectedSet.iterator();
            while (mIterator.hasNext()) {
                conditionSelectedList.add(symptom[mIterator.next()]);
            }
        }

        startActivity(new Intent(this, NurseSuggestDescribeActivity.class)
                .putExtra("currentTemp", currentTemp)
                .putExtra("conditionList", (Serializable) conditionSelectedList)
                .putExtra("isFever", isFever)
                .putExtra("isVacine", isVacine));
    }

    @Override
    protected int getNotLoginTips() {
        return R.string.string_not_login_can_not_view_nurse_suggest;
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idLoadingLayout;
    }

    @Override
    protected void initData() {
        if (!App.get().isLogined()) {
            setLoadError();
            binding.idIncludeTop.title.setText(getString(R.string.string_fever_nurse_suggest));
        } else {
            binding.idIncludeTop.title.setText(getString(R.string.string_baseinfo));
        }
    }

    @Override
    protected int generateEmptyLayout() {
        return 0;
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_nurse_suggest_base_info;
    }

    @Override
    public String getTopCenterText() {
        if (App.get().isLogined()) {
            return getResString(R.string.string_baseinfo);
        } else {
            return getResString(R.string.string_fever_nurse_suggest);
        }

    }
}

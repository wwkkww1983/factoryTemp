package com.proton.carepatchtemp.activity.measure;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.activity.common.GlobalWebActivity;
import com.proton.carepatchtemp.adapter.BabyConditionDetailAdapter;
import com.proton.carepatchtemp.bean.TipsBean;
import com.proton.carepatchtemp.databinding.ActivityNurseSuggestDescribeBinding;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 发烧护理建议详细内容页面
 * <extra>
 * currTemp String 基本信息页面的体温(带单位)
 * conditionList List<String> 宝宝症状详解选中的列表
 * isFever boolean 是否有发热惊厥史
 * isVacine boolean 是否有疫苗接种史
 * </>
 */
public class NurseSuggestDescribeActivity extends BaseActivity<ActivityNurseSuggestDescribeBinding> {
    /**
     * 贴士列表
     */
    protected List<TipsBean> tipList;
    /**
     * 体温
     */
    private float currTemp;
    /**
     * 选择的症状列表
     */
    private List<String> conditionSelectedList;

    @Override
    protected void init() {
        super.init();
        Intent mIntent = getIntent();
        currTemp = mIntent.getFloatExtra("currentTemp", 0);
        conditionSelectedList = (List<String>) mIntent.getSerializableExtra("conditionList");
        //初始化贴士建议
        tipList = new ArrayList<>();
        boolean isFever = mIntent.getBooleanExtra("isFever", false);
        if (isFever) {
            tipList.add(new TipsBean(getString(R.string.string_nursedes_fever), R.drawable.img_nursedes_fever, "http://www.protontek.com/app/fever_tip3.html"));
        }
        boolean isVacine = mIntent.getBooleanExtra("isVacine", false);
        if (isVacine) {
            tipList.add(new TipsBean(getString(R.string.string_nursedes_vacine), R.drawable.img_nursedes_vacin, "http://www.protontek.com/app/fever_tip4.html"));
        }
        //添加常用退烧药
        tipList.add(new TipsBean(getString(R.string.string_nursedes_commonDownFever), R.drawable.img_nursedes_downfever, "http://www.protontek.com/app/fever_tip2.html"));
        //添加体温小常识
        tipList.add(new TipsBean(getString(R.string.string_nursedes_tempCommon), R.drawable.img_nursedes_common, "http://www.protontek.com/app/fever_tip1.html"));
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.idTvRightOperate.setText(R.string.string_finish);
        binding.idIncludeTop.idTvRightOperate.setVisibility(View.VISIBLE);
        binding.idRvConditionDetail.setLayoutManager(new LinearLayoutManager(this));
        initSuggestStatus(currTemp);
        initBabyConditionStatus(conditionSelectedList);
        initTipsRecyclerView();
    }

    private void initTipsRecyclerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        // 设置 recyclerview 布局方式为横向布局
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.idTipsRecyclerview.setLayoutManager(mLinearLayoutManager);
        binding.idTipsRecyclerview.setAdapter(new CommonAdapter<TipsBean>(mContext, tipList, R.layout.item_layout_nursedetail_tip) {
            @Override
            public void convert(CommonViewHolder holder, TipsBean tipsBean) {
                holder.setImageResource(R.id.id_nursesuggest_pic, tipsBean.getImageRes());
                holder.getView(R.id.id_rootview).setOnClickListener(v -> startActivity(new Intent(mContext, GlobalWebActivity.class).putExtra("url", tipsBean.getUrl())));
            }
        });
    }

    /**
     * 初始化宝宝症状
     */
    private void initBabyConditionStatus(List<String> conditionSelectedList) {
        if (conditionSelectedList != null && conditionSelectedList.size() > 0) {
            binding.idLayBabyCondition.setVisibility(View.VISIBLE);
            BabyConditionDetailAdapter babyConditionDetailAdapter = new BabyConditionDetailAdapter(this, conditionSelectedList, R.layout.item_layout_baby_condition);
            binding.idRvConditionDetail.setAdapter(babyConditionDetailAdapter);
        } else {
            //症状详解不显示
            binding.idLayBabyCondition.setVisibility(View.GONE);
        }
    }


    /**
     * 初始化护理建议
     */
    private void initSuggestStatus(float currentTemp) {
        //根据体温数据给予护理建议
        int status = 0;
        if (currentTemp >= 36.0f && currentTemp <= 37.0f) {
            //正常体温
            status = R.string.string_nurseDes_normalStatus;
            binding.idTvTempStatus.setTextColor(getResources().getColor(R.color.color_temp_normal));
            binding.idTvTempDescribe.setText(getResources().getString(R.string.string_nureseDes_normalStatus_des));
        } else if (currentTemp > 37.0f && currentTemp <= 37.9f) {
            //低热
            status = R.string.string_nurseDes_lowfeverStats;
            binding.idTvTempStatus.setTextColor(getResources().getColor(R.color.color_temp_high));
            binding.idTvTempDescribe.setText(String.format(getResources().getString(R.string.string_nurseDes_lowFeverStatus_des), Utils.getTempAndUnit(24.00f), Utils.getTempAndUnit(26.00f)));
        } else if (currentTemp > 37.9f && currentTemp <= 38.9f) {
            //中热
            status = R.string.string_nurseDes_middleFeverStatus;
            binding.idTvTempStatus.setTextColor(getResources().getColor(R.color.color_temp_high));
            binding.idTvTempDescribe.setText(String.format(getResources().getString(R.string.string_nurseDes_middleFeverStatus_des), Utils.getTempAndUnit(38.5f)));
        } else if (currentTemp > 38.9f && currentTemp <= 40.9f) {
            //高热
            status = R.string.string_nurseDes_highFeverStatus;
            binding.idTvTempStatus.setTextColor(getResources().getColor(R.color.color_temp_high));
            binding.idTvTempDescribe.setText(getResources().getString(R.string.string_nurseDes_highFeverStatus_des));
        } else if (currentTemp > 40.9f) {
            //超高热
            status = R.string.string_nurseDes_veryHighFeverStatus;
            binding.idTvTempStatus.setTextColor(getResources().getColor(R.color.color_temp_high));
            binding.idTvTempDescribe.setText(getResources().getString(R.string.string_nurseDes_veryHighFeverStatus_des));
        }
        if (status != 0) {
            binding.idTvTempStatus.setText(getResources().getString(status, Utils.formatTempToStr(Utils.getTemp(currentTemp)) + Utils.getTempUnit()));
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        //完成
        binding.idIncludeTop.idTvRightOperate.setOnClickListener(v -> {
            ActivityManager.finishActivity(NurseSuggestBaseInfoActivity.class);
            finish();
        });
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_nurse_suggest_describe;
    }

    @Override
    public String getTopCenterText() {
        return UIUtils.getString(R.string.string_fever_nurse_suggest);
    }
}

package com.proton.carepatchtemp.activity.report;

import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityAddReportNotesBinding;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.MeasureReportCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.wms.logger.Logger;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * 新增记录
 * <Extra>
 * reportId  String 报告id
 * </>
 */
public class AddReportNotesActivity extends BaseActivity<ActivityAddReportNotesBinding> {

    public static final int RESULT_CODE_ADD_REMARK = 2;//添加备注返回码
    private final int REQUEST_CODE_ADD_REMARK = 1;//添加备注请求码
    /**
     * 服用药物
     */
    private String[] drugsAry;
    /**
     * 物理治疗
     */
    private String[] physicalAry;
    private String[] conditionAry;
    private TagAdapter<String> mDrugAdapter;//药物治疗适配器
    private TagAdapter<String> mPhyAdapter;//物理治疗适配器
    private TagAdapter<String> mConAdapter;//宝宝状态适配器
    private String reportId = "";//当前记录所属报告id

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_REMARK:
                if (resultCode == RESULT_CODE_ADD_REMARK) {
                    String remarkStr = data.getStringExtra("remarkStr");
                    if (!TextUtils.isEmpty(remarkStr)) {
                        binding.idTvRemark.setText(remarkStr);
                        binding.idTvRemark.setVisibility(View.VISIBLE);
                    }
                }
                break;
        }
    }

    @Override
    protected void init() {
        super.init();
        Intent mIntent = getIntent();
        reportId = mIntent.getStringExtra("reportId");
        drugsAry = new String[]{
                getResources().getString(R.string.string_note_drug_ibuprofen),
                getResources().getString(R.string.string_note_drug_paracetamol),
                getResources().getString(R.string.string_note_drug_dextro_ibuprofen_suppository),
                getResources().getString(R.string.string_note_drug_999_cold_medicine)
        };
        physicalAry = new String[]{
                getResources().getString(R.string.string_note_phy_ice_bag),
                getResources().getString(R.string.string_note_phy_warm_bath),
                getResources().getString(R.string.string_note_phy_alcohol_sponge_bath),
                getResources().getString(R.string.string_note_phy_feet_in_warm_water),
                getResources().getString(R.string.string_note_phy_cooling_gel),
                getResources().getString(R.string.string_note_phy_drink_water)
        };
        conditionAry = new String[]{
                getResources().getString(R.string.string_note_con_coughed),
                getResources().getString(R.string.string_note_con_dehydrated),
                getResources().getString(R.string.string_note_con_twitchy),
                getResources().getString(R.string.string_note_con_unconcious),
                getResources().getString(R.string.string_note_con_general_weakness),
                getResources().getString(R.string.string_note_con_lethargic),
                getResources().getString(R.string.string_note_con_diarrheic),
                getResources().getString(R.string.string_note_con_vomitive),
                getResources().getString(R.string.string_note_con_snotty),
                getResources().getString(R.string.string_note_con_inappetent),
                getResources().getString(R.string.string_note_con_crying),
                getResources().getString(R.string.string_note_con_cold_limbs)
        };
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.idTvRightOperate.setText(getResources().getString(R.string.string_save));
        binding.idIncludeTop.idTvRightOperate.setVisibility(View.VISIBLE);
        initRecordTab();
    }

    /**
     * 初始化三个记录片段数据
     */
    private void initRecordTab() {
        //服用药物
        binding.idFlowDrugTreatment.setAdapter(mDrugAdapter = new TagAdapter<String>(drugsAry) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(AddReportNotesActivity.this).inflate(R.layout.item_layout_tab_tv, binding.idFlowDrugTreatment, false);
                tv.setText(s);
                return tv;
            }
        });
        //物理治疗
        binding.idFlowPhysicalTreatment.setAdapter(mDrugAdapter = new TagAdapter<String>(physicalAry) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(AddReportNotesActivity.this).inflate(R.layout.item_layout_tab_tv, binding.idFlowPhysicalTreatment, false);
                tv.setText(s);
                return tv;
            }
        });
        //宝宝状态
        binding.idFlowBabyCondition.setAdapter(mDrugAdapter = new TagAdapter<String>(conditionAry) {

            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(AddReportNotesActivity.this).inflate(R.layout.item_layout_tab_tv, binding.idFlowBabyCondition, false);
                tv.setText(s);
                return tv;
            }
        });
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idLayAddRemark.setOnClickListener(v -> {
            //添加备注
            String remarkStr = binding.idTvRemark.getText().toString().trim();
            Intent remarkIntent = new Intent(AddReportNotesActivity.this, AddReportNotesRemarkActivity.class);
            if (!TextUtils.isEmpty(remarkStr)) {
                remarkIntent.putExtra("remarkStr", remarkStr);
            }
            startActivityForResult(remarkIntent, REQUEST_CODE_ADD_REMARK);
        });
        binding.idIncludeTop.idTvRightOperate.setOnClickListener(v -> {
            if (TextUtils.isEmpty(reportId)) {
                BlackToast.show(R.string.string_notfound_report);
                return;
            }
            //保存
            showDialog("保存数据中...");
            //服用药物选中
            Set<Integer> drugSelectedSet = binding.idFlowDrugTreatment.getSelectedList();
            //物理治疗选中
            Set<Integer> physicalSelectedSet = binding.idFlowPhysicalTreatment.getSelectedList();
            //宝宝状态选中
            Set<Integer> babyConditionSelectedSet = binding.idFlowBabyCondition.getSelectedList();
            //备注字符串
            String remarkStr = binding.idTvRemark.getText().toString().trim();
            if (drugSelectedSet.size() == 0 && physicalSelectedSet.size() == 0 && babyConditionSelectedSet.size() == 0 && TextUtils.isEmpty(remarkStr)) {
                dismissDialog();
                BlackToast.show(R.string.string_nothing_add_tip);
                return;
            }
            //保存服用药物记录数据,用";"分隔
            StringBuffer drugStrBuffer = new StringBuffer();
            if (drugSelectedSet.size() > 0) {
                Iterator<Integer> drugIterator = drugSelectedSet.iterator();
                drugStrBuffer.append(drugsAry[drugIterator.next()]);
                while (drugIterator.hasNext()) {
                    drugStrBuffer.append(";").append(drugsAry[drugIterator.next()]);
                }

            }
            //保存物理治疗记录数据,用";"分隔
            StringBuffer physicalStrBuffer = new StringBuffer();
            if (physicalSelectedSet.size() > 0) {
                Iterator<Integer> physicalIterator = physicalSelectedSet.iterator();
                physicalStrBuffer.append(physicalAry[physicalIterator.next()]);
                while (physicalIterator.hasNext()) {
                    physicalStrBuffer.append(";").append(physicalAry[physicalIterator.next()]);
                }

            }
            //保存宝宝状态数据,用";"分隔
            StringBuffer conditionStrBuffer = new StringBuffer();
            if (babyConditionSelectedSet.size() > 0) {
                Iterator<Integer> conditionIterator = babyConditionSelectedSet.iterator();
                conditionStrBuffer.append(conditionAry[conditionIterator.next()]);
                while (conditionIterator.hasNext()) {
                    conditionStrBuffer.append(";").append(conditionAry[conditionIterator.next()]);
                }
            }
            JSONObject jsonObject = new JSONObject();
            try {
                if (!TextUtils.isEmpty(drugStrBuffer)) {
                    jsonObject.put("1", drugStrBuffer.toString());
                }
                if (!TextUtils.isEmpty(physicalStrBuffer)) {
                    jsonObject.put("2", physicalStrBuffer.toString());
                }
                if (!TextUtils.isEmpty(conditionStrBuffer)) {
                    jsonObject.put("3", conditionStrBuffer.toString());
                }
                if (!TextUtils.isEmpty(remarkStr)) {
                    jsonObject.put("7", remarkStr);
                }

            } catch (Exception error) {
                Logger.w(error.getMessage() + "");
            }
            //提交随手记数据
            Logger.i("addRecord: " + jsonObject.toString());
            saveNoteData(jsonObject.toString());
        });

    }

    /**
     * 保存当前报告的记录数据
     */
    private void saveNoteData(String noteStr) {
        MeasureReportCenter.addRemark(reportId, noteStr, new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ResultPair data) {
                dismissDialog();
                BlackToast.show(R.string.string_save_success);
                setResult(ReportDetailActivity.RESULT_CODE_NOTE_ADD);
                finish();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                }
            }
        });
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_add_report_notes;
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_add_note);
    }

}

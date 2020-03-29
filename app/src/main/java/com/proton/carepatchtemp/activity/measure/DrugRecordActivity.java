package com.proton.carepatchtemp.activity.measure;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.activity.report.AddReportNotesActivity;
import com.proton.carepatchtemp.adapter.ReportNoteAdapter;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivityDrugRecordBinding;
import com.proton.carepatchtemp.net.bean.NoteBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.MeasureReportCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.view.DashLineDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 用药记录
 * <传入extra>
 * reportId String 报告id
 * </extra>
 */
public class DrugRecordActivity extends BaseActivity<ActivityDrugRecordBinding> {
    /**
     * 添加随手记录帖子
     */
    private final int REQUEST_CODE_ADD_NOTE = 1;
    /**
     * 报告id
     */
    private String reportId;
    /**
     * 帖子记录列表数据
     */
    private List<NoteBean> noteBeanList;
    private ReportNoteAdapter reportNoteAdapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteBeanList = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_NOTE:
                if (resultCode == AddReportNotesActivity.RESULT_CODE_ADD_REMARK) {
                    //刷新报告帖子
                    initData();
                }
                break;
        }
    }

    @Override
    protected void init() {
        super.init();
        Intent mIntent = getIntent();
        reportId = mIntent.getStringExtra("reportId");
        noteBeanList = new ArrayList<>();
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.title.setText(getResString(R.string.string_drug_record));
        binding.idIncludeRecord.idRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.idIncludeRecord.idRecyclerview.addItemDecoration(new DashLineDecoration());
        reportNoteAdapter = new ReportNoteAdapter(this, noteBeanList, R.layout.item_report_addnote);
        binding.idIncludeRecord.idRecyclerview.setAdapter(reportNoteAdapter);
        initRefreshLayout(binding.idIncludeRecord.idRefreshLayout, refreshlayout -> {
            initData();
        }, null);
    }

    @Override
    protected void initData() {

        if (!App.get().isLogined()) {
            setLoadError();
            return;
        }

        super.initData();
        initNotesRecord(reportId);
    }

    /**
     * 初始化随手记录列表数据
     */
    private void initNotesRecord(String reportId) {
        if (TextUtils.isEmpty(reportId)) {
            return;
        }
        MeasureReportCenter.getCommentList(reportId, new NetCallBack<List<NoteBean>>() {
            @Override
            public void noNet() {
                super.noNet();
                binding.idIncludeRecord.idRefreshLayout.finishRefresh();
                BlackToast.show(R.string.string_no_net);
                setLoadTimeOut();
            }

            @Override
            public void onSucceed(List<NoteBean> data) {
                if (data == null) {
                    return;
                }
                noteBeanList.clear();
                noteBeanList.addAll(data);
                if (noteBeanList != null && noteBeanList.size() > 0) {
                    reportNoteAdapter.setDatas(noteBeanList);
                    setLoadSuccess();
                } else {
                    setLoadEmpty();
                }
                binding.idIncludeRecord.idRefreshLayout.finishRefresh();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                binding.idIncludeRecord.idRefreshLayout.finishRefresh();
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                }
                setLoadError();
            }
        });
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idBtnAdd.setOnClickListener(v -> {
            //添加用药记录
            startActivityForResult(new Intent(DrugRecordActivity.this, AddReportNotesActivity.class).putExtra("reportId", reportId), REQUEST_CODE_ADD_NOTE);
        });
        //用药记录侧滑删除
        reportNoteAdapter.setAdapterChildClickListener((holder, view) -> {
            switch (view.getId()) {
                case R.id.id_iv_delete_note:
                    //随手记录一个帖子的删除
                    ((SwipeMenuLayout) holder.getView(R.id.id_swipe_refresh_layout)).quickClose();
                    long commentId = noteBeanList.get(holder.getItemPosition()).getId();
                    deleteNote(commentId);
                    break;
            }
        });
    }

    /**
     * @param commentId 一条记录id
     */
    private void deleteNote(long commentId) {
        MeasureReportCenter.deleteNote(commentId, new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ResultPair data) {
                //删除成功
                initData();
                BlackToast.show(R.string.string_delete_success);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                } else {
                    BlackToast.show(R.string.string_delete_failed);
                }
            }
        });
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_drug_record;
    }

    @Override
    protected int getNotLoginTips() {
        return R.string.string_not_login_can_not_view_drugs_record;
    }

    @Override
    protected View getEmptyAndLoadingView() {
        if (App.get().isLogined()) {
            return binding.idIncludeRecord.idRecyclerview;
        } else {
            return binding.idLoadingLayout;
        }
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.string_drug_record_empty);
    }
}

package com.proton.carepatchtemp.activity.report;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.activity.common.AdapterChildClickListener;
import com.proton.carepatchtemp.adapter.ReportNoteAdapter;
import com.proton.carepatchtemp.bean.ReportBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.databinding.ActivityReportDetailBinding;
import com.proton.carepatchtemp.fragment.report.ShareReportFragment;
import com.proton.carepatchtemp.net.bean.NoteBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.MeasureReportCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.ThreadUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.DashLineDecoration;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;
import com.proton.temp.connector.bean.TempDataBean;
import com.wms.adapter.CommonViewHolder;
import com.wms.logger.Logger;
import com.wms.utils.DensityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 测量报告详情
 * <传入extra>
 * String reportId    报告id
 * String maxTemp 最高温度
 * String reportUrlPath 报告网络下载地址
 * long starttime 测量开始时间
 * long endtime 测量结束时间
 * long profileId 档案id
 * String profileName 档案姓名
 * </>
 */
public class ReportDetailActivity extends BaseActivity<ActivityReportDetailBinding> implements AdapterChildClickListener, OnChartValueSelectedListener, ShareReportFragment.ShareReportListener {
    public static final String TAG = "ReportDetailActivity_chart";
    /**
     * 添加记录返回码
     */
    public static final int RESULT_CODE_NOTE_ADD = 2;
    /**
     * 请求添加记录
     */
    private final int REQUEST_CODE_NOTE_ADD = 1;
    /**
     * 报告id
     */
    private String reportId;
    /**
     * 最高温度、最低温度
     */
    private String maxTemp;

    /**
     * 高温报警最大温度
     */
    protected float mWarmHighestTemp = 37.10f;
    /**
     * 高温报警最小温度
     */
    protected float mWarmLowestTemp = 0f;

    /**
     * 报告网络下载地址
     */
    private String reportUrlPath;
    /**
     * 档案所属姓名
     */
    private String profileName;
    private ReportNoteAdapter reportNoteAdapter;
    private List<NoteBean> noteBeanList = new ArrayList<>();
    private Handler mHandler = new Handler();
    /**
     * 传入过来测量开始的时间
     */
    private long starttime;
    /**
     * 测量结束的时间
     */
    private long endTime;
    /**
     * 传递过来的档案id
     */
    private long profileId;
    private ReportBean reportBean;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_NOTE_ADD:
                if (resultCode == RESULT_CODE_NOTE_ADD) {
                    //刷新记录数据
                    initNotesRecord();
                }
                break;
        }
    }

    @Override
    protected void init() {
        super.init();
        Intent mIntent = getIntent();
        reportId = mIntent.getStringExtra("reportId");
        maxTemp = mIntent.getStringExtra("maxTemp");
        starttime = mIntent.getLongExtra("starttime", 0);
        reportUrlPath = mIntent.getStringExtra("reportUrlPath");
        endTime = mIntent.getLongExtra("endtime", 0);
        profileId = mIntent.getLongExtra("profileId", 0);
        profileName = mIntent.getStringExtra("profileName");
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        reportNoteAdapter = new ReportNoteAdapter(this, noteBeanList, R.layout.item_report_addnote);
        reportNoteAdapter.setAdapterChildClickListener(this);
        binding.idRecyclerview.setAdapter(reportNoteAdapter);
        binding.idRecyclerview.addItemDecoration(new DashLineDecoration());
        binding.idIncludeTop.title.setText(R.string.string_report_detail);
        binding.idIncludeTop.idIvRightOperte.setImageResource(R.drawable.icon_share_green);
        ViewGroup.LayoutParams params = binding.idIncludeTop.idIvRightOperte.getLayoutParams();
        params.width = DensityUtils.dip2px(this, 31);
        params.height = DensityUtils.dip2px(this, 39);
        binding.idIncludeTop.idIvRightOperte.setLayoutParams(params);
        binding.idIncludeTop.idIvRightOperte.setVisibility(View.VISIBLE);
        //初始化最高体温数据
        initMaxTempData();
        try {
            fetchChartData();
        } catch (Exception error) {
            Logger.w(error.getMessage());
        }
    }

    /**
     * 获取图标数据
     */
    @SuppressWarnings("checkResult")
    private void fetchChartData() {
        showDialog(getResources().getString(R.string.string_loading));
        ThreadUtils.runOnOtherThread(new Runnable() {
            @Override
            public void run() {
                String reportStr;//档案文件转成的字符串码
                //本地有无当前这个报告
                if (!cn.trinea.android.common.util.FileUtils.isFileExist(Utils.getLocalReportPath(reportUrlPath))) {
                    Logger.i("report_check: no report:—>" + reportId);
                    //下载报告
                    if (TextUtils.isEmpty(reportUrlPath)) {
                        runOnUiThread(() -> {
                            dismissDialog();
                            BlackToast.show(R.string.string_data_error);
                        });
                        return;
                    }
                    reportStr = FileUtils.downLoadFromUrl(reportUrlPath, FileUtils.json_filepath);
                } else {
                    //本地已有这个报告
                    Logger.i("report_check: report exist!!:—>" + reportId);
                    try {
                        reportStr = FileUtils.readJSONFile(Utils.getLocalReportPath(reportUrlPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            dismissDialog();
                            BlackToast.show(R.string.string_load_fail);
                        });
                        return;
                    }
                }
                //第一阶段文件转换为JSON对象
                reportBean = JSONUtils.getObj(reportStr, ReportBean.class);
                if (null == reportBean) {
                    mHandler.post(() -> {
                        Logger.w("Load report file failed ! Please retry!");
                        FileUtils.deleteFile(FileUtils.json_filepath, String.valueOf(reportId));
                        dismissDialog();
                    });
                    return;
                } else {
                    //之前的版本算法和姿势状态是没有上传的，这里需要做兼容处理
                    boolean isOldVersion = false;
                    List<TempDataBean> allTemps = new ArrayList<>();
                    List<Long> tempTimes = reportBean.getTempTimes();
                    List<Integer> states = reportBean.getStates();
                    List<Integer> gestures = reportBean.getGestures();
                    List<Float> sourceTemps = reportBean.getSourceTemps();//真实温度
                    List<Float> processTemps = reportBean.getProcessTemps();//算法温度

                    //没有温度数据
                    if (sourceTemps == null || sourceTemps.size() == 0 || processTemps == null || processTemps.size() == 0) {
                        dismissDialog();
                        return;
                    }
                    long minSize;
                    //没有状态和姿势数据
                    if (states == null || states.size() == 0 || gestures == null || gestures.size() == 0) {
                        isOldVersion = true;
                        minSize = Math.min(tempTimes.size(), Math.min(processTemps.size(), sourceTemps.size()));
                    } else {
                        //规避集合大小不一致，防止崩溃
                        minSize = Math.min(tempTimes.size(), Math.min(Math.min(states.size(), gestures.size()), Math.min(processTemps.size(), sourceTemps.size())));
                    }

                    for (int i = 0; i < minSize - 1; i++) {
                        TempDataBean tempDataBean = new TempDataBean(tempTimes.get(i), sourceTemps.get(i), processTemps.get(i));
                        if (!isOldVersion) {
                            tempDataBean.setMeasureStatus(states.get(i));
                            tempDataBean.setGesture(gestures.get(i));
                        } else {
                            tempDataBean.setMeasureStatus(0);
                            tempDataBean.setGesture(0);
                        }
                        allTemps.add(tempDataBean);
                    }

                    if (allTemps == null || allTemps.size() <= 0) {
                        dismissDialog();
                        return;
                    }
                    Logger.w("");
                    runOnUiThread(() -> {
                        binding.idCurveView.setChartType(App.get().getInstructionConstant());
                        binding.idCurveView.addDatas(allTemps, mWarmHighestTemp, mWarmLowestTemp);
                        dismissDialog();
                    });
                }
            }
        });

    }


    @Override
    protected void initData() {
        super.initData();
        initNotesRecord();
    }

    /**
     * 初始化随手记录列表数据
     */
    private void initNotesRecord() {
        if (TextUtils.isEmpty(reportId)) {
            return;
        }
        MeasureReportCenter.getCommentList(reportId, new NetCallBack<List<NoteBean>>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(List<NoteBean> data) {
                if (data == null) {
                    return;
                }
                noteBeanList.clear();
                noteBeanList.addAll(data);
                reportNoteAdapter.setDatas(noteBeanList);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                }
            }
        });
    }

    private void initMaxTempData() {
        //温度单位设置
        int tempUnit = SpUtils.getInt(AppConfigs.SP_KEY_TEMP_UNIT, AppConfigs.TEMP_UNIT_DEFAULT);
        if (AppConfigs.SP_VALUE_TEMP_C == tempUnit) {
            binding.idTvTempUnit.setText(App.get().getText(R.string.string_temp_C));
        } else if (AppConfigs.SP_VALUE_TEMP_F == tempUnit) {
            binding.idTvTempUnit.setText(App.get().getText(R.string.string_temp_F));
        }
        if (maxTemp == null) {
            return;
        }
        Float maxTempFloat = Float.parseFloat(maxTemp);
        TextView highestTempTv = binding.idTvHighestTemp;
        TextView temTipTv = binding.idTvNoteTipTxt;
        //最高体温赋值
        highestTempTv.setText(UIUtils.currMaxTemp(maxTempFloat));
        int currentTempUnit = SpUtils.getInt(AppConfigs.SP_KEY_TEMP_UNIT, AppConfigs.TEMP_UNIT_DEFAULT);
        //温度范围
        int tempLevel = UIUtils.tempLevel(maxTempFloat);
        switch (tempLevel) {
            case 0:
                //正常体温
                temTipTv.setText(R.string.string_temp_normal);
                break;
            case 1:
                //低热
                temTipTv.setText(String.format(getString(R.string.string_temp_low_fever), Utils.getTempAndUnit(37.00f), Utils.getTempAndUnit(38.00f), Utils.getTempAndUnit(40.00f), Utils.getTempAndUnit(38.00f)));
                break;
            case 2:
                //中热
                temTipTv.setText(String.format(getString(R.string.string_temp_high_fever), Utils.getTempAndUnit(37.00f), Utils.getTempAndUnit(38.00f), Utils.getTempAndUnit(40.00f)));
                break;
            case 3:
                //高热
                temTipTv.setText(R.string.string_temp_very_highFever);
                break;
            default:
                //非正常体温
                temTipTv.setText(String.format(getString(R.string.string_out_range), Utils.getTempAndUnit(36.00f), Utils.getTempAndUnit(42.99f)));
                break;
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        //添加随手记录
        binding.idTvAddnotes.setOnClickListener(v -> {
            Intent mIntent = new Intent(ReportDetailActivity.this, AddReportNotesActivity.class);
            mIntent.putExtra("reportId", reportId);
            startActivityForResult(mIntent, REQUEST_CODE_NOTE_ADD);
        });
        //曲线图大图展示
        binding.idIvChartRotate.setOnClickListener(v -> startActivity(new Intent(this, ReportLandscapChartActivity.class)
                .putExtra("reportId", reportId)
                .putExtra("maxTemp", maxTemp)
                .putExtra("reportUrlPath", reportUrlPath)
                .putExtra("starttime", starttime)));
        //分享
        binding.idIncludeTop.idIvRightOperte.setOnClickListener(v -> {
            ShareReportFragment shareReportFragment = new ShareReportFragment();
            Bundle paramsBdle = new Bundle();
            //报告id
            paramsBdle.putString("reportId", reportId);
            //测量起始时间
            paramsBdle.putLong("starttime", starttime);
            //测量结束时间
            paramsBdle.putLong("endtime", endTime);
            //档案id
            paramsBdle.putLong("profileId", profileId);
            //最高体温
            paramsBdle.putString("maxTemp", maxTemp);
            //档案测量姓名
            paramsBdle.putString("profileName", profileName);
            //报告详情
            paramsBdle.putString("reportUrlPath", reportUrlPath);
            shareReportFragment.setArguments(paramsBdle);
            shareReportFragment.setShareReportListener(this);
            shareReportFragment.show(getFragmentManager(), reportId);
        });
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_report_detail;
    }

    protected BaseViewModel getViewModel() {
        return null;
    }

    //随手记录Item点击事件
    @Override
    public void onClick(CommonViewHolder commonViewHolder, View view) {
        switch (view.getId()) {
            case R.id.id_iv_delete_note:
                //随手记录一个帖子的删除
                ((SwipeMenuLayout) commonViewHolder.getView(R.id.id_swipe_refresh_layout)).quickClose();
                long commentId = noteBeanList.get(commonViewHolder.getItemPosition()).getId();
                deleteNote(commentId);
                break;
        }
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
                initNotesRecord();
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

    /**
     * 重载体温图表选中值后的变化
     */
    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    /**
     * 分享创建png图片文件(在子线程中操作)
     */
    @Override
    public void createPng() {
        Bitmap reportChartBp = binding.idCurveView.mLineChart.getChartBitmap();
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(FileUtils.getReport() + File.separator + reportId + ".png");
            reportChartBp.compress(Bitmap.CompressFormat.PNG, 40, stream);
            stream.flush();
            stream.close();
            //回收
            reportChartBp = null;
        } catch (Exception error) {
            Logger.w(error.getMessage());
        }
    }
}

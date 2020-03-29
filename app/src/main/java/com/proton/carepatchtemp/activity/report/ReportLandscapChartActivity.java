package com.proton.carepatchtemp.activity.report;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.bean.ReportBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivityReportLandscapChartBinding;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.StatusBarUtil;
import com.proton.carepatchtemp.utils.ThreadUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.temp.connector.bean.TempDataBean;
import com.wms.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 体温曲线大图查看
 * <传入extra>
 * String reportId    报告id
 * String maxTemp 最高温度
 * String reportUrlPath 报告网络下载地址
 * long starttime 测量开始时间
 * </>
 */
public class ReportLandscapChartActivity extends BaseActivity<ActivityReportLandscapChartBinding> implements OnChartValueSelectedListener {
    ArrayList<Entry> mYVals = new ArrayList<>();
    private String reportId;//报告id
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

    private String reportUrlPath;//报告网络下载地址
    private long starttime;//报告开始测量的时间
    private Handler mHandler = new Handler();
    private ReportBean reportBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        StatusBarUtil.hideFakeStatusBarView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mYVals = null;
    }

    @Override
    protected void init() {
        super.init();
        Intent mItent = getIntent();
        reportId = mItent.getStringExtra("reportId");
        maxTemp = mItent.getStringExtra("maxTemp");
        reportUrlPath = mItent.getStringExtra("reportUrlPath");
        starttime = mItent.getLongExtra("starttime", 0);
        try {
            fetchChartData();
        } catch (Exception error) {
            Logger.w(error.getMessage());
        }
        //初始化最高体温数据
        binding.idTvMaxTemp.setText(Utils.getFormartTempStr(Float.valueOf(maxTemp)));
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
    protected int inflateContentView() {
        return R.layout.activity_report_landscap_chart;
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_temp_chart);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}

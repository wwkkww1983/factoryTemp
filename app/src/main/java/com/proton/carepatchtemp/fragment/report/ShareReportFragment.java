package com.proton.carepatchtemp.fragment.report;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.report.PrePDFActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.database.ProfileManager;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.bean.ReportBeanData;
import com.proton.carepatchtemp.socailauth.PlatformType;
import com.proton.carepatchtemp.socailauth.SocialApi;
import com.proton.carepatchtemp.socailauth.listener.ShareListener;
import com.proton.carepatchtemp.socailauth.share_media.IShareMedia;
import com.proton.carepatchtemp.socailauth.share_media.ShareFileMedia;
import com.proton.carepatchtemp.socailauth.share_media.ShareWebMedia;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.utils.FormatUtils;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.Settings;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.ThreadUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.utils.net.OSSUtils;
import com.proton.carepatchtemp.utils.pdf.PdfUtil;
import com.proton.carepatchtemp.view.chartview.DayAxisValueFormatter;
import com.proton.carepatchtemp.view.chartview.MyAxisValueFormatter;
import com.proton.carepatchtemp.view.chartview.P03AxisValueFormatter;
import com.proton.carepatchtemp.view.chartview.XYMarkerView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXFileObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.wms.logger.Logger;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.Type;
import io.reactivex.Observable;

/**
 * Created by luochune on 2018/4/8.
 * 备注: 这个类如果设置 ShareReportListener 将用来生成png报告图片
 * 分享dialog
 * <bundle arguments>
 * reportId String 报告id
 * starttime long 报告起始时间
 * endTime long 报告结束时间
 * maxTemp String 最高温度
 * profileId long 档案id
 * profileName String 档案姓名
 * reportUrlPath String 报告下载地址
 * </arguments>
 */

public class ShareReportFragment extends DialogFragment {
    private final int finishCreatePdfWhat = 2;
    /**
     * 没有设置ShareReportListener通知
     */
    private final int noReportPicMsgWhat = 3;
    /**
     * 生成pdf时档案未找到
     */
    private final int profileNotFound = 1;
    /**
     * 报告不存在发送的消息int
     */
    private final int reportNoExistMsgWhat = 1;
    private LinearLayout mLl_wechat;
    private LinearLayout mLl_wechat_circle;
    private LinearLayout mLl_wechat_mail;
    private LinearLayout mLl_wechat_prelook;
    /**
     * 分享列表中默默生成图表
     */
    private LineChart lineChartView;
    /**
     * 体温图表x轴数值转换器
     */
    private IAxisValueFormatter mXAxisFormatter;
    private TextView mTv_cancel_share;
    private ShareReportListener shareReportListener;
    /**
     * pdf 文件
     */
    private File pdfFile;
    /**
     * pdf 文件路径
     */
    private String pdfFilePath;
    private SweetAlertDialog mDialog;
    /**
     * 报告下载地址
     */
    private String reportUrlPath;
    /**
     * 报告id
     */
    private String reportId = "";
    /**
     * 起始时间
     */
    private long starttime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 最高温度
     */
    private String maxTemp;
    /**
     * 档案姓名
     */
    private String profileName;
    /**
     * 图表点集合
     */
    private ArrayList<Entry> mChartEntryList = new ArrayList<>();
    /**
     * 图表点的y集合
     */
    private ArrayList<Entry> yVals1 = new ArrayList<>();
    /**
     * 报告不存在时下载的报告实体对象
     */
    private ReportBeanData reportBeanData;
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.arg1) {
                case noReportPicMsgWhat:
                    BlackToast.show(R.string.string_no_picFile);
                    break;
                case reportNoExistMsgWhat:
                    BlackToast.show(R.string.string_report_not_exist);
                    break;
                case finishCreatePdfWhat:
                    //选择哪种分享方式
                    int shareId = msg.arg2;
                    //创建pdf结束
                    mDialog.dismiss();
                    File pdfFile = new File(pdfFilePath);
                    if (!pdfFile.exists()) {
                        mHandler.sendEmptyMessage(reportNoExistMsgWhat);
                        return;
                    } else {
                        BlackToast.show(R.string.string_pdf_createSuccess);
                        switch (shareId) {
                            case 0:
                                shareWeChat();
                                break;
                            case 1:
                                shareWechatCircle();
                                break;
                            case 2:
                                shareMail();
                                break;
                            case 3:
                                prePdf();
                                break;
                            default:
                                break;
                        }
                    }
                    break;
            }
        }
    };
    /**
     * 档案id
     */
    private long profileId;
    /**
     * y 左侧y坐标
     */
    private YAxis mLeftAxis;


    /**
     * 微信分享，登录工具类
     */
    private SocialApi mSocialApi;


    // private ReportListItemBean mReport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle paramsBdle = getArguments();
        reportId = paramsBdle.getString("reportId", "");
        starttime = paramsBdle.getLong("starttime", 0);
        endTime = paramsBdle.getLong("endtime", 0);
        profileId = paramsBdle.getLong("profileId", profileId);
        maxTemp = paramsBdle.getString("maxTemp", "0");
        profileName = paramsBdle.getString("profileName");
        reportUrlPath = paramsBdle.getString("reportUrlPath");
        Logger.i("share_report:" + reportId);

        mSocialApi = SocialApi.get(getActivity());
    }

    //  使得菜单宽度能够适应屏幕
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (null != dialog) {
            dialog.getWindow().setLayout(-1, -2);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //  菜单位于底部设置
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        pdfFilePath = FileUtils.getDirectoryP("pdf") + File.separator + String.valueOf(reportId) + ".pdf";
        View rootView = inflater.inflate(R.layout.fragment_share_report, container, false);
        bindViews(rootView);

        initClick();
        mDialog = new SweetAlertDialog(ActivityManager.currentActivity());
        mDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        mDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        mDialog.setTitleText(UIUtils.getString(R.string.string_pdf_creating));
        mDialog.changeAlertType(Type.PROGRESS_TYPE);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initClick() {
        mTv_cancel_share.setOnClickListener(v -> {
            dismiss();
        });

        mLl_wechat.setOnClickListener(v -> {
            //先测试微信分享
            pdfFile = new File(pdfFilePath);
            if (!pdfFile.exists()) {
                createPDF(0);
            } else {
                shareWeChat();
            }
            dismiss();
        });

        mLl_wechat_circle.setOnClickListener(v -> {
            pdfFile = new File(pdfFilePath);
            if (!pdfFile.exists()) {
                createPDF(1);
            } else {
                shareWechatCircle();
            }
            dismiss();
        });

        mLl_wechat_mail.setOnClickListener(v -> {
            pdfFile = new File(pdfFilePath);
            if (!pdfFile.exists()) {
                createPDF(2);
            } else {
                shareMail();
            }
        });

        mLl_wechat_prelook.setOnClickListener(v -> {
            if (!new File(pdfFilePath).exists()) {
                createPDF(3);
            } else {
                prePdf();
            }
            dismiss();
        });
    }

    /**
     * @param i 0:微信分享  1:朋友圈分享   2:邮件分享  3:预览pdf
     */
    private void createPDF(int i) {
        mDialog.show();
        //本地本地图表png不存在，先生成，生成成功后再调用创建pdf
        if (!FileUtils.isFileExist(FileUtils.report, reportId + ".png")) {
            createChart(i, lineChartView);
            return;
        }
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                if (!FileUtils.isFileExist(FileUtils.report, reportId + ".png") && shareReportListener == null) {
                    mHandler.sendEmptyMessage(noReportPicMsgWhat);
                    return null;
                    //TODO 当前页中生成图表
                    // createChart(lineChartView);
                } else if (shareReportListener != null) {
                    //报告详情中生成图片
                    shareReportListener.createPng();
                }
                /**pdf报告异步线程生成**/
                PdfUtil pdfUtil = new PdfUtil();
                //   String msg = VCare.getACacheInstance().getAsString(Extras.ACHCHE_PROFILE);
                //  mProfile = ProfileManager.getProfile(mReport.getProfileid());
                ProfileBean profileBean = ProfileManager.getProfileBean(profileId);
                if (profileBean == null) {
                    mHandler.sendEmptyMessage(profileNotFound);
                    return null;
                }
                String sex = 1 == profileBean.getGender() ? UIUtils.getString(R.string.string_boy) : UIUtils.getString(R.string.string_girl);
                String fever;
                if (Float.parseFloat(maxTemp) > 37.5) {
                    fever = UIUtils.getString(R.string.string_yes);
                } else {
                    fever = UIUtils.getString(R.string.string_false);
                }
                String testTime = FormatUtils.formatTime(endTime - starttime);
                String tempUnit = UIUtils.getString(R.string.string_temp_C);
                Float maxTempFloat = Float.parseFloat(maxTemp);
                if (AppConfigs.SP_VALUE_TEMP_C == SpUtils.getInt(AppConfigs.SP_KEY_TEMP_UNIT, AppConfigs.TEMP_UNIT_DEFAULT)) {
                    tempUnit = UIUtils.getString(R.string.string_temp_C);
                } else {
                    maxTempFloat = FormatUtils.c2F(maxTempFloat);
                    tempUnit = UIUtils.getString(R.string.string_temp_F);
                }
                pdfUtil.createPdf(ActivityManager.currentActivity(), profileBean.getRealname(), sex, profileBean.getAge(), maxTempFloat + "", fever, testTime, tempUnit, reportId);
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject aVoid) {
                super.onPostExecute(aVoid);
                Message msg = new Message();
                msg.arg1 = finishCreatePdfWhat;
                msg.arg2 = i;
                mHandler.sendMessage(msg);
                //mHandler.sendEmptyMessage(finishCreatePdfWhat);
            }
        }.execute();
        // }
    }

    /**
     * 初始化体温图表
     *
     * @param i 图表创建来时分享渠道
     */
    private void createChart(int i, LineChart lineChart) {
        initTempChart(lineChart);
        try {
            setChartData(i, reportUrlPath, reportId, starttime, lineChart);
        } catch (Exception error) {
            Logger.w(error.getMessage());
        }

    }

    /**
     * 设置图表的填充数据
     *
     * @param shareId 图表创建来时分享渠道
     */
    private void setChartData(int shareId, String reportUrlPath, String reportId, long starttime, LineChart lineChart) {
        //  showDialog(getResources().UIUtils.getString(R.string.string_loading));
        ThreadUtils.runOnOtherThread(() -> {
            boolean isCTempUnit = true;//温度单位是否是C摄氏温度
            isCTempUnit = Utils.isSelsiusUnit();
            String reportStr = "";//档案文件转成的字符串码
            // JSONObject reportJsonObj;
            //本地有无当前这个报告
            if (!cn.trinea.android.common.util.FileUtils.isFileExist(Utils.getLocalReportPath(reportUrlPath))) {
                Logger.i("report_check: no report:—>" + reportId);
                //下载报告
                if (TextUtils.isEmpty(reportUrlPath)) {
                    ActivityManager.currentActivity().runOnUiThread(() -> {
                        //  dismissDialog();
                        //mDialog.dismiss();
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
                    ActivityManager.currentActivity().runOnUiThread(() -> {
                        //  mDialog.dismiss();
                        BlackToast.show(R.string.string_load_fail);
                    });
                    reportStr = null;
                    return;
                }
            }
            //第一阶段文件转换为JSON对象
          /*  try {
                reportJsonObj = new JSONObject(reportStr);
            } catch (JSONException e) {
                reportJsonObj = null;
                e.printStackTrace();
            }*/
            //第一阶段文件转换为JSON对象
            reportBeanData = JSONUtils.getObj(reportStr, ReportBeanData.class);
            if (null == reportBeanData) {
                Logger.w("Load report file failed ! Please retry!");
                lineChart.setNoDataText(UIUtils.getString(R.string.string_load_fail));
                FileUtils.deleteFile(FileUtils.json_filepath, String.valueOf(reportId));
                mHandler.post(() -> {
                    //mDialog.dismiss();
                    if (null != lineChart.getData()) {
                        lineChart.getData().notifyDataChanged();
                        lineChart.notifyDataSetChanged();
                    }
                    lineChart.invalidate();
                });
                return;
            } else {
                //报告json obj不为空
                if (mChartEntryList != null) {
                    mChartEntryList.clear();
                } else {
                    mChartEntryList = new ArrayList<Entry>();
                }
                yVals1.clear();
                LimitLine limitLine = new LimitLine(Utils.getTemp(37.1f), Utils.getTempAndUnit(37.1f));
                limitLine.setTextSize(10f);
                limitLine.setLineWidth(2f);
                limitLine.enableDashedLine(10f, 10f, 0f);
                limitLine.setTextColor(ContextCompat.getColor(App.get(), R.color.color_orange_ff6));
                limitLine.setLineColor(ContextCompat.getColor(App.get(), R.color.color_orange_ff6));
                mLeftAxis.addLimitLine(limitLine);
                //是否包含RawData字段
                List<Float> list = reportBeanData.getFloats();
                if (list == null || list.size() == 0) {
                    //数据为空
                    BlackToast.show(R.string.string_data_empty);
                    return;
                }
                //最高体温数值
                String maxTemp = String.valueOf(Collections.max(list));
                Iterator<Float> iterator = list.iterator();
                int i = 0;
                while (iterator.hasNext()) {
                    if (isCTempUnit) {
                        //C 温度单位
                        yVals1.add(new BarEntry(i, iterator.next()));
                        i++;
                    } else {
                        //F 温度单位
                        yVals1.add(new BarEntry(i, FormatUtils.c2F(iterator.next())));
                        i++;
                    }
                }
                LineDataSet mLineDataSet;
                if (lineChart.getData() != null &&
                        lineChart.getData().getDataSetCount() > 0) {
                    mLineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                    mLineDataSet.setValues(yVals1);
                    lineChart.getData().notifyDataChanged();
                    lineChart.notifyDataSetChanged();
                } else {
                    mLineDataSet = new LineDataSet(yVals1, "The year 2018");
                    mLineDataSet.setDrawFilled(true);
                    mLineDataSet.setDrawCircles(false);
                    mLineDataSet.disableDashedLine();
                    // mLineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
                    mLineDataSet.setColor(Color.TRANSPARENT);
                    mLineDataSet.setCircleColor(Color.BLACK);
                    mLineDataSet.setLineWidth(0f);
                    mLineDataSet.setDrawCircleHole(false);
                    mLineDataSet.setDrawValues(false);
                    mLineDataSet.setFormLineWidth(1f);
                    mLineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                    mLineDataSet.setFormSize(15.f);
                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(mLineDataSet); // add the datasets
                    Drawable drawable = ContextCompat.getDrawable(ActivityManager.currentActivity(), R.drawable.line_chart);
                    drawable.setAlpha(120);//图表透明度
                    mLineDataSet.setFillDrawable(drawable);
                    LineData data = new LineData(dataSets);
                    data.setDrawValues(false);
                    //mDialog.dismiss();
                    lineChart.setData(data);
                    if (Float.valueOf(maxTemp) < 30) {
                        Description description = new Description();
                        description.setText(UIUtils.getString(R.string.string_out_of_range_temp));
                        lineChart.setDescription(description);
                    } else {
                        lineChart.setDescription(null);
                    }
                }
                //更新界面
                mHandler.post(() -> {
                    XAxis xAxis = lineChart.getXAxis();
                    List<Long> timeList = reportBeanData.getTimes();
                    XYMarkerView mv = null;
                    if (timeList != null && timeList.size() > 0) {
                        //用横向数值点作为时间
                        //x 轴赋值
                        mXAxisFormatter = new P03AxisValueFormatter(timeList);
                        xAxis.setValueFormatter(mXAxisFormatter);
                        //markview 赋值
                        mv = new XYMarkerView(ActivityManager.currentActivity(), starttime, timeList);
                    } else {
                        //x 轴赋值
                        mXAxisFormatter = new DayAxisValueFormatter();
                        ((DayAxisValueFormatter) mXAxisFormatter).setStartTime(starttime);
                        xAxis.setValueFormatter(mXAxisFormatter);
                        //mark view 赋值
                        mv = new XYMarkerView(ActivityManager.currentActivity(), starttime, null);
                    }

                    xAxis.setGridColor(Color.parseColor("#e0e0e0"));
                    mv.setChartView(lineChart); // For bounds control
                    lineChart.setMarker(mv); // Set the marker to the chart
                    if (null != lineChart.getData()) {
                        lineChart.getData().notifyDataChanged();
                        lineChart.notifyDataSetChanged();
                    }
                    lineChart.invalidate();
                    //lineChart.setVisibility(View.VISIBLE);
                    Observable
                            .just(1)
                            .delay(2, TimeUnit.SECONDS)
                            .subscribe(integer -> {
                                createChartPng(shareId, lineChart);
                            });
                });
            }

        });
    }

    /**
     * 生成图表png图并关闭页面
     *
     * @param i 图表分享渠道
     */
    private void createChartPng(int i, LineChart lineChart) {
        Bitmap reportChartBp = lineChart.getChartBitmap();
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(FileUtils.getReport() + File.separator + reportId + ".png");
            reportChartBp.compress(Bitmap.CompressFormat.PNG, 40, stream);
            stream.flush();
            stream.close();
            //回收
            reportChartBp = null;
            createPDF(i);
        } catch (Exception error) {
            Logger.w(error.getMessage());
        }
    }

    /**
     * 初始化体温图表
     */
    private void initTempChart(LineChart lineChart) {
        //lineChart.getDescription().setEnabled(false);
        lineChart.setDrawMarkers(true);
        lineChart.setPinchZoom(true);
        lineChart.setScaleYEnabled(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setVisibleYRange(Utils.getTemp(Settings.CURVE_MIN_TEMP), Utils.getTemp(Settings.CURVE_MAX_TEMP), YAxis.AxisDependency.LEFT);
        lineChart.setDrawBorders(false);
        lineChart.animateX(1000);
        lineChart.setNoDataText(UIUtils.getString(R.string.string_loading));
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelCount(5);
        xAxis.setAxisMinimum(0);
        mXAxisFormatter = new DayAxisValueFormatter();
        xAxis.setValueFormatter(mXAxisFormatter);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        IAxisValueFormatter custom = new MyAxisValueFormatter();
        mLeftAxis = lineChart.getAxisLeft();
        mLeftAxis.setGridColor(getResources().getColor(R.color.color_gray_e0));
        mLeftAxis.setAxisMinimum(Utils.getTemp(Settings.CURVE_MIN_TEMP));
        mLeftAxis.setAxisMaximum(Utils.getTemp(Settings.CURVE_MAX_TEMP));
        mLeftAxis.setValueFormatter(custom);
        mLeftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        mLeftAxis.setSpaceTop(15f);
        mLeftAxis.enableGridDashedLine(10f, 10f, 0f);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        Legend l = lineChart.getLegend();
        l.setEnabled(false);
    }

    /**
     * 预览pdf
     */
    private void prePdf() {
        ActivityManager.currentActivity().startActivity(new Intent(ActivityManager.currentActivity(), PrePDFActivity.class).putExtra("filePath", pdfFilePath));
    }

    /**
     * 分享到邮件
     */
    private void shareMail() {
        ThreadUtils.runOnOtherThread(() -> {
            String type = "application/pdf";
            Uri uri;
            Intent intent = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(ActivityManager.currentActivity(),
                        App.get().getPackageName() + ".FileProvider",
                        new File(pdfFilePath));
            } else {
                uri = Uri.fromFile(new File(pdfFilePath));
            }
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            // 主题
            intent.putExtra(Intent.EXTRA_SUBJECT, UIUtils.getString(R.string.string_carepatch_report));
            intent.putExtra(Intent.EXTRA_TEXT, profileName + UIUtils.getString(R.string.string_one_temp_rpt)); // 正文
            intent.setType(type);
            intent.setType("image/*");
            intent.setType("message/rfc882");
            startActivity(Intent.createChooser(intent, getString(R.string.string_choose_email_application)));
        });
    }

    /**
     * 分享到朋友圈
     */
    private void shareWechatCircle() {
        ThreadUtils.runOnOtherThread(() -> {
            //先上传pdf报告到阿里云(也可上传一次后做保存)
            String ossReportUrl = OSSUtils.uploadSharePdf(FileUtils. getByte(pdfFile), reportId);
            if (TextUtils.isEmpty(ossReportUrl)) {
                mHandler.sendEmptyMessage(reportNoExistMsgWhat);
            } else {

      /*          WXWebpageObject webpage = new WXWebpageObject();
                webpage.webpageUrl = OSSUtils.getSaveUrl(ossReportUrl);
                Logger.i("ossReportUrl: " + OSSUtils.getSaveUrl(ossReportUrl));
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = webpage;
                msg.title = profileName + UIUtils.getString(R.string.string_one_temp_rpt);
                msg.description = UIUtils.getString(R.string.string_from_carepatch_report);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = String.valueOf(System.currentTimeMillis());
                req.message = msg;
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                App.get().wxApi.sendReq(req);*/

                IShareMedia shareMedia = new ShareWebMedia();
                ((ShareWebMedia) shareMedia).setWebPageUrl(OSSUtils.getSaveUrl(ossReportUrl));
                ((ShareWebMedia) shareMedia).setDescription(UIUtils.getString(R.string.string_from_carepatch_report));
                ((ShareWebMedia) shareMedia).setTitle(profileName + UIUtils.getString(R.string.string_one_temp_rpt));
                mSocialApi.doShare(getActivity(), PlatformType.WEIXIN_CIRCLE, shareMedia, new ShareListener() {
                    @Override
                    public void onComplete(PlatformType platform_type) {
                        Logger.w("微信分享onComplete");
//                        BlackToast.show(R.string.string_share_success);
                    }

                    @Override
                    public void onError(PlatformType platform_type, String err_msg) {
                        Logger.w("微信分享onError", err_msg);
                        BlackToast.show(err_msg);
                    }

                    @Override
                    public void onCancel(PlatformType platform_type) {
                        Logger.w("微信分享onCancel");
                        BlackToast.show(R.string.string_share_cancel);
                    }
                });

            }
            dismiss();
        });

    }

    /**
     * 微信分享
     */
    private void shareWeChat() {
     /*   WXFileObject fileobject = new WXFileObject();
        fileobject.setFilePath(pdfFilePath);
        WXMediaMessage mWXMediaMessage = new WXMediaMessage();
        mWXMediaMessage.mediaObject = fileobject;
        mWXMediaMessage.title = profileName + UIUtils.getString(R.string.string_one_temp_rpt);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("name");
        req.message = mWXMediaMessage;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        if (App.get().wxApi.sendReq(req)) {
            Logger.w("share_report_success");
        }
*/
        ShareFileMedia shareMedia = new ShareFileMedia();
        shareMedia.setTitle(profileName + UIUtils.getString(R.string.string_one_temp_rpt)+".pdf");
        shareMedia.setFilePath(pdfFilePath);
        mSocialApi.doShare(getActivity(), PlatformType.WEIXIN, shareMedia, new ShareListener() {
            @Override
            public void onComplete(PlatformType platform_type) {
                Logger.w("微信分享onComplete");
//                BlackToast.show(R.string.string_share_success);
            }

            @Override
            public void onError(PlatformType platform_type, String err_msg) {
                Logger.w("微信分享onError", err_msg);
                BlackToast.show(err_msg);
            }

            @Override
            public void onCancel(PlatformType platform_type) {
                Logger.w("微信分享onCancel");
                BlackToast.show(R.string.string_share_cancel);
            }
        });

        dismiss();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void bindViews(View rootView) {
        mLl_wechat = rootView.findViewById(R.id.ll_wechat);
        mLl_wechat_circle = rootView.findViewById(R.id.ll_wechat_circle);
        mLl_wechat_mail = rootView.findViewById(R.id.ll_wechat_mail);
        mLl_wechat_prelook = rootView.findViewById(R.id.ll_wechat_prelook);
        mTv_cancel_share = rootView.findViewById(R.id.tv_cancel_share);
        lineChartView = rootView.findViewById(R.id.id_line_chart);
        //国际版不适用微信分享
        if (AppConfigs.isInternal) {
            mLl_wechat_circle.setVisibility(View.GONE);
            mLl_wechat.setVisibility(View.GONE);
        }
    }

    public void setShareReportListener(ShareReportListener shareReportListener) {
        this.shareReportListener = shareReportListener;
    }

    public interface ShareReportListener {
        void createPng();
    }
}

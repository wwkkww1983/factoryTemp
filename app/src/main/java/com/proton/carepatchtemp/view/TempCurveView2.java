package com.proton.carepatchtemp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.enums.InstructionConstant;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.temp.connector.bean.TempDataBean;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangmengsi on 2018/3/28.
 * 实时温度曲线
 */

public class TempCurveView2 extends FrameLayout {
    public LineChart mLineChart;
    private LineData mLineData;
    /**
     * 最大温度
     */
    private float mHighestTemp = Utils.getTemp(37.50f);
    /**
     * 最低温度
     */
    private float mLowestTemp = Utils.getTemp(35.00f);
    /**
     * 保存温度数据
     */
    private List<TempDataBean> mTempList = new ArrayList<>();
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private LimitLine highestLine, lowLimitLine;

    /**
     * 温度曲线类型
     */
    private InstructionConstant chartType = InstructionConstant.aa;

    /**
     * 原始温度
     */
    public static final String REAL_LABEL = "实时温度";
    /**
     * 算法温度，显示给用户的温度
     */
    public static final String ALGORITHM_LABEL = "算法温度";

    public static final String ALGORITHM_STATUS_LABEL = "算法状态";
    public static final String ALGORITHM_GESTURE_LABEL = "算法姿势";

    public TempCurveView2(@NonNull Context context) {
        this(context, null);
    }

    public TempCurveView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempCurveView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_temp_curve, this);
        mLineChart = findViewById(R.id.id_line_chart);
        initLineChart();
        initLineData();
    }

    /**
     * 设置曲线类型
     *
     * @param chartType
     */
    public void setChartType(InstructionConstant chartType) {
        this.chartType = chartType;
        switch (chartType) {
            case aa://算法温度曲线
                initDataSet(ALGORITHM_LABEL, R.color.color_algorithm_temp);
                mLineChart.getAxisRight().setEnabled(false);
                break;
            case ab://算法温度+真实温度曲线
                initDataSet(ALGORITHM_LABEL, R.color.color_algorithm_temp);
                initDataSet(REAL_LABEL, R.color.color_real_temp);
                initDataSet(ALGORITHM_STATUS_LABEL, R.color.color_algorithm_status);
                initDataSet(ALGORITHM_GESTURE_LABEL, R.color.black);
                mLineChart.getAxisRight().setEnabled(true);
                break;
            case bb://真实温度曲线
                initDataSet(REAL_LABEL, R.color.color_real_temp);
                initDataSet(ALGORITHM_STATUS_LABEL, R.color.color_algorithm_status);
                initDataSet(ALGORITHM_GESTURE_LABEL, R.color.black);
                mLineChart.getAxisRight().setEnabled(true);
                break;
        }
        addMarkView();
    }

    @SuppressLint("SetTextI18n")
    private void addLineLimit() {
        if (highestLine == null) {
            highestLine = new LimitLine(mHighestTemp, Utils.getTempAndUnit(mHighestTemp));
            highestLine.setTextSize(10f);
            highestLine.setLineWidth(3f);
            highestLine.enableDashedLine(10f, 10f, 0f);
            highestLine.setTextColor(Color.parseColor("#ff604f"));
            highestLine.setLineColor(Color.parseColor("#ff604f"));
            mLineChart.getAxisLeft().addLimitLine(highestLine);
        }

        if (lowLimitLine == null) {
            lowLimitLine = new LimitLine(mLowestTemp, Utils.getTempAndUnit(mLowestTemp));
            lowLimitLine.setTextSize(10f);
            lowLimitLine.setLineWidth(3f);
            lowLimitLine.enableDashedLine(10f, 10f, 0f);
            lowLimitLine.setTextColor(Color.parseColor("#7aabe2"));
            lowLimitLine.setLineColor(Color.parseColor("#7aabe2"));
            mLineChart.getAxisLeft().addLimitLine(lowLimitLine);
        }
    }

    private void initLineData() {
        if (mLineData == null) {
            mLineData = new LineData();
            mLineData.setDrawValues(false);
        }
        mLineChart.setData(mLineData);
    }

    private void initLineChart() {
        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleEnabled(true);
        mLineChart.setPinchZoom(false);
        mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setScaleYEnabled(false);

        mLineChart.getXAxis().setDrawGridLines(true);
        mLineChart.getXAxis().setGridColor(Color.parseColor("#E8E8E8"));
        mLineChart.getXAxis().enableGridDashedLine(dip2px(10), dip2px(10), 0);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        //x轴文字
        mLineChart.getXAxis().setValueFormatter((value, axis) -> {

            if (value < 0 || value >= mTempList.size()) {
                return "";
            }
            if (value == (int) value) {
                return format.format(mTempList.get((int) value).getTime());
            } else {
                return "";
            }
        });

        YAxis leftAxis = mLineChart.getAxisLeft();
        leftAxis.setAxisMaximum(42.0f);
        leftAxis.setAxisMinimum(30.0f);
        leftAxis.enableGridDashedLine(dip2px(10), dip2px(10), 0);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E8E8E8"));
        leftAxis.setDrawLabels(true);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return Utils.getTempAndUnit(value);
            }
        });

        YAxis rightAxis = mLineChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(15f);

        mLineChart.getViewPortHandler().setMaximumScaleX(1);
        mLineChart.getViewPortHandler().setMaximumScaleY(1);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setNoDataText(getResources().getString(R.string.string_no_chart_data));
    }

    private void addMarkView() {
        final DecimalFormat tempFormat = new DecimalFormat("###.00");
        CurveMarkView markView = new CurveMarkView(getContext(), chartType == InstructionConstant.bb ? 15 : 0);
//        final NewMarkerView markView = new NewMarkerView(getContext());
        markView.setOnMarkViewListener((timeText, tempText, e) -> {
            String chartLabel = (String) e.getData();
            if (chartLabel.equalsIgnoreCase(REAL_LABEL) || chartLabel.equalsIgnoreCase(ALGORITHM_LABEL)) {
                timeText.setText(getContext().getString(R.string.string_time) + format.format(mTempList.get((int) e.getX()).getTime()));
                tempText.setText(getContext().getString(R.string.string_temp) + Utils.getTempAndUnit(tempFormat.format(e.getY())));
            } else if (chartLabel.equalsIgnoreCase(ALGORITHM_STATUS_LABEL)) {
                timeText.setText(getContext().getString(R.string.string_time) + format.format(mTempList.get((int) e.getX()).getTime()));
                tempText.setText(getContext().getString(R.string.string_algorithm_status) + getBigDecimalStatus((e.getY() - 30) * 1.25));
            } else if (chartLabel.equalsIgnoreCase(ALGORITHM_GESTURE_LABEL)) {
                timeText.setText(getContext().getString(R.string.string_time) + format.format(mTempList.get((int) e.getX()).getTime()));
                tempText.setText(getContext().getString(R.string.string_algorithm_gesture) + getBigDecimalStatus((e.getY() - 30) * 1.25));
            }
        });
        markView.setChartView(mLineChart); // For bounds control
        mLineChart.setMarker(markView); // Set the marker to the chart
    }


    public void addDatas(List<TempDataBean> datas, float highWarmTemp, float loweWarmTemp) {

        LineDataSet dataSetReal = (LineDataSet) mLineData.getDataSetByLabel(REAL_LABEL, true);
        LineDataSet dataSetAlgorithm = (LineDataSet) mLineData.getDataSetByLabel(ALGORITHM_LABEL, true);

        LineDataSet dataSetAlgorithmStatus = (LineDataSet) mLineData.getDataSetByLabel(ALGORITHM_STATUS_LABEL, true);
        LineDataSet dataSetAlgorithmGesture = (LineDataSet) mLineData.getDataSetByLabel(ALGORITHM_GESTURE_LABEL, true);


        if (datas == null || datas.size() <= 0) {
//            entries.clear();
            mLineChart.clear();
            return;
        }
        for (TempDataBean dataBean : datas) {
            mTempList.add(dataBean);
            addEntry(dataBean, dataSetReal);
            addEntry(dataBean, dataSetAlgorithm);
            addEntry(dataBean, dataSetAlgorithmStatus);
            addEntry(dataBean, dataSetAlgorithmGesture);
        }

        mHighestTemp = highWarmTemp;
        mLowestTemp = loweWarmTemp;
        addLineLimit();
        mLineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
    }

    public void addData(float data, float algorithmTemp, int status, int gesture) {
        TempDataBean tempDataBean = new TempDataBean(System.currentTimeMillis(), data, algorithmTemp);
        tempDataBean.setMeasureStatus(status);
        tempDataBean.setGesture(gesture);
        mTempList.add(tempDataBean);
        if (chartType != InstructionConstant.bb) {
            LineDataSet dataSetAlgorithm = (LineDataSet) mLineData.getDataSetByLabel(ALGORITHM_LABEL, true);
            addEntry(tempDataBean, dataSetAlgorithm);
        }

        if (chartType != InstructionConstant.aa) {
            LineDataSet dataSetReal = (LineDataSet) mLineData.getDataSetByLabel(REAL_LABEL, true);
            LineDataSet dataSetAlgorightmStatus = (LineDataSet) mLineData.getDataSetByLabel(ALGORITHM_STATUS_LABEL, true);
            LineDataSet dataSetAlgorighmGesture = (LineDataSet) mLineData.getDataSetByLabel(ALGORITHM_GESTURE_LABEL, true);
            addEntry(tempDataBean, dataSetReal);
            addEntry(tempDataBean, dataSetAlgorightmStatus);
            addEntry(tempDataBean, dataSetAlgorighmGesture);
        }
        addLineLimit();
        mLineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
        mLineChart.getViewPortHandler().refresh(new Matrix(), mLineChart, true);
    }

    private void addEntry(TempDataBean data, LineDataSet dataSet) {
        if (dataSet == null) {
            return;
        }
        String label = dataSet.getLabel();
        if (label.equalsIgnoreCase(REAL_LABEL)) {
            dataSet.addEntry(new Entry(dataSet.getEntryCount(), data.getTemp(), REAL_LABEL));
        } else if (label.equalsIgnoreCase(ALGORITHM_LABEL)) {
            dataSet.addEntry(new Entry(dataSet.getEntryCount(), data.getAlgorithmTemp(), ALGORITHM_LABEL));
        } else if (label.equalsIgnoreCase(ALGORITHM_STATUS_LABEL)) {
            dataSet.addEntry(new Entry(dataSet.getEntryCount(), statusOrGestureSwitch(data.getMeasureStatus()), ALGORITHM_STATUS_LABEL));
        } else if (label.equalsIgnoreCase(ALGORITHM_GESTURE_LABEL)) {
            dataSet.addEntry(new Entry(dataSet.getEntryCount(), statusOrGestureSwitch(data.getGesture()), ALGORITHM_GESTURE_LABEL));
        }
    }

    private float dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return dpValue * scale;
    }

    /**
     * 初始化算法温度曲线数据
     */
    private void initDataSet(String lable, int color) {
        //图例
        Legend legend = mLineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);

        //数据
        LineDataSet mDataSet = new LineDataSet(null, lable);
        mDataSet.setDrawCircles(false);
        mDataSet.setDrawCircleHole(false);
        mDataSet.setDrawValues(false);//是否显示折线上的数据
        mDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        mDataSet.setFormSize(15.f);
        mDataSet.enableDashedHighlightLine(10f, 10f, 0f);
        if (chartType == InstructionConstant.aa) {
            Drawable fillDrawable = ContextCompat.getDrawable(getContext(), R.drawable.drawable_line_chart);
            fillDrawable.setAlpha(120);
            mDataSet.setFillDrawable(fillDrawable);
            mDataSet.setDrawFilled(true);
            legend.setEnabled(false);
            mDataSet.setFormLineWidth(1f);
            mDataSet.disableDashedLine();
            mDataSet.setColor(Color.TRANSPARENT);
            mDataSet.setCircleColor(Color.BLACK);
            mDataSet.setLineWidth(0f);
            mDataSet.setCircleRadius(3f);
            mDataSet.setDrawFilled(true);
            mDataSet.setHighLightColor(Color.BLACK);
        } else {
            legend.setEnabled(true);
            mDataSet.setFormLineWidth(3f);
            mDataSet.setColor(ContextCompat.getColor(getContext(), color));
            mDataSet.setCircleColor(Color.BLACK);
            if (lable.equalsIgnoreCase(ALGORITHM_GESTURE_LABEL) || lable.equalsIgnoreCase(ALGORITHM_STATUS_LABEL)) {
                mDataSet.setMode(LineDataSet.Mode.STEPPED);
            } else {
                mDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
            }
        }
        mLineData.addDataSet(mDataSet);
    }

    /**
     * 把状态和姿势的权限整体向上移动
     *
     * @param data
     * @return
     */
    private float statusOrGestureSwitch(int data) {
        float percent = 12 / 15f;
        return 30 + data * percent;
    }

    /**
     * 姿势、状态取整
     *
     * @param staOrGes
     * @return
     */
    private int getBigDecimalStatus(double staOrGes) {
        BigDecimal bigDecimal = new BigDecimal(staOrGes).setScale(0, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.intValue();
    }

}

package com.proton.carepatchtemp.view.chartview;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.utils.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class XYMarkerView extends MarkerView {

    private TextView tvContent;

    private DecimalFormat format;

    private long startTime = 0;
    /**
     * p03温度图表的时间点
     */
    private List<Long> times;
    private SimpleDateFormat formatter;

    public XYMarkerView(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.custom_marker_view);
        formatter = new SimpleDateFormat("HH:mm:ss");
        tvContent = findViewById(R.id.tvContent);
        format = new DecimalFormat("###.00");
    }

    public XYMarkerView(Context context, long startTime, List<Long> times) {
        super(context, R.layout.custom_marker_view);
        formatter = new SimpleDateFormat("HH:mm:ss");
        tvContent = findViewById(R.id.tvContent);
        format = new DecimalFormat("###.00");
        this.startTime = startTime;
        if (times != null && times.size() > 0) {
            this.times = times;
        }
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
//        if (25F < e.getY() && e.getY() < 42F) {
        String unit = "℃";
        if (Utils.isSelsiusUnit()) {
            unit = "℃";
        } else {
            unit = "℉";
        }
        if (times == null) {
            long time = startTime + (int) e.getX() * AppConfigs.TEMP_LOAD_TIME_DIV_MISECONDS;
            tvContent.setText(getContext().getString(R.string.string_time) + formatter.format(time) + "\n" + getContext().getString(R.string.string_temp) + format.format(e.getY()) + unit);
        } else {
            int xAxisNum = (int) e.getX();
            if (xAxisNum < times.size()) {
                //毫秒单位
                long time = times.get(xAxisNum) * 1000;
                tvContent.setText(getContext().getString(R.string.string_time) + formatter.format(time) + "\n" + getContext().getString(R.string.string_temp) + format.format(e.getY()) + unit);
            }
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}

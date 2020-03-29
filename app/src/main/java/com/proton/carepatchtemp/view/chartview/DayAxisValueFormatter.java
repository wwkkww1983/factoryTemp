package com.proton.carepatchtemp.view.chartview;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.proton.carepatchtemp.constant.AppConfigs;

import java.text.SimpleDateFormat;

/**
 * Created by philipp on 02/06/16.
 */
public class DayAxisValueFormatter implements IAxisValueFormatter {

    private long startTime = 0;
    private long div = AppConfigs.TEMP_LOAD_TIME_DIV_MISECONDS;
    private SimpleDateFormat formatter;

    public DayAxisValueFormatter() {
        formatter = new SimpleDateFormat("HH:mm:ss");//初始化Formatter的转换格式。
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        long time = startTime + (int) value * div;
        return formatter.format(time);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}

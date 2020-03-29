package com.proton.carepatchtemp.view.chartview;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by luochune on 2018/5/10.
 */

public class P03AxisValueFormatter implements IAxisValueFormatter {
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    private List<Long> timeXaisList;

    public P03AxisValueFormatter(List<Long> timeXaisList) {
        this.timeXaisList = timeXaisList;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        if (timeXaisList != null && timeXaisList.size() > 0 && value < timeXaisList.size()) {
            if (value==(int)value) {
                long time = timeXaisList.get((int) value);
                return format.format(time * 1000);
            }else {
                return "";
            }

        } else {
            return "";
        }
    }
}

package com.proton.carepatchtemp.view;

import android.content.Context;
import android.util.TypedValue;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.proton.carepatchtemp.R;

/**
 * Created by wangmengsi on 2018/3/28.
 */
public class CurveMarkView extends MarkerView {

    private TextView mIdTime;
    private TextView mIdTemp;
    private OnMarkViewListener onMarkViewListener;
    /**
     * y轴上的间距
     */
    private int yOffset;

    public CurveMarkView(Context context, int yOffset) {
        super(context, R.layout.layout_curve_mark);
        mIdTime = findViewById(R.id.id_time);
        mIdTemp = findViewById(R.id.id_temp);
        this.yOffset = yOffset;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (onMarkViewListener != null) {
            onMarkViewListener.onMarkViewSelected(mIdTime, mIdTemp, e);
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        if (yOffset != 0) {
            return new MPPointF(-(getWidth() / 2), -getHeight() - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, yOffset, getResources().getDisplayMetrics()));
        } else {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }

    }

    public void setOnMarkViewListener(OnMarkViewListener onMarkViewListener) {
        this.onMarkViewListener = onMarkViewListener;
    }

    public interface OnMarkViewListener {
        void onMarkViewSelected(TextView timeText, TextView tempText, Entry e);
    }
}

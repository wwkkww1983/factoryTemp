package com.proton.carepatchtemp.view.chartview;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.proton.carepatchtemp.R;


/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class NewMarkerView extends MarkerView {

    private TextView mIdTime;
    private TextView mIdTemp;
    private LinearLayout llRootview;
    private OnMarkViewListener onMarkViewListener;

    public NewMarkerView(Context context) {
        super(context, R.layout.layout_curve_mark);
        mIdTime = findViewById(R.id.id_time);
        mIdTemp = findViewById(R.id.id_temp);
        llRootview=findViewById(R.id.id_rootview);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (onMarkViewListener != null) {
            onMarkViewListener.onMarkViewSelected(mIdTime, mIdTemp, e);
        }
        super.refreshContent(e, highlight);
    }

 /*   @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() - TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics()));
    }
*/
    @Override
    public MPPointF getOffset() {
        llRootview.setBackgroundResource(R.drawable.chart_popu);
        return new MPPointF(0, -getHeight());
    }

    @Override
    public MPPointF getOffsetRight() {
        llRootview.setBackgroundResource(R.drawable.chart_popu_right);
        return new MPPointF(-getWidth(), -getHeight());
    }


    public void setOnMarkViewListener(OnMarkViewListener onMarkViewListener) {
        this.onMarkViewListener = onMarkViewListener;
    }

    public interface OnMarkViewListener {
        void onMarkViewSelected(TextView timeText, TextView tempText, Entry e);
    }
}

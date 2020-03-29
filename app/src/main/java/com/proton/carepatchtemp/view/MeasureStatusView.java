package com.proton.carepatchtemp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.proton.carepatchtemp.R;

/**
 * Created by wangmengsi on 2018/3/22.
 */

public class MeasureStatusView extends View {

    private Paint mPaint;
    private RectF mRect;
    private int mCircleWidth;

    public MeasureStatusView(Context context) {
        this(context, null);
    }

    public MeasureStatusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeasureStatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mCircleWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7.5f, getResources().getDisplayMetrics());
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_temp_normal));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        width = width - 2 * mCircleWidth;
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        w = w - 2 * mCircleWidth;
        mRect = new RectF(-w / 2, -w / 2, w / 2, w / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getMeasuredWidth() / 2 + mCircleWidth, getMeasuredHeight() / 2 + mCircleWidth);
        canvas.drawArc(mRect, 135, 270, false, mPaint);
    }

    public void setStatus(Status status) {
        int color = R.color.color_temp_normal;
        switch (status) {
            case Normal:
                color = R.color.color_temp_normal;
                break;
            case High:
                color = R.color.color_temp_high;
                break;
            case Low:
                color = R.color.color_temp_low;
                break;
        }
        mPaint.setColor(ContextCompat.getColor(getContext(), color));
        invalidate();
    }

    public enum Status {
        Normal, High, Low
    }
}

package com.proton.carepatchtemp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.proton.carepatchtemp.R;

/**
 * Created by 王梦思 on 2017/7/8.
 */

public class CountDownProgress extends View {

    /**
     * 外圆画笔
     */
    private Paint mCirclePaint = new Paint();

    private Paint mUnfinishPaint = new Paint();
    /**
     * 进度条画笔
     */
    private Paint mProgressPaint = new Paint();
    /**
     * 文字画笔
     */
    private Paint mTextPaint = new Paint();
    /**
     * 外圆颜色
     */
    private int mCircleColor = Color.parseColor("#0095e4");
    /**
     * 进度条颜色
     */
    private int mProgressColor = Color.parseColor("#0095e4");

    /**
     * 文字颜色
     */
    private int mTextColor = Color.GRAY;
    private int mCircleRadius;
    private RectF mRectF = new RectF();
    private float mProgress = 0;
    private CountDownTimer mCountDown;
    private String mCenterText = "";
    private float mTextWidth;
    //圆圈宽度
    private float mCircleWidth;
    private float borderValue;
    // 外圆线宽
    private float borderWidth;
    private float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
    private float mBaseLine;
    private long mFinalTime;
    private boolean isRunning;

    public CountDownProgress(Context context) {
        this(context, null);
    }

    public CountDownProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountDownProgress, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CountDownProgress_circleColor:
                    // 渐变色之起始颜色，默认设置为红色
                    mCircleColor = a.getColor(attr, Color.parseColor("#0095e4"));
                    break;
                case R.styleable.CountDownProgress_max:
                    break;
                case R.styleable.CountDownProgress_finishedColor:
                    // 渐变色之结束颜色，默认设置为品红
                    mProgressColor = a.getColor(attr, Color.parseColor("#ffffff"));
                    break;
                case R.styleable.CountDownProgress_borderCircleWidth:
                    // 进度条默认颜色，默认设置为灰色
                    borderWidth = a.getDimension(attr, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CountDownProgress_circleWidth:
                    mCircleWidth = a.getDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CountDownProgress_centerTextColor:
                    mTextColor = a.getColor(attr, Color.GRAY);
                    break;

                case R.styleable.CountDownProgress_centerTextSize:
                    textSize = a.getDimension(attr, textSize);
                    break;
            }
        }
        a.recycle();
        init();
    }

    private void init() {
        initPaint(mCirclePaint, mCircleColor);
        initPaint(mProgressPaint, mProgressColor);
        initPaint(mTextPaint, mTextColor);
        initPaint(mUnfinishPaint, Color.WHITE);

        borderValue = (mCircleWidth - borderWidth) / 2F;

        mUnfinishPaint.setStrokeWidth(mCircleWidth - borderWidth);
        mUnfinishPaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(borderValue);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mCircleWidth);
        mTextPaint.setTextSize(textSize);

    }

    private void initPaint(Paint paint, int color) {
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCircleRadius = (int) (w / 2 - mCircleWidth * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制外圆
        canvas.save();
        canvas.translate(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        canvas.drawCircle(0, 0, mCircleRadius, mCirclePaint);
        canvas.drawCircle(0, 0, mCircleRadius - mCircleWidth, mCirclePaint);
        mRectF.set(-mCircleRadius + mCircleWidth / 2F, -mCircleRadius + mCircleWidth / 2F, mCircleRadius - mCircleWidth / 2F, mCircleRadius - mCircleWidth / 2F);
        canvas.drawArc(mRectF, 270, mProgress, false, mProgressPaint);
        canvas.restore();
        canvas.save();
        //绘制中间文字
        if (!TextUtils.isEmpty(mCenterText)) {
            canvas.translate(getMeasuredWidth() / 2 - mTextWidth / 2, getMeasuredHeight() / 2);
            canvas.drawText(mCenterText, 0, mBaseLine, mTextPaint);
        }
        canvas.restore();
    }

    /**
     * 设置倒计时
     *
     * @param time 单位秒
     */
    public void startCountDown(long time, final OnCountDownFinishListener listener) {

        mFinalTime = time * 1000;

        setVisibility(VISIBLE);

        mCountDown = new CountDownTimer(mFinalTime, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTextWidth = mTextPaint.measureText(getContext().getString(R.string.string_preheating));
                mProgress = (mFinalTime - millisUntilFinished) / (mFinalTime * 1.0f / 360);
                if (listener != null) {
                    listener.onTick(millisUntilFinished);
                }
                setText(getContext().getString(R.string.string_preheating));
                invalidate();
            }

            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onFinish();
                }
                isRunning = false;
            }
        };

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mBaseLine = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        mCountDown.start();
        isRunning = true;
    }

    public void cancelCountDown() {
        if (mCountDown != null) {
            mCountDown.cancel();
        }
        setVisibility(View.GONE);
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setText(String text) {
        this.mCenterText = text;
    }

    public interface OnCountDownFinishListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }
}

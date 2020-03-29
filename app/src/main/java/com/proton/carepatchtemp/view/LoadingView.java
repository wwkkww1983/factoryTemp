package com.proton.carepatchtemp.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.wms.utils.CommonUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 王梦思 on 2016/6/21 0021.
 * <p/>
 * 加載动画 ...
 */
public class LoadingView extends View {

    /**
     * view默认大小
     */
    public static final int DEFAULT_SIZE = 30;
    /**
     * 间距
     */
    public static final int SPACING = 4;
    private static final float SCALE = 1;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 颜色
     */
    private int mColor;
    private float[] scales = new float[]{
            SCALE,
            SCALE,
            SCALE
    };
    /**
     * 存储动画
     */
    private List<WeakReference<ValueAnimator>> mAnimators = new ArrayList<>();

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
//        mColor = a.getColor(R.styleable.LoadingView_loading_color, Color.WHITE);
//        a.recycle();

        init();
        startAnimation();
    }

    /**
     * 应用动画
     */
    public void startAnimation() {
        int[] delays = new int[]{120, 240, 360};
        for (int i = 0; i < 3; i++) {
            WeakReference<ValueAnimator> animator = new WeakReference<>(ValueAnimator.ofFloat(1, 0.3f, 1));
            animator.get().setDuration(750);
            animator.get().setRepeatCount(-1);
            animator.get().setStartDelay(delays[i]);

            final int finalI = i;
            animator.get().addUpdateListener(animation -> {
                scales[finalI] = (float) animation.getAnimatedValue();
                postInvalidate();
            });

            animator.get().start();
            mAnimators.add(animator);
        }
    }

    private void init() {
        mColor = ContextCompat.getColor(getContext(), R.color.color_loading);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(dp2px(DEFAULT_SIZE), widthMeasureSpec);
        int height = measureDimension(dp2px(DEFAULT_SIZE), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getVisibility() == GONE || getVisibility() == INVISIBLE) {
            return;
        }
        int radius = (getWidth() - SPACING * 2) / 6;
        int x;
        int y = getHeight() / 2;

        for (int i = 0; i < 3; i++) {
            canvas.save();
            x = radius * (i + 1) + (radius + SPACING) * i;
            canvas.translate(x, y);
            canvas.scale(scales[i], scales[i]);
            canvas.drawCircle(0, 0, radius, mPaint);
            canvas.restore();
        }
    }

    @Override
    public void setVisibility(int v) {
        super.setVisibility(v);
        if (v == GONE || v == INVISIBLE) {
            stopAnimation();
        } else {
            startAnimation();
        }
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    private int dp2px(int dpValue) {
        return (int) getContext().getResources().getDisplayMetrics().density * dpValue;
    }

    public void stopAnimation() {
        if (!CommonUtils.listIsEmpty(mAnimators)) {
            for (WeakReference<ValueAnimator> animator : mAnimators) {
                if (animator.get() != null) {
                    animator.get().cancel();
                }
            }
            mAnimators.clear();
        }

        scales = new float[]{
                SCALE,
                SCALE,
                SCALE
        };
    }
}

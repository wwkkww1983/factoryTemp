package com.proton.carepatchtemp.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by luochune on 2018/4/22.
 */

public class DashLineDecoration extends RecyclerView.ItemDecoration {
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //以下计算主要用来确定绘制的位置
            final int top = child.getBottom() + params.bottomMargin;

            //绘制虚线
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor("#3AB8FB"));
            Path path = new Path();
            path.moveTo(left, top);
            path.lineTo(right, top);
            PathEffect effects = new DashPathEffect(new float[]{15, 15, 15, 15}, 5);//此处单位是像素不是dp  注意 请自行转化为dp
            paint.setPathEffect(effects);
            c.drawPath(path, paint);


        }
    }

   /* @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            //以下计算主要用来确定绘制的位置
            final int top = child.getBottom() + params.bottomMargin;

            //绘制虚线
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor("#3AB8FB"));
            Path path = new Path();
            path.moveTo(left, top);
            path.lineTo(right, top);
            PathEffect effects = new DashPathEffect(new float[]{DensityUtils.dip2px(App.get(), 5), DensityUtils.dip2px(App.get(), 5), DensityUtils.dip2px(App.get(), 5), DensityUtils.dip2px(App.get(), 5)},DensityUtils.dip2px(App.get(), 5));//此处单位是像素不是dp  注意 请自行转化为dp
            paint.setPathEffect(effects);
            c.drawPath(path, paint);
        }
    }*/
}

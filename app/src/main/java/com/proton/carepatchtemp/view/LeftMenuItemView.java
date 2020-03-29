package com.proton.carepatchtemp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.wms.utils.DensityUtils;

/**
 * Created by wangmengsi on 2018/2/27.
 */

public class LeftMenuItemView extends FrameLayout {
    private ImageView imageView;
    private int normalImg;
    private int selectImg;

    public LeftMenuItemView(@NonNull Context context) {
        this(context, null);
    }

    public LeftMenuItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LeftMenuItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_left_menu_item, this, true);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LeftMenuItemView, defStyleAttr, 0);
        int n = a.getIndexCount();
        String text = "";
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.LeftMenuItemView_left_menu_normal_img:
                    // 渐变色之起始颜色，默认设置为红色
                    normalImg = a.getResourceId(attr, 0);
                    break;
                case R.styleable.LeftMenuItemView_left_menu_select_img:
                    selectImg = a.getResourceId(attr, 0);
                    break;
                case R.styleable.LeftMenuItemView_left_menu_text:
                    text = a.getString(attr);
                    break;
            }
        }
        a.recycle();

        imageView = findViewById(R.id.id_img);
        imageView.setImageResource(normalImg);
        TextView textView = findViewById(R.id.id_text);
        textView.setText(text);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightSize > DensityUtils.dip2px(getContext(), 61)) {
            heightSize = DensityUtils.dip2px(getContext(), 61);
        }
        imageView.setLayoutParams(new LinearLayout.LayoutParams(heightSize, heightSize));
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize, heightMode));
    }

    public void setSelect() {
        imageView.setImageResource(selectImg);
    }

    public void setUnSelect() {
        imageView.setImageResource(normalImg);
    }
}

package com.proton.carepatchtemp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.proton.carepatchtemp.R;

/**
 * Created by 王梦思 on 2018/12/5.
 * <p/>
 */
public class EllipsizeTextView extends FrameLayout {

    private TextView firstTextView, ellipsizeTextView;
    private int maxLength = -1;
    private String text;
    private String ellipsizeText = "...";

    public EllipsizeTextView(Context context) {
        this(context, null);
    }

    public EllipsizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EllipsizeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_ellipsize_textview, this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.EllipsizeTextView);
        maxLength = a.getInteger(R.styleable.EllipsizeTextView_ellipsize_maxLength, maxLength);

        firstTextView = findViewById(R.id.id_first_text);
        ellipsizeTextView = findViewById(R.id.id_ellipsize_text);

        firstTextView.setTextColor(a.getColor(R.styleable.EllipsizeTextView_ellipsize_textColor, Color.BLACK));
        ellipsizeTextView.setTextColor(a.getColor(R.styleable.EllipsizeTextView_ellipsize_textColor, Color.BLACK));
        firstTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.EllipsizeTextView_ellipsize_textSize
                , (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics())));
        ellipsizeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimension(R.styleable.EllipsizeTextView_ellipsize_textSize
                , (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics())));

        setText(a.getString(R.styleable.EllipsizeTextView_ellipsize_text));
        String ellipsizeText = a.getString(R.styleable.EllipsizeTextView_ellipsize_ellipsizeText);
        if (TextUtils.isEmpty(ellipsizeText)) {
            ellipsizeText = this.ellipsizeText;
        }
        ellipsizeTextView.setText(ellipsizeText);
        a.recycle();
    }

    public EllipsizeTextView setText(String tempText) {
        this.text = tempText;
        if (maxLength != -1 && maxLength > 0 && !TextUtils.isEmpty(tempText) && tempText.length() > maxLength) {
            tempText = tempText.substring(0, maxLength);
            int[] length = length(tempText);

            if (length[1] != 0) {
                maxLength = maxLength + length[1] / 2;
            }

            if (this.text.length() > maxLength) {
                tempText = this.text.substring(0, maxLength);
            }

            ellipsizeTextView.setVisibility(View.VISIBLE);
        } else {
            ellipsizeTextView.setVisibility(View.GONE);
        }
        firstTextView.setText(tempText);
        return this;
    }

    public EllipsizeTextView setTextColor(@ColorInt int color) {
        firstTextView.setTextColor(color);
        ellipsizeTextView.setTextColor(color);
        return this;
    }

    public EllipsizeTextView setTextSize(int size) {
        firstTextView.setTextSize(size);
        ellipsizeTextView.setTextSize(size);
        return this;
    }

    public EllipsizeTextView setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        setText(text);
        return this;
    }

    public void setEllipsizeText(String text) {
        this.ellipsizeText = text;
        ellipsizeTextView.setText(text);
    }

    public String getText() {
        return text;
    }

    /**
     * 获取中文和其他文字长度
     */
    private int[] length(String value) {
        int chineseLength = 0;
        int otherLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                chineseLength += 1;
            } else {
                otherLength += 1;
            }
        }
        return new int[]{chineseLength, otherLength};
    }
}

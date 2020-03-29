package com.proton.carepatchtemp.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.proton.carepatchtemp.component.App;

/**
 * Created by luochune on 2018/4/26.
 */

public class CustomClickableSpan extends ClickableSpan {
    /**
     * 连接文字颜色
     */
    private int highLightColor;
    /**
     * 是否需要文字下划线
     */
    private boolean isNeedUnderLine = false;
    private SpanClickListener spanClickListener;
    private int clickPosition;

    public CustomClickableSpan(int highLightColor, boolean isNeedUnderLine, int position, SpanClickListener spanClickListener) {
        this.highLightColor = highLightColor;
        this.isNeedUnderLine = isNeedUnderLine;
        this.spanClickListener = spanClickListener;
        this.clickPosition = position;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(App.get().getResources().getColor(highLightColor));
        ds.setUnderlineText(isNeedUnderLine);
    }

    @Override
    public void onClick(View widget) {
        if (spanClickListener != null) {
            spanClickListener.clickPosition(clickPosition);
        }
    }

    public interface SpanClickListener {
        void clickPosition(int position);

    }
}

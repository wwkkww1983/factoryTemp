package com.proton.carepatchtemp.view;


import android.view.View;

/**
 * 双击事件
 * Created by yuxiongfeng.
 * Date: 2019/8/20
 */
public class OnDoubleClickListener implements View.OnClickListener {

    private int count = 0;
    private long firstClickTime = 0;
    private long secondClickTime = 0;
    private DoubleClickCallback doubleClickCallback;

    /**
     * 两次点击在一秒内完成，视为双击
     */
    public static final int AVALIABLE_CLICK_DURATION = 1000;

    public OnDoubleClickListener(DoubleClickCallback doubleClickCallback) {
        this.doubleClickCallback = doubleClickCallback;
    }

    @Override
    public void onClick(View v) {
        count++;
        if (count == 1) {
            firstClickTime = System.currentTimeMillis();
        } else if (count == 2) {
            secondClickTime = System.currentTimeMillis();

            if (secondClickTime - firstClickTime <= AVALIABLE_CLICK_DURATION) {
                doubleClickCallback.onDoubleClick();
                count = 0;
                firstClickTime = 0;
                secondClickTime = 0;
            } else {
                count = 1;
                firstClickTime = secondClickTime;
                secondClickTime = 0;
            }

        }
    }


    public interface DoubleClickCallback {
        void onDoubleClick();
    }

}

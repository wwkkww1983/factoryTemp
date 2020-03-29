package com.proton.carepatchtemp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.wms.utils.DensityUtils;

/**
 * Created by wangmengsi on 2018/1/9.
 */

public class BlackToast {

    private static Toast toastStart;
    private static Handler mMainHandler = new Handler(Looper.getMainLooper());

    public static void show(String message) {
        mMainHandler.post(() -> {
            if (TextUtils.isEmpty(message)) {
                return;
            }
            Context context = App.get();
            if (ActivityManager.currentActivity() != null) {
                context = ActivityManager.currentActivity();
            }
            //加载Toast布局
            View toastRoot = LayoutInflater.from(context).inflate(R.layout.layout_black_toast, null);
            //初始化布局控件
            TextView mTextView = toastRoot.findViewById(R.id.id_message);
            //为控件设置属性
            mTextView.setText(message);
            //Toast的初始化
            if (toastStart == null) {
                toastStart = new Toast(context);
            }
            //获取屏幕高度
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                int height = wm.getDefaultDisplay().getHeight();
                //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
                toastStart.setGravity(Gravity.TOP, 0, height / 2 - DensityUtils.dip2px(App.get(), 50));
            }
            toastStart.setDuration(Toast.LENGTH_SHORT);
            toastStart.setView(toastRoot);
            toastStart.show();
        });
    }

    public static void show(@StringRes int messageRes) {
        show(UIUtils.getString(messageRes));
    }
}

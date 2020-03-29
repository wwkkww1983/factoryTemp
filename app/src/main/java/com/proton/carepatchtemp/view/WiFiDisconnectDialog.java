package com.proton.carepatchtemp.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.proton.carepatchtemp.R;

/**
 * Created by yuxiongfeng.
 * Date: 2019/5/7
 */
public class WiFiDisconnectDialog extends Dialog implements SystemDialog {
    private Activity hostActivity;

    public WiFiDisconnectDialog(@NonNull Activity activity) {
        super(activity);
        hostActivity = activity;
        initDialog();
    }

    private void initDialog() {
        setContentView(R.layout.dialog_wifi_disconnect_layout);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getContext().getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.9);
        lp.height = (int) (d.widthPixels * 0.8);
        lp.dimAmount = 0.3F;
        dialogWindow.setAttributes(lp);
        setCanceledOnTouchOutside(false);
        setCancelable(true);

        initView();
    }

    private void initView() {
        Button btnKnow = findViewById(R.id.id_btn_konw);
        btnKnow.setOnClickListener(v -> dismiss());
    }


    @Override
    public Activity getHostActivity() {
        return hostActivity;
    }


}

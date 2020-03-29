package com.proton.carepatchtemp.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.common.GlobalWebActivity;
import com.proton.carepatchtemp.utils.HttpUrls;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;

/**
 * Created by wangmengsi on 2018/04/18.
 * 设备断开连接对话框
 */
public class DisconnectDialog extends Dialog implements SystemDialog {

    private TextView mTopText;
    private TextView mWifiConnect;
    private TextView mBluetoothConnect;
    private TextView idTitle;
    private Activity hostActivity;
    private String macaddress;

    public DisconnectDialog(@NonNull Activity activity, String macaddress) {
        super(activity);
        hostActivity = activity;
        this.macaddress = macaddress;
        initDialog();
    }

    private void initDialog() {
        setContentView(R.layout.layout_disconnect_dialog);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getContext().getResources().getDisplayMetrics();
        lp.width = (int) (d.widthPixels * 0.8);
        lp.dimAmount = 0.3F;
        dialogWindow.setAttributes(lp);
        setCanceledOnTouchOutside(false);
        setCancelable(true);

        initView();
    }

    private void initView() {
        mTopText = findViewById(R.id.id_top_text);
        mWifiConnect = findViewById(R.id.id_wifi_connect);
        mBluetoothConnect = findViewById(R.id.id_bluetooth_connect);
        idTitle = findViewById(R.id.id_title);
        findViewById(R.id.id_confirm_text).setOnClickListener(v -> dismiss());
    }

    public DisconnectDialog setTopColor(@ColorInt int color) {
        mTopText.setBackgroundColor(color);
        return this;
    }

    public DisconnectDialog setTopText(int text) {
        mTopText.setText(text);
        return this;
    }

    public void setType(int type) {
        if (type == 0) {
            mWifiConnect.setVisibility(View.GONE);
            mBluetoothConnect.setVisibility(View.VISIBLE);
            mWifiConnect.setText(R.string.string_empty_device_p02);
        } else {
            mWifiConnect.setVisibility(View.VISIBLE);
            mBluetoothConnect.setVisibility(View.GONE);
//            String[] clickAryStr = new String[]{UIUtils.getString(R.string.string_wifi_reconnect), UIUtils.getString(R.string.string_click_here)};
            String[] clickAryStr = new String[]{UIUtils.getString(R.string.string_not_show_green)};
            UIUtils.spanStr(mWifiConnect, UIUtils.getString(R.string.string_p03device_empty), clickAryStr, R.color.color_blue_005c, true, position -> {
                switch (position) {
                    case 0:
                        //重新配网
                        IntentUtils.goToDockerSetNetwork(getContext(), true);
                        dismiss();
                        break;
                    case 1:
                        //点击这里
                        getContext().startActivity(new Intent(getContext(), GlobalWebActivity.class).putExtra("url", HttpUrls.URL_NO_DEVICE_SEARCH));
                        dismiss();
                        break;
                }
            });
        }

//        idTitle.setText(String.format(getContext().getString(R.string.string_can_not_get_data_please_confirm), Utils.getShowMac(macaddress)));
        idTitle.setText(R.string.data_confirm);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Utils.cancelVibrateAndSound();
    }

    @Override
    public Activity getHostActivity() {
        return hostActivity;
    }
}

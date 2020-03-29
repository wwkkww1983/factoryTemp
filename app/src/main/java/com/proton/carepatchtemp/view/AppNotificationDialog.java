package com.proton.carepatchtemp.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.wms.utils.DensityUtils;

/**
 * Created by wangmengsi on 2018/04/18.
 * 通知相关的对话框
 */
public class AppNotificationDialog extends Dialog implements SystemDialog {

    private TextView mTitle;
    private TextView mConfirmText;
    private TextView mContent;
    private TextView mDescription;
    private ImageView mCloseImg;
    private Activity hostActivity;

    public AppNotificationDialog(@NonNull Activity activity) {
        super(activity);
        hostActivity = activity;
        initDialog();
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_app_notification_dialog);
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = DensityUtils.dip2px(getContext(), 310);
            lp.dimAmount = 0.3F;
            dialogWindow.setAttributes(lp);
            lp.gravity = Gravity.CENTER;
            dialogWindow.setWindowAnimations(R.style.style_warm_animation);
        }
        setCanceledOnTouchOutside(false);
        setCancelable(false);

        initView();
    }

    private void initView() {
        mTitle = findViewById(R.id.id_title);
        mConfirmText = findViewById(R.id.id_confirm_text);
        mContent = findViewById(R.id.id_content);
        mDescription = findViewById(R.id.id_description);
        mCloseImg = findViewById(R.id.id_close);
        setConfirmListener(null);
    }

    public AppNotificationDialog setTitle(String text) {
        mTitle.setText(text);
        return this;
    }

    public AppNotificationDialog setContent(String text) {
        if (!TextUtils.isEmpty(text)) {
            mContent.setVisibility(View.VISIBLE);
        }
        mContent.setText(text);
        return this;
    }

    public AppNotificationDialog setDescription(String text) {
        if (!TextUtils.isEmpty(text)) {
            mDescription.setVisibility(View.VISIBLE);
        }
        mDescription.setText(text);
        return this;
    }

    public AppNotificationDialog setConfirmText(String text) {
        if (TextUtils.isEmpty(text)) {
            text = getContext().getString(R.string.string_confirm);
        }
        mConfirmText.setText(text);
        return this;
    }

    public AppNotificationDialog setConfirmListener(View.OnClickListener listener) {
        mConfirmText.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onClick(v);
            }
        });
        return this;
    }

    public AppNotificationDialog setCloseListener(View.OnClickListener listener) {
        mCloseImg.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onClick(v);
            }
        });
        return this;
    }

    public AppNotificationDialog setCloseable(boolean closable) {
        mCloseImg.setVisibility(closable ? View.VISIBLE : View.GONE);
        return this;
    }

    @Override
    public Activity getHostActivity() {
        return hostActivity;
    }
}

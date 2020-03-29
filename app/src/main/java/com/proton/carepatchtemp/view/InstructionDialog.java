package com.proton.carepatchtemp.view;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.proton.carepatchtemp.R;

/**
 * Created by yuxiongfeng.
 * Date: 2019/8/20
 */
public class InstructionDialog extends Dialog implements SystemDialog {
    private Activity hostActivity;
    private TextView mTopText;
    private TextView mConfirmText;
    private TextView mCancelText;
    private EditText etInstruction;

    public InstructionDialog(@NonNull Activity activity) {
        super(activity);
        this.hostActivity = activity;
        initDialog();
    }

    private void initDialog() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_instruction_layout);
        Window window = getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            int widthPixels = displayMetrics.widthPixels;
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.3f;
            lp.width = (int) (0.8 * widthPixels);
            window.setAttributes(lp);
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.style_warm_animation);
            setCancelable(false);
            setCanceledOnTouchOutside(false);
        }
        initView();
    }

    private void initView() {
        mTopText = findViewById(R.id.id_top_text);
        mConfirmText = findViewById(R.id.id_confirm_text);
        mCancelText = findViewById(R.id.id_cancel_text);
        etInstruction = findViewById(R.id.id_input_instruction);
        setConfirmListener(null);
        setCancelListener(null);
    }


    public InstructionDialog setConfirmListener(View.OnClickListener listener) {
        mConfirmText.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
        });
        return this;
    }

    public InstructionDialog setCancelListener(View.OnClickListener listener) {
        mCancelText.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(v);
            }
            dismiss();
        });
        return this;
    }

    public InstructionDialog setConfirmTextColor(int color) {
        mConfirmText.setTextColor(color);
        return this;
    }

    public String getInputInstruction() {
        if (TextUtils.isEmpty(etInstruction.getText().toString())) {
            return "";
        } else {
            return etInstruction.getText().toString();
        }
    }

    @Override
    public Activity getHostActivity() {
        return hostActivity;
    }
}

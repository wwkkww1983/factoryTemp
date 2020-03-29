package com.proton.carepatchtemp.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.proton.carepatchtemp.R;
import com.wms.logger.Logger;

public abstract class BaseDialogFragment extends DialogFragment {
    private OnDialogFragmentDismissListener onDialogFragmentDismissListener;


    protected void initWindow(int gravity) {
        setCancelable(isCanceable());
        if (getDialog() == null) return;
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        if (window == null) return;
        getDialog().getWindow().setWindowAnimations(R.style.animate_dialog);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(gravity); //可设置dialog的位置
        window.getDecorView().setPadding(64, 0, 64, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE));
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(metrics);
        }
        lp.width = (int) (metrics.widthPixels * widthRadio());   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if (!isBackgroundDark()) {
            lp.dimAmount = 0F;
        }

        window.setAttributes(lp);
    }

    protected String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 设置窗口是否变暗
     */
    public boolean isBackgroundDark() {
        return true;
    }

    public boolean isCanceable() {
        return false;
    }

    public float widthRadio() {
        return 0.8f;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Logger.w("对话框消失了");
        if (onDialogFragmentDismissListener != null) {
            onDialogFragmentDismissListener.onDiologDismiss();
        }
    }

    public void setOnDialogFragmentDismissListener(OnDialogFragmentDismissListener onDialogFragmentDismissListener) {
        this.onDialogFragmentDismissListener = onDialogFragmentDismissListener;
    }

    public interface OnDialogFragmentDismissListener {
        void onDiologDismiss();
    }
}

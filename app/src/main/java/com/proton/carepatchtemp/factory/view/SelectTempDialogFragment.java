package com.proton.carepatchtemp.factory.view;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.databinding.FragmentSelectTempLayoutBinding;
import com.proton.carepatchtemp.utils.Utils;
import com.wms.logger.Logger;
import com.wms.utils.DensityUtils;
import com.zyyoona7.wheel.WheelView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SelectTempDialogFragment extends DialogFragment {
    private List<Float> temps = new ArrayList<>();

    private FragmentSelectTempLayoutBinding binding;
    private int selectPosition = 0;

    /**
     * 选择温度弹框的默认值
     */
    private float referenceTemp;

    public void setReferenceTemp(float referenceTemp) {
        this.referenceTemp = referenceTemp;
    }

    public static SelectTempDialogFragment newInstance() {
        Bundle args = new Bundle();
        SelectTempDialogFragment fragment = new SelectTempDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_temp_layout, container, false);
        initWindow(Gravity.BOTTOM);
        ViewGroup.LayoutParams layoutParams = binding.idRootview.getLayoutParams();
        layoutParams.width = DensityUtils.getScreenWidth(getActivity());
        binding.idRootview.setLayoutParams(layoutParams);

        binding.wheelview.setData(getTemps());
        binding.wheelview.setOnWheelChangedListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {
                Logger.d("onWheelScroll  scrollOffsetY ", scrollOffsetY);
            }

            @Override
            public void onWheelItemChanged(int oldPosition, int newPosition) {
                Logger.d("onWheelItemChanged ");
            }

            @Override
            public void onWheelSelected(int position) {
                Logger.d("onWheelSelected  position ", position);
                selectPosition = position;
            }

            @Override
            public void onWheelScrollStateChanged(int state) {
                Logger.d("onWheelScrollStateChanged  state ", state);
            }
        });

        if (referenceTemp > 0) {
            selectPosition = getTemps().indexOf(referenceTemp);
            Logger.w("referenceTemp:", referenceTemp, " ,selectPosition: ", selectPosition);
            binding.wheelview.setSelectedItemPosition(selectPosition);
        }

        binding.idCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                selectPosition = 0;
            }
        });

        binding.idConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDialogFragmentDismissListener.onDialogSelect(temps.get(selectPosition));
                dismiss();
                selectPosition = 0;
            }
        });

        return binding.getRoot();
    }

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
//        window.getDecorView().setPadding(64, 0, 64, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE));
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(metrics);
        }
        int screenWidth = DensityUtils.getScreenWidth(getActivity());
        lp.width = screenWidth;   //设置宽度充满屏幕
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
        return 1f;
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

        void onDialogSelect(Float waterTemp);
    }

    private List<Float> getTemps() {
        if (temps != null) {
            temps.clear();
        }
        for (int i = 0; i < 2000; i++) {
            float temp = (float) (20f + i * 0.01);
            temps.add(temp);
        }
        return temps;
    }
}

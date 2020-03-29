package com.proton.carepatchtemp.fragment.measure;

import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.databinding.LayoutKindlyRemindBinding;

/**
 * Created by luochune on 2018/5/3.
 */

public class TipFragment extends DialogFragment {
    private LayoutKindlyRemindBinding layoutKindlyRemindBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setWindowAnimations(R.style.animate_dialog);
        layoutKindlyRemindBinding = DataBindingUtil.inflate(inflater, R.layout.layout_kindly_remind, container, false);
        setListener();
        return layoutKindlyRemindBinding.getRoot();
    }

    private void setListener() {
        layoutKindlyRemindBinding.idBtnTip.setOnClickListener(v -> dismiss());
    }
}

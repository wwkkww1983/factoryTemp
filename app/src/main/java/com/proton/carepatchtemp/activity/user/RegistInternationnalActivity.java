package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityRegistInternationnalBinding;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.user.InternationalLoginViewModel;

public class RegistInternationnalActivity extends BaseViewModelActivity<ActivityRegistInternationnalBinding, InternationalLoginViewModel> {

    @Override
    protected void init() {
        super.init();
        binding.setViewModel(viewmodel);
        viewmodel.isRegist.set(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idContent.setOnClickListener(v -> Utils.hideKeyboard(this,binding.idContent));
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_regist_internationnal;
    }

    @Override
    protected InternationalLoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(InternationalLoginViewModel.class);
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_regist);
    }
}

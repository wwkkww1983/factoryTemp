package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityForgetPwdInternationnalBinding;
import com.proton.carepatchtemp.viewmodel.user.InternationalLoginViewModel;

    public class ForgetPwdInternationalActivity extends BaseViewModelActivity<ActivityForgetPwdInternationnalBinding, InternationalLoginViewModel> implements View.OnClickListener {

    @Override
    protected void initView() {
        super.initView();
        binding.setViewModel(viewmodel);
        binding.setViewClickListener(this);
        viewmodel.isRegist.set(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String email = getIntent().getStringExtra("email");
        viewmodel.email.set(email);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_forget_pwd_internationnal;
    }

    @Override
    protected InternationalLoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(InternationalLoginViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn_reset:

                break;
        }
    }
}

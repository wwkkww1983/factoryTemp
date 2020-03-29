package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityBindEmailBinding;
import com.proton.carepatchtemp.viewmodel.user.InternationalLoginViewModel;

public class BindEmailActivity extends BaseViewModelActivity<ActivityBindEmailBinding, InternationalLoginViewModel> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setViewModel(viewmodel);
        String certToken=getIntent().getStringExtra("cert_token");
        viewmodel.certToken.set(certToken);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_bind_email;
    }

    @Override
    protected InternationalLoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(InternationalLoginViewModel.class);
    }
}

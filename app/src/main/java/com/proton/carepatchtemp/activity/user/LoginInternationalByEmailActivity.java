package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityLoginInternationnalByEmailBinding;
import com.proton.carepatchtemp.viewmodel.user.InternationalLoginViewModel;

public class LoginInternationalByEmailActivity extends BaseViewModelActivity<ActivityLoginInternationnalByEmailBinding, InternationalLoginViewModel> implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setViewmodel(viewmodel);
        binding.setViewClickListener(this);
    }

    @Override
    protected void init() {
        super.init();
        /**
         * 重置密码成功后，自动登录
         */
        String email = getIntent().getStringExtra("email");
        String pwd = getIntent().getStringExtra("pwd");

        if (!TextUtils.isEmpty(email)) {
            viewmodel.email.set(email);
        }

        if (!TextUtils.isEmpty(pwd)) {
            viewmodel.pwdNum.set(pwd);
        }

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
            viewmodel.loginByEmail(false);
        }

    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_login_internationnal_by_email;
    }

    @Override
    protected InternationalLoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(InternationalLoginViewModel.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_tv_forgetpwd:
                //忘记密码
                Intent intent = new Intent(this, ForgetPwdInternationalActivity.class);
                intent.putExtra("email", viewmodel.email.get());
                startActivity(intent);
                break;
        }
    }

}

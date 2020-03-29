package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityVerifyEmailCodeBinding;
import com.proton.carepatchtemp.viewmodel.user.InternationalLoginViewModel;

/**
 * Created by yxf on 2019/4/29
 * <传入Extra>
 * is_regist :true表示由RegistInternationnalActivity传过来   false表示由ForgetPwdInternationnalActivity传过来
 * </Extra>
 */
public class InternationalVerifyEmailCodeActivity extends BaseViewModelActivity<ActivityVerifyEmailCodeBinding, InternationalLoginViewModel> {

    @Override
    protected void initView() {
        super.initView();
        binding.setViewmodel(viewmodel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String email = getIntent().getStringExtra("email");
        String pwd = getIntent().getStringExtra("pwd");
        String certToken = getIntent().getStringExtra("cert_token");
        boolean isRegist=getIntent().getBooleanExtra("is_regist",false);
        boolean emailBind = getIntent().getBooleanExtra("email_bind", false);
        viewmodel.email.set(email);
        viewmodel.pwdNum.set(pwd);
        viewmodel.isRegist.set(isRegist);
        viewmodel.doCodeCountDown(60);
        if (emailBind) {
            viewmodel.isBindEmail.set(emailBind);
            viewmodel.certToken.set(certToken);
        }

    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_verify_email_code;
    }

    @Override
    protected InternationalLoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(InternationalLoginViewModel.class);
    }
}

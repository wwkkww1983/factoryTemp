package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityLoginBinding;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.user.LoginViewModel;

/**
 * Created by wangmengsi on 2018/2/26.
 * <传入Extra>
 * String from measureSave 从测量保存报告进来
 * </Extra>
 */

public class LoginActivity extends BaseViewModelActivity<ActivityLoginBinding, LoginViewModel> implements View.OnClickListener {
    @Override
    protected LoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    protected void init() {
        super.init();
        binding.setViewmodel(viewmodel);
        binding.setViewClickListener(this);
        String from = getIntent().getStringExtra("from");
        viewmodel.from.set(from);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        super.initView();
        binding.getRoot().setOnClickListener(v -> Utils.hideKeyboard(mContext, binding.getRoot()));
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_login);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_tv_forgetpwd:
                //忘记密码
                startActivity(new Intent(this, ForgetPwdActivity.class));
                break;
        }
    }
}

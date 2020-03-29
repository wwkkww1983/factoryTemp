package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityBindPhoneBinding;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.user.LoginViewModel;

/**
 * Created by 王梦思 on 2018/9/25.
 * <p/>
 */
public class BindPhoneActivity extends BaseViewModelActivity<ActivityBindPhoneBinding, LoginViewModel> {

    @Override
    protected int inflateContentView() {
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void init() {
        super.init();
        viewmodel.phoneNum.set("");
        binding.setViewModel(viewmodel);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idContent.setOnClickListener(v -> Utils.hideKeyboard(mContext, binding.idContent));
    }

    @Override
    protected LoginViewModel getViewModel() {
        return ViewModelProviders.of(ActivityManager.findActivity(LoginFirstActivity.class)).get(LoginViewModel.class);
    }

}

package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityNewProfileStep1Binding;
import com.proton.carepatchtemp.utils.StatusBarUtil;
import com.proton.carepatchtemp.viewmodel.AddProfileViewModel;

public class NewProfileStep1Activity extends BaseViewModelActivity<ActivityNewProfileStep1Binding, AddProfileViewModel> {

    private boolean canSkip;

    @Override
    protected void init() {
        super.init();
        binding.setViewModel(viewmodel);
        canSkip = getIntent().getBooleanExtra("canSkip", true);
    }

    @Override
    protected void setStatusBarColor() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_main_bg));
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.idTvRightOperate.setText(R.string.string_goOver);
        if (canSkip) {
            binding.idIncludeTop.idTvRightOperate.setVisibility(View.VISIBLE);
        } else {
            binding.idIncludeTop.idTvRightOperate.setVisibility(View.GONE);
        }
        binding.idIncludeTop.idTvRightOperate.setOnClickListener(v -> viewmodel.goOverProfilFirstStep());
        viewmodel.stepNum.set(1);
    }

    @Override
    protected boolean showBackBtn() {
        return false;
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_new_profile_step1;
    }

    @Override
    protected AddProfileViewModel getViewModel() {
        return ViewModelProviders.of(this).get(AddProfileViewModel.class);
    }
}

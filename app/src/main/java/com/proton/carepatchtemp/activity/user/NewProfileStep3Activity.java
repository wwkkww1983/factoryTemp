package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityNewProfileStep3Binding;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.viewmodel.AddProfileViewModel;

/**
 * 新注册用户添加档案第三步
 * <extra>
 * profile ProfileBean 显示档案实体对象
 * </extra>
 */
public class NewProfileStep3Activity extends BaseViewModelActivity<ActivityNewProfileStep3Binding, AddProfileViewModel> {

    private ProfileBean profile;

    @Override
    protected void init() {
        super.init();
        binding.setViewModel(viewmodel);
        Intent mIntent = getIntent();
        profile = (ProfileBean) mIntent.getSerializableExtra("profile");
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_new_profile_step3;
    }

    @Override
    protected void initView() {
        super.initView();
        viewmodel.stepNum.set(3);
        //初始化界面
        if (profile != null) {
            viewmodel.name.set(profile.getRealname());
            viewmodel.age.set(profile.getAge());
            String avatorUrl = profile.getAvatar();
            if (!TextUtils.isEmpty(avatorUrl)) {
                binding.idProfileSdvAvatar.setImageURI(avatorUrl);
            }
        }
    }

    @Override
    protected AddProfileViewModel getViewModel() {
        return ViewModelProviders.of(this).get(AddProfileViewModel.class);
    }
}

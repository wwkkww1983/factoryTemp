package com.proton.carepatchtemp.activity.profile;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityProfileNameEditBinding;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;

/**
 * <extra>
 * String name
 * </>
 */
public class ProfileNameEditActivity extends BaseViewModelActivity<ActivityProfileNameEditBinding, BaseViewModel> {
    @Override
    protected int inflateContentView() {
        return R.layout.activity_profile_name_edit;
    }

    @Override
    protected void init() {
        super.init();
        Intent editProfileIntent = getIntent();
        String name = editProfileIntent.getStringExtra("name");
        binding.idEtProfileName.setText(name);
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idEtProfileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    binding.idIvNamedel.setVisibility(View.INVISIBLE);
                } else {
                    binding.idIvNamedel.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.idIncludeTopNav.idTvRightOperate.setOnClickListener(v -> {
            String editName = binding.idEtProfileName.getText().toString();
            if (!TextUtils.isEmpty(editName)) {
                setResult(RESULT_OK, new Intent(ProfileNameEditActivity.this, ProfileEditActivity.class).putExtra("editName", editName));
                finish();
            } else {
                BlackToast.show(getResources().getString(R.string.string_input_name_tip));
            }
        });
        binding.idIvNamedel.setOnClickListener(v -> binding.idEtProfileName.setText(""));
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTopNav.idTvRightOperate.setText(R.string.string_confirm);
        binding.idIncludeTopNav.idTvRightOperate.setVisibility(View.VISIBLE);
    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_real_name);
    }
}

package com.proton.carepatchtemp.activity.report;

import android.content.Intent;
import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityAddRemarkBinding;
import com.proton.carepatchtemp.utils.BlackToast;

/**
 * 添加随手记录备注
 * <Extra>
 * remarkStr string 备注信息
 * </>
 */
public class AddReportNotesRemarkActivity extends BaseActivity<ActivityAddRemarkBinding> {
    @Override
    protected int inflateContentView() {
        return R.layout.activity_add_remark;
    }

    @Override
    protected void initView() {
        super.initView();
        if (getIntent().hasExtra("remarkStr")) {
            String remarkStr = getIntent().getStringExtra("remarkStr");
            binding.idTvNoteRemark.setText(remarkStr);
        }
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idBtnFinish.setOnClickListener(v -> {
            String remarkStr = binding.idTvNoteRemark.getText().toString().trim();
            if (TextUtils.isEmpty(remarkStr)) {
                BlackToast.show(R.string.string_string_input_remark);
                return;
            }
            setResult(AddReportNotesActivity.RESULT_CODE_ADD_REMARK, new Intent().putExtra("remarkStr", remarkStr));
            finish();
        });
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_remark);
    }
}

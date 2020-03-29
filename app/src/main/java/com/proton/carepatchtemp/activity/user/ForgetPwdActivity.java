package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityForgetPwdBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.user.LoginViewModel;

public class ForgetPwdActivity extends BaseViewModelActivity<ActivityForgetPwdBinding, LoginViewModel> implements View.OnClickListener {

    /**
     * 页面来自AccountAndSafeActivity的跳转,如果是则手机号不能更改
     */
    private boolean isFromAccountAndSafe;
    /**
     * 密码是否可见
     */
    public boolean isShowPwd = false;

    @Override
    protected void init() {
        super.init();
        isFromAccountAndSafe = getIntent().getBooleanExtra("is_from_account_and_safe", false);
        binding.setViewModel(viewmodel);
        binding.setIsFromAccountAndSafeAct(isFromAccountAndSafe);
        binding.setViewClickListener(this);

    }

    @Override
    protected void initView() {
        super.initView();
        binding.idContent.setOnClickListener(v -> Utils.hideKeyboard(mContext, binding.idContent));
    }

    @Override
    protected void setListener() {
        super.setListener();
        binding.idInputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputPhone = s.toString();
                if (TextUtils.isEmpty(inputPhone)) {
                    viewmodel.isCanSendCode.set(false);
                } else {
                    if (Utils.isMobile(inputPhone)) {
                        viewmodel.isCanSendCode.set(true);
                    } else {
                        viewmodel.isCanSendCode.set(false);
                    }
                }
            }
        });
    }

    @Override
    protected LoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_complete:
                //完成
                doComplete();
                break;
            case R.id.id_display_password:
                isShowPwd = !isShowPwd;
                binding.idDisplayPassword.setImageResource(isShowPwd ? R.drawable.display : R.drawable.hide);
                if (isShowPwd) {
                    //设置EditText的密码为可见的
                    binding.etNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //设置密码为隐藏的
                    binding.etNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                binding.etNewPwd.setSelection(binding.etNewPwd.getText().length());
                break;
        }
    }

    /**
     * 完成
     */
    private void doComplete() {
        String phoneNum = viewmodel.phoneNum.get().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            BlackToast.show(R.string.string_input_your_phone);
            return;
        }
        String pwd = viewmodel.pwdNum.get().trim();
        if (TextUtils.isEmpty(pwd)) {
            BlackToast.show(R.string.string_input_password);
            return;
        }
        String smsCode = viewmodel.codeNum.get().trim();
        if (TextUtils.isEmpty(smsCode)) {
            BlackToast.show(R.string.string_input_code);
            return;
        }
        //完成
        //验证码验证
        viewmodel.validateSmsCode(phoneNum, smsCode, new NetCallBack<ResultPair>() {
            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showDialog(getResources().getString(R.string.string_pwd_reset_loading));
            }

            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_please_check_your_network);
            }

            @Override
            public void onSucceed(ResultPair data) {
                dismissDialog();
                //重置密码
                if (!TextUtils.isEmpty(data.getData())) {
                    resetLoginPwd(data.getData());
                } else {
                    BlackToast.show(R.string.string_smscode_validate_error);
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                if (resultPair != null && !TextUtils.isEmpty(resultPair.getData())) {
                    BlackToast.show(resultPair.getData() + "");
                } else {
                    BlackToast.show(R.string.string_pwd_reset_faild);
                }
            }
        });
    }

    /**
     * 完成第二步，重置登录密码
     */
    private void resetLoginPwd(String code) {
        viewmodel.resetLoginPwd(viewmodel.phoneNum.get().trim(), viewmodel.pwdNum.get().trim(), code, new NetCallBack<ResultPair>() {
            @Override
            public void onSubscribe() {
                super.onSubscribe();
            }

            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_please_check_your_network);
            }

            @Override
            public void onSucceed(ResultPair data) {
                if (data == null) {
                    return;
                }
                if (data.isSuccess()) {
                    BlackToast.show("重置密码成功");
                    finish();
                    MessageEvent messageEvent = new MessageEvent(MessageEvent.EventType.RESET_PWD_SUCCESS);
                    messageEvent.setMsg(viewmodel.phoneNum.get());
                    messageEvent.setMsg2(viewmodel.pwdNum.get().trim());
                    EventBusManager.getInstance().post(messageEvent);
                } else {
                    BlackToast.show(data.getData() + "");
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && !TextUtils.isEmpty(resultPair.getData())) {
                    BlackToast.show(resultPair.getData() + "");
                } else {
                    BlackToast.show(R.string.string_pwd_reset_faild);
                }
            }
        });
    }

    @Override
    public String getTopCenterText() {
      /*  if (isResetPassword) {
            return getString(R.string.string_reset_pwd);
        }
        return getString(R.string.string_reget_pwd);*/
        return "";
    }
}

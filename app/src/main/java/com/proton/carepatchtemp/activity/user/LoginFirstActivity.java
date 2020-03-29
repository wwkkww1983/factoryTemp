package com.proton.carepatchtemp.activity.user;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;

import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.databinding.ActivityLoginFirstBinding;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.socailauth.PlatformType;
import com.proton.carepatchtemp.socailauth.SocialApi;
import com.proton.carepatchtemp.socailauth.listener.AuthListener;
import com.proton.carepatchtemp.utils.AlipayLogin;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.user.LoginViewModel;
import com.wms.logger.Logger;

import java.util.Map;

public class LoginFirstActivity extends BaseViewModelActivity<ActivityLoginFirstBinding, LoginViewModel> implements View.OnClickListener {

    private SocialApi mSocialApi;
    /**
     * 密码是否可见
     */
    public boolean isShowPwd = false;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_login_first;
    }

    @Override
    protected void init() {
        super.init();
        binding.setViewModel(viewmodel);
        binding.setOnClick(this);
        Utils.checkUpdate(this, false);
    }

    @Override
    protected void initView() {
        super.initView();
        if (BuildConfig.IS_INTERNAL) {
            IntentUtils.goToInternalLogin(mContext);
            finish();
        }
        mSocialApi = SocialApi.get(mContext);
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
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected LoginViewModel getViewModel() {
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_verify_code_login:
                viewmodel.isVerifyCodeLogin.set(true);
                if (!binding.ckVerifyCodeLogin.isChecked()) {
                    viewmodel.codeNum.set("");
                }
                viewmodel.pwdNum.set("");
                break;
            case R.id.id_password_login:
                viewmodel.isVerifyCodeLogin.set(false);
                if (!binding.ckPasswordLogin.isChecked()) {
                    viewmodel.pwdNum.set("");
                }
                viewmodel.codeNum.set("");
                break;
            case R.id.btn_regist_or_login:
                if (viewmodel.isVerifyCodeLogin.get()) {
                    viewmodel.verifyCaptchaLoginCode();
                } else {
                    viewmodel.loginByPwd(false);
                }
                break;
            case R.id.id_alipay:
                aliplayLogin();
                break;
            case R.id.id_wechat:
                wechatLogin();
                break;
            case R.id.id_tv_forgetpwd:
                IntentUtils.goToForgetPwdActivity(mContext);
                break;
            case R.id.id_display_password:
                isShowPwd = !isShowPwd;
                binding.idDisplayPassword.setImageResource(isShowPwd ? R.drawable.display : R.drawable.hide);
                if (isShowPwd) {
                    //设置EditText的密码为可见的
                    binding.idPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //设置密码为隐藏的
                    binding.idPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                binding.idPwd.setSelection(binding.idPwd.getText().length());
                break;
        }
    }

    /**
     * 微信登录
     */
    private void wechatLogin() {
        mSocialApi.doOauthVerify(this, PlatformType.WEIXIN, new MyAuthListener());
    }

    public class MyAuthListener implements AuthListener {
        @Override
        public void onComplete(PlatformType platform_type, Map<String, String> map) {
            String code = map.get("code");
            Logger.w("微信登录code :", code);
            viewmodel.wechatLogin(code);
        }

        @Override
        public void onError(PlatformType platform_type, String err_msg) {
            Logger.w("微信登录code", err_msg);
            BlackToast.show(err_msg);
        }

        @Override
        public void onCancel(PlatformType platform_type) {
            Logger.w("微信登录code", "login onCancel");
            BlackToast.show(R.string.string_login_cancel);
        }
    }

    /**
     * 支付宝登录
     */
    private void aliplayLogin() {
        AlipayLogin.login(this, new AlipayLogin.AlipayLoginCallback() {
            @Override
            public void onSuccess(String authCode) {
                viewmodel.alipayLogin(authCode);
            }

            @Override
            public void onFail() {
                BlackToast.show(R.string.string_login_fail);
            }

            @Override
            public void onCancel() {
                BlackToast.show(R.string.string_login_cancel);
            }
        });
    }


    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.RESET_PWD_SUCCESS) {
            String phone = event.getMsg();
            String pwd = event.getMsg2();
            if (TextUtils.isEmpty(phone)) {
                return;
            }
            binding.idInputPhone.setText(phone);
            binding.idPwd.setText(pwd);
            new Handler().postDelayed(() -> viewmodel.loginByPwd(false), 500);
        }
    }
}

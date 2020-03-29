package com.proton.carepatchtemp.viewmodel.user;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.profile.AddProfileActivity;
import com.proton.carepatchtemp.bean.BindEmailBean;
import com.proton.carepatchtemp.component.AliyunService;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.ProfileCenter;
import com.proton.carepatchtemp.net.center.UserCenter;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.HttpUrls;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import java.util.List;

import cn.pedant.SweetAlert.Type;

/**
 * Created by yuxiongfeng.
 * Date: 2019/5/8
 */
public class InternationalLoginViewModel extends BaseViewModel {

    /**
     * true表示跳转到邮箱验证页面  false表示重新发送验证码
     */
    private boolean isGoToVerifyEmail = false;

    /**
     * 国际版--用于标志 是由注册页面到到校验验证码或者由忘记密码页面到校验验证码
     */
    public ObservableField<Boolean> isRegist = new ObservableField<>(false);

    /**
     * 国际版--用于标志是绑定邮箱页面进入
     */
    public ObservableField<Boolean> isBindEmail = new ObservableField<>(false);

    /**
     * facebook  google登录的凭证
     */
    public ObservableField<String> certToken = new ObservableField<>("");

    /**
     * 电子邮箱地址
     */
    public ObservableField<String> email = new ObservableField<>("");

    /**
     * 密码
     */
    public ObservableField<String> pwdNum = new ObservableField<>("");

    /**
     * 验证码
     */
    public ObservableField<String> codeNum = new ObservableField<>("");

    /**
     * 验证码发送文字
     */
    public ObservableField<String> sendCodeStr = new ObservableField<>(UIUtils.getString(R.string.string_click_send_code));
    public ObservableBoolean isCanSendCode = new ObservableBoolean(true);

    private CountDownTimer mCodeCountDownTimer;
    private NetCallBack<String> mSendCodeCallback = new NetCallBack<String>() {

        @Override
        public void noNet() {
            super.noNet();
            BlackToast.show(R.string.string_please_check_your_network);
        }

        @Override
        public void onSubscribe() {
            super.onSubscribe();
//            showDialog(R.string.string_loading);
        }

        @Override
        public void onSucceed(String data) {
            dismissDialog();
            BlackToast.show(R.string.string_get_code_success);
            if (isGoToVerifyEmail) {
                goToVerifyEmailCode();
            }
        }

        @Override
        public void onFailed(ResultPair resultPair) {
            dismissDialog();
            BlackToast.show(resultPair.getData());
        }
    };


    /**
     * 国际版邮箱登录--是否是首次登录
     */
    public void loginByEmail(boolean isFirstLogin) {

        if (!Utils.isEmail(email.get())) {
            BlackToast.show(R.string.string_email_not_correct);
            return;
        }

        if (TextUtils.isEmpty(pwdNum.get())) {
            BlackToast.show(R.string.string_password_can_not_be_null);
            return;
        }

        UserCenter.loginByEmail(email.get(), pwdNum.get(), new InternationalLoginViewModel.LoginCallback(isFirstLogin));

    }

    /**
     * 国际版--验证邮箱验证码
     * isRegist:true表示注册页面发送的验证码   false表示忘记密码页面发送的验证码
     * 1.注册邮箱的时候验证
     * 2.忘记密码时候验证
     */
    public void validateEmailCode() {
        if (TextUtils.isEmpty(codeNum.get())) {
            BlackToast.show(R.string.string_code_can_not_be_null);
            return;
        }

        if (isRegist.get()) {
            validateRegistEmailCode();
            return;
        }

        if (isBindEmail.get()) {
            if (TextUtils.isEmpty(certToken.get())) {
                BlackToast.show("certToken is null");
                return;
            }
            verifyBindEmailCode(certToken.get());
            return;
        }

        validateFPwdEmailCode();

    }

    public void sendEmailCode(boolean isGoToVerifyEmail) {
        if (isRegist.get()) {
            senRegistEmailCode(isGoToVerifyEmail);
        } else {
            sendForgetPwdEmailCode(isGoToVerifyEmail);
        }
    }

    /**
     * 国际版--发送邮箱注册验证码
     */
    public void senRegistEmailCode(boolean isGoToVerifyEmail) {
        this.isGoToVerifyEmail = isGoToVerifyEmail;
        Logger.w("sendRegistEmailCode...");
        if (checkEmailCanRegist(isGoToVerifyEmail)) {
            UserCenter.sendRegistEamilCode(email.get(), mSendCodeCallback);
        }
    }

    /**
     * 国际版--发送邮件忘记密码的验证码
     */
    public void sendForgetPwdEmailCode(boolean isGoToVerifyEmail) {
        this.isGoToVerifyEmail = isGoToVerifyEmail;
        if (checkEmailCanRegist(isGoToVerifyEmail)) {
            UserCenter.sendForgetPwdEmailCode(email.get(), mSendCodeCallback);
        }
    }

    /**
     * 国际版--验证邮箱验证码，注册邮箱
     */
    public void validateRegistEmailCode() {
        UserCenter.registEmail(email.get(), pwdNum.get(), codeNum.get(), new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_please_check_your_network);
            }

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showDialog(R.string.string_registing);
            }

            @Override
            public void onSucceed(ResultPair data) {
                loginByEmail(true);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
                dismissDialog();
            }
        });
    }

    /**
     * 国际版--忘记密码，验证邮件验证码，重置密码
     */
    public void validateFPwdEmailCode() {
        if (checkEmailCanRegist(false)) {
            UserCenter.validateFPwdEmailCode(email.get(), codeNum.get(), pwdNum.get(), new NetCallBack<ResultPair>() {
                @Override
                public void noNet() {
                    super.noNet();
                    BlackToast.show(R.string.string_please_check_your_network);
                }

                @Override
                public void onSubscribe() {
                    super.onSubscribe();
                    BlackToast.show(R.string.string_pwd_reset_loading);
                }

                @Override
                public void onSucceed(ResultPair data) {
                    dismissDialog();
                    //重置密码
                    if (!TextUtils.isEmpty(data.getData())) {
                        BlackToast.show(data.getData());
                        goToInternalLoginByEmail(email.get(), pwdNum.get());
                    } else {
                        BlackToast.show(R.string.string_verify_code_error);
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
    }


    private boolean checkEmailCanRegist(boolean isGoToVerifyEmail) {
        if (!Utils.isEmail(email.get())) {
            BlackToast.show(R.string.string_email_not_correct);
            return false;
        }

        if (isGoToVerifyEmail) {
            if (TextUtils.isEmpty(pwdNum.get())) {
                BlackToast.show(R.string.string_password_can_not_be_null);
                return false;
            }

            if (pwdNum.get().length() < 6) {
                BlackToast.show(R.string.string_invalid_pwd);
                return false;
            }
        }

        return true;
    }

    public void goToInternalLoginByEmail(String email, String pwd) {
        IntentUtils.goToLoginByEmail(getContext(), email, pwd);
        finishActivity();
    }

    public void goToBindEmail() {
        IntentUtils.goToBindEmail(getContext(), certToken.get());
    }

    public void goToVerifyEmailCode() {
        if (isBindEmail.get()) {
            IntentUtils.goToVerifyEmailCode(getContext(), email.get(), certToken.get(), true, false);
            finishActivity();
            return;
        }
        IntentUtils.goToVerifyEmailCode(getContext(), email.get(), pwdNum.get(), isRegist.get());
        finishActivity();
    }


    public void goToRegisterInternational() {
        IntentUtils.goToRegistInternationnal(getContext());
        finishActivity();
    }

    /**
     * 显示隐私
     */
    public void showPrivicy() {
        IntentUtils.goToWeb(getContext(), HttpUrls.URL_PRIVICY);
    }

    /**
     * 显示用户协议
     */
    public void showUserAgreement() {
        IntentUtils.goToWeb(getContext(), HttpUrls.URL_AGREEMENT);
    }


    public void doCodeCountDown(int leftTime) {
        mCodeCountDownTimer = new CountDownTimer(leftTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isCanSendCode.set(false);
                sendCodeStr.set((int) (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                isCanSendCode.set(true);
                sendCodeStr.set(UIUtils.getString(R.string.string_resend));
            }
        };
        mCodeCountDownTimer.start();
    }

    /**
     * facebook登录
     */
    public void loginFaceBook(String userId, String token) {
        UserCenter.loginFaceBook(userId, token, new NetCallBack<BindEmailBean>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_please_check_your_network);
            }

            @Override
            public void onSucceed(BindEmailBean emailBean) {
                certToken.set(emailBean.getCert());
                if (emailBean.isMailBind()) {//如果绑定了直接登录
                    loginSuccess(false);
                } else {
                    goToBindEmail();
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
                dismissDialog();
            }
        });
    }

    public void loginGoogle(String userId, String token) {
        UserCenter.loginGoogle(userId, token, new NetCallBack<BindEmailBean>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_please_check_your_network);
            }

            @Override
            public void onSucceed(BindEmailBean emailBean) {
                certToken.set(emailBean.getCert());
                if (emailBean.isMailBind()) {//如果绑定了直接登录
                    loginSuccess(false);
                } else {
                    goToBindEmail();
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
                dismissDialog();
            }
        });
    }


    /**
     * 发送绑定邮箱的验证码
     */
    public void sendBindEmailCode() {
        isGoToVerifyEmail = true;
        isBindEmail.set(true);
        UserCenter.sendBindEmailCode(email.get(), mSendCodeCallback);
    }

    /**
     * 验证(facebook,google)绑定邮箱的验证码，并且绑定邮箱
     */
    public void verifyBindEmailCode(String certToken) {
        UserCenter.verifyBindEmailCode(email.get(), certToken, codeNum.get(), new NetCallBack<Boolean>() {
            @Override
            public void onSucceed(Boolean data) {
                loginSuccess(true);
            }
        });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (mCodeCountDownTimer != null) {
            mCodeCountDownTimer.cancel();
        }
    }

    class LoginCallback extends NetCallBack<Boolean> {
        private boolean isFirstLogin;

        public LoginCallback(boolean isFirstLogin) {
            this.isFirstLogin = isFirstLogin;
        }

        @Override
        public void noNet() {
            super.noNet();
            BlackToast.show(R.string.string_please_check_your_network);
        }

        @Override
        public void onSubscribe() {
            showDialog(R.string.string_logining, false);
        }

        @Override
        public void onSucceed(Boolean data) {
            loginSuccess(isFirstLogin);
        }

        @Override
        public void onFailed(ResultPair resultPair) {
            BlackToast.show(resultPair.getData());
            dismissDialog();
        }
    }

    private void loginSuccess(boolean isFirstLogin) {
        ProfileCenter.getProfileList(new NetCallBack<List<ProfileBean>>() {

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                doLoginSuccess(isFirstLogin);
            }

            @Override
            public void onSucceed(List<ProfileBean> data) {
                //当前应该是游客模式过来的
                dismissDialog();
                //开启阿里云服务
                IntentUtils.startAliyunService(getContext());
                //通知需要因为登录状态而刷新的地方
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.LOGIN));

                if (CommonUtils.listIsEmpty(data) && isFirstLogin) {
                    getContext().startActivity(new Intent(getContext(), AddProfileActivity.class)
                            .putExtra("canSkip", false)
                            .putExtra("needScanQRCode", true));
                    //跳转到添加档案界面
                    ActivityManager.finishOthersActivity(HomeActivity.class);
                    return;
                }
                doLoginSuccess(isFirstLogin);
            }
        });
    }

    private void doLoginSuccess(boolean isFirstLogin) {
        showDialog(R.string.string_login_success, Type.SUCCESS_MSG_TYPE);
        //开启阿里云服务
        IntentUtils.startAliyunService(getContext());
        //通知需要因为登录状态而刷新的地方
        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.LOGIN));
        //获取所有的档案
        getDialog().setOnDismissListener(dialog -> {
            //首次注册登录跳转至新建档案
            if (isFirstLogin) {
                //扫描二维码
                IntentUtils.goToScanQRCode(getContext());
            } else {
                //首次使用app,是否选择过设备型号，没有则选择
                IntentUtils.goToMain(getContext());
                ActivityManager.finishOthersActivity(HomeActivity.class);
            }
        });
        dismissDialog();
    }

}

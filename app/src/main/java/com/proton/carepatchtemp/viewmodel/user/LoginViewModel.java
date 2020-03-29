package com.proton.carepatchtemp.viewmodel.user;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.profile.AddProfileActivity;
import com.proton.carepatchtemp.bean.BindBean;
import com.proton.carepatchtemp.bean.WechatUserInfo;
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
import com.proton.carepatchtemp.utils.Constants;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.HttpUrls;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.PwdCheckUtil;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.UmengUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;
import com.tencent.bugly.crashreport.CrashReport;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import java.util.List;

import cn.pedant.SweetAlert.Type;

/**
 * Created by wangmengsi on 2018/2/26.
 */

public class LoginViewModel extends BaseViewModel {

    /**
     * 手机号码
     */
    public ObservableField<String> phoneNum = new ObservableField<>("");

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
     * 从哪个页进入登录，需要做相应处理
     */
    public ObservableField<String> from = new ObservableField<>("");
    /**
     * 验证码发送文字
     */
    public ObservableField<String> sendCodeStr = new ObservableField<>(UIUtils.getString(R.string.string_resend));
    public ObservableBoolean isCanSendCode = new ObservableBoolean(false);

    public ObservableField<Integer> leftTimes = new ObservableField<>(0);
    /**
     * 发送验证码后是否可以继续编辑手机号
     */
    public ObservableBoolean isCanEditPhone = new ObservableBoolean(true);

    /**
     * 是否是验证码登录 true:验证码登录  false：密码登录
     */
    public ObservableBoolean isVerifyCodeLogin = new ObservableBoolean(true);

    /**
     * 新用户登录的默认档案
     */
    private ProfileBean profileBean;

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
            showDialog(R.string.string_send_coding);
        }

        @Override
        public void onSucceed(String data) {
            dismissDialog();
            BlackToast.show(R.string.string_get_code_success);
            doCodeCountDown(60);
        }

        @Override
        public void onFailed(ResultPair resultPair) {
            dismissDialog();
            try {
                leftTimes.set(Integer.parseInt(resultPair.getData()));
//                leftTime = Integer.parseInt(resultPair.getData());
                doCodeCountDown(leftTimes.get());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                BlackToast.show(resultPair.getData());
            }
        }
    };
    private String checkToken;

    public LoginViewModel() {
        super();
        phoneNum.set(SpUtils.getString(Constants.ACCOUNT, ""));
        isCanSendCode.set(Utils.isMobile(phoneNum.get()));
    }

    /**
     * 是否是首次登录
     */
    public void loginByPwd(boolean isFirstLogin) {
        if (TextUtils.isEmpty(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_can_not_be_null);
            return;
        }
        if (!Utils.isMobile(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return;
        }
        if (TextUtils.isEmpty(pwdNum.get())) {
            BlackToast.show(R.string.string_password_can_not_be_null);
            return;
        }

        UserCenter.login(phoneNum.get(), pwdNum.get(), new LoginCallback(isFirstLogin));
    }


    /**
     * 手机号注册
     */
    public void regist() {
        if (!checkCanRegist(true)) return;
        UserCenter.regist(phoneNum.get(), pwdNum.get(), codeNum.get(), new NetCallBack<ResultPair>() {

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
                loginByPwd(true);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                BlackToast.show(resultPair.getData());
                dismissDialog();
            }
        });
    }


    /**
     * 验证码登录-------------------------------------start
     */

    /**
     * 发送验证码
     */
    public void fetchCaptchaLoginCode() {
        if (!Utils.isMobilePhone(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return;
        }
        if (!Utils.isMobile(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return;
        }
        UserCenter.fetchCaptchaLoginCode(phoneNum.get(), mSendCodeCallback);
    }

    /**
     * 验证该验证码是否正确
     */
    public void verifyCaptchaLoginCode() {

        if (!Utils.isMobilePhone(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return;
        }
        if (!Utils.isMobile(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return;
        }

        if (TextUtils.isEmpty(codeNum.get())) {
            BlackToast.show(R.string.string_input_code);
            return;
        }
        UserCenter.verifyCaptchaLoginCode(phoneNum.get(), codeNum.get(), new NetCallBack<String>() {

            @Override
            public void noNet() {
                BlackToast.show(R.string.string_please_check_your_network);
            }

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showDialog(R.string.string_logining, false);
            }

            @Override
            public void onSucceed(String json) {
                Boolean isNew = JSONUtils.getBooon(json, "isNew");
                loginSuccess(isNew);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                BlackToast.show(resultPair.getData());
            }
        });
    }

    /**
     * 微信登录
     */
    public void wechatLogin(String code) {
        UserCenter.wechatLogin(code, new NetCallBack<WechatUserInfo>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_please_check_your_network);
            }

            @Override
            public void onSucceed(WechatUserInfo wechatUserInfo) {
                Logger.w("微信登录成功", wechatUserInfo.toString());
                if (wechatUserInfo.isBind()) {
                    SpUtils.saveString(Constants.APITOKEN, wechatUserInfo.getToken());
                    SpUtils.saveString(Constants.APIUID, wechatUserInfo.getUid());
                    SpUtils.saveString(Constants.ACCOUNT, wechatUserInfo.getMobile());
                    if (!TextUtils.isEmpty(UmengUtils.getDeviceToken())) {
                        UserCenter.setUmengToken(UmengUtils.getDeviceToken(), new NetCallBack<Boolean>() {
                            @Override
                            public void onSucceed(Boolean data) {
                                Logger.w("友盟推送token设置成功");
                            }
                        });
                    }
                    CrashReport.setUserId(App.get().getApiUid());
                    loginSuccess(wechatUserInfo.isNew());
                } else {
                    checkToken = wechatUserInfo.getCheckToken();
                    IntentUtils.goToBindPhone(getContext());
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
            }
        });
    }

    /**
     * 清空手机号
     */
    public void clearPhoneNum() {
        if (!TextUtils.isEmpty(phoneNum.get())) {
            phoneNum.set("");
        }
    }

    /**
     * 清空密码
     */
    public void clearPwdNum() {
        if (!TextUtils.isEmpty(pwdNum.get())) {
            pwdNum.set("");
        }
    }

    /**
     * 清空验证码
     */
    public void clearCodeNum() {
        if (!TextUtils.isEmpty(codeNum.get())) {
            codeNum.set("");
        }
    }

    /**
     * 重置密码的校验条件
     *
     * @param phone
     * @param code
     * @param pwd
     * @return
     */
    public boolean checkCanResetPwd(String phone, String code, String pwd) {
        if (!Utils.isMobilePhone(phone)) {
            return false;
        }

        if (!Utils.isMobile(phone)) {
            return false;
        }

        if (TextUtils.isEmpty(code) || code.length() < 6) {
            return false;
        }
        return PwdCheckUtil.checkContainTwo(pwd);
    }

    /**
     * 校验是否满足点击登录按钮的条件
     *
     * @param phone
     * @param pwd
     * @param code
     * @return
     */
    public boolean checkCanLogin(String phone, String pwd, String code) {
        if (!Utils.isMobilePhone(phone)) {
            return false;
        }
        if (!Utils.isMobile(phone)) {
            return false;
        }

        if (isVerifyCodeLogin.get()) {
            if (TextUtils.isEmpty(code) || code.length() < 6) {
                return false;
            }
        } else {
            if (TextUtils.isEmpty(pwd) || pwd.trim().length() < 6) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验是否满足点击绑定手机号按钮的条件
     *
     * @param phone
     * @param code
     * @return
     */
    public boolean checkCanBindPhone(String phone, String code) {
        if (!Utils.isMobilePhone(phone)) {
            return false;
        }
        if (!Utils.isMobile(phone)) {
            return false;
        }

        if (TextUtils.isEmpty(code) || code.length() < 6) {
            return false;
        }
        return true;
    }

    /**
     * 验证码登录-------------------------------------end
     */


    /**
     * 发送注册验证码
     */
    public void sendRegistCode() {
        if (checkCanRegist(false)) {
            UserCenter.sendRegistCode(phoneNum.get(), mSendCodeCallback);
        }
    }


    /**
     * 检查是否能注册
     *
     * @param checkCode 是否验证验证码
     */
    private boolean checkCanRegist(boolean checkCode) {
        if (!Utils.isMobilePhone(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return false;
        }
        if (!Utils.isMobile(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return false;
        }
        if (TextUtils.isEmpty(pwdNum.get())) {
            BlackToast.show(R.string.string_password_can_not_be_null);
            return false;
        }
        if (checkCode) {
            if (TextUtils.isEmpty(codeNum.get())) {
                BlackToast.show(R.string.string_code_can_not_be_null);
                return false;
            }
        }
        return true;
    }


    /**
     * 检查是否能绑定
     */
    private boolean checkCanBind(boolean checkCode) {
        if (!Utils.isMobilePhone(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return false;
        }
        if (!Utils.isMobile(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return false;
        }
        if (checkCode) {
            if (TextUtils.isEmpty(codeNum.get())) {
                BlackToast.show(R.string.string_code_can_not_be_null);
                return false;
            }
        }
        return true;
    }

    public void goToLogin() {
        IntentUtils.goToLogin(getContext());
        finishActivity();
    }


    public void goToRegist() {
        IntentUtils.goToRegist(getContext());
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

    /**
     * 发送忘记密码短信验证码
     */
    public void sendForgetPwdCode() {
        if (!Utils.isMobilePhone(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return;
        }
        if (!Utils.isMobile(phoneNum.get())) {
            BlackToast.show(R.string.string_phone_not_correct);
            return;
        }
        UserCenter.sendForgetPwdSmsCode(phoneNum.get(), mSendCodeCallback);
    }

    /**
     * 验证忘记密码短信验证码
     */
    public void validateSmsCode(String phoneNum, String smsCode, NetCallBack<ResultPair> netCallBack) {
        UserCenter.validateSmsCode(phoneNum, smsCode, netCallBack);
    }

    /**
     * 忘记密码重置密码
     */
    public void resetLoginPwd(String mobile, String pwd, String smsCode, NetCallBack<ResultPair> netCallBack) {
        UserCenter.resetLoginPwdByForget(mobile, pwd, smsCode, netCallBack);
    }

    public void doCodeCountDown(int leftTime) {
        mCodeCountDownTimer = new CountDownTimer(leftTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isCanSendCode.set(false);
                sendCodeStr.set((int) (millisUntilFinished / 1000) + "s");
                leftTimes.set((int) (millisUntilFinished / 1000));
                Logger.w("left time is :", leftTimes.get());
            }

            @Override
            public void onFinish() {
                isCanEditPhone.set(true);
                isCanSendCode.set(true);
                sendCodeStr.set(UIUtils.getString(R.string.string_resend));
                leftTimes.set(0);
            }
        };
        isCanEditPhone.set(false);
        mCodeCountDownTimer.start();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        if (mCodeCountDownTimer != null) {
            mCodeCountDownTimer.cancel();
        }
    }

    /**
     * 支付宝登录
     */
    public void alipayLogin(String authCode) {
        UserCenter.alipayLogin(authCode, new BindCallback());
    }

    public void sendBindCode() {
        if (checkCanBind(false)) {
            UserCenter.sendBindCode(phoneNum.get(), checkToken, mSendCodeCallback);
        }
    }

    public void bindPhone() {
        if (checkCanBind(true)) {
            UserCenter.bindPhone(phoneNum.get(), codeNum.get(), checkToken, new BindCallback());
        }
    }

    class BindCallback extends NetCallBack<BindBean> {

        @Override
        public void noNet() {
            super.noNet();
            BlackToast.show(R.string.string_please_check_your_network);
        }

        @Override
        public void onSucceed(BindBean data) {
            if (data.isBind()) {
                SpUtils.saveString(Constants.ACCOUNT, data.getMobile());
                SpUtils.saveString(Constants.APITOKEN, data.getToken());
                SpUtils.saveString(Constants.APIUID, data.getUid());
                if (!TextUtils.isEmpty(UmengUtils.getDeviceToken())) {
                    UserCenter.setUmengToken(UmengUtils.getDeviceToken(), new NetCallBack<Boolean>() {
                        @Override
                        public void onSucceed(Boolean data) {
                            Logger.w("友盟推送token设置成功");
                        }
                    });
                }
                CrashReport.setUserId(App.get().getApiUid());
                loginSuccess(data.isNew());
            } else {
                checkToken = data.getCheckToken();
                IntentUtils.goToBindPhone(getContext());
            }
        }

        @Override
        public void onFailed(ResultPair resultPair) {
            BlackToast.show(resultPair.getData());
            dismissDialog();
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
                profileBean = data.get(0);
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

        //设置是否是新用户
        App.get().setNewUser(isFirstLogin);

        getDialog().setOnDismissListener(dialog -> {
            //首次注册登录跳转至新建档案
            if (isFirstLogin) {
                //扫描二维码
                IntentUtils.goToScanQRCode(getContext(), profileBean);
            } else {
                //首次使用app,是否选择过设备型号，没有则选择
                IntentUtils.goToMain(getContext());
                ActivityManager.finishOthersActivity(HomeActivity.class);
            }
        });
        dismissDialog();
    }
}

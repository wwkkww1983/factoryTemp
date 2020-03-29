package com.proton.carepatchtemp.activity.user;

import android.view.View;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.databinding.ActivityAccountAndSafeBinding;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.UserCenter;
import com.proton.carepatchtemp.socailauth.PlatformType;
import com.proton.carepatchtemp.socailauth.SocialApi;
import com.proton.carepatchtemp.socailauth.listener.AuthListener;
import com.proton.carepatchtemp.utils.AlipayLogin;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.view.WarmDialogHorizental;
import com.wms.logger.Logger;

import java.util.Map;

public class AccountAndSafeActivity extends BaseActivity<ActivityAccountAndSafeBinding> implements View.OnClickListener {

    private boolean wechatStatus = false;
    private boolean alipayStatus = false;
    public static final int wechatType = 1;
    public static final int alipayType = 4;
    private SocialApi mSocialApi;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_account_and_safe;
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_account_safe);
    }

    @Override
    protected void init() {
        super.init();
        mSocialApi = SocialApi.get(mContext);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.setOnClick(this);

    }

    @Override
    protected void initData() {
        super.initData();
        queryThirdAuthStatus();
    }


    /**
     * 查询第三方绑定状态
     */
    private void queryThirdAuthStatus() {
        UserCenter.queryThirdAuthStatus(new NetCallBack<String>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(String json) {
                wechatStatus = JSONUtils.getBooon(json, "wechat");
                alipayStatus = JSONUtils.getBooon(json, "aliPay");
                Logger.w("查询绑定状态成功 wechat : ", wechatStatus, " alipay : ", alipayStatus);
                binding.idWechatBindStatus.setText(wechatStatus ? R.string.string_is_binding : R.string.string_to_bind);
                binding.idWechatBindStatus.setTextColor(wechatStatus ? getResColor(R.color.color_gray_b3) : getResColor(R.color.color_blue_30));
                binding.idAlipayBindStatus.setText(alipayStatus ? R.string.string_is_binding : R.string.string_to_bind);
                binding.idAlipayBindStatus.setTextColor(alipayStatus ? getResColor(R.color.color_gray_b3) : getResColor(R.color.color_blue_30));
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_account_safe:
                IntentUtils.goToForgetPwdActivity(mContext, true);
                break;
            case R.id.id_third_wechat:
                if (wechatStatus) {
                    unBindDialog(wechatType);
                } else {//去绑定
                    mSocialApi.doOauthVerify(this, PlatformType.WEIXIN, new MyAuthListener());
                }
                break;
            case R.id.id_third_aliPay:
                if (alipayStatus) {
                    unBindDialog(alipayType);
                } else {//去绑定
                    aliplayLogin();
                }
                break;
        }
    }


    /**
     * 解绑第三方绑定的弹框
     *
     * @param type 1:wechat 4:alipay
     */
    private void unBindDialog(int type) {
        String content = null;
        if (type == wechatType) {
            content = "确定要解除账号与微信的关联吗?";
        } else if (type == alipayType) {
            content = "确定要解除账号与支付宝的关联吗?";
        }
        WarmDialogHorizental dialogHorizental = new WarmDialogHorizental(this);
        dialogHorizental.setTopText(R.string.string_unbind);
        dialogHorizental.setConfirmText("解除绑定");
        dialogHorizental.setContent(content);
        dialogHorizental.setConfirmListener(v -> {
            dialogHorizental.dismiss();
            unBind(type);
        });
        dialogHorizental.setOnCancelListener(dialog -> dialogHorizental.dismiss());
        dialogHorizental.show();
    }

    /**
     * 解绑第三方绑定
     *
     * @param type 1:wechat 4:alipay
     */
    private void unBind(int type) {
        showDialog(getString(R.string.string_unbinding));
        UserCenter.unbindThirdAuth(type, new NetCallBack<Boolean>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(Boolean data) {
                dismissDialog();
                BlackToast.show(R.string.string_unbind_success);
                queryThirdAuthStatus();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
            }
        });
    }

    /**
     * 绑定第三方账号
     */
    private void bindThirdAuth(int type, String authCode) {
        UserCenter.bindThirdAuth(type, authCode, new NetCallBack<Boolean>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(Boolean data) {
                Logger.w("绑定成功");
                dismissDialog();
                BlackToast.show(R.string.string_bind_success);
                queryThirdAuthStatus();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                BlackToast.show(resultPair.getData());
            }
        });
    }


    /**
     * 微信授权回调
     */
    public class MyAuthListener implements AuthListener {
        @Override
        public void onComplete(PlatformType platform_type, Map<String, String> map) {
            String code = map.get("code");
            Logger.w("微信登录code :", code);
            bindThirdAuth(wechatType, code);
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
     * 支付宝授权回调
     */
    private void aliplayLogin() {
        AlipayLogin.login(this, new AlipayLogin.AlipayLoginCallback() {
            @Override
            public void onSuccess(String authCode) {
                bindThirdAuth(alipayType, authCode);
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


}

package com.proton.carepatchtemp.net.center;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.proton.carepatchtemp.bean.BindBean;
import com.proton.carepatchtemp.bean.BindEmailBean;
import com.proton.carepatchtemp.bean.MessageBean;
import com.proton.carepatchtemp.bean.WechatUserInfo;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.net.RetrofitHelper;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.NetSubscriber;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.Constants;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.Settings;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.utils.UmengUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.tencent.bugly.crashreport.CrashReport;
import com.wms.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import io.reactivex.functions.Function;

/**
 * Created by wangmengsi on 2018/2/26.
 */

public class UserCenter extends DataCenter {

    /**
     * 2019 8 22 新增 验证码登录 和 微信登录 ------------------------------------------start
     */
    /**
     * 获取验证码
     *
     * @param mobile
     * @param callBack
     */
    public static void fetchCaptchaLoginCode(String mobile, NetCallBack<String> callBack) {
        Map<String, String> params = new WeakHashMap<>();
        params.put("mobile", Utils.encrypt(mobile, Settings.ENCRYPT_KEY));
        RetrofitHelper.getUserApi().fetchCaptchaLoginCode(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String aBoolean) {
                        callBack.onSucceed(aBoolean);
                    }
                });
    }

    /**
     * 验证获取的验证码是否正确
     *
     * @param mobile
     * @param captcha
     * @param callBack
     */
    public static void verifyCaptchaLoginCode(String mobile, String captcha, NetCallBack<String> callBack) {
        Map<String, String> params = new WeakHashMap<>();
        params.put("mobile", mobile);
        params.put("captcha", captcha);
        RetrofitHelper.getUserApi().verifyCaptchaLoginCode(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    String uid = JSONUtils.getString(resultPair.getData(), "uid");
                    String token = JSONUtils.getString(resultPair.getData(), "token");
                    SpUtils.saveString(Constants.ACCOUNT, mobile);
                    SpUtils.saveString(Constants.APIUID, uid);
                    SpUtils.saveString(Constants.APITOKEN, token);
                    if (!TextUtils.isEmpty(UmengUtils.getDeviceToken())) {
                        UserCenter.setUmengToken(UmengUtils.getDeviceToken(), new NetCallBack<Boolean>() {
                            @Override
                            public void onSucceed(Boolean data) {
                                Logger.w("友盟推送token设置成功");
                            }
                        });
                    }
                    CrashReport.setUserId(App.get().getApiUid());
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String s) {
                        callBack.onSucceed(s);
                    }
                });
    }

    /**
     * 微信登录
     *
     * @param authCode
     * @param callBack
     */
    public static void wechatLogin(String authCode, NetCallBack<WechatUserInfo> callBack) {
        Map<String, String> params = new WeakHashMap<>();
        params.put("auth_code", authCode);
        RetrofitHelper.getUserApi().wechatLogin(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        WechatUserInfo wechatUserInfo = JSONUtils.getObj(resultPair.getData(), WechatUserInfo.class);
                        return wechatUserInfo;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<WechatUserInfo>(callBack) {
                    @Override
                    public void onNext(WechatUserInfo wechatUserInfo) {
                        callBack.onSucceed(wechatUserInfo);
                    }
                });
    }


    /**
     * 解绑第三方登录
     *
     * @param type     1:wechat  4:aliPay
     * @param callBack
     */
    public static void unbindThirdAuth(int type, NetCallBack<Boolean> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(type));
        RetrofitHelper.getUserApi().unbindThirdAuth(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        return true;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<Boolean>(callBack) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        callBack.onSucceed(aBoolean);
                    }
                });
    }

    /**
     * 绑定第三方账号
     *
     * @param type     1:wechat  4:aliPay
     * @param callBack
     */
    public static void bindThirdAuth(int type, String auth_code, NetCallBack<Boolean> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(type));
        params.put("auth_code", auth_code);
        RetrofitHelper.getUserApi().bindThirdAuth(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        return true;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<Boolean>(callBack) {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        callBack.onSucceed(aBoolean);
                    }
                });
    }


    /**
     * 查询第三方绑定状态
     *
     * @param callBack
     */
    public static void queryThirdAuthStatus(NetCallBack<String> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("apiuid", App.get().getApiUid());
        RetrofitHelper.getUserApi().queryThirdAuthStatus(params)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        ResultPair resultPair = parseResult(s);
                        if (resultPair.isSuccess()) {
                            return resultPair.getData();
                        } else {
                            throw new ParseResultException(resultPair.getData());
                        }
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String data) {
                        callBack.onSucceed(data);
                    }
                });
    }


    /**
     * 2019 8 22 新增 验证码登录 和 微信登录 ------------------------------------------end
     */


    /**
     * 手机号密码登录
     */
    public static void login(String phoneNum, String pwd, NetCallBack<Boolean> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phoneNum);
        params.put("pwd", Utils.md5(pwd));
        params.put("type", "0");

        RetrofitHelper.getUserApi().login(params)
                .map(s -> {
                    Logger.json(s);
                    ResultPair resultPair = DataCenter.parseResult(s);
                    if (resultPair.isSuccess()) {
                        SpUtils.saveString(Constants.ACCOUNT, phoneNum);
                        SpUtils.saveString(Constants.APITOKEN
                                , JSONUtils.getString(resultPair.getData(), Constants.APITOKEN));
                        SpUtils.saveString(Constants.APIUID
                                , JSONUtils.getString(JSONUtils.getString(resultPair.getData(), "user"), "id"));
                        if (!TextUtils.isEmpty(UmengUtils.getDeviceToken())) {
                            UserCenter.setUmengToken(UmengUtils.getDeviceToken(), new NetCallBack<Boolean>() {
                                @Override
                                public void onSucceed(Boolean data) {
                                    Logger.w("友盟推送token设置成功");
                                }
                            });
                        }
                        CrashReport.setUserId(App.get().getApiUid());
                        return resultPair.isSuccess();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<Boolean>(callBack) {
                    @Override
                    public void onNext(Boolean result) {
                        callBack.onSucceed(result);
                    }
                });
    }

    public static void alipayLogin(String authCode, NetCallBack<BindBean> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("auth_code", authCode);
        RetrofitHelper.getUserApi()
                .alipayLogin(params)
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        return JSONUtils.getObj(resultPair.getData(), BindBean.class);
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<BindBean>(callBack) {
                    @Override
                    public void onNext(BindBean value) {
                        callBack.onSucceed(value);
                    }
                });
    }

    /**
     * 发送注册验证码
     */
    public static void sendRegistCode(String phoneNum, NetCallBack<String> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", Utils.encrypt(phoneNum, Settings.ENCRYPT_KEY));
        RetrofitHelper.getUserApi()
                .sendRegistCode(params)
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String value) {
                        callBack.onSucceed(value);
                    }
                });
    }

    /**
     * 忘记密码发送短信验证码
     */
    public static void sendForgetPwdSmsCode(String phoneNum, NetCallBack<String> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", Utils.encrypt(phoneNum, Settings.ENCRYPT_KEY));
        RetrofitHelper.getUserApi()
                .sendForgetPwdSmsCode(params)
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String value) {
                        callBack.onSucceed(value);
                    }
                });
    }

    /**
     * 手机号注册
     */
    public static void regist(String phone, String pwd, String code, NetCallBack<ResultPair> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("pwd", Utils.md5(pwd));
        params.put("code", code);

        //先验证验证码是否正确，接口设计的很弱智
        validateRegistCode(phone, code, new NetCallBack<ResultPair>() {
            @Override
            public void onSucceed(ResultPair data) {
                RetrofitHelper.getUserApi().regist(params)
                        .map(DataCenter::parseResult)
                        .compose(threadTrans())
                        .subscribe(new NetSubscriber<ResultPair>(callBack) {
                            @Override
                            public void onNext(ResultPair resultPair) {
                                if (resultPair.isSuccess()) {
                                    callBack.onSucceed(resultPair);
                                } else {
                                    callBack.onFailed(resultPair);
                                }
                            }
                        });
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                callBack.onFailed(resultPair);
            }
        });
    }

    /**
     * 验证注册验证码
     */
    private static void validateRegistCode(String phone, String code, NetCallBack<ResultPair> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("captcha", code);

        RetrofitHelper.getUserApi().validateRegistCode(params)
                .map(DataCenter::parseResult)
                .compose(threadTrans())
                .subscribe(new NetSubscriber<ResultPair>(callBack) {
                    @Override
                    public void onNext(ResultPair resultPair) {
                        if (resultPair.isSuccess()) {
                            callBack.onSucceed(resultPair);
                        } else {
                            callBack.onFailed(resultPair);
                        }
                    }
                });
    }

    /**
     * 校验忘记密码的短信验证码
     */
    public static void validateSmsCode(String phoneNum, String smsCode, NetCallBack<ResultPair> netCallBack) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("mobile", phoneNum);
        paramsMap.put("captcha", smsCode);
        RetrofitHelper.getUserApi().validateForgetPwdSmsCode(paramsMap).map(DataCenter::parseResult).compose(threadTrans())
                .subscribe(new NetSubscriber<ResultPair>(netCallBack) {
                    @Override
                    public void onNext(ResultPair resultPair) {
                        if (resultPair.isSuccess()) {
                            netCallBack.onSucceed(resultPair);
                        } else {
                            netCallBack.onFailed(resultPair);
                        }
                    }
                });
    }

    public static void resetLoginPwdByForget(String mobile, String pwd, String smsCode, NetCallBack<ResultPair> netCallBack) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mobile", mobile);
        paramMap.put("pwd", Utils.md5(pwd));
        paramMap.put("code", smsCode);
        paramMap.put("newpwd", Utils.md5(pwd));
        RetrofitHelper.getUserApi().resetLoginPwdByForgeting(paramMap).map(DataCenter::parseResult).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(netCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                if (resultPair.isSuccess()) {
                    netCallBack.onSucceed(resultPair);
                } else {
                    netCallBack.onFailed(resultPair);
                }
            }
        });
    }


    /**
     * 国际版--邮箱登录
     *
     * @param email
     * @param pwd
     * @param callBack
     */
    public static void loginByEmail(String email, String pwd, NetCallBack<Boolean> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mail", email);
        params.put("password", Utils.md5(pwd));
        params.put("version", App.get().getVersion());
        params.put("company", Settings.COMPANY);
        RetrofitHelper.getUserApi().loginEmail(params)
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        SpUtils.saveString(Constants.ACCOUNT, email);
                        SpUtils.saveString(Constants.APITOKEN, JSONUtils.getString(resultPair.getData(), Constants.EMAIL_TOKEN));
                        SpUtils.saveString(Constants.APIUID, JSONUtils.getString(resultPair.getData(), Constants.EMAIL_UID));
                        if (!TextUtils.isEmpty(UmengUtils.getDeviceToken())) {
                            UserCenter.setUmengToken(UmengUtils.getDeviceToken(), new NetCallBack<Boolean>() {
                                @Override
                                public void onSucceed(Boolean data) {
                                    Logger.w("友盟推送token设置成功");
                                }
                            });
                        }
                        CrashReport.setUserId(App.get().getApiUid());
                        return true;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<Boolean>(callBack) {
                    @Override
                    public void onNext(Boolean result) {
                        callBack.onSucceed(result);
                    }
                });
    }


    /**
     * 国际版--发送邮箱注册验证码
     */
    public static void sendRegistEamilCode(String email, NetCallBack<String> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mail", email);
        RetrofitHelper.getUserApi().sendRegistEmailCode(params)
                .compose(threadTrans())
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String value) {
                        callBack.onSucceed(value);
                    }
                });
    }


    /**
     * 国际版--注册邮箱
     *
     * @param email
     * @param pwdNum
     * @param codeNum
     * @param callBack
     */
    public static void registEmail(String email, String pwdNum, String codeNum, NetCallBack<ResultPair> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mail", email);
        params.put("password", Utils.md5(pwdNum));
        params.put("captcha", codeNum);
        RetrofitHelper.getUserApi().registEamil(params)
                .compose(threadTrans())
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    return resultPair;
                })
                .subscribe(new NetSubscriber<ResultPair>(callBack) {
                    @Override
                    public void onNext(ResultPair resultPair) {
                        if (resultPair.isSuccess()) {
                            callBack.onSucceed(resultPair);
                        } else {
                            callBack.onFailed(resultPair);
                        }
                    }
                });
    }

    /**
     * 国际版--校验忘记密码的邮件验证码,修改密码
     */
    public static void validateFPwdEmailCode(String email, String codeNum, String password, NetCallBack<ResultPair> netCallBack) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("mail", email);
        paramsMap.put("captcha", codeNum);
        paramsMap.put("password", Utils.md5(password));
        RetrofitHelper.getUserApi().validateFPwdEmailCode(paramsMap).map(DataCenter::parseResult).compose(threadTrans())
                .subscribe(new NetSubscriber<ResultPair>(netCallBack) {
                    @Override
                    public void onNext(ResultPair resultPair) {
                        if (resultPair.isSuccess()) {
                            netCallBack.onSucceed(resultPair);
                        } else {
                            netCallBack.onFailed(resultPair);
                        }
                    }
                });
    }

    /**
     * 忘记密码发送邮件验证码
     */
    public static void sendForgetPwdEmailCode(String email, NetCallBack<String> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mail", email);
        RetrofitHelper.getUserApi()
                .sendForgetPwdEmailCode(params)
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String value) {
                        callBack.onSucceed(value);
                    }
                });
    }


    /**
     * 设置友盟token
     */
    public static void setUmengToken(String deviceToken, NetCallBack<Boolean> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("umtoken", deviceToken);
        RetrofitHelper.getUserApi()
                .setUmengToken(params)
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        Logger.w("设置友盟token成功");
                        return resultPair.isSuccess();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<Boolean>(callBack) {
                    @Override
                    public void onNext(Boolean value) {
                        callBack.onSucceed(value);
                    }
                });
    }

    /**
     * facebook登录 登录成功后判断是否验证过邮箱
     *
     * @param userId
     * @param token
     * @param callback
     */
    public static void loginFaceBook(String userId, String token, NetCallBack<BindEmailBean> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("facebookId", userId);
        params.put("idToken", token);
        RetrofitHelper.getUserApi().loginFacebook(params)
                .compose(threadTrans())
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        BindEmailBean emailBean = JSONUtils.getObj(resultPair.getData(), BindEmailBean.class);
                        if (emailBean.isMailBind()) {//如果已经绑定，则保持当前用户信息
                            saveInternalLoginInfo(emailBean);
                        }
                        return emailBean;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }

                })
                .subscribe(new NetSubscriber<BindEmailBean>(callback) {
                    @Override
                    public void onNext(BindEmailBean emailBean) {
                        callback.onSucceed(emailBean);
                    }
                });
    }

    public static void loginGoogle(String userId, String token, NetCallBack<BindEmailBean> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("googleId", userId);
        params.put("idToken", token);
        RetrofitHelper.getUserApi().loginGoogle(params)
                .compose(threadTrans())
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        BindEmailBean emailBean = JSONUtils.getObj(resultPair.getData(), BindEmailBean.class);
                        if (emailBean.isMailBind()) {
                            saveInternalLoginInfo(emailBean);
                        }
                        return emailBean;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }

                })
                .subscribe(new NetSubscriber<BindEmailBean>(callback) {
                    @Override
                    public void onNext(BindEmailBean emailBean) {
                        callback.onSucceed(emailBean);
                    }
                });
    }

    public static void sendBindEmailCode(String mail, NetCallBack<String> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mail", mail);
        RetrofitHelper.getUserApi().sendBindEmailCode(params)
                .compose(threadTrans())
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String result) {
                        callBack.onSucceed(result);
                    }
                });
    }

    public static void verifyBindEmailCode(String mail, String certToken, String captcha, NetCallBack<Boolean> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("mail", mail);
        params.put("certToken", certToken);
        params.put("captcha", captcha);
        RetrofitHelper.getUserApi().verifyBindEmailCode(params)
                .compose(threadTrans())
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        BindEmailBean emailBean = JSONUtils.getObj(resultPair.getData(), BindEmailBean.class);
                        if (emailBean.isMailBind()) {
                            saveInternalLoginInfo(emailBean);
                        }
                        return true;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .subscribe(new NetSubscriber<Boolean>(callBack) {
                    @Override
                    public void onNext(Boolean result) {
                        callBack.onSucceed(result);
                    }
                });
    }

    /**
     * 本地缓存当前用户数据
     *
     * @param emailBean
     */
    @NonNull
    private static void saveInternalLoginInfo(BindEmailBean emailBean) {
        Logger.json(emailBean.toString());
        SpUtils.saveString(Constants.ACCOUNT, emailBean.getMail());
        SpUtils.saveString(Constants.APITOKEN, emailBean.getToken());
        SpUtils.saveString(Constants.APIUID, String.valueOf(emailBean.getUid()));
        if (!TextUtils.isEmpty(UmengUtils.getDeviceToken())) {
            UserCenter.setUmengToken(UmengUtils.getDeviceToken(), new NetCallBack<Boolean>() {
                @Override
                public void onSucceed(Boolean data) {
                    Logger.w("友盟推送token设置成功");
                }
            });
        }
        CrashReport.setUserId(App.get().getApiUid());
    }

    public static void sendBindCode(String phoneNum, String checkToken, NetCallBack<String> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", Utils.encrypt(phoneNum, Settings.ENCRYPT_KEY));
        params.put("check_token", checkToken);
        RetrofitHelper.getUserApi()
                .sendBindCode(params)
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callback) {
                    @Override
                    public void onNext(String value) {
                        callback.onSucceed(value);
                    }
                });
    }

    public static void bindPhone(String phoneNum, String code, String checkToken, NetCallBack<BindBean> callback) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phoneNum);
        params.put("check_token", checkToken);
        params.put("captcha", code);
        RetrofitHelper.getUserApi()
                .bindPhone(params)
                .map(json -> {
                    ResultPair resultPair = DataCenter.parseResult(json);
                    if (resultPair.isSuccess()) {
                        return JSONUtils.getObj(resultPair.getData(), BindBean.class);
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<BindBean>(callback) {
                    @Override
                    public void onNext(BindBean value) {
                        callback.onSucceed(value);
                    }
                });
    }

    public static void getNewestMsg(NetCallBack<List<MessageBean>> callback) {
        RetrofitHelper.getUserApi().getNewestMsg().map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                Type listType = new TypeToken<ArrayList<MessageBean>>() {
                }.getType();
                return JSONUtils.<MessageBean>getObj(resultPair.getData(), listType);
            } else {
                throw new ParseResultException(resultPair.getData());
            }

        }).compose(threadTrans()).subscribe(new NetSubscriber<List<MessageBean>>(callback) {

            @Override
            public void onNext(List<MessageBean> datas) {
                callback.onSucceed(datas);
            }
        });
    }

    public static void markMsgAsRead(long messageId, int flag, NetCallBack<String> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("messageId", messageId);
        params.put("flag", flag);
        RetrofitHelper.getUserApi().markMsgAsRead(params).map(json -> {
            Logger.json(json);
            ResultPair resultPair = DataCenter.parseResult(json);
            if (resultPair.isSuccess()) {
                return resultPair.getData();
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<String>(callback) {

            @Override
            public void onNext(String datas) {
                callback.onSucceed(datas);
            }
        });
    }
}

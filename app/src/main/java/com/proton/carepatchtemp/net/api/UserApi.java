package com.proton.carepatchtemp.net.api;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by 王梦思 on 2017/4/10.
 */

public interface UserApi {

    String regist = "openapi/sign/register";
    String login = "openapi/sign/login";
    //    String loginGoogle = "openapi/sign/googleLogin";
//    String loginFacebook = "openapi/sign/faceBookLogin";
    String loginFacebook = "openapi/sign/facebook/login";
    String loginGoogle = "openapi/sign/google/login";
    String sendRegistCode = "openapi/sign/sendregistercaptcha";
    String sendBindCode = "openapi/login/third/mobile/captcha/get";
    String validateRegistCode = "openapi/sign/validateregistercaptcha";
    String getFpwdCap = "openapi/sign/sendforgetpwcaptcha";
    String valiFpwdCap = "openapi/sign/validateforgetpwcaptcha";
    String changeFpwd = "openapi/sign/changeforgetpw";
    String setUmengToken = "openapi/user/setumtoken";

    String sendRegistEmailCode = "openapi/sign/mail/register/captcha";
    String registEmail = "openapi/sign/mail/register/verify";
    String loginEmail = "openapi/sign/mail/login";
    String getFpwdCapEmail = "openapi/sign/mail/forget/captcha";
    String valiFpwdCapEmail = "openapi/sign/mail/forget/verify";
    String sendBindEmailCode = "openapi/sign/mail/bind/captcha";
    String verifyBindEmailCode = "openapi/sign/mail/bind/verify";

    /**
     * 2019 8 22 新增
     *
     * @param params
     * @return
     */
    String getCaptchaLoginCode = "openapi/sign/mobile/captcha/get";
    String verifyCaptchaLoginCode = "openapi/sign/mobile/captcha/verify";
    String wechatLogin = "openapi/login/third/wechat";
    String unbindThirdAuth = "openapi/third/unbind";
    String bindThirdAuth = "openapi/third/bind";
    String queryThirdAuthStatus = "openapi/third/status";


    @FormUrlEncoded
    @POST(login)
    Observable<String> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(loginFacebook)
    Observable<String> loginFacebook(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(loginGoogle)
    Observable<String> loginGoogle(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(sendRegistCode)
    Observable<String> sendRegistCode(@FieldMap Map<String, String> params);

    @GET(sendBindCode)
    Observable<String> sendBindCode(@QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST(regist)
    Observable<String> regist(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(validateRegistCode)
    Observable<String> validateRegistCode(@FieldMap Map<String, String> params);

    /**
     * 国际版--邮箱登录
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(loginEmail)
    Observable<String> loginEmail(@FieldMap Map<String, String> params);

    /**
     * 国际版邮箱注册--发送验证码
     *
     * @param params
     * @return
     */
    @GET(sendRegistEmailCode)
    Observable<String> sendRegistEmailCode(@QueryMap Map<String, String> params);

    /**
     * 国际版邮箱--邮箱验证、注册
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(registEmail)
    Observable<String> registEamil(@FieldMap Map<String, String> params);

    /**
     * 国际版--忘记密码发送邮件验证码
     */
    @GET(getFpwdCapEmail)
    Observable<String> sendForgetPwdEmailCode(@QueryMap Map<String, String> params);

    /**
     * 国际版--忘记密码验证邮件验证码,修改密码
     */
    @FormUrlEncoded
    @POST(valiFpwdCapEmail)
    Observable<String> validateFPwdEmailCode(@FieldMap Map<String, String> paramsMap);


    /**
     * 发送绑定邮箱的验证码
     *
     * @param paramsMap
     * @return
     */
    @GET(sendBindEmailCode)
    Observable<String> sendBindEmailCode(@QueryMap Map<String, String> paramsMap);

    /**
     * 验证绑定邮箱验证码
     *
     * @param paramsMap
     * @return
     */
    @FormUrlEncoded
    @POST(verifyBindEmailCode)
    Observable<String> verifyBindEmailCode(@FieldMap Map<String, String> paramsMap);

    /**
     * 忘记密码发送短信验证码
     */
    @FormUrlEncoded
    @POST(getFpwdCap)
    Observable<String> sendForgetPwdSmsCode(@FieldMap Map<String, String> params);

    /**
     * 忘记密码验证短信验证码
     */
    @FormUrlEncoded
    @POST(valiFpwdCap)
    Observable<String> validateForgetPwdSmsCode(@FieldMap Map<String, String> paramsMap);

    /**
     * 在忘记密码页重置登录密码
     */
    @FormUrlEncoded
    @POST(changeFpwd)
    Observable<String> resetLoginPwdByForgeting(@FieldMap Map<String, String> paramMap);

    @POST(setUmengToken)
    Observable<String> setUmengToken(@QueryMap Map<String, String> map);

    @POST("openapi/login/third/alipay")
    Observable<String> alipayLogin(@QueryMap Map<String, String> params);

    @POST("openapi/login/third/mobile/captcha/verify")
    Observable<String> bindPhone(@QueryMap Map<String, String> params);

    @GET("openapi/message/home/list")
    Observable<String> getNewestMsg();

    @FormUrlEncoded
    @POST("openapi/message/home/read")
    Observable<String> markMsgAsRead(@FieldMap Map<String, Object> paramMap);


    /**
     * 2019 8 22 新增 验证码登录 和 微信登录 两种方式
     */

    /**
     * 获取验证码登录的验证码
     *
     * @param params
     * @return
     */
    @GET(getCaptchaLoginCode)
    Observable<String> fetchCaptchaLoginCode(@QueryMap Map<String, String> params);

    /**
     * 验证该验证码是否正确
     */

    @FormUrlEncoded
    @POST(verifyCaptchaLoginCode)
    Observable<String> verifyCaptchaLoginCode(@FieldMap Map<String, String> paramMap);


    /**
     * 微信登录
     */
    @FormUrlEncoded
    @POST(wechatLogin)
    Observable<String> wechatLogin(@FieldMap Map<String, String> paramMap);


    /**
     * 解绑第三方账号
     */
    @FormUrlEncoded
    @POST(unbindThirdAuth)
    Observable<String> unbindThirdAuth(@FieldMap Map<String, String> paramMap);

    /**
     * 绑定第三方账号
     */
    @FormUrlEncoded
    @POST(bindThirdAuth)
    Observable<String> bindThirdAuth(@FieldMap Map<String, String> paramMap);


    /**
     * 查询第三方绑定状态
     *
     * @param params
     * @return
     */
    @GET(queryThirdAuthStatus)
    Observable<String> queryThirdAuthStatus(@QueryMap Map<String, String> params);
}

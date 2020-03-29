package com.proton.carepatchtemp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.common.GlobalWebActivity;
import com.proton.carepatchtemp.activity.common.ScanQRCodeActivity;
import com.proton.carepatchtemp.activity.device.DeviceBaseConnectingActivity;
import com.proton.carepatchtemp.activity.device.DeviceBasePatchPowerActivity;
import com.proton.carepatchtemp.activity.measure.AddNewDeviceActivity;
import com.proton.carepatchtemp.activity.measure.TempCurveActivity;
import com.proton.carepatchtemp.activity.profile.ProfileEditActivity;
import com.proton.carepatchtemp.activity.user.BindEmailActivity;
import com.proton.carepatchtemp.activity.user.BindPhoneActivity;
import com.proton.carepatchtemp.activity.user.ForgetPwdActivity;
import com.proton.carepatchtemp.activity.user.LoginActivity;
import com.proton.carepatchtemp.activity.user.LoginFirstActivity;
import com.proton.carepatchtemp.activity.user.LoginInternationalActivity;
import com.proton.carepatchtemp.activity.user.LoginInternationalByEmailActivity;
import com.proton.carepatchtemp.activity.user.RegistActivity;
import com.proton.carepatchtemp.activity.user.RegistInternationnalActivity;
import com.proton.carepatchtemp.activity.user.InternationalVerifyEmailCodeActivity;
import com.proton.carepatchtemp.component.AliyunService;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.component.OfflineReportUploadService;
import com.proton.carepatchtemp.net.bean.ProfileBean;

/**
 * Created by wangmengsi on 2018/2/26.
 * activity跳转统一管理
 */

public class IntentUtils {
    /**
     * 跳转到登录注册页面
     */
    public static void goToLoginFirst(Context context) {
        Intent intent = new Intent(context, LoginFirstActivity.class);
        context.startActivity(intent);
        finishActivity(context);
    }

    /**
     * 登录界面
     */
    public static void goToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * 注册界面
     */
    public static void goToRegist(Context context) {
        Intent intent = new Intent(context, RegistActivity.class);
        context.startActivity(intent);
    }


    /**
     * 国际版--跳转到邮箱登录
     */
    public static void goToLoginByEmail(Context context) {
        goToLoginByEmail(context, null, null);
    }

    /**
     * 国际版--跳转到邮箱登录
     */
    public static void goToLoginByEmail(Context context, String email, String pwd) {
        Intent intent = new Intent(context, LoginInternationalByEmailActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("pwd", pwd);
        context.startActivity(intent);
    }

    /**
     * 国际版--跳转到邮箱注册
     */
    public static void goToRegistInternationnal(Context context) {
        Intent intent = new Intent(context, RegistInternationnalActivity.class);
        context.startActivity(intent);
    }

    /**
     * 国际版--跳转到邮箱验证
     */
    public static void goToVerifyEmailCode(Context context, String email, String pwd, boolean isRegist) {
        Intent intent = new Intent(context, InternationalVerifyEmailCodeActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("pwd", pwd);
        intent.putExtra("is_regist", isRegist);
        context.startActivity(intent);
    }

    public static void goToVerifyEmailCode(Context context, String email, String certToken, boolean iSBindEmail, boolean isRegist) {
        Intent intent = new Intent(context, InternationalVerifyEmailCodeActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("email_bind", iSBindEmail);
        intent.putExtra("cert_token", certToken);
        context.startActivity(intent);
    }


    private static void finishActivity(Context context) {
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    /**
     * 跳转到首页
     */
    public static void goToMain(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
        ActivityManager.finishOthersActivity(HomeActivity.class);
    }

    /**
     * log
     * 跳转到首页
     *
     * @param recreate 重新创建activity
     */
    public static void goToMain(Context context, boolean recreate) {
        if (recreate) {
            ActivityManager.finishActivity(HomeActivity.class);
        }
        goToMain(context);
    }

    /**
     * 跳转到网页
     */
    public static void goToWeb(Context context, String url) {
        goToWeb(context, url, false);
    }

    /**
     * 跳转到网页
     */
    public static void goToWeb(Context context, String url, boolean isBackBtnClose) {
        Intent intent = new Intent(context, GlobalWebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("backBtnClose", isBackBtnClose);
        context.startActivity(intent);
    }

    /**
     * 跳转到温度实时曲线
     */
    public static void goToTempCurve(Context context, String macaddress, float mWarmHighestTemp, float mWarmLowestTemp) {
        Intent intent = new Intent(context, TempCurveActivity.class);
        intent.putExtra("macaddress", macaddress);
        intent.putExtra("warmHighTemp", mWarmHighestTemp);
        intent.putExtra("warmLowTemp", mWarmLowestTemp);
        context.startActivity(intent);
    }

    /**
     * 配网
     *
     * @param ssid     wifi名称
     * @param password wifi密码
     */
    public static void goToDockerSetingNetwork(Context context, String ssid, String password, ProfileBean profileBean) {
        Intent intent = new Intent(context, DeviceBaseConnectingActivity.class);
        intent.putExtra("ssid", ssid);
        intent.putExtra("password", password);
        intent.putExtra("profileBean", profileBean);
        context.startActivity(intent);
    }

    /**
     * 配网
     */
    public static void goToDockerSetNetwork(Context context, boolean isReSet, String dockerMac) {
        goToDockerSetNetwork(context, isReSet, dockerMac, null);
    }

    public static void goToDockerSetNetwork(Context context, boolean isReSet, String dockerMac, ProfileBean profileBean) {
        Intent intent = new Intent(context, DeviceBasePatchPowerActivity.class);
        intent.putExtra("isReSet", isReSet);
        if (profileBean != null) {
            intent.putExtra("profileBean", profileBean);
        }
        intent.putExtra("dockerMac", dockerMac);
        context.startActivity(intent);
    }

    /**
     * 配网
     */
    public static void goToDockerSetNetwork(Context context, boolean isReset) {
        goToDockerSetNetwork(context, isReset, "");
    }

    /**
     * 配网
     */
    public static void goToDockerSetNetwork(Context context) {
        goToDockerSetNetwork(context, false, "");
    }

    public static void goToAddNewDevice(Context context) {
        goToAddNewDevice(context, false, null);
    }

    public static void goToAddNewDevice(Context context, boolean directToScan, ProfileBean profile) {
        Intent intent = new Intent(context, AddNewDeviceActivity.class);
        intent.putExtra("directToScan", directToScan);
        intent.putExtra("profile", profile);
        context.startActivity(intent);
    }

    /**
     * 二维码扫描
     *
     * @param go2ScanDevice 是否跳转到扫描设备
     */
    public static void goToScanQRCode(Context context, ProfileBean profile, boolean go2ScanDevice) {
        Intent intent = new Intent(context, ScanQRCodeActivity.class);
        if (profile != null) {
            intent.putExtra("profile", profile);
        }
        intent.putExtra("go2ScanDevice", go2ScanDevice);
        context.startActivity(intent);
    }

    /**
     * 二维码扫描
     */
    public static void goToScanQRCode(Context context, ProfileBean profile) {
        Intent intent = new Intent(context, ScanQRCodeActivity.class);
        if (profile != null) {
            intent.putExtra("profile", profile);
        }
        intent.putExtra("go2ScanDevice", false);
        context.startActivity(intent);
    }

    public static void goToScanQRCode(Context context) {
        goToScanQRCode(context, null);
    }

    public static void goToInternalLogin(Context context) {
        context.startActivity(new Intent(context, LoginInternationalActivity.class));
    }

    /**
     * 绑定手机号
     */
    public static void goToBindPhone(Context context) {
        context.startActivity(new Intent(context, BindPhoneActivity.class));
    }

    public static void goToBindEmail(Context context, String certToken) {
        Intent intent = new Intent(context, BindEmailActivity.class);
        intent.putExtra("cert_token", certToken);
        context.startActivity(intent);
    }

    /**
     * 修改档案
     */
    public static void goToEditProfile(Context context, ProfileBean profileBean) {
        Intent intent = new Intent(context, ProfileEditActivity.class);
        intent.putExtra("profileBean", profileBean);
        context.startActivity(intent);
    }

    /**
     * 忘记密码
     *
     * @param context
     */
    public static void goToForgetPwdActivity(Context context) {
        context.startActivity(new Intent(context, ForgetPwdActivity.class));
    }

    public static void goToForgetPwdActivity(Context context, boolean isFromAccountAndSafe) {
        context.startActivity(new Intent(context, ForgetPwdActivity.class).putExtra("is_from_account_and_safe", isFromAccountAndSafe));
    }

    /**
     * 对于8.0系统后台启动服务，java.lang.IllegalStateException: Not allowed to start service Intent xxx app is in background uid UidRecord
     * 8.0后台启动service需要使用startForeGroundService(),并在service中指定StartForeGround(),会出现前台通知，所以此项目暂时通过判断app是否处于前台进行处理。
     * 开启阿里云服务统一管理
     *
     * @param context
     */
    public static void startAliyunService(Context context) {
        //开启阿里云服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (App.get().isForeground()) {
                context.startService(new Intent(context, AliyunService.class));
            }
        } else {
            context.startService(new Intent(context, AliyunService.class));
        }
    }

    /**
     * 开启离线报告上传服务
     *
     * @param context
     */
    public static void startOffLineReportUploadService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (App.get().isForeground()) {
                context.startService(new Intent(context, OfflineReportUploadService.class));
            }
        } else {
            context.startService(new Intent(context, OfflineReportUploadService.class));
        }
    }


}

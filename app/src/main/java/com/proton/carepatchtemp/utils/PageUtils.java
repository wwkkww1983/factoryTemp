package com.proton.carepatchtemp.utils;

import android.text.TextUtils;

import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.common.PermissionsActivity;
import com.proton.carepatchtemp.activity.common.ScanQRCodeActivity;
import com.proton.carepatchtemp.activity.common.SplashActivity;
import com.proton.carepatchtemp.activity.common.UserGuideActivity;
import com.proton.carepatchtemp.activity.device.DeviceBaseCheckWifiConnectActivity;
import com.proton.carepatchtemp.activity.device.DeviceBaseConnectFailActivity;
import com.proton.carepatchtemp.activity.device.DeviceBaseConnectingActivity;
import com.proton.carepatchtemp.activity.device.DeviceBaseInputWifiPwdActivity;
import com.proton.carepatchtemp.activity.device.DeviceBasePatchPowerActivity;
import com.proton.carepatchtemp.activity.device.DockerDetailActivity;
import com.proton.carepatchtemp.activity.device.PatchDetailActivity;
import com.proton.carepatchtemp.activity.device.FirewareUpdatingActivity;
import com.proton.carepatchtemp.activity.managecenter.AboutProtonActivity;
import com.proton.carepatchtemp.activity.managecenter.DeviceUpdateMsgActivity;
import com.proton.carepatchtemp.activity.managecenter.FeedBackActivity;
import com.proton.carepatchtemp.activity.managecenter.MsgCenterActivity;
import com.proton.carepatchtemp.activity.measure.AddNewDeviceActivity;
import com.proton.carepatchtemp.activity.measure.DeviceShareActivity;
import com.proton.carepatchtemp.activity.measure.DrugRecordActivity;
import com.proton.carepatchtemp.activity.measure.NurseSuggestBaseInfoActivity;
import com.proton.carepatchtemp.activity.measure.NurseSuggestDescribeActivity;
import com.proton.carepatchtemp.activity.measure.TempCurveActivity;
import com.proton.carepatchtemp.activity.profile.AddProfileActivity;
import com.proton.carepatchtemp.activity.profile.ProfileEditActivity;
import com.proton.carepatchtemp.activity.profile.ProfileNameEditActivity;
import com.proton.carepatchtemp.activity.report.AddReportNotesActivity;
import com.proton.carepatchtemp.activity.report.AddReportNotesRemarkActivity;
import com.proton.carepatchtemp.activity.report.PrePDFActivity;
import com.proton.carepatchtemp.activity.report.ReportDetailActivity;
import com.proton.carepatchtemp.activity.report.ReportLandscapChartActivity;
import com.proton.carepatchtemp.activity.report.SomOneMeasureReportActivity;
import com.proton.carepatchtemp.activity.user.ForgetPwdActivity;
import com.proton.carepatchtemp.activity.user.LoginActivity;
import com.proton.carepatchtemp.activity.user.LoginFirstActivity;
import com.proton.carepatchtemp.activity.user.LoginInternationalActivity;
import com.proton.carepatchtemp.activity.user.NewProfileStep1Activity;
import com.proton.carepatchtemp.activity.user.NewProfileStep2Activity;
import com.proton.carepatchtemp.activity.user.NewProfileStep3Activity;
import com.proton.carepatchtemp.activity.user.RegistActivity;
import com.proton.carepatchtemp.fragment.devicemanage.DeviceManageFragment;
import com.proton.carepatchtemp.fragment.home.HealthyTipsFragment;
import com.proton.carepatchtemp.fragment.home.ReportFragment;
import com.proton.carepatchtemp.fragment.home.SettingFragment;
import com.proton.carepatchtemp.fragment.measure.BeforeMeasureFragment;
import com.proton.carepatchtemp.fragment.measure.MeasureCardsFragment;
import com.proton.carepatchtemp.fragment.measure.MeasureChooseProfileFragment;
import com.proton.carepatchtemp.fragment.measure.MeasureContainerFragment;
import com.proton.carepatchtemp.fragment.measure.MeasureFragment;
import com.proton.carepatchtemp.fragment.measure.MeasureItemFragment;
import com.proton.carepatchtemp.fragment.measure.MeasureScanDeviceFragment;
import com.proton.carepatchtemp.fragment.measure.MeasureShareItemFragment;
import com.proton.carepatchtemp.fragment.profile.ProfileFragment;
import com.proton.carepatchtemp.fragment.report.ReportsFragment;

public class PageUtils {
    public static String getStateName(Object context) {
        if (context == null) return "";
        String name = context.getClass().getName();
        if (SplashActivity.class.getName().equalsIgnoreCase(name)) {
            return "启动页";
        }
        if (UserGuideActivity.class.getName().equalsIgnoreCase(name)) {
            return "引导页";
        }
        if (LoginFirstActivity.class.getName().equalsIgnoreCase(name)) {
            return "登录注册";
        }
        if (LoginActivity.class.getName().equalsIgnoreCase(name)) {
            return "登录";
        }
        if (RegistActivity.class.getName().equalsIgnoreCase(name)) {
            return "注册";
        }
        if (ForgetPwdActivity.class.getName().equalsIgnoreCase(name)) {
            return "找回密码";
        }
        if (HomeActivity.class.getName().equalsIgnoreCase(name)) {
            return "首页";
        }
        if (ScanQRCodeActivity.class.getName().equalsIgnoreCase(name)) {
            return "扫描二维码";
        }
        if (DeviceBaseCheckWifiConnectActivity.class.getName().equalsIgnoreCase(name)) {
            return "连接手机Wi-Fi提示";
        }
        if (DeviceBaseInputWifiPwdActivity.class.getName().equalsIgnoreCase(name)) {
            return "输入Wi-Fi密码";
        }
        if (DeviceBaseConnectingActivity.class.getName().equalsIgnoreCase(name)) {
            return "正在连接Wi-Fi";
        }
        if (MeasureContainerFragment.class.getName().equalsIgnoreCase(name)) {
            return "选择宝宝";
        }
        if (MeasureChooseProfileFragment.class.getName().equalsIgnoreCase(name)) {
            return "选择宝宝";
        }
        if (MeasureScanDeviceFragment.class.getName().equalsIgnoreCase(name)) {
            return "搜索设备";
        }
        if (BeforeMeasureFragment.class.getName().equalsIgnoreCase(name)) {
            return "测量准备";
        }
        if (MeasureCardsFragment.class.getName().equalsIgnoreCase(name)) {
            return "测量卡片";
        }
        if (MeasureFragment.class.getName().equalsIgnoreCase(name)) {
            return "测量卡片";
        }
        if (MeasureItemFragment.class.getName().equalsIgnoreCase(name)) {
            return "测量卡片";
        }
        if (AddNewDeviceActivity.class.getName().equalsIgnoreCase(name)) {
            return "新设备";
        }
        if (NurseSuggestBaseInfoActivity.class.getName().equalsIgnoreCase(name)) {
            return "护理建议-基本信息";
        }
        if (NurseSuggestDescribeActivity.class.getName().equalsIgnoreCase(name)) {
            return "护理建议";
        }
        if (DrugRecordActivity.class.getName().equalsIgnoreCase(name)) {
            return "用药记录列表";
        }
        if (AddReportNotesActivity.class.getName().equalsIgnoreCase(name)) {
            return "新增用药记录";
        }
        if (TempCurveActivity.class.getName().equalsIgnoreCase(name)) {
            return "温度曲线";
        }
        if (DeviceShareActivity.class.getName().equalsIgnoreCase(name)) {
            return "远程分享";
        }
        if (MeasureShareItemFragment.class.getName().equalsIgnoreCase(name)) {
            return "被分享卡片";
        }
        if (ReportsFragment.class.getName().equalsIgnoreCase(name)) {
            return "报告列表";
        }
        if (ReportFragment.class.getName().equalsIgnoreCase(name)) {
            return "报告列表";
        }
        if (ReportDetailActivity.class.getName().equalsIgnoreCase(name)) {
            return "报告详情";
        }
        if (HealthyTipsFragment.class.getName().equalsIgnoreCase(name)) {
            return "健康贴士";
        }
        if (ProfileFragment.class.getName().equalsIgnoreCase(name)) {
            return "档案管理列表";
        }
        if (AddProfileActivity.class.getName().equalsIgnoreCase(name)) {
            return "添加档案";
        }
        if (ProfileEditActivity.class.getName().equalsIgnoreCase(name)) {
            return "编辑档案";
        }
        if (ProfileNameEditActivity.class.getName().equalsIgnoreCase(name)) {
            return "编辑档案名称";
        }
        if (DeviceManageFragment.class.getName().equalsIgnoreCase(name)) {
            return "设备管理列表";
        }
        if (SettingFragment.class.getName().equalsIgnoreCase(name)) {
            return "设置";
        }
        if (DockerDetailActivity.class.getName().equalsIgnoreCase(name)) {
            return "充电器详情";
        }
        if (PatchDetailActivity.class.getName().equalsIgnoreCase(name)) {
            return "体温贴设备详情";
        }
        if (DeviceUpdateMsgActivity.class.getName().equalsIgnoreCase(name)) {
            return "固件升级";
        }
        if (FirewareUpdatingActivity.class.getName().equalsIgnoreCase(name)) {
            return "更新固件";
        }
        if (MsgCenterActivity.class.getName().equalsIgnoreCase(name)) {
            return "消息中心";
        }
        if (AboutProtonActivity.class.getName().equalsIgnoreCase(name)) {
            return "关于质子";
        }
        if (FeedBackActivity.class.getName().equalsIgnoreCase(name)) {
            return "意见反馈";
        }
        if (PermissionsActivity.class.getName().equalsIgnoreCase(name)) {
            return "权限检查";
        }
        if (PrePDFActivity.class.getName().equalsIgnoreCase(name)) {
            return "PDF预览";
        }
        if (ReportLandscapChartActivity.class.getName().equalsIgnoreCase(name)) {
            return "横屏报告";
        }
        if (SomOneMeasureReportActivity.class.getName().equalsIgnoreCase(name)) {
            return "报告列表";
        }
        if (NewProfileStep1Activity.class.getName().equalsIgnoreCase(name)) {
            return "注册新建档案第一步";
        }
        if (NewProfileStep2Activity.class.getName().equalsIgnoreCase(name)) {
            return "注册新建档案第二步";
        }
        if (NewProfileStep3Activity.class.getName().equalsIgnoreCase(name)) {
            return "注册新建档案第三步";
        }
        if (DeviceBasePatchPowerActivity.class.getName().equalsIgnoreCase(name)) {
            return "配网第一步";
        }
        if (DeviceBaseConnectFailActivity.class.getName().equalsIgnoreCase(name)) {
            return "配网失败";
        }
        if (AddReportNotesRemarkActivity.class.getName().equalsIgnoreCase(name)) {
            return "随手记备注";
        }
        if (LoginInternationalActivity.class.getName().equalsIgnoreCase(name)) {
            return "国际版登录";
        }
        return "";
    }

    public static String getWebStateName(String url) {
        if (TextUtils.isEmpty(url)) {
            return "跳转网页";
        }
        if (url.equals(HttpUrls.URL_POWER_LIGHT_NOT_SHINING)) {
            return "网页:指示灯未闪烁提示";
        }
        if (url.equals(HttpUrls.URL_NO_DEVICE_SEARCH)) {
            return "网页:使用帮助";
        }
        if (url.equals(HttpUrls.URL_ATTENTION)) {
            return "网页:注意事项";
        }
        if (url.equals(HttpUrls.URL_SCAN_HELP)) {
            return "网页:使用教程";
        }
        if (url.equals(HttpUrls.URL_PRIVICY)) {
            return "网页:隐私条款";
        }
        if (url.equals(HttpUrls.URL_PRIVICY)) {
            return "网页:用户协议";
        }
        return "跳转网页";
    }
}

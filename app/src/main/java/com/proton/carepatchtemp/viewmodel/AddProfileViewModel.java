package com.proton.carepatchtemp.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.user.LoginFirstActivity;
import com.proton.carepatchtemp.activity.user.NewProfileStep1Activity;
import com.proton.carepatchtemp.activity.user.NewProfileStep2Activity;
import com.proton.carepatchtemp.activity.user.NewProfileStep3Activity;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.ProfileCenter;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.DateUtils;
import com.proton.carepatchtemp.utils.net.OSSUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by luochune on 2018/4/28.
 */

public class AddProfileViewModel extends BaseViewModel implements OnDateSetListener {

    /**
     * 档案姓名
     */
    public ObservableField<String> name = new ObservableField<String>();
    /**
     * 档案年龄
     */
    public ObservableField<String> age = new ObservableField<>("");
    /**
     * 性别是否选择男宝宝
     */
    public ObservableBoolean isBoy = new ObservableBoolean(true);
    /**
     * 出生日期
     */
    public ObservableField<String> birthday = new ObservableField<>("");
    /**
     * 性别 1男  2女
     */
    public ObservableInt gender = new ObservableInt(1);
    /**
     * 添加报告第几步
     */
    public ObservableInt stepNum = new ObservableInt();
    /**
     * 远程头像地址
     */
    public ObservableField<String> ossAvatorUri = new ObservableField<>(AppConfigs.DEFAULT_AVATOR_URL);
    /**
     * 出生日期选择
     */
    private TimePickerDialog mDialogYearMonthDay;

    /**
     * 跳过，进入首页
     */
    public void goOverProfilFirstStep() {
        startActivity(HomeActivity.class);
        //关闭第一个登录页面
        ActivityManager.finishActivity(LoginFirstActivity.class);
    }

    /**
     * 选择出生日期
     */
    public void chooseBirthday() {
        if (mDialogYearMonthDay == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//时间格式化类
            long minMillSeconds = 0;
            try {
                Date date = sdf.parse("1900-1-1");//解析到一个时间
                minMillSeconds = date.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
                minMillSeconds = new Date().getTime();
            }
            mDialogYearMonthDay = new TimePickerDialog.Builder()
                    .setCallBack(this)
                    .setCancelStringId(getResString(R.string.string_cancel))
                    .setSureStringId(getResString(R.string.string_finish))
                    .setTitleStringId(getResString(R.string.string_choose_birthday_title))
                    .setYearText(getResString(R.string.string_year))
                    .setMonthText(getResString(R.string.string_month))
                    .setDayText(getResString(R.string.day))
                    .setHourText(getResString(R.string.hour))
                    .setMinuteText(getResString(R.string.minute))
                    .setMinMillseconds(minMillSeconds)
                    .setMaxMillseconds(System.currentTimeMillis())
                    .setType(Type.YEAR_MONTH_DAY)
                    .setThemeColor(getContext().getResources().getColor(R.color.color_gray_b3))
                    .setToolBarTextColor(getContext().getResources().getColor(R.color.color_main))
                    .build();
        }
        Activity activity = (Activity) getContext();
        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            mDialogYearMonthDay.show(fragmentActivity.getSupportFragmentManager(), "");
        }
    }

    /**
     * 下一步完成档案的添加
     */
    public void finishAddProfile() {
        HashMap<String, String> map = new HashMap<String, String>();
        if (TextUtils.isEmpty(ossAvatorUri.get())) {
            ossAvatorUri.set(AppConfigs.DEFAULT_AVATOR_URL);
        } else {
            ossAvatorUri.set(OSSUtils.getSaveUrl(ossAvatorUri.get()));
        }
        map.put("avatar", ossAvatorUri.get());
        //姓名
        if (TextUtils.isEmpty(name.get())) {
            BlackToast.show(R.string.string_name_profile_tip);
            return;
        } else {
            map.put("realname", name.get());
            map.put("title", name.get());
        }
        map.put("gender", gender.get() + "");// 1男  2女
        //生日
        if (TextUtils.isEmpty(birthday.get())) {
            BlackToast.show(R.string.string_choose_birthday);
            return;
        } else {
            map.put("birthday", birthday.get());
        }
        showDialog();
        requestAddProfile(map);
    }

    /**
     * 下次再添加
     */
    public void noMoreAddProfile() {
        //没有设备类型则选择设备
        startActivity(HomeActivity.class);
        ActivityManager.finishActivity(LoginFirstActivity.class);
        finishActivity();
    }

    public void addAnotherBaby() {
        startActivity(NewProfileStep1Activity.class);
        finishActivity();
    }

    /**
     * @param map
     */
    private void requestAddProfile(HashMap<String, String> map) {
        ProfileCenter.addProfile(new NetCallBack<ProfileBean>() {
            @Override
            public void noNet() {
                super.noNet();
                dismissDialog();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ProfileBean data) {
                dismissDialog();
                BlackToast.show(R.string.string_profile_add);
                ActivityManager.finishActivity(NewProfileStep1Activity.class);
                //档案添加完成页
                getContext().startActivity(new Intent(getContext(), NewProfileStep3Activity.class).putExtra("profile", data));
                finishActivity();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                dismissDialog();
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                }
            }
        }, map);
    }

    /**
     * 跳转至给档案添加头像
     */
    public void goToAddAvater() {
        if (TextUtils.isEmpty(name.get())) {
            BlackToast.show(R.string.string_input_name);
            return;
        }
        if (TextUtils.isEmpty(birthday.get())) {
            BlackToast.show(R.string.string_input_birthday);
            return;
        }
        int gender = 1;
        if (isBoy.get()) {
            gender = 1;
        } else {
            gender = 2;
        }
        Intent mIntent = new Intent(getContext(), NewProfileStep2Activity.class)
                .putExtra("realname", name.get())
                .putExtra("gender", gender)
                .putExtra("birthday", birthday.get());
        getContext().startActivity(mIntent);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        birthday.set(DateUtils.dateStrToYMD(millseconds));
    }
}

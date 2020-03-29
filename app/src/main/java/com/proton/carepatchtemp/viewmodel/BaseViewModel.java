package com.proton.carepatchtemp.viewmodel;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableField;
import android.support.annotation.StringRes;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.UIUtils;
import com.wms.logger.Logger;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.Type;

/**
 * Created by wangmengsi on 2017/12/14.
 */

public class BaseViewModel extends ViewModel {

    /**
     * 数据加载状态
     */
    public ObservableField<Status> status = new ObservableField<>(Status.Loading);
    protected SweetAlertDialog mDialog;

    public BaseViewModel() {
    }

    /**
     * 隐藏dialog
     */
    protected void dismissDialog() {
        try {
            if (mDialog != null) {
                mDialog.dismiss();
                mDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示dialog
     */
    protected void showDialog(final String msg, final boolean canceable) {
        try {
            initDialog();
            mDialog.setTitleText(msg).changeAlertType(Type.PROGRESS_TYPE).setCancelable(canceable);
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示dialog
     */
    protected void showDialog(@StringRes final int msg, final boolean canceable) {
        showDialog(UIUtils.getString(msg), canceable);
    }

    /**
     * 显示dialog
     */
    protected void showDialog() {
        showDialog(R.string.string_loading, true);
    }

    /**
     * 显示dialog
     */
    protected void showDialog(final String msg) {
        showDialog(msg, true);
    }

    /**
     * 显示dialog
     */
    protected void showDialog(@StringRes final int msg) {
        showDialog(UIUtils.getString(msg), true);
    }

    /**
     * 显示dialog
     */
    protected void showDialog(final String msg, final int type) {
        try {
            initDialog();
            mDialog.setTitleText(msg).changeAlertType(type);
            mDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示dialog
     */
    protected void showDialog(@StringRes final int msg, final int type) {
        showDialog(UIUtils.getString(msg), type);
    }

    private void initDialog() {
        if (mDialog == null) {
            mDialog = new SweetAlertDialog(getContext());
        }
        mDialog.setOnDismissListener(null);
    }

    protected SweetAlertDialog getDialog() {
        initDialog();
        return mDialog;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mDialog = null;
        Logger.w(getClass().getSimpleName() + " 销毁了");
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    protected void startActivity(Class<?> clz) {
        getContext().startActivity(new Intent(getContext(), clz));
    }

    /**
     * finish activity
     */
    public void finishActivity() {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).finish();
        }
    }

    /**
     * 获取资源字符串
     */
    public String getResString(int resId) {
        return App.get().getResources().getString(resId);
    }

    protected Context getContext() {
        return ActivityManager.currentActivity();
    }

    /**
     * 加载数据的状态
     */
    public enum Status {
        Loading, Success, Fail, Empty, NO_NET
    }

}

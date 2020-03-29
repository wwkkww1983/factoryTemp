package com.proton.carepatchtemp.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * 检查权限的工具类
 * <p/>
 * Created by wangchenlong on 16/1/26.
 */
public class PermissionsChecker {
    // 所需的全部权限
    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,//精确位置权限，电话、蓝牙、wifi都需要这个权限
    };
    private final Context mContext;

    public PermissionsChecker(Context context) {
        mContext = context.getApplicationContext();
    }

    // 判断是否缺少权限
    public static boolean lacksPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    // 判断是否缺少权限
    public boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    // 判断权限集合
    public boolean lacksPermissions() {
        for (String permission : PERMISSIONS) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }
}

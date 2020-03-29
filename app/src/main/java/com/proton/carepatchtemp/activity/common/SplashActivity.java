package com.proton.carepatchtemp.activity.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.component.AliyunService;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.net.center.MeasureReportCenter;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.PermissionsChecker;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.temp.connector.TempConnectorManager;
import com.taobao.sophix.SophixManager;
import com.wms.logger.Logger;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by wangmengsi on 2018/2/26.
 */

public class SplashActivity extends BaseActivity {

    private PermissionsChecker mPermissionsChecker;

    @Override
    protected int inflateContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    Logger.w("程序销毁重新开启,是否有测量设备:", TempConnectorManager.hasConnectDevice());
                    finish();
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        mPermissionsChecker = new PermissionsChecker(this);
        //获取阿里云token
        MeasureReportCenter.getAliyunToken();
        App.get().initRefresh();
        if (!BuildConfig.DEBUG) {
            SophixManager.getInstance().queryAndLoadNewPatch();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions()) {
            PermissionsActivity.startActivityForResult(this, 0);
        } else {
            //第一次安装应用打开启动页
            boolean isFirstInstall = SpUtils.getBoolean(AppConfigs.SP_KEY_SHOW_GUIDE, true);
            if (isFirstInstall) {
                startActivity(new Intent(this, UserGuideActivity.class));
                finish();
            } else {
                goToMain();
            }
        }
    }

    @SuppressLint("CheckResult")
    private void goToMain() {
        Observable
                .just(1)
                .delay(2, TimeUnit.SECONDS)
                .subscribe(integer -> {
                    if (!App.get().isLogined()) {
                        IntentUtils.goToLoginFirst(mContext);
                    } else {
                        IntentUtils.goToMain(mContext);
                        //开启服务
                        //开启阿里云服务
                        IntentUtils.startAliyunService(this);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
        if (requestCode == 0 && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }
}

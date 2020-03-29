package com.proton.carepatchtemp.activity.managecenter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivityAboutProtonBinding;
import com.proton.carepatchtemp.utils.BlackToast;
import com.wms.utils.CommonUtils;

/**
 * 关于质子
 */
public class AboutProtonActivity extends BaseActivity<ActivityAboutProtonBinding> {

    private final int readphone_permission_request_code = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case readphone_permission_request_code:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限请求允许
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + "400-826-5551"));
                    AboutProtonActivity.this.startActivity(intent);
                } else {
                    //权限拒绝
                    BlackToast.show(R.string.string_callphone_denied);
                }
                break;
        }
    }

    @Override
    protected void initView() {
        super.initView();
        //版本号
        StringBuffer versionStrBuffer = new StringBuffer();
        versionStrBuffer.append(CommonUtils.getAppVersion(this)).append("  Build  ").append(App.get().getVersionCode()).append("  ").append(BuildConfig.BUILD_TYPE);
        binding.idTvAppVersion.setText(getResources().getString(R.string.string_version_tip, versionStrBuffer.toString()));
        if (BuildConfig.IS_INTERNAL) {
            binding.idTvContactus.setText("Contact Us：contact@protontek.com");
        }
        //拨打电话
        binding.idTvContactus.setOnClickListener(view -> {
            if (!BuildConfig.IS_INTERNAL) {
                callPhone();
            } else {
                sendEmail();
            }
        });
    }

    private void sendEmail() {
        String[] receiver = new String[]{"contact@protontek.com"};
        String subject = "Feed back";
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        // 设置邮件发收人
        email.putExtra(Intent.EXTRA_EMAIL, receiver);
        // 设置邮件标题
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        // 调用系统的邮件系统
        startActivity(Intent.createChooser(email, "Please choose an email app"));
    }

    private void callPhone() {
        if (getPackageManager().checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_DENIED) {
            //权限拒绝，申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, readphone_permission_request_code);
        } else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + "400-826-5551"));
            AboutProtonActivity.this.startActivity(intent);
        }
    }

    @Override
    protected int inflateContentView() {
        return R.layout.activity_about_proton;
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_about_proton);
    }
}

package com.proton.carepatchtemp.utils;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.ui.UILifecycleListener;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by MoonlightSW on 2017/8/17.
 */

public class BuglyUtils {

    public static void init(Context context) {
        initUpdateUI();
        Bugly.init(context, context.getString(R.string.buglyAppid), BuildConfig.DEBUG);
        CrashReport.setUserId(App.get().getApiUid());
    }

    public static void initUpdateUI() {

        /**
         *  设置自定义升级对话框UI布局
         *  注意：因为要保持接口统一，需要用户在指定控件按照以下方式设置tag，否则会影响您的正常使用：
         *  标题：beta_title，如：android:tag="beta_title"
         *  升级信息：beta_upgrade_info  如： android:tag="beta_upgrade_info"
         *  更新属性：beta_upgrade_feature 如： android:tag="beta_upgrade_feature"
         *  取消按钮：beta_cancel_button 如：android:tag="beta_cancel_button"
         *  确定按钮：beta_confirm_button 如：android:tag="beta_confirm_button"
         *  详见layout/fragment_updatee.xml
         */
        Beta.upgradeDialogLayoutId = R.layout.fragment_update;
        Beta.strUpgradeDialogCancelBtn = "";
        Beta.autoDownloadOnWifi = true;
        Beta.autoCheckUpgrade = true;
        Beta.appChannel = "1";
        /**
         * 设置自定义tip弹窗UI布局
         * 注意：因为要保持接口统一，需要用户在指定控件按照以下方式设置tag，否则会影响您的正常使用：
         *  标题：beta_title，如：android:tag="beta_title"
         *  提示信息：beta_tip_message 如： android:tag="beta_tip_message"
         *  取消按钮：beta_cancel_button 如：android:tag="beta_cancel_button"
         *  确定按钮：beta_confirm_button 如：android:tag="beta_confirm_button"
         *  详见layout/tips_dialog.xml
         */
//        Beta.tipsDialogLayoutId = R.layout.tips_dialog;

        /**
         *  如果想监听升级对话框的生命周期事件，可以通过设置OnUILifecycleListener接口
         *  回调参数解释：
         *  context - 当前弹窗上下文对象
         *  view - 升级对话框的根布局视图，可通过这个对象查找指定view控件
         *  upgradeInfo - 升级信息
         */
        Beta.upgradeDialogLifecycleListener = new UILifecycleListener<UpgradeInfo>() {
            @Override
            public void onCreate(Context context, View view, UpgradeInfo upgradeInfo) {
                // 注：可通过这个回调方式获取布局的控件，如果设置了id，可通过findViewById方式获取，如果设置了tag，可以通过findViewWithTag，具体参考下面例子:

                // 通过id方式获取控件，并更改imageview图片
//                ImageView imageView = (ImageView) view.findViewWithTag(R.id.imageview);
//                imageView.setImageResource(R.mipmap.ic_launcher);

                // 通过tag方式获取控件，并更改布局内容
                TextView textView = view.findViewWithTag("beta_upgrade_feature");
                textView.setMovementMethod(ScrollingMovementMethod.getInstance());

                // 更多的操作：比如设置控件的点击事件
            }

            @Override
            public void onStart(Context context, View view, UpgradeInfo upgradeInfo) {

            }

            @Override
            public void onResume(Context context, View view, UpgradeInfo upgradeInfo) {

            }

            @Override
            public void onPause(Context context, View view, UpgradeInfo upgradeInfo) {

            }

            @Override
            public void onStop(Context context, View view, UpgradeInfo upgradeInfo) {

            }

            @Override
            public void onDestroy(Context context, View view, UpgradeInfo upgradeInfo) {

            }
        };
        Beta.checkUpgrade(false, true);
    }
}

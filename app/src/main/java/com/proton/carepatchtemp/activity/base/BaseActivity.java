package com.proton.carepatchtemp.activity.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.Density;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.LanguageUtils;
import com.proton.carepatchtemp.utils.PageUtils;
import com.proton.carepatchtemp.utils.StatusBarUtil;
import com.proton.carepatchtemp.utils.Utils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.wms.layout.LoadingLayout;
import com.wms.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.Type;

/**
 * Created by 王梦思 on 2017/09/27.
 * <p/>
 */

public abstract class BaseActivity<DB extends ViewDataBinding> extends AppCompatActivity {

    protected DB binding;
    protected SweetAlertDialog mDialog;
    protected Context mContext;
    protected LoadingLayout mLoadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isShowActivityFromBottom()) {
            overridePendingTransition(R.anim.dialog_in_bottom, R.anim.dialog_out_bottom);
        }
        super.onCreate(savedInstanceState);
        mContext = this;
        Density.setOrientation(this, getOrientation());

        Utils.setStatusBarTextColor(this, isDarkIcon());
        long startTime = System.currentTimeMillis();
        int layoutID = inflateContentView();
        if (layoutID != 0) {
            binding = DataBindingUtil.setContentView(this, layoutID);
        }
        Logger.w("耗时:" + (System.currentTimeMillis() - startTime) + "," + this.getClass().getSimpleName());
        initSwipeLayout();
        init();
        setStatusBar();
        initView();
        setListener();
        initEmptyView();
        initData();
        setTopTextView();
        if (isRegistEventBus()) {
            EventBusManager.getInstance().register(this);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
//        super.attachBaseContext(BuildConfig.IS_INTERNAL ? newBase : LanguageUtils.attachBaseContext(newBase, "zh_CN"));
    }

    protected void setTopTextView() {
        TextView titleText = findViewById(R.id.title);
        if (titleText != null && !TextUtils.isEmpty(getTopCenterText())) {
            titleText.setText(getTopCenterText());
        }
    }

    protected void setStatusBar() {
        StatusBarUtil.setStatusBarDrawable(this, R.drawable.drawable_status_bar);
        initToolbar();
    }

    protected void setStatusBarColor() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.white));
    }

    protected Density.Orientation getOrientation() {
        return Density.Orientation.WIDTH;
    }

    /**
     * 为activity设置布局
     *
     * @return layout布局
     */
    abstract protected int inflateContentView();

    /**
     * 设置监听器
     */
    protected void setListener() {

    }

    protected void init() {

    }

    /**
     * 初始化滑动返回
     */
    private void initSwipeLayout() {
    }

    /**
     * 是否从底部弹出
     */
    protected boolean isShowActivityFromBottom() {
        return false;
    }

    /**
     * 初始化下拉刷新
     */
    protected void initRefreshLayout(SmartRefreshLayout refreshLayout, OnRefreshListener onRefreshListener) {
        if (refreshLayout == null) return;
        initRefreshLayout(refreshLayout, onRefreshListener, null);
    }

    /**
     * 初始化下拉刷新
     */
    protected void initRefreshLayout(SmartRefreshLayout refreshLayout, OnRefreshListener onRefreshListener, OnLoadmoreListener onLoadMoreListener) {
        if (refreshLayout == null) return;
        refreshLayout.setEnableScrollContentWhenLoaded(true);
        if (onRefreshListener != null) {
            refreshLayout.setEnableRefresh(true);
            refreshLayout.setOnRefreshListener(onRefreshListener);
        }

        if (onLoadMoreListener != null) {
            refreshLayout.setOnLoadmoreListener(onLoadMoreListener);
            refreshLayout.setEnableLoadmore(true);
        } else {
            refreshLayout.setEnableLoadmore(false);
        }
    }

    private void initEmptyView() {
        if (getEmptyAndLoadingView() == null) return;

        mLoadingLayout = LoadingLayout.wrap(getEmptyAndLoadingView());
        if (generateEmptyLayout() != 0) {
            mLoadingLayout.setEmpty(generateEmptyLayout());
            if (!TextUtils.isEmpty(getEmptyText())) {
                mLoadingLayout.setEmptyText(getEmptyText());
            }
            mLoadingLayout.setOnEmptyInflateListener(inflated -> {
                initEmptyInflateListener(inflated);
            });
        }

        if (generateLoadingLayout() != 0) {
            mLoadingLayout.setLoading(generateLoadingLayout());
        }

        if (App.get().isLogined()) {
            if (generateTimeoutLayout() != 0) {
                mLoadingLayout.setError(generateTimeoutLayout());
            } else {
                mLoadingLayout.setErrorImage(R.drawable.no_net_bitmap);
            }
        } else {
            mLoadingLayout.setError(generateNotLoginLayout());
            initNotLoginInflateListener();
        }

        mLoadingLayout.setRetryListener(v -> {
            mLoadingLayout.showLoading();
            mLoadingLayout.postDelayed(this::initData, 300);
        });
    }

    /**
     * 未登录布局监听器
     */
    private void initNotLoginInflateListener() {
        if (mLoadingLayout == null) return;
        mLoadingLayout.setOnErrorInflateListener(inflated -> {
            TextView goToLogin = inflated.findViewById(R.id.id_go_to_login);
            if (goToLogin != null) {
                goToLogin.setText(Html.fromHtml("赶快<font color='#30b8ff'><U>去登录</U></font>吧"));
                goToLogin.setOnClickListener(v -> IntentUtils.goToLogin(mContext));
            }
            TextView notLoginTips = inflated.findViewById(R.id.id_not_login_tips);
            if (getNotLoginTips() != 0) {
                notLoginTips.setText(getNotLoginTips());
            }
        });
    }

    /**
     * 未登录提示
     */
    protected @StringRes
    int getNotLoginTips() {
        return 0;
    }

    /**
     * empty view监听器
     */
    protected void initEmptyInflateListener(View emptyView) {
        TextView retryBtn = emptyView.findViewById(R.id.retry_button);
        if (!TextUtils.isEmpty(getRetryText())) {
            retryBtn.setText(getRetryText());
        }
        if (retryBtn != null) {
            retryBtn.setOnClickListener(v -> {
                doRetry();
            });
        }
    }

    /**
     * 是否可以重试
     */
    protected boolean isCanRetry() {
        return true;
    }

    protected void doRetry() {
        initData();
    }

    /**
     * 获取加载中，加载失败，空白页的显示的view
     */
    protected View getEmptyAndLoadingView() {
        return null;
    }

    /**
     * 设置加载失败
     */
    protected void setLoadError() {
        if (mLoadingLayout == null) return;
        mLoadingLayout.showError();
    }

    /**
     * 设置加载成功
     */
    protected void setLoadSuccess() {
        if (mLoadingLayout == null) return;
        mLoadingLayout.showContent();
    }

    /**
     * 设置网络异常
     */
    protected void setLoadTimeOut() {
        if (mLoadingLayout == null) return;
        mLoadingLayout.showError();
    }

    /**
     * 设置加载数据为空
     */
    protected void setLoadEmpty() {
        if (mLoadingLayout == null) return;
        if (generateEmptyLayout() == 0) {
            mLoadingLayout.showContent();
        } else {
            mLoadingLayout.showEmpty();
        }
    }

    /**
     * 设置正在加载数据
     */
    protected void showLoading() {
        if (mLoadingLayout == null) return;
        mLoadingLayout.showLoading();
    }

    /**
     * 设置加载中页面布局
     */
    protected @LayoutRes
    int generateLoadingLayout() {
        return R.layout.layout_loading;
    }

    /**
     * 设置重试页面布局
     */
    protected @LayoutRes
    int generateTimeoutLayout() {
        return R.layout.layout_timeout;
    }

    /**
     * 设置空页面布局
     */
    protected @LayoutRes
    int generateEmptyLayout() {
        return R.layout.layout_empty;
    }

    /**
     * 设置未登录布局
     */
    protected @LayoutRes
    int generateNotLoginLayout() {
        return R.layout.layout_not_login;
    }

    /**
     * 数据为空的时候显示的文字
     */
    protected String getEmptyText() {
        return "";
    }

    /**
     * 重试按钮文字
     */
    protected String getRetryText() {
        return "";
    }

    /**
     * 网络请求
     */
    protected void initData() {
        if (getEmptyAndLoadingView() != null && mLoadingLayout != null) {
            showLoading();
        }
    }

    protected void jumpToActivity(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    protected boolean swipeBackEnable() {
        return true;
    }

    /**
     * 标题
     */
    public String getTopCenterText() {
        return "";
    }

    protected void initDialog() {
        mDialog = new SweetAlertDialog(this, Type.PROGRESS_TYPE);
        mDialog.setTitleText(getString(R.string.string_loading));
        mDialog.setCancelable(true);
        mDialog.setOnDismissListener(dialog -> onDialogDismiss());
    }

    protected void onDialogDismiss() {

    }

    protected void initView() {

    }

    /**
     * 隐藏dialog
     */
    public void dismissDialog() {
        runOnUiThread(() -> {
            if (mDialog == null) {
                initDialog();
            }
            mDialog.dismiss();
        });
    }

    /**
     * 显示dialog
     */
    public void showDialog() {
        if (mDialog == null) {
            initDialog();
        }
        showDialog(R.string.string_loading, true);
    }

    /**
     * 显示dialog
     */
    public void showDialog(final int msg, final boolean canceable) {
        showDialog(getString(msg), canceable);
    }

    /**
     * 显示dialog
     */
    public void showDialog(String msg) {
        if (mDialog == null) {
            initDialog();
        }
        showDialog(msg, true);
    }

    /**
     * 显示dialog
     */
    public void showDialog(boolean cancelable) {
        if (mDialog == null) {
            initDialog();
        }
        showDialog("", cancelable);
    }

    /**
     * 显示dialog
     */
    public void showDialog(final String msg, final boolean canceable) {
        runOnUiThread(() -> {
            if (mDialog == null) {
                initDialog();
            }
            if (!mDialog.isShowing()) {
                mDialog.changeAlertType(Type.PROGRESS_TYPE)
                        .setTitleText(msg);
                mDialog.show();
            }
        });
    }

    /**
     * 显示dialog
     */
    public void showDialog(final String msg, final int type) {
        runOnUiThread(() -> {
            if (mDialog == null) {
                initDialog();
            }
            if (!mDialog.isShowing()) {
                mDialog.changeAlertType(type)
                        .setTitleText(msg);
                mDialog.show();
            }
        });
    }

    protected void initToolbar() {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setStatusBarColor();
        if (null != findViewById(R.id.toolbar)) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("");
            if (getBackIcon() != -1) {
                toolbar.setNavigationIcon(getBackIcon());
            }
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(showBackBtn());
            getSupportActionBar().setHomeButtonEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    protected int getBackIcon() {
        return R.drawable.btn_back_img;
    }

    protected boolean showBackBtn() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onActionBackClick();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 是否注册eventbus
     */
    protected boolean isRegistEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.w("onResume:", getClass().getSimpleName());
        if (!openStat()) return;
        String statName = getStatName();
        MobclickAgent.onPageStart(statName);
        MobclickAgent.onResume(this);
        StatService.onPageStart(this, statName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.w("onPause:", getClass().getSimpleName());
        if (!openStat()) return;
        String statName = getStatName();
        MobclickAgent.onPageEnd(statName);
        MobclickAgent.onPause(this);
        StatService.onPageEnd(this, statName);
    }

    protected String getStatName() {
        return PageUtils.getStateName(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegistEventBus()) {
            EventBusManager.getInstance().unregister(this);
        }
        Logger.w("onDestroy:" + this.getClass().getSimpleName());
    }

    /**
     * 是否打开统计
     */
    protected boolean openStat() {
        return true;
    }

    /**
     * 设置 app 不随着系统字体的调整而变化
     */
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.fontScale = 1;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }

    protected boolean isDarkIcon() {
        return false;
    }

    protected void onActionBackClick() {
        finish();
    }

    /**
     * 根据字符串资源id返回字符串
     */
    protected String getResString(int id) {
        return this.getResources().getString(id);
    }

    /**
     * 获取color资源对应的颜色int值
     */
    protected int getResColor(int id) {
        return ContextCompat.getColor(this, id);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logger.w("内存不足:", getClass().getSimpleName());
    }

    //判断Activity是否Destroy
    protected boolean isDestroy(Activity activity) {
        return activity == null || activity.isFinishing() ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed());
    }


}

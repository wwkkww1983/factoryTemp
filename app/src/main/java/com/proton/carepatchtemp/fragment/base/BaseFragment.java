package com.proton.carepatchtemp.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.PageUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.wms.layout.LoadingLayout;
import com.wms.logger.Logger;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.pedant.SweetAlert.SweetMessageDialog;
import cn.pedant.SweetAlert.Type;

/**
 * Created by 王梦思 on 2015/12/18 0018.
 * <p/>
 * Fragment的基类
 */
public abstract class BaseFragment<DB extends ViewDataBinding> extends Fragment {
    protected Context mContext;
    protected View mInflatedView;
    protected DB binding;
    protected SweetMessageDialog mDialog;
    /**
     * 是否正在获取数据
     */
    protected boolean isGettingData;
    protected LoadingLayout mLoadingLayout;
    protected boolean isVisible = false;//当前Fragment是否可见
    private boolean isInitView = false;//是否与View建立起映射关系
    private boolean isFirstLoad = true;//是否是第一次加载数据

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            long startTime = System.currentTimeMillis();
            binding = DataBindingUtil.inflate(getLayoutInflater(), inflateContentView(), container, false);
            if (binding == null) {
                getActivity().finish();
                return null;
            }
            mInflatedView = binding.getRoot();
            TextView titleText = mInflatedView.findViewById(R.id.title);
            if (titleText != null && !TextUtils.isEmpty(getTopCenterText())) {
                titleText.setText(getTopCenterText());
            }
            fragmentInit();
            initView();
            initEmptyView();
            isInitView = true;
            if (isLazyLoad()) {
                lazyLoadData();
            } else {
                initData();
            }
            if (isRegistEventBus()) {
                EventBusManager.getInstance().register(this);
            }
            Logger.w("耗时:" + (System.currentTimeMillis() - startTime) + "," + this.getClass().getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mInflatedView;
    }

    /**
     * 是否懒加载
     */
    protected boolean isLazyLoad() {
        return false;
    }

    protected SweetMessageDialog getDialog() {

        Activity activity = ActivityManager.currentActivity();

        boolean added = isAdded();

        Logger.w("getActivity():" , getActivity(),"," , this.getClass().getSimpleName());

        if (mDialog == null) {
            mDialog = new SweetMessageDialog(getActivity());
            mDialog.changeAlertType(Type.PROGRESS_TYPE).setTitleText(getString(R.string.string_loading));
            mDialog.setCancelable(false);
            mDialog.setOnCancelListener(dialog -> onDialogCancel());
            mDialog.setOnDismissListener(dialog -> onDialogDismiss());
        }
        return mDialog;
    }

    protected void onDialogDismiss() {
    }

    protected void onDialogCancel() {
    }

    protected void initView() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * 网络请求
     */
    protected void initData() {
        isGettingData = true;
        if (getEmptyAndLoadingView() != null && mLoadingLayout != null) {
            showLoading();
        }
    }

    /**
     * 为fragment设置布局
     *
     * @return layout布局
     */
    abstract protected int inflateContentView();

    /**
     * 初始化fragment
     */
    abstract protected void fragmentInit();

    private void initEmptyView() {
        if (getEmptyAndLoadingView() == null) return;

        mLoadingLayout = LoadingLayout.wrap(getEmptyAndLoadingView());
        if (generateEmptyLayout() != 0) {
            mLoadingLayout.setEmpty(generateEmptyLayout());
            if (!TextUtils.isEmpty(getEmptyText())) {
                mLoadingLayout.setEmptyText(getEmptyText());
            }
            initEmptyInflateListener();
        }

        if (generateLoadingLayout() != 0) {
            mLoadingLayout.setLoading(generateLoadingLayout());
        }

        if (App.get().isLogined() || !isNeedLoginLoad()) {
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
     * 是否需要登录才能加载
     */
    protected boolean isNeedLoginLoad() {
        return true;
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
    protected void initEmptyInflateListener() {
        if (mLoadingLayout == null) return;
        mLoadingLayout.setOnEmptyInflateListener(inflated -> {
            TextView retryBtn = inflated.findViewById(R.id.retry_button);
            if (retryBtn != null) {
                retryBtn.setOnClickListener(v -> {
                    mLoadingLayout.showLoading();
                    mLoadingLayout.postDelayed(this::initData, 300);
                });
            }
        });
    }

    /**
     * 重试
     */
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
     * 数据为空的时候显示的文字
     */
    protected String getEmptyText() {
        return "";
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
        isGettingData = true;
        if (mLoadingLayout == null) return;
        if (generateLoadingLayout() == 0) {
            mLoadingLayout.showContent();
        } else {
            mLoadingLayout.showLoading();
        }
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

    public String getTopCenterText() {
        return "";
    }

    /**
     * 隐藏dialog
     */
    public void dismissDialog() {
        runOnUiThread(() -> getDialog().dismiss());
    }

    /**
     * 显示dialog
     */
    public void showDialog() {
        showDialog(R.string.string_loading, true);
    }

    /**
     * 显示dialog
     */
    public void showDialog(boolean cancelable) {
        showDialog("", cancelable);
    }

    public void showDialog(int title) {
        showDialog(UIUtils.getString(title), true);
    }

    /**
     * 显示dialog
     */
    public void showDialog(final String msg, final boolean canceable) {
        runOnUiThread(() -> {
            getDialog().setCancelable(canceable);
            if (!getDialog().isShowing()) {
                getDialog().changeAlertType(Type.PROGRESS_TYPE)
                        .setTitleText(msg);
                getDialog().show();
            }
        });
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
    public void showDialog(final String msg, final int type) {
        runOnUiThread(() -> {
            if (!getDialog().isShowing()) {
                getDialog().changeAlertType(type)
                        .setTitleText(msg);
                getDialog().show();
            }
        });
    }

    /**
     * 返回
     */
    protected void finish() {
        getActivity().onBackPressed();
    }

    /**
     * 在主线程运行
     *
     * @param runnable runnable
     */
    protected void runOnUiThread(Runnable runnable) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(runnable);
        }
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
        refreshLayout.setEnableAutoLoadmore(true);//开启自动加载功能（非必须）
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
            lazyLoadData();
        } else {
            isVisible = false;
        }
    }

    @Override
    public void onDestroy() {
        if (isRegistEventBus()) {
            EventBusManager.getInstance().unregister(this);
        }
        Logger.w("onDestroy:", this.getClass().getSimpleName());
        super.onDestroy();
    }

    private void lazyLoadData() {
        if (isFirstLoad) {
            Logger.w("第一次加载 " + " isInitView  " + isInitView + "  isVisible  " + isVisible + "   " + this.getClass().getSimpleName());
        } else {
            Logger.w("不是第一次加载" + " isInitView  " + isInitView + "  isVisible  " + isVisible + "   " + this.getClass().getSimpleName());
        }
        if (!isFirstLoad || !isVisible || !isInitView) {
            Logger.w("不加载" + "   " + this.getClass().getSimpleName());
            return;
        }
        Logger.w("完成数据第一次加载");
        initData();
        isFirstLoad = false;
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
    public void onResume() {
        super.onResume();
        Logger.w("onResume:", getClass().getSimpleName());
        if (!openStat()) return;
//        if (BuildConfig.DEBUG) return;
        String statName = getStatName();
        MobclickAgent.onPageStart(statName); //统计页面，"MainScreen"为页面名称，可自定义
        StatService.onPageStart(getActivity(), statName);
    }

    @Override
    public void onPause() {
        super.onPause();
        Logger.w("onPause:", getClass().getSimpleName());
        if (!openStat()) return;
//        if (BuildConfig.DEBUG) return;
        String statName = getStatName();
        MobclickAgent.onPageEnd(statName);
        StatService.onPageEnd(getActivity(), statName);
    }

    protected String getStatName() {
        return PageUtils.getStateName(this);
    }

    /**
     * 是否打开统计
     */
    protected boolean openStat() {
        return true;
    }

    public String getResString(int resId) {
        return App.get().getResources().getString(resId);
    }
}

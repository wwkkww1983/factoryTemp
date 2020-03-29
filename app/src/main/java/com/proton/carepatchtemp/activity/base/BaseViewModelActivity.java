package com.proton.carepatchtemp.activity.base;

import android.databinding.Observable;
import android.databinding.ViewDataBinding;

import com.proton.carepatchtemp.viewmodel.BaseViewModel;
import com.wms.logger.Logger;

/**
 * Created by wangmengsi on 2018/1/12.
 */

public abstract class BaseViewModelActivity<DB extends ViewDataBinding, VM extends BaseViewModel> extends BaseActivity<DB> {
    protected VM viewmodel;
    private Observable.OnPropertyChangedCallback mStatusCallback = new Observable.OnPropertyChangedCallback() {
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            //数据加载的状态监听
            Logger.w("status = " + viewmodel.status.get());
            switch (viewmodel.status.get()) {
                case Loading:
                    showLoading();
                    break;
                case Success:
                    setLoadSuccess();
                    break;
                case Fail:
                    setLoadError();
                    break;
                case NO_NET:
                    setLoadTimeOut();
                    break;
                case Empty:
                    setLoadEmpty();
                    break;
            }
        }
    };

    @Override
    protected void init() {
        super.init();
        viewmodel = getViewModel();

        addStatusListener();
    }

    /**
     * 添加数据加载状态监听
     */
    protected void addStatusListener() {
        if (viewmodel != null) {
            viewmodel.status.addOnPropertyChangedCallback(mStatusCallback);
        }
    }

    @Override
    protected void showLoading() {
        super.showLoading();
        //状态必须统一，不然出问题
        viewmodel.status.set(BaseViewModel.Status.Loading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewmodel != null) {
            viewmodel.status.removeOnPropertyChangedCallback(mStatusCallback);
        }
    }

    protected abstract VM getViewModel();
}

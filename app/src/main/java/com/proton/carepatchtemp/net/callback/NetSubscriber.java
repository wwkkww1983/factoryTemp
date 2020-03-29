package com.proton.carepatchtemp.net.callback;

import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.utils.Constants;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.wms.logger.Logger;
import com.wms.utils.NetUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.adapter.rxjava2.HttpException;

public abstract class NetSubscriber<T> implements Observer<T> {
    private NetCallBack mCallBack;

    public NetSubscriber(NetCallBack<T> callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (!NetUtils.isConnected(App.get())) {
            if (mCallBack != null) {
                mCallBack.noNet();
            }
            d.dispose();
            return;
        }
        if (mCallBack != null) {
            mCallBack.onSubscribe();
        }
    }

    @Override
    public void onError(Throwable e) {
        Logger.w(e.toString());

        ResultPair resultPair = new ResultPair();
        resultPair.setRet(Constants.FAIL);

        if (e instanceof NoRouteToHostException) {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        } else if (e instanceof SocketTimeoutException) {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        } else if (e instanceof ConnectException) {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        } else if (e instanceof HttpException) {
            resultPair.setData(UIUtils.getString(R.string.string_server_error));
        } else if (e instanceof IOException) {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        } else if (e instanceof ParseResultException) {
            String failData = JSONUtils.getString(e.getMessage(), "data");
            if (!TextUtils.isEmpty(failData)) {
                resultPair.setData(failData);
            } else {
                resultPair.setData(e.getMessage());
            }
        } else {
            resultPair.setData(UIUtils.getString(R.string.string_network_error));
        }

        if (mCallBack != null) {
            if (!TextUtils.isEmpty(resultPair.getData())) {
                mCallBack.onFailed(resultPair);
            }
        }
    }

    @Override
    public void onComplete() {
        if (mCallBack != null) {
            mCallBack.onComplete();
        }
    }
}

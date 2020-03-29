package com.proton.carepatchtemp.net.callback;

/**
 * Created by MoonlightSW on 2017/4/10.
 */

public abstract class NetCallBack<T> extends BaseNetCallBack {

    /**
     * UI弹出Dialog等
     */
    public void onSubscribe() {

    }

    public abstract void onSucceed(T data);

    public void onFailed(ResultPair resultPair) {

    }

    public void noNet() {

    }

    public void onComplete() {

    }

    public void onTimeOut(){}
}

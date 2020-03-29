package com.proton.carepatchtemp.net.center;

import com.proton.carepatchtemp.factory.bean.CalibrateRequest;
import com.proton.carepatchtemp.net.RetrofitHelper;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.NetSubscriber;
import com.proton.carepatchtemp.net.callback.ResultPair;


public class CalibrateCenter extends DataCenter {
    public static void calibrateCheck(CalibrateRequest  request, NetCallBack<String> callBack) {
        RetrofitHelper.getCalibrateApi().calibrateCheck("application/json", "E01AZP8D9XNLD0", request)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.getRet().equalsIgnoreCase("SUCCESS")) {
                        return resultPair.getData();
                    } else {
                        return resultPair.getData();
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String data) {
                        callBack.onSucceed(data);
                    }
                });
    }

}

package com.proton.carepatchtemp.net.center;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.Constants;
import com.proton.carepatchtemp.utils.UIUtils;
import com.wms.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DataCenter {

    protected static final String RET_F = Constants.FAIL;

    public static boolean isSuccess(String ret) {
        return Constants.SUCCESS.equalsIgnoreCase(ret);
    }

    public static <T> ObservableTransformer<T, T> threadTrans() {
        return upstream ->
                upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public static boolean noNet() {
        boolean noNet = !NetUtils.isConnected(App.get());
        if (noNet) {
            BlackToast.show(R.string.string_no_net);
        }
        return noNet;
    }

    public static ResultPair parseResult(String data) {

        if (data.contains("LOGIN")) {
            if (!data.contains("1")) {
                //token与uid匹配错误，需要重新登录
                ResultPair resultPair = new ResultPair();
                resultPair.setRet(Constants.FAIL);
                resultPair.setData("");
                App.get().kickOff();
                return resultPair;
            } else {
                //未登录
                ResultPair resultPair = new ResultPair();
                resultPair.setRet(Constants.SUCCESS);
                resultPair.setData("");
                return resultPair;
            }
        }

        JSONObject response;
        try {
            response = new JSONObject(data);
        } catch (JSONException e) {
            ResultPair resultPair = new ResultPair();
            resultPair.setRet(Constants.FAIL);
            resultPair.setData(UIUtils.getString(R.string.string_parse_data_failed));
            return resultPair;
        }

        ResultPair resultPair = new ResultPair();
        resultPair.setRet(Constants.FAIL);
        try {
            resultPair.setRet(response.getString("ret"));
        } catch (JSONException e) {
            resultPair.setRet(Constants.FAIL);
            resultPair.setData(UIUtils.getString(R.string.string_parse_data_failed));
        }

        try {
            resultPair.setData(response.getString("data"));
        } catch (JSONException e) {
            resultPair.setRet(Constants.FAIL);
            resultPair.setData(UIUtils.getString(R.string.string_parse_data_failed));
        }
        return resultPair;
    }
}

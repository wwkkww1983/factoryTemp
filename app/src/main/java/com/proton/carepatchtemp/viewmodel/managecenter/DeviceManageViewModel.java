package com.proton.carepatchtemp.viewmodel.managecenter;

import com.google.gson.reflect.TypeToken;
import com.proton.carepatchtemp.net.bean.DeviceBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;
import com.wms.logger.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.proton.carepatchtemp.net.center.DataCenter.parseResult;

/**
 * Created by luochune on 2018/3/12.
 */

public class DeviceManageViewModel extends BaseViewModel {


    /*  RetrofitHelper.getHealthTips(params)
              .map(ArticleCenter::parseArticle)
              .compose(threadTrans())
          .subscribe(new NetSubscriber<List<ArticleBean>>(callBack) {
      @Override
      public void onNext(List<ArticleBean> value) {
          callBack.onSucceed(value);
      }
  });*/
    //  private LiveData<List<DeviceBean>> deviceManageListLv;
    public DeviceManageViewModel() {
        //deviceManageListLv=new Abs
    }

    public static void getDeviceList(boolean isNeedRefresh, NetCallBack<List<DeviceBean>> callBack) {
     /*   RetrofitHelper.getManagerCenterApi().getDeviceManageList().map(DeviceManageViewModel::parseDevicelist).compose(threadTrans())
                .subscribe(new NetSubscriber<List<DeviceBean>>(callBack) {
                    @Override
                    public void onNext(List<DeviceBean> articleBeans) {
                        callBack.onSucceed(articleBeans);
                    }
                });*/
    }

    private static List<DeviceBean> parseDevicelist(String json) throws Exception {
        Logger.json(json);
        ResultPair resultPair = parseResult(json);
        if (resultPair.isSuccess()) {
            Type type = new TypeToken<ArrayList<DeviceBean>>() {
            }.getType();
            return JSONUtils.getObj(resultPair.getData(), type);
        } else {
            throw new ParseResultException(resultPair.getData());
        }
    }

 /*   public LiveData<List<DeviceBean>> getDeviceManageListLv() {
        return deviceManageListLv;
    }*/
}

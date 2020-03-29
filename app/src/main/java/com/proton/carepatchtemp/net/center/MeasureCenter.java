package com.proton.carepatchtemp.net.center;

import com.google.gson.reflect.TypeToken;
import com.proton.carepatchtemp.bean.DeviceOnlineBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.KafkaRetrofitHelper;
import com.proton.carepatchtemp.net.RetrofitHelper;
import com.proton.carepatchtemp.net.bean.ShareHistoryBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.NetSubscriber;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.wms.logger.Logger;

import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Created by luochune on 2018/4/26.
 * 测量请求
 */

public class MeasureCenter extends DataCenter {
    /**
     * 检查贴是否在用
     */
    public static void checkPatchIsMeasuring(String patchMac, NetCallBack<DeviceOnlineBean> netCallBack) {
      /*  HashMap<String, String> map = new HashMap<>();
        map.put("patchMac", patchMac);
        RetrofitHelper.getMeasureCenterApi().checkPatchIsMeasuring(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return JSONUtils.getObj(resultPair.getData(), DeviceOnlineBean.class);
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<DeviceOnlineBean>(netCallBack) {
            @Override
            public void onNext(DeviceOnlineBean mqttOnlineBean) {
                netCallBack.onSucceed(mqttOnlineBean);
            }
        });*/

        HashMap<String, String> map = new HashMap<>();
        map.put("patchMac", patchMac);
        RetrofitHelper.getMeasureCenterApi().checkPatchIsMeasuring(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return JSONUtils.getObj(resultPair.getData(), DeviceOnlineBean.class);
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new Observer<DeviceOnlineBean>() {
            @Override
            public void onSubscribe(Disposable d) {
                Logger.w("onSubscribe");
            }

            @Override
            public void onNext(DeviceOnlineBean deviceOnlineBean) {
                Logger.w("onNext");
                netCallBack.onSucceed(deviceOnlineBean);
            }

            @Override
            public void onError(Throwable e) {
                Logger.w("onError");
                if (e instanceof SocketTimeoutException) {
                    netCallBack.onTimeOut();
                }
            }

            @Override
            public void onComplete() {
                Logger.w("onComplete");
            }
        });


    }

    /**
     * 检查mqtt是否在线
     */
    public static void checkMqttIsOnline(String clientId, NetCallBack<Boolean> netCallBack) {
        HashMap<String, String> map = new HashMap<>();
        if (clientId.contains(":")) {
            clientId = clientId.replace(":", "");
        }
        map.put("clientId", clientId);
        KafkaRetrofitHelper.getMeasureCenterApi().checkMqttisLine(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                boolean isOnline = "1".equals(JSONUtils.getString(resultPair.getData(), "online"));
                if (isOnline) {
                    return true;
                } else {
                    throw new ParseResultException(json);
                }
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<Boolean>(netCallBack) {
            @Override
            public void onNext(Boolean mqttOnlineBean) {
                netCallBack.onSucceed(mqttOnlineBean);
            }
        });
    }

    /**
     * 分享设备
     */
    public static void shareDevice(String deviceId, String mobile, String profileId, NetCallBack<ResultPair> netCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceid", deviceId);
        map.put("mobile", mobile);
        map.put("profileid", profileId);
        RetrofitHelper.getMeasureCenterApi().shareDevice(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(netCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                netCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 获取一个设备的共享历史
     */
    public static void getShareHistory(String profileid, NetCallBack<List<ShareHistoryBean>> netCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("profileid", profileid);
        RetrofitHelper.getMeasureCenterApi().getShareHistory(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                Type type = new TypeToken<List<ShareHistoryBean>>() {
                }.getType();
                return JSONUtils.<ShareHistoryBean>getObj(resultPair.getData(), type);
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<List<ShareHistoryBean>>(netCallBack) {
            @Override
            public void onNext(List<ShareHistoryBean> shareHistoryBean) {
                netCallBack.onSucceed(shareHistoryBean);
            }
        });
    }

    /**
     * 取消向当前这个手机号用户的分享
     */
    public static void cancelShare(String mobile, String profileId, NetCallBack<ResultPair> netCallBack) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("profileid", profileId);
        RetrofitHelper.getMeasureCenterApi().cancelShareHistory(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(netCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                netCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 取消向当前这个手机号用户的分享
     */
    public static void getShareWechatUrl(long profileId, NetCallBack<String> netCallBack) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("profileId", profileId);
        RetrofitHelper.getMeasureCenterApi().getShareWechatUrl(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return JSONUtils.getString(resultPair.getData(), "url");
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<String>(netCallBack) {
            @Override
            public void onNext(String resultPair) {
                netCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 获取算法配置
     *
     * @param callBack
     */
    public static void getAlgorithmConfig(NetCallBack<String> callBack) {
        RetrofitHelper.getMeasureCenterApi().getAlgorithmConfig()
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        long duration = Long.parseLong(JSONUtils.getString(resultPair.getData(), "duration"));
                        float percentage = Float.parseFloat(JSONUtils.getString(resultPair.getData(), "percentage"));
                        App.get().setDuration(duration);
                        App.get().setPercentage(percentage);
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String s) {
                        if (callBack!=null) {
                            callBack.onSucceed(s);
                        }
                    }
                });
    }

    /**
     * 获取算法配置
     *
     * @param callBack
     */
    public static void getDockerAlgorithmVersion(String dockerMac, NetCallBack<String> callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("dockerMac", dockerMac);
        RetrofitHelper.getMeasureCenterApi().getDockerAlgorithmVersion(params)
                .map(s -> {
                    ResultPair resultPair = parseResult(s);
                    if (resultPair.isSuccess()) {
                        return resultPair.getData();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                })
                .compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String s) {
                        callBack.onSucceed(s);
                    }
                });
    }
}

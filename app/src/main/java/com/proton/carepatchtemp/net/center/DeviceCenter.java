package com.proton.carepatchtemp.net.center;

import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.LastUseBean;
import com.proton.carepatchtemp.bean.ScanDeviceInfoBean;
import com.proton.carepatchtemp.bean.ShareBean;
import com.proton.carepatchtemp.net.RetrofitHelper;
import com.proton.carepatchtemp.net.bean.DeviceBean;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.UpdateFirmwareBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.NetSubscriber;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.FileUtils;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.temp.connector.bean.DeviceType;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import org.litepal.LitePal;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.proton.carepatchtemp.net.center.DataCenter.parseResult;
import static com.proton.carepatchtemp.net.center.DataCenter.threadTrans;

/**
 * Created by luochune on 2018/3/29.
 */

public class DeviceCenter {
    /**
     * 获取设备列表
     */
    public static void getDeviceList(NetCallBack<List<DeviceBean>> callBack) {
        RetrofitHelper.getManagerCenterApi().getDeviceList().map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                Type type = new TypeToken<ArrayList<DeviceBean>>() {
                }.getType();
                List<DeviceBean> patchDevices = JSONUtils.getObj(JSONUtils.getString(resultPair.getData(), "devices"), type);
                List<DeviceBean> dockerDevices = JSONUtils.getObj(JSONUtils.getString(resultPair.getData(), "deviceBases"), type);
                List<DeviceBean> allDevices = new ArrayList<>();

                if (!CommonUtils.listIsEmpty(dockerDevices)) {
                    for (DeviceBean device : dockerDevices) {
                        device.setDocker(true);
                    }
                }

                allDevices.addAll(dockerDevices);
                allDevices.addAll(patchDevices);

                LitePal.deleteAll(DeviceBean.class);
                LitePal.saveAll(allDevices);
                return allDevices;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<List<DeviceBean>>(callBack) {
            @Override
            public void onNext(List<DeviceBean> articleBeans) {
                callBack.onSucceed(articleBeans);
            }
        });
    }

    /**
     * 添加设备
     *
     * @param deviceType P02,P03,P04,P05
     */
    public static void addDevice(String deviceType, String sn, String macaddres, String hardVersion, NetCallBack<String> callBack) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("patchType", DeviceType.valueOf(deviceType).getValue());
        params.put("type", "1");
        params.put("name", UIUtils.getString(R.string.string_device_name) + Utils.getShowMac(macaddres));
        params.put("btaddress", macaddres);
        if (!TextUtils.isEmpty(hardVersion)) {
            params.put("version", hardVersion);
        }
        if (!TextUtils.isEmpty(sn)) {
            params.put("sn", sn);
        }
        RetrofitHelper.getManagerCenterApi().addDevice(params)
                .map(json -> {
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        return JSONUtils.getString(resultPair.getData(), "id");
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }).compose(threadTrans())
                .subscribe(new NetSubscriber<String>(callBack) {
                    @Override
                    public void onNext(String result) {
                        callBack.onSucceed(result);
                    }
                });
    }

    /**
     * 获取固件升级包
     */
    public static void getUpdatePackage(NetCallBack<List<UpdateFirmwareBean>> netCallBack) {
        HashMap<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("all", 1);
        RetrofitHelper.getManagerCenterApi().getUpdatePacckage(paramsMap).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                Type type = new TypeToken<ArrayList<UpdateFirmwareBean>>() {
                }.getType();
                List<UpdateFirmwareBean> allPackage = JSONUtils.getObj(resultPair.getData(), type);
                if (!CommonUtils.listIsEmpty(allPackage)) {
                    for (UpdateFirmwareBean packageBean : allPackage) {
                        //下载固件更新包
                        //packageBean.setVersion("V3.0.0");
                        String path = FileUtils.getFirewareDir(DeviceType.valueOf(packageBean.getDeviceType()));
                        if (!new File(path, packageBean.getVersion()).exists()) {
                            Logger.w("固件不存在");
                            FileUtils.downloadFile(packageBean.getUrl(), path, packageBean.getVersion());
                        } else {
                            Logger.w("固件存在");
                        }
                    }
                    LitePal.deleteAll(UpdateFirmwareBean.class);
                    LitePal.saveAll(allPackage);
                }
                return allPackage;
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<List<UpdateFirmwareBean>>(netCallBack) {
            @Override
            public void onNext(List<UpdateFirmwareBean> updateFirmwareBean) {
                netCallBack.onSucceed(updateFirmwareBean);
            }
        });
    }

    /**
     * 绑定用户设备类型
     *
     * @param type P02, P03, P04, P05
     */
    public static void changeDeviceType(String type, NetCallBack<Boolean> resultPairNetCallBack) {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("type", type);
        RetrofitHelper.getManagerCenterApi().boundUserDevice(paramsMap).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return true;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<Boolean>(resultPairNetCallBack) {
            @Override
            public void onNext(Boolean resultPair) {
                resultPairNetCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 绑定充电器
     */
    public static void addDocker(String macaddress, String serial, String hardVersion, String ssid, String bssid, NetCallBack<Boolean> callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("baseaddress", macaddress);
        params.put("basesn", serial);
        params.put("baseversion", hardVersion);
        params.put("ssid", ssid);
        params.put("bssid", bssid);
        params.put("wifiname", ssid);
        RetrofitHelper.getManagerCenterApi().addDocker(params)
                .map(json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        return resultPair.isSuccess();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }).compose(threadTrans()).subscribe(new NetSubscriber<Boolean>(callBack) {
            @Override
            public void onNext(Boolean resultPair) {
                callBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 获取分享人列表
     */
    public static void getSharedList(NetCallBack<List<ShareBean>> callBack) {
        RetrofitHelper.getManagerCenterApi().getSharedList()
                .map(json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        Type type = new TypeToken<ArrayList<ShareBean>>() {
                        }.getType();
                        return JSONUtils.<ShareBean>getObj(resultPair.getData(), type);
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }).compose(threadTrans()).subscribe(new NetSubscriber<List<ShareBean>>(callBack) {
            @Override
            public void onNext(List<ShareBean> resultPair) {
                callBack.onSucceed(resultPair);
            }
        });
    }

    public static void editShareProfile(String profileId, String deviceId, boolean needPush, NetCallBack<Boolean> callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("profileid", profileId);
        params.put("deviceid", deviceId);
        params.put("push", String.valueOf(needPush));
        RetrofitHelper.getManagerCenterApi().editShareProfile(params)
                .map(json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.BIND_DEVICE_SUCCESS));
                        return resultPair.isSuccess();
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }).compose(threadTrans()).subscribe(new NetSubscriber<Boolean>(callBack) {
            @Override
            public void onNext(Boolean resultPair) {
                callBack.onSucceed(resultPair);
            }
        });
    }

    public static void deleteDevice(int deviceId, NetCallBack<ResultPair> netCallBack) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("deviceid", deviceId);
        RetrofitHelper.getManagerCenterApi().deleteDevice(params).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(netCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                netCallBack.onSucceed(resultPair);
            }
        });
    }

    public static void getLastUseDevice(long profileId, NetCallBack<LastUseBean> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", profileId);
        RetrofitHelper.getManagerCenterApi().getLastUseDevice(params).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return JSONUtils.getObj(resultPair.getData(), LastUseBean.class);
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<LastUseBean>(callback) {
            @Override
            public void onNext(LastUseBean deviceDetailBean) {
                callback.onSucceed(deviceDetailBean);
            }
        });
    }

    /**
     * 获取扫描设备信息
     */
    public static void getScanDeviceInfo(String macaddress, NetCallBack<ScanDeviceInfoBean> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("mac", macaddress);
        RetrofitHelper.getManagerCenterApi().getScanDeviceInfo(params).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return JSONUtils.getObj(resultPair.getData(), ScanDeviceInfoBean.class);
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ScanDeviceInfoBean>(callback) {
            @Override
            public void onNext(ScanDeviceInfoBean deviceDetailBean) {
                callback.onSucceed(deviceDetailBean);
            }
        });
    }

    public static void unbind(long profileId, NetCallBack<Boolean> callback) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("profileid", profileId);
        RetrofitHelper.getManagerCenterApi().unbind(params).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return true;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<Boolean>(callback) {
            @Override
            public void onNext(Boolean isSuccess) {
                callback.onSucceed(isSuccess);
            }
        });
    }
}

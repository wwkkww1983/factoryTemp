package com.proton.carepatchtemp.net.api;

import com.proton.carepatchtemp.BuildConfig;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by luochune on 2018/3/13.
 */

public interface ManagerCenterApi {
    /**
     * 设备列表
     */
    String addDevice = "openapi/device/add";
    /**
     * 意见反馈
     */
    String feedBack = "openapi/feedback/add";
    /**
     * 获取消息列表
     */
    String systemUrlStr = "openapi/message/getlist";
    /**
     * 删除某条消息
     */
    String deleteMsgUrlStr = "openapi/message/delete";
    /**
     * 删除多条消息
     */
    String deleteNumMsgsUrlStr = "openapi/messages/delete";
    /**
     * 根据设备类型获取设备列表
     */
    String getDeviceList = "openapi/device/all";
    String getUpdatePackage = "openapi/update/patch";
    /**
     * 绑定设备类型
     */
    String boundDeviceType = "openapi/device/bindrecentlyused";
    /**
     * 添加充电器
     */
    String addDocker = "openapi/device/addbase";
    /**
     * 获取共享列表
     */
    String getSharedList = "openapi/profile/getsharedpersons";
    String editShareProfile = "openapi/share/profile/change";
    /**
     * 删除设备
     */
    String deleteDevice = "openapi/device/delete";
    /**
     * 获取上一次使用的设备
     */
    String getLastUseDevice = "openapi/profile/recent";
    String getScanDeviceInfo = BuildConfig.IS_INTERNAL ? "openapi/deviceinfo/oversea" : "openapi/deviceinfo/get";

    @FormUrlEncoded
    @POST(addDevice)
    Observable<String> addDevice(@FieldMap Map<String, Object> params);

    @POST(feedBack)
    Observable<String> feedBack(@QueryMap Map<String, String> map);

    @GET(systemUrlStr)
    Observable<String> getMsgList(@QueryMap Map<String, String> params);

    @POST(deleteMsgUrlStr)
    Observable<String> deleteMsg(@QueryMap HashMap<String, Object> params);

    @POST(deleteNumMsgsUrlStr)
    Observable<String> deleteMsgNums(@QueryMap HashMap<String, String> params);

    @GET(getDeviceList)
    Observable<String> getDeviceList();

    @GET(getUpdatePackage)
    Observable<String> getUpdatePacckage(@QueryMap HashMap<String, Object> paramsMap);

    @POST(boundDeviceType)
    Observable<String> boundUserDevice(@QueryMap HashMap<String, String> paramsMap);

    @POST(addDocker)
    Observable<String> addDocker(@QueryMap HashMap<String, String> params);

    @GET(getSharedList)
    Observable<String> getSharedList();

    @POST(editShareProfile)
    Observable<String> editShareProfile(@QueryMap HashMap<String, String> params);

    @POST(deleteDevice)
    Observable<String> deleteDevice(@QueryMap HashMap<String, Object> params);

    @GET(getLastUseDevice)
    Observable<String> getLastUseDevice(@QueryMap HashMap<String, Object> params);

    @GET(getScanDeviceInfo)
    Observable<String> getScanDeviceInfo(@QueryMap HashMap<String, Object> params);

    @POST("openapi/share/profile/unbind")
    Observable<String> unbind(@QueryMap HashMap<String, Object> params);
}

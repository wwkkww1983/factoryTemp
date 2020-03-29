package com.proton.carepatchtemp.net.api;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by luochune on 2018/4/26.
 * 测量中心api
 */

public interface MeasureCenterApi {
    String mqttIsOnline = "mqtt/clients";
    String checkPatchIsMeasuring = "openapi/device/docker/get";
    /**
     * 设备分享
     */
    String shareDevice = "openapi/profile/sharenew";
    /**
     * 取消分享
     */
    String cancleShare = "openapi/profile/cancelsharenew";
    /**
     * 分享历史
     */
    String getShareHistory = "openapi/share/history/mobiles";

    String getAlgorithmConfig = "openapi/config/algorithm";

    String getDockerAlgorithmVersion = "openapi/docker/version/get";

    @GET(mqttIsOnline)
    Observable<String> checkMqttisLine(@QueryMap HashMap<String, String> map);

    @GET(checkPatchIsMeasuring)
    Observable<String> checkPatchIsMeasuring(@QueryMap HashMap<String, String> map);

    @POST(shareDevice)
    Observable<String> shareDevice(@QueryMap HashMap<String, String> map);

    @GET(getShareHistory)
    Observable<String> getShareHistory(@QueryMap HashMap<String, String> map);

    @POST(cancleShare)
    Observable<String> cancelShareHistory(@QueryMap HashMap<String, Object> map);

    @GET("openapi/share/basic/url")
    Observable<String> getShareWechatUrl(@QueryMap HashMap<String, Object> map);

    /**
     * 获取算法配置
     *
     * @return
     */
    @GET(getAlgorithmConfig)
    Observable<String> getAlgorithmConfig();

    /**
     * 获取底座算法版本号
     *
     * @return
     */
    @GET(getDockerAlgorithmVersion)
    Observable<String> getDockerAlgorithmVersion(@QueryMap Map<String, String> params);


}

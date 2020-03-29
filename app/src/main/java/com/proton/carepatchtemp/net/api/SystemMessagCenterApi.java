package com.proton.carepatchtemp.net.api;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by luochune on 2018/3/14.
 * 消息中心api
 */

public interface SystemMessagCenterApi {
    String systemUrlStr = "openapi/message/getlist";

    @GET(systemUrlStr)
    Observable<String> getMsgList(@QueryMap Map<String, String> params);
}

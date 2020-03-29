package com.proton.carepatchtemp.net.api;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by 王梦思 on 2017/4/19.
 */

public interface ArticleApi {
    String healthTips = "openapi/healthtip/getHealthtipTitle";

    @POST(healthTips)
    Observable<String> getHealthTips(@QueryMap Map<String, String> params);

}

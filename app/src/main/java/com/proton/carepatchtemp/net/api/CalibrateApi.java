package com.proton.carepatchtemp.net.api;

import com.proton.carepatchtemp.factory.bean.CalibrateRequest;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * 校准
 */
public interface CalibrateApi {
    /**
     * 体温校准上传相关数据
     */
    String calibrateCheck = "openapi/deviceinfo/batch/check";

    @POST(calibrateCheck)
    Observable<String> calibrateCheck(@Header("Content-Type") String contentType, @Header("passport") String passPort, @Body CalibrateRequest calibrateRequest);
}

package com.proton.carepatchtemp.net.api;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by luochune on 2018/3/16.
 */

public interface ProfileApi {
    String getProfileList = "openapi/tempprofile/getlists";
    String addProfile = "openapi/tempprofile/add";
    String deleteProfile = "openapi/profile/delete";
    String editProfile = "openapi/profile/edit";

    @GET(getProfileList)
    Observable<String> getProfileFileList();

    @POST(addProfile)
    Observable<String> addProfile(@QueryMap Map<String, String> map);

    @POST(deleteProfile)
    Observable<String> deleteProfile(@QueryMap Map<String, Object> map);

    @POST(editProfile)
    Observable<String> editProfile(@QueryMap Map<String, String> map);
}

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
 * Created by luochune on 2018/3/15.
 */

public interface ReportResultApi {
    String reportListUrl = "openapi/report/getnewTemplists";
    String deleteReport = "openapi/report/delete";
    String collect = "openapi/report/collect";
    String cancelcollect = "openapi/report/cancelcollect";
    String delete = "openapi/report/delete";//单个报告删除(目前使用的是多个报告接口，这个暂时不用)
    String editReport = "openapi/reporttemp/edit";  //   上传报告
    String reprotNote = "openapi/report/getcommentlist"; //获取报告随手记
    String addComments = "openapi/report/addcomments";//  添加多条随手记
    String aliyunToken = "openapi/user/getSTSAuthorization";//获取阿里云授权token
    String addReport = "openapi/tempreport/add";//获取阿里云授权token
    String oneBabyReportList = "openapi/profile/getreportlist";//获取某个档案下面的报告列表
    String oneBabyCollectReportList = "openapi/profile/getCollectReportlist";//获取某个档案下面的收藏报告列表
    String deleteNoteComment = "openapi/report/deletecomment";//删除一条帖子记录

    @GET(reportListUrl)
    Observable<String> getReportList(@QueryMap Map<String, String> params);

    @POST(deleteReport)
    Observable<String> deleteReports(@QueryMap Map<String, String> params);

    @POST(collect)
    Observable<String> collectReport(@QueryMap Map<String, String> collcetReportMap);

    @POST(cancelcollect)
    Observable<String> cancelCollectReport(@QueryMap Map<String, String> cancelCollcetReportMap);

    @GET(reprotNote)
    Observable<String> getReportNote(@QueryMap Map<String, String> reportNoteMap);

    @POST(addComments)
    Observable<String> addNotes(@QueryMap HashMap<String, String> map);

    @POST(aliyunToken)
    Observable<String> getAliyunToken();

    @FormUrlEncoded
    @POST(addReport)
    Observable<String> addReport(@FieldMap HashMap<String, String> map);

    @FormUrlEncoded
    @POST(editReport)
    Observable<String> editReport(@FieldMap HashMap<String, String> map);

    @GET(oneBabyReportList)
    Observable<String> getOneBabyReportList(@QueryMap HashMap<String, Object> paramMap);

    @GET(oneBabyCollectReportList)
    Observable<String> getOneBabyCollectReportList(@QueryMap HashMap<String, Object> paramMap);

    @POST(deleteNoteComment)
    Observable<String> deleteNoteComments(@QueryMap HashMap<String, Object> noteMap);
}

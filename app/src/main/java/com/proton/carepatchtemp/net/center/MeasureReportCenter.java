package com.proton.carepatchtemp.net.center;

import com.google.gson.reflect.TypeToken;
import com.proton.carepatchtemp.bean.AliyunToken;
import com.proton.carepatchtemp.bean.ReportBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.RetrofitHelper;
import com.proton.carepatchtemp.net.bean.NoteBean;
import com.proton.carepatchtemp.net.bean.ReportListItemBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.NetSubscriber;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.FormatUtils;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.AliLogUtils;
import com.proton.carepatchtemp.utils.net.OSSUtils;
import com.wms.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by luochune on 2018/3/23.
 */

public class MeasureReportCenter extends DataCenter {
    /**
     * 获取测量报告列表
     */
    public static void getReportList(HashMap<String, String> map, NetCallBack<List<ReportListItemBean>> netCallBack) {
        RetrofitHelper.getReportResultApi().getReportList(map).map(
                json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        Type type = new TypeToken<List<ReportListItemBean>>() {
                        }.getType();
                        return JSONUtils.<ReportListItemBean>getObj(resultPair.getData(), type);
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }
        ).compose(threadTrans()).subscribe(new NetSubscriber<List<ReportListItemBean>>(netCallBack) {
            @Override
            public void onNext(List<ReportListItemBean> reportList) {
                netCallBack.onSucceed(reportList);
            }
        });
    }

    /**
     * @param allReportCurrPageIndex 当前页
     * @param endtime                本地最新报告时间
     * @param type                   1,体温 2，心电 3，医生端 4，微信
     * @param collectlist            收藏collectlist==1 取全部:0
     * @param pagesize               //每页数目
     * @param netCallBack            网络请求回调
     */
    public static void getOneBabyReportList(long profileid, int allReportCurrPageIndex, int endtime, int type, int collectlist, int pagesize, NetCallBack<List<ReportListItemBean>> netCallBack) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("page", allReportCurrPageIndex + "");//页码
        paramMap.put("endtime", "0");//本地最新报告时间
        paramMap.put("type", "1");//1,体温 2，心电 3，医生端 4，微信
        paramMap.put("collectlist", "0");//收藏collectlist==1 取全部:0
        paramMap.put("pagesize", pagesize + "");//每页数目
        paramMap.put("profileid", profileid);
        RetrofitHelper.getReportResultApi().getOneBabyReportList(paramMap).map(
                json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        Type mType = new TypeToken<List<ReportListItemBean>>() {
                        }.getType();
                        return JSONUtils.<ReportListItemBean>getObj(resultPair.getData(), mType);
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }
        ).compose(threadTrans()).subscribe(new NetSubscriber<List<ReportListItemBean>>(netCallBack) {
            @Override
            public void onNext(List<ReportListItemBean> reportList) {
                netCallBack.onSucceed(reportList);
            }
        });
    }

    /**
     * 获取单个宝宝下面的收藏报告列表
     *
     * @param profileId 档案id
     * @param page      页码
     * @param pagesize  每页显示数据条数
     */
    public static void getOneBabyCollcetReportList(long profileId, int page, int endtime, int type, int collectlist, int pagesize, NetCallBack<List<ReportListItemBean>> reportNetCallBack) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("page", page);//页码
        paramMap.put("endtime", endtime);//本地最新报告时间
        paramMap.put("type", type);//1,体温 2，心电 3，医生端 4，微信
        paramMap.put("collectlist", collectlist);//收藏collectlist==1 取全部:0
        paramMap.put("pagesize", pagesize);//每页数目
        paramMap.put("profileid", profileId);
        RetrofitHelper.getReportResultApi().getOneBabyCollectReportList(paramMap).map(
                json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        Type mType = new TypeToken<List<ReportListItemBean>>() {
                        }.getType();
                        return JSONUtils.<ReportListItemBean>getObj(resultPair.getData(), mType);
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }
        ).compose(threadTrans()).subscribe(new NetSubscriber<List<ReportListItemBean>>(reportNetCallBack) {
            @Override
            public void onNext(List<ReportListItemBean> reportList) {
                reportNetCallBack.onSucceed(reportList);
            }
        });
    }

    /**
     * 删除多个测量报告
     */
    public static void deleteMeasureReport(String reportIds, NetCallBack<ResultPair> resultPairNetCallBack) {
        HashMap<String, String> reportMap = new HashMap<>();
        reportMap.put("reportids", reportIds);
        RetrofitHelper.getReportResultApi().deleteReports(reportMap).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(resultPairNetCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                resultPairNetCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 收藏某个报告
     */
    public static void collectReport(HashMap<String, String> collcetReportMap, NetCallBack<ResultPair> resultPairNetCallBack) {
        RetrofitHelper.getReportResultApi().collectReport(collcetReportMap).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(resultPairNetCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                resultPairNetCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 取消收藏列表
     */
    public static void cancelReport(HashMap<String, String> collcetReportMap, NetCallBack<ResultPair> resultPairNetCallBack) {
        RetrofitHelper.getReportResultApi().cancelCollectReport(collcetReportMap).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(resultPairNetCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                resultPairNetCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 获取随手记录列表
     */
    public static void getCommentList(String reportid, NetCallBack<List<NoteBean>> noteListNetCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("reportid", reportid);
        RetrofitHelper.getReportResultApi().getReportNote(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                Type listType = new TypeToken<ArrayList<NoteBean>>() {
                }.getType();
                return JSONUtils.<NoteBean>getObj(resultPair.getData(), listType);
            } else {
                throw new ParseResultException(resultPair.getData());
            }

        }).compose(threadTrans()).subscribe(new NetSubscriber<List<NoteBean>>(noteListNetCallBack) {

            @Override
            public void onNext(List<NoteBean> noteBeans) {
                noteListNetCallBack.onSucceed(noteBeans);
            }
        });
    }

    /**
     * 添加测量报告备注数据
     *
     * @param reportId 报告id
     * @param noteStr  添加json内容
     */
    public static void addRemark(String reportId, String noteStr, NetCallBack<ResultPair> resultPairNetCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("reportid", reportId);
        map.put("jsonComments", noteStr);
        RetrofitHelper.getReportResultApi().addNotes(map).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(resultPairNetCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                resultPairNetCallBack.onSucceed(resultPair);
            }
        });
    }

    /**
     * 添加报告
     */
    public static void addReport(String deviceId, String profileId, long startTime, NetCallBack<String> callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("deviceid", deviceId);
        params.put("profileid", profileId);
        params.put("starttime", FormatUtils.getTime(startTime, "yyyy-MM-dd HH:mm:ss"));
        params.put("type", "1");
        RetrofitHelper.getReportResultApi().addReport(params)
                .map(json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        return JSONUtils.getString(resultPair.getData(), "id");
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }).compose(threadTrans()).subscribe(new NetSubscriber<String>(callBack) {
            @Override
            public void onNext(String result) {
                callBack.onSucceed(result);
            }
        });
    }

    /**
     * 编辑报告
     */
    public static void editReport(ReportBean report, NetCallBack<String> callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("deviceid", report.getDeviceId());
        params.put("profileid", report.getProfileId());
        params.put("starttime", FormatUtils.getTime(report.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
        params.put("endtime", FormatUtils.getTime(report.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
        params.put("filepath", OSSUtils.getSaveUrl(report.getFilePath()));
        params.put("type", "1");
        params.put("reportid", report.getReportId());
        params.put("config", "{}");
        params.put("stage", String.valueOf(report.getType()));
        try {
            JSONObject data = new JSONObject();
            data.put("time", (report.getEndTime() - report.getStartTime()) / 1000);
            data.put("heart_rate_avg", report.getMaxAlarmTemp());
            data.put("temp_max", report.getMaxTemp());
            data.put("conception_rate", report.getMinAlarmTemp());
            data.put("temp_bbt", "0");
            params.put("data", data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitHelper.getReportResultApi().editReport(params)
                .map(json -> {
                    Logger.json(json);
                    ResultPair resultPair = parseResult(json);
                    if (resultPair.isSuccess()) {
                        return json;
                    } else {
                        throw new ParseResultException(resultPair.getData());
                    }
                }).compose(threadTrans()).subscribe(new NetSubscriber<String>(callBack) {
            @Override
            public void onNext(String result) {
                callBack.onSucceed(result);
            }
        });
    }

    /**
     * 获取oss上传的阿里云token
     */
    public static void getAliyunToken(NetCallBack<AliyunToken> aliyunCallBack) {
        //用户未登录时不获取阿里云token
        if (!App.get().isLogined()) {
            return;
        }
        RetrofitHelper.getReportResultApi().getAliyunToken().map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                Logger.w("阿里云token获取成功");
                AliyunToken token = JSONUtils.getObj(resultPair.getData(), AliyunToken.class);
                App.get().aliyunToken = token;
                return token;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<AliyunToken>(aliyunCallBack) {
            @Override
            public void onNext(AliyunToken aliyunToken) {
                if (aliyunCallBack != null) {
                    aliyunCallBack.onSucceed(aliyunToken);
                }
            }
        });
    }

    public static void getAliyunToken() {
        getAliyunToken(null);
    }

    /**
     * 删除一条帖子
     */
    public static void deleteNote(long commentId, NetCallBack<ResultPair> resultPairNetCallBack) {
        HashMap<String, Object> noteMap = new HashMap<>();
        noteMap.put("commentid", commentId);
        RetrofitHelper.getReportResultApi().deleteNoteComments(noteMap).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair != null && resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(json);
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(resultPairNetCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                resultPairNetCallBack.onSucceed(resultPair);
            }
        });
    }

    public static void deleteReport(String reportId, NetCallBack<Boolean> callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("reportids", reportId);
        RetrofitHelper.getReportResultApi().deleteReports(params)
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
}

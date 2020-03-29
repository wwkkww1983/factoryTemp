package com.proton.carepatchtemp.utils;

import android.text.TextUtils;

import com.aliyun.sls.android.sdk.ClientConfiguration;
import com.aliyun.sls.android.sdk.LOGClient;
import com.aliyun.sls.android.sdk.LogException;
import com.aliyun.sls.android.sdk.SLSLog;
import com.aliyun.sls.android.sdk.core.auth.StsTokenCredentialProvider;
import com.aliyun.sls.android.sdk.core.callback.CompletedCallback;
import com.aliyun.sls.android.sdk.model.Log;
import com.aliyun.sls.android.sdk.model.LogGroup;
import com.aliyun.sls.android.sdk.request.PostLogRequest;
import com.aliyun.sls.android.sdk.result.PostLogResult;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.bean.LogBean;
import com.wms.logger.Logger;
import com.wms.utils.NetUtils;

import org.litepal.LitePal;

/**
 * @Description: 日志服务工具
 * @Author: yxf
 * @CreateDate: 2020/1/15 17:25
 * @UpdateUser: yxf
 * @UpdateDate: 2020/1/15 17:25
 */
public class AliLogUtils {


    /**
     * 填入必要的参数
     */
    public static String endpoint = "cn-shanghai.log.aliyuncs.com";
    public static String project = "app-log-analysis";
    public static String logStore = "app-log";
    public String source_ip = "";
    public boolean isAsyncGetIp = false;
    //client的生命周期和app保持一致
    public static LOGClient logClient;


    public static void init() {
        setupSLSClient();
        SLSLog.enableLog();
    }

    private static void setupSLSClient() {
        //        移动端是不安全环境，不建议直接使用阿里云主账号ak，sk的方式。建议使用STS方式。具体参见
//        https://help.aliyun.com/document_detail/62681.html
//        注意：SDK 提供的 PlainTextAKSKCredentialProvider 只建议在测试环境或者用户可以保证阿里云主账号AK，SK安全的前提下使用。
//		  具体使用如下

//        主账户使用方式

       /* String AK = "********";
        String SK = "********";
        PlainTextAKSKCredentialProvider credentialProvider =
                new PlainTextAKSKCredentialProvider(AK, SK);*/
//        STS使用方式
        String STS_AK = App.get().aliyunToken.getAccessKeyId();
        String STS_SK = App.get().aliyunToken.getAccessKeySecret();
        String STS_TOKEN = App.get().aliyunToken.getSecurityToken();
        StsTokenCredentialProvider credentialProvider =
                new StsTokenCredentialProvider(STS_AK, STS_SK, STS_TOKEN);
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        conf.setCachable(false);
        conf.setConnectType(ClientConfiguration.NetworkPolicy.WWAN_OR_WIFI);
        SLSLog.enableLog(); // log打印在控制台

        logClient = new LOGClient(App.get(), endpoint, credentialProvider, conf);
    }

    /**
     * 上传app日志
     * 推荐使用的方式，直接调用异步接口，通过callback 获取回调信息
     *
     * @param data  json数据
     * @param topic mqtt bluetooth warn network
     */
    public static void asyncUploadLog(String topic, String data, UploadCallBack callBack) {
        /* 创建logGroup */
        LogGroup logGroup = new LogGroup("sls test", null);
        /* 存入一条log */
        Log log = new Log();
        log.PutContent("topic", topic);
        log.PutContent("data", data);
        log.PutContent("uid", App.get().getApiUid());
        log.PutContent("time", DateUtils.dateStrToYMDHMS(System.currentTimeMillis()));
        log.PutContent("app", "carePatch-release");
        log.PutContent("app-version", App.get().getVersion());

        logGroup.PutLog(log);

        try {
            PostLogRequest request = new PostLogRequest(project, logStore, logGroup);
            logClient.asyncPostLog(request, new CompletedCallback<PostLogRequest, PostLogResult>() {
                @Override
                public void onSuccess(PostLogRequest request, PostLogResult result) {
                    Logger.w("上传日志成功");
                    if (callBack != null) {
                        callBack.onSuccess();
                    }
                }

                @Override
                public void onFailure(PostLogRequest request, LogException exception) {
                    Logger.w("上传日志失败：", exception.getErrorMessage());
                    if (callBack != null) {
                        callBack.onFail(exception.getErrorMessage());
                    }
                    //缓存到本地数据库
                    LogBean logBean = JSONUtils.getObj(data, LogBean.class);
                    try {
                        LogBean firstLogBean = LitePal.where("uid= ? and createTime= ?", App.get().getApiUid(), logBean.getCreateTime()).findFirst(LogBean.class);
                        if (firstLogBean == null) {
                            boolean save = logBean.save();
                            if (save) {
                                if (logBean != null) {
                                    Logger.w("日志缓存数据库成功,缓存的日志详情：", logBean.toString());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (LogException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取上传日志的json
     *
     * @param logType          日志类型
     * @param connectStatus
     * @param connectType
     * @param dockerMacAddress 底座mac
     * @return
     */
    public static String getLogJson(String logType, int connectStatus, int connectType, String dockerMacAddress, String warnType, boolean timeOut) {
        LogBean logBean = new LogBean();
        logBean.setUid(App.get().getApiUid());
        logBean.setLogType(logType);
        logBean.setCheckPatchTimeOut(timeOut);
        String connectStatusStr;
        //0 未连接 1连接中 2已连接 3手动断开连接
        switch (connectStatus) {
            case 0:
                connectStatusStr = "异常断开连接";
                break;
            case 1:
                connectStatusStr = "连接中";
                break;
            case 2:
                connectStatusStr = "已连接";
                break;
            case 3:
                connectStatusStr = "手动断开连接";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + connectStatus);
        }
        logBean.setConnectStatus(connectStatusStr);
        logBean.setConnectType(connectType);
        //如果是mqtt连接则保存dockerMacAddress
        if (connectType == 2 && !TextUtils.isEmpty(dockerMacAddress)) {
            logBean.setTopic(com.proton.temp.connector.utils.Utils.getTopicByMacAddress(dockerMacAddress));
        }
        logBean.setNetworkAvailable(NetUtils.isConnected(App.get()));
        logBean.setWarn(MediaManager.getInstance().isPlaying());
        if (!TextUtils.isEmpty(warnType)) {
            logBean.setWarnType(warnType);
        } else {
            logBean.setWarnType("无");
        }
        logBean.setCreateTime(DateUtils.dateStrToYMDHMS(System.currentTimeMillis()));
        return JSONUtils.toJson(logBean);
    }

    public interface UploadCallBack {
        void onSuccess();

        void onFail(String msg);
    }
}

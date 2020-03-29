package com.proton.carepatchtemp.component;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.proton.carepatchtemp.bean.AliyunToken;
import com.proton.carepatchtemp.bean.ReportBean;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.DeviceCenter;
import com.proton.carepatchtemp.net.center.MeasureReportCenter;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.utils.net.OSSUtils;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import org.litepal.LitePal;

import java.util.List;

import cn.trinea.android.common.util.FileUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangmengsi on 2018/2/26.
 * 离线报告上传服务
 */
public class OfflineReportUploadService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.w("开启离线报告上传服务");
        uploadOfflineReport();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 上传离线报告
     */
    @SuppressLint("CheckResult")
    private void uploadOfflineReport() {
        //先获取一次阿里云token
        MeasureReportCenter.getAliyunToken(new NetCallBack<AliyunToken>() {
            @Override
            public void onSucceed(AliyunToken data) {
                Logger.w("离线报告获取阿里云token成功");
                List<ReportBean> reportBeans = LitePal.where("userId = ?", App.get().getApiUid()).find(ReportBean.class);
                Logger.w("离线报告size:" + reportBeans.size());
                if (CommonUtils.listIsEmpty(reportBeans)) {
                    stopSelf();
                    return;
                }
                for (ReportBean report : reportBeans) {
                    //上传报告
                    if (TextUtils.isEmpty(report.getFilePath())) {
                        Logger.w("离线报告本地路径为空删除");
                        report.delete();
                        continue;
                    }

                    if (!report.getFilePath().startsWith("http") && !FileUtils.isFileExist(report.getFilePath())) {
                        Logger.w("离线报告本地路径文件不存在删除");
                        report.delete();
                        continue;
                    }

                    if (Utils.checkProfileIsMeasuring(Long.parseLong(report.getProfileId()))) {
                        continue;
                    }

                    //有文件路径
                    if (!report.getFilePath().startsWith("http")) {
                        io.reactivex.Observable.just(1)
                                .map(integer -> OSSUtils.uploadReportJson(report.getFilePath(), report.getStartTime()))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(filePath -> {
                                    report.setFilePath(filePath);
                                    Logger.w("上传报告json成功:" + filePath);
                                    editOrAddReport(report);
                                });
                    } else {
                        //已经上传到阿里云了
                        editOrAddReport(report);
                    }
                }
            }
        });
    }

    private void editOrAddReport(ReportBean report) {
        if (!TextUtils.isEmpty(report.getDeviceId())) {
            if (!TextUtils.isEmpty(report.getReportId())) {
                //有设备id和报告id
                editReport(report);
            } else {
                addReport(report);
            }
        } else {
            addDeviceToServer(report);
        }
    }

    /**
     * 添加设备到服务器
     */
    private void addDeviceToServer(ReportBean report) {
        DeviceCenter.addDevice(report.getPatchDeviceType(), report.getPatchSerialNum(), report.getPatchMacaddress(), report.getPatchVersion(), new NetCallBack<String>() {
            @Override
            public void onSucceed(String data) {
                Logger.w("添加设备设备成功:id = " + data);
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_CHANGED));
                report.setDeviceId(data);
                addReport(report);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("添加设备失败:" + resultPair.getData());
            }
        });
    }

    /**
     * 添加报告
     */
    private void addReport(ReportBean report) {
        MeasureReportCenter.addReport(report.getDeviceId(), report.getProfileId(), report.getStartTime(), new NetCallBack<String>() {
            @Override
            public void onSucceed(String data) {
                Logger.w("添加报告成功:" + data);
                report.setReportId(data);
                editReport(report);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("添加报告失败:" + resultPair.getData());
            }
        });
    }

    private void editReport(ReportBean report) {
        MeasureReportCenter.editReport(report, new NetCallBack<String>() {
            @Override
            public void onSucceed(String data) {
                Logger.w("编辑报告成功:" + report.getReportId());
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.ADD_REPORT));
                report.delete();
                int count = LitePal.count(ReportBean.class);
                Logger.w("当前离线报告数量:" + count);
                if (count <= 0) {
                    stopSelf();
                }
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("编辑报告失败:" + resultPair.getData());
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.w("离线报告上传服务销毁");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

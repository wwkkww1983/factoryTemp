package com.proton.carepatchtemp.viewmodel.measure;

import android.databinding.ObservableBoolean;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.ShareBean;
import com.proton.carepatchtemp.bean.ShareTempBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.MeasureCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.MQTTShareManager;
import com.proton.temp.connector.utils.Utils;
import com.wms.logger.Logger;
import com.wms.utils.NetUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wangmengsi on 2018/3/22.
 * 共享测量
 */
public class ShareMeasureViewModel extends MeasureViewModel {
    /**
     * 是否取消共享
     */
    public ObservableBoolean isCancelShare = new ObservableBoolean(false);
    /**
     * 是否结束测量
     */
    public ObservableBoolean isEndMeasure = new ObservableBoolean();
    /**
     * 无法获取温度数据
     */
    public ObservableBoolean canNotGetData = new ObservableBoolean();
    private ShareBean mShareBean;
    /**
     * 收到了201状态码，必须先收到该状态码才能进行测量
     */
    private boolean hasReceivecode201;
    /**
     * 定时检测201有没有收到
     */
    private Timer mTimer;
    /**
     * 201定时器开始时间
     */
    private long mCheck201StartTime;
    /**
     * 是否是订阅底座
     */
    private boolean isSubscribeDocker;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private MQTTShareManager.MQTTShareListener mMQTTListener = new MQTTShareManager.MQTTShareListener() {
        @Override
        public void receiveMQTTData(ShareTempBean shareTemp) {
            doShareData(shareTemp);
        }

        @Override
        public void onSubscribeSuccess() {
            //握手
            Logger.w("订阅成功");
            connectStatus.set(2);
            //p03不用握手
            if (isSubscribeDocker) return;
            ShareTempBean share = new ShareTempBean(200);
            share.setProfileId(mShareBean.getProfileId());
            MQTTShareManager.getInstance().publish(getShareTopic(), share);
        }

        @Override
        public void onDisconnect() {
            Logger.w("mqtt分享服务断开连接");
            if (NetUtils.isConnected(getContext())) {
                //网络是连接的断开连接可能是被挤掉线
                connectStatus.set(0);
                canNotGetData.notifyChange();
                MQTTShareManager.getInstance().unsubscribe(getShareTopic());
            }
        }

        @Override
        public void onConnectFaild() {
            Logger.w("mqtt分享服务连接失败");
            connectStatus.set(0);
            canNotGetData.notifyChange();
            MQTTShareManager.getInstance().unsubscribe(getShareTopic());
        }
    };

    /**
     * 连接共享设备
     * 1,检查底座是否在线
     * 2,检查贴是否在线
     */
    public void connect(ShareBean shareBean) {
        mShareBean = shareBean;
        connectStatus.set(1);
        if (!TextUtils.isEmpty(shareBean.getDockerMacaddress())) {
            checkDockerOnline(shareBean);
        } else {
            checkPatchOnline(shareBean);
        }
    }

    /**
     * 检查底座在不在线
     */
    private void checkDockerOnline(ShareBean shareBean) {
        MeasureCenter.checkMqttIsOnline(shareBean.getDockerMacaddress(), new NetCallBack<Boolean>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
                connectStatus.set(0);
            }

            @Override
            public void onSucceed(Boolean isOnline) {
                //连接在线
                isSubscribeDocker = true;
                Logger.w("底座在线，订阅底座:" + getShareTopic());
                MQTTShareManager.getInstance().subscribe(getShareTopic(), mMQTTListener);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                checkPatchOnline(shareBean);
                Logger.w("底座不在线");
            }
        });
    }

    /**
     * 检查底座在不在线
     */
    private void checkPatchOnline(ShareBean shareBean) {
        String clientId = "proton" + shareBean.getId();
        MeasureCenter.checkMqttIsOnline(clientId, new NetCallBack<Boolean>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
                connectStatus.set(0);
            }

            @Override
            public void onSucceed(Boolean isOnline) {
                //连接在线
                isSubscribeDocker = false;
                Logger.w("贴在线，订阅贴:" + getShareTopic());
                MQTTShareManager.getInstance().subscribe(getShareTopic(), mMQTTListener);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                canNotGetData.notifyChange();
                connectStatus.set(0);
                Logger.w("贴不在线");
            }
        });
    }

    public void reconnect() {
        connect(mShareBean);
    }

    @Override
    public void disConnect() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        MQTTShareManager.getInstance().unsubscribe(getShareTopic());
    }

    @Override
    public void cancelConnect() {
        disConnect();
    }

    /**
     * 处理共享温度数据逻辑
     */
    private void doShareData(ShareTempBean shareTemp) {

        if (shareTemp == null || shareTemp.getCode() == 200) {
            return;
        }

        if (shareTemp.getCode() == 201) {
            hasReceivecode201 = true;
        }

        if (!hasReceivecode201) {
            Logger.w("没有收到201");
            startCheck201Timer();
            return;
        }

        if (mTimer != null) {
            mTimer.cancel();
        }

        if (shareTemp.getCode() == 201) {
            //收到201代表当前设备在测温
            Logger.w("共享设备在测量");
            connectStatus.set(2);
            currentTemp.set(shareTemp.getCurrentTemp());
            if (shareTemp.getHighestTemp() != 0) {
                highestTemp.set(shareTemp.getHighestTemp());
            }
        }

        if (shareTemp.getCode() == 202) {
            //实时温度
            Logger.w("收到共享温度");
            connectStatus.set(2);
            currentTemp.set(shareTemp.getCurrentTemp());
            if (shareTemp.getHighestTemp() != 0) {
                highestTemp.set(shareTemp.getHighestTemp());
            }
        }

        if (shareTemp.getCode() == 203) {
            //结束测量
            Logger.w("共享结束");
            isEndMeasure.notifyChange();
        }

        if (shareTemp.getCode() == 204) {
            //取消共享
            if (shareTemp.getSharedUid() == Long.parseLong(App.get().getApiUid())) {
                Logger.w("取消共享");
                isCancelShare.set(true);
                //通知取消共享
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.MQTT_SHARE_CANCEL, mShareBean));
            }
        }
    }

    private void startCheck201Timer() {
        if (mTimer == null) {
            mCheck201StartTime = System.currentTimeMillis();
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Logger.w("检测201定时器");
                    if (System.currentTimeMillis() - mCheck201StartTime > 10000) {
                        mHandler.post(() -> {
                            canNotGetData.notifyChange();
                            connectStatus.set(0);
                            mTimer.cancel();
                            mTimer = null;
                            disConnect();
                        });
                    }
                }
            }, 0, 1000);
        }
    }

    private String getShareTopic() {
        if (mShareBean == null) return "";
        //连接在线
        String topic;
        if (!isSubscribeDocker) {
            //不是p03则订阅贴
            topic = com.proton.carepatchtemp.utils.Utils.getShareTopic(mShareBean.getMacaddress());
        } else {
            //p03直接订阅充电器
            topic = Utils.getTopicByMacAddress(mShareBean.getDockerMacaddress());
        }
        return topic;
    }

    @Override
    public boolean isShare() {
        return true;
    }
}

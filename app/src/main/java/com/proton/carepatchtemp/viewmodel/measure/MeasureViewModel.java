package com.proton.carepatchtemp.viewmodel.measure;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableFloat;
import android.databinding.ObservableInt;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.bean.AliyunToken;
import com.proton.carepatchtemp.bean.DeviceOnlineBean;
import com.proton.carepatchtemp.bean.LogBean;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.bean.ReportBean;
import com.proton.carepatchtemp.bean.ShareTempBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.fragment.measure.TipFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.DeviceCenter;
import com.proton.carepatchtemp.net.center.MeasureCenter;
import com.proton.carepatchtemp.net.center.MeasureReportCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.MQTTShareManager;
import com.proton.carepatchtemp.utils.Settings;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.utils.net.OSSUtils;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;
import com.proton.temp.connector.TempConnectorManager;
import com.proton.temp.connector.bean.ConnectionType;
import com.proton.temp.connector.bean.DeviceType;
import com.proton.temp.connector.bean.TempDataBean;
import com.proton.temp.connector.interfaces.AlgorithmStatusListener;
import com.proton.temp.connector.interfaces.ConnectStatusListener;
import com.proton.temp.connector.interfaces.ConnectionTypeListener;
import com.proton.temp.connector.interfaces.DataListener;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import org.litepal.LitePal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import cn.trinea.android.common.util.FileUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wangmengsi on 2018/3/22.
 */

public class MeasureViewModel extends BaseViewModel {

    //--------------------------------------体温校准新增变量start------------------
    /**
     * 测温点1 ：  0 未检查  1：稳定  2：不稳定
     */
    public ObservableInt firstStableState = new ObservableInt(0);
    /**
     * 测温点1稳定温度
     */
    public ObservableFloat firstStableTemp = new ObservableFloat(0);
    /**
     * 测温点1：水槽温度
     */
    public ObservableFloat firstSinkTemp = new ObservableFloat(0);
    /**
     * 第一次测温允许误差
     */
    public ObservableFloat firstAllowableError = new ObservableFloat(0);
    /**
     * 测温点2:   0 未检查  1 稳定  2 不稳定
     */
    public ObservableInt secondStableState = new ObservableInt(0);
    /**
     * 温度点2稳定温度
     */
    public ObservableFloat secondStableTemp = new ObservableFloat(0);
    /**
     * 测温点2 ：水槽温度
     */
    public ObservableFloat secondSinkTemp = new ObservableFloat(0);
    /**
     * 第二次温度允许误差
     */
    public ObservableFloat secondAllowableError = new ObservableFloat(0);

    /**
     * 校准前选择是否进行校准,默认进行校准
     */
    public ObservableBoolean calibrateSelect = new ObservableBoolean(true);
    /**
     * 校准状态： 0 未校准   1 已校准  2 校准错误
     */
    public ObservableInt calibrationStatus = new ObservableInt(0);
    /**
     * 校准值
     */
    public ObservableFloat calibration = new ObservableFloat(0);
    /**
     * 合格状态  0 没有进入合格状态  1 已合格  2 未合格
     */
    public ObservableInt qualificationStatus = new ObservableInt(0);

    //--------------------------------------体温校准新增变量end------------------


    public ObservableField<MeasureBean> measureInfo = new ObservableField<>();
    /**
     * 当前温度,用于显示给用户,为算法温度
     */
    public ObservableFloat currentTemp = new ObservableFloat(0);

    /**
     * 当前算法温度
     */
    public ObservableFloat algorithmTemp = new ObservableFloat(0);

    /**
     * 原始温度
     */
    public ObservableFloat originalTemp = new ObservableFloat(0);

    /**
     * 最高温
     */
    public ObservableFloat highestTemp = new ObservableFloat(0);
    /**
     * 电量
     */
    public ObservableInt battery = new ObservableInt(-1);

    /**
     * 蓝牙信号强度
     */
    public ObservableInt bleRssi = new ObservableInt(0);
    /**
     * wifi信号强度
     */
    public ObservableInt wifiRssi = new ObservableInt(0);

    /**
     * 是否连接上了设备
     * 0 未连接 1连接中 2已连接 3手动断开连接
     */
    public ObservableInt connectStatus = new ObservableInt();
    /**
     * 连接方式
     */
    public ObservableField<ConnectionType> connectionType = new ObservableField<>();
    /**
     * 是否贴上了设备
     */
    public ObservableBoolean hasStick = new ObservableBoolean(false);

    /**
     * 体温贴是否断裂
     */
    public ObservableBoolean carePatchEnable = new ObservableBoolean(true);
    /**
     * 温度稳定状态
     */
    public ObservableBoolean tempStabled = new ObservableBoolean(false);
    /**
     * 需要跳转到实时测量
     */
    public ObservableBoolean needGoToMeasure = new ObservableBoolean(false);
    /**
     * 是否需要交替显示温度和文字（实时温度+预热中）
     */
    public ObservableBoolean needShowPreheating = new ObservableBoolean(false);
    /**
     * 是否需要交替显示温度和文字（实时温度+手臂张开，温度偏低）
     */
    public ObservableBoolean needShowTempLow = new ObservableBoolean(false);

    /**
     * 用于标识当前是（预热中的定时器）还是（温度偏低的定时器）  默认是（预热中）的定时器
     */
    public ObservableBoolean isShowPreheating = new ObservableBoolean(true);

    /**
     * 需要显示断开连接对话框
     */
    public ObservableBoolean needShowDisconnectDialog = new ObservableBoolean(false);
    /**
     * 保存报告
     */
    public ObservableField<ReportBean> saveReport = new ObservableField<>();
    /**
     * 固件版本
     */
    public ObservableField<String> hardVersion = new ObservableField<>("");
    /**
     * 序列号
     */
    public ObservableField<String> serialNumber = new ObservableField<>("");
    /**
     * 是否充电
     */
    public ObservableBoolean isCharge = new ObservableBoolean(false);
    /**
     * 设备id
     */
    public ObservableField<String> deviceId = new ObservableField<>("");
    /**
     * 报告id
     */
    public ObservableField<String> reportId = new ObservableField<>("");
    /**
     * 贴的mac地址
     */
    public ObservableField<String> patchMacaddress = new ObservableField<>("");
    /**
     * 测量提示文字
     */
    public ObservableField<String> measureTips = new ObservableField<>();

    /**
     * 当前接收数据的序列号和上次不一致
     */
    private ObservableBoolean isNotSameDevice = new ObservableBoolean(false);

    /**
     * 是否是锁屏状态
     */
//    public ObservableBoolean isScreenLocked = new ObservableBoolean(false);

    /**
     * 共享信息
     */
    private ShareTempBean mShareTemp = new ShareTempBean();
    /**
     * 正在添加报告
     */
    private boolean isAddingReport;
    /**
     * 是否是测量准备页
     */
    private boolean isBeforeMeasure = true;
    private Timer mSwitchTimer;
    private Disposable mResetTempDisposed;
    /**
     * 上一次收到温度的时间
     */
    private long mLastReceiveTempTime;
    private Timer mCheckReceiveDataTimer;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 定时保存体温数据，防止闪退，app杀死造成数据丢失
     */
    private Timer mTimer;
    /**
     * 点击保存按钮、或者activity被销毁的时候，停止定时器
     */
    private boolean stopTimer;

    /**
     * 锁屏时，切换连接方式时，如果当前是网络连接则需要先检查当前体温贴是否在线，如果在线则仍然保持网络连接
     */
    boolean isNeedCheck = false;
    /**
     * 算法版本号
     */
    public String algorithVersion;
    /**
     * 算法状态
     */
    public int algorithStatus;
    /**
     * 算法姿势
     */
    public int algorithGesture;

    /**
     * 定义算法时长，及姿势（2 3 4 ）的占比
     */
    private long duration = App.get().getDuration();

    /**
     * 算法姿势（2 3 4 ）在duration内占比阈值
     */
    private float percentage = App.get().getPercentage();

    /**
     * 算法姿势总次数
     */
    private float totalAlgorithmCount = 0f;
    /**
     * 算法姿势在（2、3、4）的次数
     */
    private float isInConditionAlgorithmGestureCount = 0f;
    /**
     * 算法姿势为（2、3、4）的占比
     */
    private float inConditionPercent = 0f;

    /**
     * 算法姿势占比定时器
     */
    private Timer mAlgorithmGestureTimer;

    /**
     * 底座算法版本号
     */
    private String dockerAlgVersion;

    /**
     * mqtt连接，首次30秒未收到数据，弹框提醒
     */
    private boolean isFirstOffLine = true;


    /**
     * 上次连接成功的类型（主要用于有wifi连接切换到蓝牙连接的时候，且蓝牙连接一直连接不上的时候（应用场景：父母上班时候体温贴断开，这个时候好截图给在家的爷爷奶奶，所以需要显示wifi连接不上的弹框））
     */
    public ConnectionType lastConnectType;

    /**
     * 首次重连10分钟的时候
     */
    public boolean isFirstArrive15Min = true;//现在改成了15min中

    Settings settings = new Settings();

    /**
     * 上传日志相关-----start
     */
    private int logCacheSize = 0;
    private int currentIndex = 0;
    private List<LogBean> logBeans = new ArrayList<>();
    private Timer uploadLogTimer;
    //定时上传日志时间间隔
    private long interval = 2 * 60 * 1000;
    /**
     * 上传日志相关-----end
     */

//    private BroadcastReceiver mNetReceiver = new BroadcastReceiver() {
//        private boolean isFirstNetChanged = true;
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
//                saveLog(LogTopicConstant.LOG_NETWORK_TOPIC);
//                if (!isFirstNetChanged) {
//                    if (NetUtils.isConnected(App.get())) {
//                        Logger.w("网络已连接");
//                        getDockerMacaddressByPatch(true);
//                    } else {
//                        Logger.w("网络未连接");
//                        if (!BluetoothUtils.isBluetoothOpened()) {
//                            BluetoothUtils.openBluetooth();
//                        }
//                      /*  if (isScreenLocked.get()) {
//                            getConnectorManager().switchConnectionType(ConnectionType.BLUETOOTH);
//                        } else {
//                            getConnectorManager().switchConnectionType(ConnectionType.BROADCAST);
//                        }*/
//
//                        //是否在前台（锁屏和按home键都是处于后台,变成蓝牙连接）
//                        boolean foreground = App.get().isForeground();
//                        if (foreground) {
//                            getConnectorManager().switchConnectionType(ConnectionType.BROADCAST);
//                        } else {
//                            getConnectorManager().switchConnectionType(ConnectionType.BLUETOOTH);
//                        }
//                    }
//                }
//                isFirstNetChanged = false;
//            }
//        }
//    };
    private ConnectStatusListener mConnectorListener = new ConnectStatusListener() {

        @Override
        public void onConnectSuccess() {
            needShowDisconnectDialog.set(false);
            connectStatus.set(2);
//            doMQTTShare(205);
            addDeviceToServer();
            lastConnectType = getConnectType();
            Logger.w("上次连接成功的类型是:", lastConnectType.name());

            //蓝牙广播连接成功后防止底座订阅被取消不能切换回wifi
            if (getConnectType() == ConnectionType.BROADCAST || getConnectType() == ConnectionType.BLUETOOTH) {
                bleRssi.set(measureInfo.get().getDevice().getBluetoothRssi());
                if (patchMacaddress != null) {
                    //订阅底座
                    if (!MQTTShareManager.getInstance().isSubscribe("patch/" + patchMacaddress.get() + "/docker")) {
                        subscribeDockerStatus();
                        Logger.w("broadcast connect 重新订阅");
                    }
                }
            }
        }

        @Override
        public void onConnectFaild() {
            //连接断开则重连
            connectStatus.set(1);
        }

        @Override
        public void onDisconnect(boolean isManual) {
            connectStatus.set(isManual ? 3 : 0);
            doDisconnect();
        }

        @Override
        public void receiveReconnectTimes(int retryCount, int leftCount, long totalReconnectTime) {
            Logger.w("重连次数:", retryCount, " 剩余重连次数 :", leftCount, String.format(" ,当前重连总时间为%s", totalReconnectTime));
            if (totalReconnectTime >= Settings.RECONNECT_TIME && isFirstArrive15Min) {
                needShowDisconnectDialog.set(true);
                needShowDisconnectDialog.notifyChange();
                isFirstArrive15Min = false;
                Logger.w("重连时间达到15min，开始报警");
            }
        }

        /**
         * 测量准备 未连接6秒弹框提示
         */
        @Override
        public void showBeforeMeasureDisconnect() {
            if (isBeforeMeasure) {
                needShowDisconnectDialog.set(true);
            }
        }

        @Override
        public void receiveNotSampleDevice(String oldMac, String newMac) {
            connectStatus.set(1);
        }

        @Override
        public void receiveDockerOffline(boolean isOffline) {
            connectStatus.set(isOffline ? 1 : 2);
            if (isOffline) {
                ConnectionType connectType = getConnectType();
                if (connectType == ConnectionType.NET) {
                    getDockerMacaddressByPatch(false);
                } else {
                    //是否处于后台（锁屏和按下home键都是处于后台）
                    boolean foreground = App.get().isForeground();
                    if (!foreground) {
                        getConnectorManager().switchConnectionType(ConnectionType.BLUETOOTH);
                    } else {
                        getConnectorManager().switchConnectionType(ConnectionType.BROADCAST);
                    }
                }

                Logger.w("首次30秒未收到数据  isFirstOffline:", isFirstOffLine);

                if (isFirstOffLine && isBeforeMeasure) {
                    isFirstOffLine = false;
                    needShowDisconnectDialog.set(true);
                    needShowDisconnectDialog.notifyChange();
                }
            }
        }
    };

    /**
     * 数据接收
     */
    private DataListener mDataListener = new DataListener() {
        @Override
        public void receiveHardVersion(String version) {
            super.receiveHardVersion(version);
            Logger.w("固件版本:" + version);
            hardVersion.set(version);
        }

        @Override
        public void receiveSerial(String serial) {
            super.receiveSerial(serial);
            Logger.w("序列号:" + serial);
            if (!TextUtils.isEmpty(serialNumber.get()) && !serial.equalsIgnoreCase(serialNumber.get())) {
                isNotSameDevice.set(true);
                return;
            }
            serialNumber.set(serial);
        }

        @Override
        public void receiveCurrentTemp(List<TempDataBean> temps) {
            if (CommonUtils.listIsEmpty(temps)) return;
            //重置测量准备首次断开连接的标识
            isFirstOffLine = true;
            //重置实时测量断开连接15分钟的报警
            isFirstArrive15Min = true;
            mLastReceiveTempTime = System.currentTimeMillis();
            connectStatus.set(2);
            currentTemp.set(temps.get(temps.size() - 1).getAlgorithmTemp());
            algorithmTemp.set(temps.get(temps.size() - 1).getAlgorithmTemp());
            originalTemp.set(temps.get(temps.size() - 1).getTemp());
            Logger.w("原始温度 : ", originalTemp.get(), " 算法温度 ： ", algorithmTemp.get());
//            doMQTTShare(202);

            if (!Utils.isTempInRange(currentTemp.get())) {
                if (!isBeforeMeasure) {
                    measureTips.set("不在测量范围内\n" +
                            "（25~45℃）");
                } else if (isBeforeMeasure) {
                    measureTips.set("不在测量范围内（25~45℃）");
                }
                stopSwitchTempAndTipsTimer();
            }
            EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.TEMP_CHANGE));
        }


        @Override
        public void receiveBattery(Integer batteryValue) {
            battery.set(batteryValue);
        }

        @Override
        public void receiveCharge(boolean charge) {
            isCharge.set(charge);
        }


        @Override
        public void judgeCarepatchEnable(boolean isEnable) {
            carePatchEnable.set(isEnable);
            Logger.w("体温贴是否可用 isEnable= " + isEnable);
        }

        @Override
        public void receiveBleAndWifiRssi(Integer bleBssi, Integer wifiBssi) {
            bleRssi.set(bleBssi);
            wifiRssi.set(wifiBssi);
            Logger.w("ble信号强度： ", bleBssi, " wifi信号强度: ", wifiBssi);
        }
    };

    private AlgorithmStatusListener mAlgorithmStatusListener = new AlgorithmStatusListener() {

        @Override
        public void receiveAlgorithmVersionType(int type) {
            if (type == 0) {//本地算法版本号
                algorithVersion = getConnectorManager().getLocalAlgorithmVersion();
            } else {//底座算法版本号
                algorithVersion = dockerAlgVersion;
            }
            Logger.w("当前算法版本号: ", algorithVersion, " 算法类型: ", type == 0 ? "本地算法" : "底座算法");
        }

        /**
         * status状态说明
         * 蓝牙和广播连接的时候状态值:0、1、3、5、6
         * wifi连接用的是底座的算法所以状态值为：0、1、3、5、10
         * @param status
         * @param gesture
         */
        @Override
        public void receiveMeasureStatusAndGesture(int status, int gesture) {
            Logger.w("status : " + status, "   gesture : " + gesture, " patchAddress : ", measureInfo.get().getPatchMac());

            algorithStatus = status;
            algorithGesture = gesture;

            if (isBeforeMeasure) {
                //测量准备页面状态为3，4，5，6，10跳转到实时测量
                if (status == 3 || status == 4 || status == 5 || status == 6 || status == 10) {
                    needGoToMeasure.notifyChange();
                    return;
                }
            }

            String tips = UIUtils.getString(R.string.string_keep_arm_clamp_2);

            /**
             * 一代体温贴：只有0 1 2 3 四个状态   如果status=0、1、2则显示温度和预热  status=3 显示实时温度
             */
            DeviceType deviceType = measureInfo.get().getDevice().getDeviceType();
            if (deviceType == DeviceType.P02) {//如果是一代体温贴
                if (status == 3) {
                    tips = "";
                    stopSwitchTempAndTipsTimer();
                } else {
                    startSwitchTempAndTipsTimer();
                }
                measureTips.set(tips);
                return;
            }

            totalAlgorithmCount++;
            if (gesture == 2 || gesture == 3 || gesture == 4) {
                isInConditionAlgorithmGestureCount++;
            }
            inConditionPercent = (isInConditionAlgorithmGestureCount / totalAlgorithmCount) * 100;
            Logger.w("gesture percent : ", inConditionPercent, " gesture阈值 : ", percentage);

            /**
             * 如果算法状态是2、4、7 则交替显示：实时温度+预热中
             */
            if (status == 2 || status == 4 || status == 7) {
                isShowPreheating.set(true);
                if (needShowTempLow.get()) {
                    needShowTempLow.set(false);
                }
                startSwitchTempAndTipsTimer();
            } else {//算法状态是0、1、3、5、10--->1.6.6之后算法状态变为了 0、1、3、5、6
                if (needShowPreheating.get()) {
                    needShowPreheating.set(false);
                }
                /**
                 * 交替显示：实时温度+手臂张开，温度偏低
                 */
                if (gesture == 2 || gesture == 3 || gesture == 4) {
                    if (inConditionPercent >= percentage) {
                        isShowPreheating.set(false);
                        startSwitchTempAndTipsTimer();
                    } else {
                        stopSwitchTempAndTipsTimer();
                    }
                }

                if (gesture == 0 || gesture == 1) {
                    stopSwitchTempAndTipsTimer();
                    tips = "";
                }
            }

            if (Utils.isTempInRange(currentTemp.get())) {
                measureTips.set(tips);
            } else {
                if (!isBeforeMeasure) {
                    measureTips.set("不在测量范围内\n" +
                            "（25~45℃）");
                } else if (isBeforeMeasure) {
                    measureTips.set("不在测量范围内（25~45℃）");
                }
                stopSwitchTempAndTipsTimer();
            }

            if (!carePatchEnable.get()) {
                measureTips.set(App.get().getString(R.string.string_captch_unable));
                stopSwitchTempAndTipsTimer();
            }
            Logger.w("tips is :", tips);
        }
    };
    private ConnectionTypeListener mConnectionTypeListener = new ConnectionTypeListener() {
        @Override
        public void receiveConnectType(ConnectionType type) {
            Logger.w("当前连接类型:", type.toString());
            connectionType.set(type);
        }
    };

    public MeasureViewModel() {
        currentTemp.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                //共享和mqtt连接不用发布消息
                highestTemp.set(Math.max(currentTemp.get(), highestTemp.get()));
            }
        });
        measureInfo.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                patchMacaddress.set(measureInfo.get().getPatchMac());
                hardVersion.set(measureInfo.get().getHardVersion());
                serialNumber.set(measureInfo.get().getSerialNum());
                Logger.w("连接设备的mac地址:" + measureInfo.get().getMacaddress()
                        + ",贴的mac地址:" + patchMacaddress.get()
                        + ",固件版本:" + hardVersion.get()
                        + ",序列号:" + serialNumber.get());
            }
        });
        connectStatus.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (!isConnected()) {
                    if (!isManualDisconnect()) {
                        mResetTempDisposed = io.reactivex.Observable.just(1)
                                .delay(10, TimeUnit.MINUTES)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(integer -> {
                                    currentTemp.set(0);
                                    algorithmTemp.set(0);
                                    originalTemp.set(0);
                                    highestTemp.set(0);
                                });
                    }
                    measureTips.set("");
                    stopSwitchTempAndTipsTimer();
                    stopCacheReportTimer();
                    stopAlgorithmGestureTimer();
                } else {
                    if (mResetTempDisposed != null && !mResetTempDisposed.isDisposed()) {
                        mResetTempDisposed.dispose();
                    }
                    startAlgorithmGestureTimer();
                }
            }

        });


        needGoToMeasure.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                measureTips.set("");
                stopSwitchTempAndTipsTimer();
            }
        });

    }

    /**
     * 连接设备
     */
    public void connectDevice() {
        connectDevice(Integer.MAX_VALUE);
    }

    public void connectDevice(int retryCount) {
        if (isShare()) {
            return;
        }
        if (!getConnectorManager().isConnected()) {
            if (connectStatus.get() == 1) {
                connectStatus.notifyChange();
            } else {
                connectStatus.set(1);
            }
        }
        getConnectorManager().addConnectionTypeListener(mConnectionTypeListener);
//        if (measureInfo.get().getDevice().getConnectionType() == ConnectionType.BLUETOOTH) {
//            getConnectorManager().setConnectionType(ConnectionType.BROADCAST);
//        }
        getConnectorManager()
                .setReconnectCount(retryCount)
                .setEnableCacheTemp(false)
                .addAlgorithmStatusListener(mAlgorithmStatusListener)
                .connect(mConnectorListener, mDataListener, true);
//        subscribeDockerStatus();
    }

    /**
     * 订阅底座上下线
     */
    private void subscribeDockerStatus() {
        MQTTShareManager.getInstance().subscribe("patch/" + patchMacaddress.get() + "/docker", new MQTTShareManager.MQTTShareListener() {
            @Override
            public void receiveMQTTData(String data) {
                try {
                    Type type = new TypeToken<ArrayList<String>>() {
                    }.getType();
                    List<String> datas = new Gson().fromJson(JSONUtils.getString(data, "list"), type);
                    if (!CommonUtils.listIsEmpty(datas)) {
                        Logger.w("收到底座上线通知:", datas.size(), ",dockerMac:", datas.get(datas.size() - 1));

                        String currentConnectDockerMac = getConnectorManager().getDockerMacaddress();
                        //当前连接的底座mac地址是否在线
                        boolean currentConnectDockerIsOnline = false;
                        for (String mac : datas) {
                            if (mac.equals(currentConnectDockerMac)) {
                                currentConnectDockerIsOnline = true;
                                break;
                            }
                        }

                        if (!currentConnectDockerIsOnline || !getConnectorManager().isMQTTConnect()) {
                            //当前连接的底座mac地址不在线才需要切换
                            getConnectorManager().setDockerMacaddress(datas.get(datas.size() - 1)).switchConnectionType(ConnectionType.NET);
                        }
                    } else {
                        Logger.w("没有底座在用");
                        boolean foreground = App.get().isForeground();
                        if (!foreground) {
                            getConnectorManager().switchConnectionType(ConnectionType.BLUETOOTH);
                        } else {
                            getConnectorManager().switchConnectionType(ConnectionType.BROADCAST);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void cancelConnect() {
        getConnectorManager().cancelConnect();
    }

    public List<TempDataBean> getAllTemps() {
        return getConnectorManager().getAllTemps();
    }

    public void disConnect() {
        doDisconnect();
        if (Utils.getPatchMeasureSize(measureInfo.get().getMacaddress()) <= 1) {
            getConnectorManager().disConnect();
        } else {
            getConnectorManager().removeConnectStatusListener(mConnectorListener);
            getConnectorManager().removeDataListener(mDataListener);
            getConnectorManager().removeAlgorithmStatusListener(mAlgorithmStatusListener);
            getConnectorManager().removeConnectionTypeListener(mConnectionTypeListener);
        }
    }

    private void doDisconnect() {
//        doMQTTShare(203);
        clear();
    }

    /**
     * 添加设备到服务器
     */
    public void addDeviceToServer() {

        String macaddress = patchMacaddress.get();
        if (TextUtils.isEmpty(macaddress) || !App.get().isLogined()) {
            return;
        }

        Logger.w("添加设备:deviecId = "
                + deviceId.get()
                + ",reportId = " + reportId.get()
                + "sn = " + serialNumber.get()
                + "hardversion = " + hardVersion.get());

        DeviceType type = measureInfo.get().getDevice().getDeviceType();
        DeviceCenter.addDevice(type.toString(), serialNumber.get(), macaddress, hardVersion.get(), new NetCallBack<String>() {

            @Override
            public void onSucceed(String data) {
                deviceId.set(data);
                Logger.w("添加设备成功:id = " + deviceId.get());
                if (!TextUtils.isEmpty(serialNumber.get()) && !TextUtils.isEmpty(hardVersion.get())) {
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_CHANGED));
                }
                addReport();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("添加设备失败:" + resultPair.getData());
            }
        });
    }

    private void editShareProfile() {
        DeviceCenter.editShareProfile(String.valueOf(measureInfo.get().getProfile().getProfileId()), deviceId.get(), true, new NetCallBack<Boolean>() {
            @Override
            public void onSucceed(Boolean data) {
                Logger.w("更新分享设备成功");
            }
        });
    }

    /**
     * 添加报告
     */
    private void addReport() {
        if (!TextUtils.isEmpty(reportId.get())
                || isAddingReport
                || measureInfo.get().getProfile().getProfileId() == -1) {
            return;
        }
        isAddingReport = true;
        MeasureReportCenter.addReport(deviceId.get(), String.valueOf(measureInfo.get().getProfile().getProfileId()), System.currentTimeMillis(), new NetCallBack<String>() {

            @Override
            public void noNet() {
                super.noNet();
                isAddingReport = false;
            }

            @Override
            public void onSucceed(String data) {
                Logger.w("添加报告成功:" + data);
                reportId.set(data);
                isAddingReport = false;
                //更新当前正在测量的设备
                editShareProfile();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("添加报告失败:" + resultPair.getData());
                isAddingReport = false;
            }
        });
    }

    /**
     * 编辑报告
     */
    private void editReport(ReportBean report) {
        MeasureReportCenter.editReport(report, new NetCallBack<String>() {

            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
                doSaveReportFail();
            }

            @Override
            public void onSucceed(String data) {
                Logger.w("编辑报告成功");
                dismissDialog();
                saveReport.set(report);
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.ADD_REPORT));
                report.delete();
                Logger.w("当前报告存储报告数量:" + LitePal.count(ReportBean.class));
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("编辑报告失败:" + resultPair.getData());
                doSaveReportFail();
            }
        });
    }

    /**
     * 保存报告失败
     */
    public void doSaveReportFail() {
        dismissDialog();
        if (saveReport.get() == null) {
            saveReport.notifyChange();
        } else {
            saveReport.set(null);
        }
    }

    /**
     * 删除报告
     */
    public void deleteReport() {
        if (TextUtils.isEmpty(reportId.get())) {
            return;
        }
        MeasureReportCenter.deleteReport(reportId.get(), new NetCallBack<Boolean>() {
            @Override
            public void onSucceed(Boolean data) {
                Logger.w("删除报告成功");
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w("删除报告失败");
            }
        });
    }

    /**
     * 保存报告数据
     */
    public void saveReport() {
        if (!isBeforeMeasure) {
            showDialog(R.string.string_report_uploading);
        }
        //上传json
        MeasureReportCenter.getAliyunToken(new NetCallBack<AliyunToken>() {
            @Override
            public void noNet() {
                uploadReport();
            }

            @Override
            public void onSucceed(AliyunToken data) {
                uploadReport();
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                uploadReport();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void uploadReport() {
        io.reactivex.Observable.just(1)
                .map(ReportBean -> saveReport2Json())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(report -> {
                    disConnect();
                    if (!TextUtils.isEmpty(report.getFilePath())) {
                        report.save();
                        Logger.w("上传报告json成功:" + report.getFilePath());
                        Logger.w("当前报告存储报告数量:" + LitePal.count(ReportBean.class));
                        editReport(report);
                    } else {
                        doSaveReportFail();
                    }
                });
    }

    /**
     * 保存成json文件
     */
   /* public ReportBean saveReport2Json() {
        ReportBean report = new ReportBean();
        //准备json数据
        List<TempDataBean> allTemps = getAllTemps();
        List<Float> sourceTemps = new ArrayList<>();
        List<Float> processTemps = new ArrayList<>();
        List<Long> tempTimes = new ArrayList<>();
        for (TempDataBean tempData : allTemps) {
            if (tempData == null) {
                continue;
            }
            sourceTemps.add(tempData.getTemp());
            processTemps.add(tempData.getAlgorithmTemp());
            tempTimes.add(tempData.getTime() / 1000);
        }

        report.setSample(getConnectorManager().getAllSample());
        report.setConnectType(getConnectorManager().getAllConnectionType());
        report.setSourceTemps(sourceTemps);
        report.setProcessTemps(processTemps);
        report.setTempTimes(tempTimes);
        report.setMaxTemp(getConnectorManager().getHighestTemp());
        report.setMinTemp(getConnectorManager().getLowestTemp());
        report.setHardVersion(hardVersion.get());
        if (allTemps.size() > 0) {
            report.setStartTime(allTemps.get(0).getTime());
        } else {
            report.setStartTime(System.currentTimeMillis());
        }
        report.setEndTime(System.currentTimeMillis());
        report.setDeviceId(deviceId.get());
        report.setReportId(reportId.get());
        report.setProfileId(String.valueOf(Objects.requireNonNull(measureInfo.get()).getProfile().getProfileId()));
        report.setProfileName(Objects.requireNonNull(measureInfo.get()).getProfile().getRealname());
        if (getConnectorManager().isMQTTConnect()) {
            report.setDeviceType("MQTT");
        } else {
            report.setDeviceType(measureInfo.get().getDevice().getDeviceType().toString());
        }
        //上传json文件
        String jsonPath = Utils.getReportJsonPath(report.getStartTime());
        FileUtils.writeFile(jsonPath, Utils.createJsonSkipLitepal(report));
        Logger.w("生成报告json成功");
        report.setFilePath(jsonPath);
        report.setUserId(App.get().getApiUid());
        //用于离线报告的数据
        report.setPatchVersion(hardVersion.get());
        report.setPatchSerialNum(serialNumber.get());
        report.setPatchMacaddress(patchMacaddress.get());
        report.setPatchDeviceType(measureInfo.get().getDevice().getDeviceType().toString());
        report.setType(isBeforeMeasure ? 1 : 0);
        //保存报告，方便做离线缓存
        report.save();
        Logger.w("当前报告存储报告数量:" + LitePal.count(ReportBean.class));
        report.setFilePath(OSSUtils.uploadReportJson(jsonPath, report.getStartTime()));
        return report;
    }
*/

    /**
     * 保存成json文件
     */
    public ReportBean saveReport2Json() {
        stopTimer = true;
        //准备json数据
        List<TempDataBean> allTemps = getAllTemps();
        List<Float> sourceTemps = new ArrayList<>();
        List<Float> processTemps = new ArrayList<>();
        List<Long> tempTimes = new ArrayList<>();
        List<Integer> states = new ArrayList<>();
        List<Integer> gestures = new ArrayList<>();
        List<String> algSdkVer = new ArrayList<>();

        for (TempDataBean tempData : allTemps) {
            if (tempData == null) {
                continue;
            }
            sourceTemps.add(tempData.getTemp());
            processTemps.add(tempData.getAlgorithmTemp());
            tempTimes.add(tempData.getTime());
            states.add(tempData.getMeasureStatus());
            gestures.add(tempData.getGesture());
            if (tempData.getAlgorithmVerType() == 0) {
                algSdkVer.add(getConnectorManager().getLocalAlgorithmVersion());
            } else {
                algSdkVer.add(dockerAlgVersion);
            }
        }

        long startTime;
        if (allTemps.size() > 0) {
            startTime = allTemps.get(0).getTime();
        } else {
            startTime = System.currentTimeMillis();
        }
//        ReportBean report = LitePal.where("userId = ?", App.get().getApiUid()).where("startTime = ?", String.valueOf(startTime)).findFirst(ReportBean.class);
//        LitePal.deleteAll(ReportBean.class, "userId = ? and startTime = ?", App.get().getApiUid(), String.valueOf(startTime));
        LitePal.deleteAll(ReportBean.class, "userId=? and reportId= ? ", App.get().getApiUid(), reportId.get());
        ReportBean report = new ReportBean();
        report.setAlgSdkVer(algSdkVer);
        report.setStates(states);
        report.setGestures(gestures);
        report.setSample(getConnectorManager().getAllSample());
        report.setConnectType(getConnectorManager().getAllConnectionType());
        report.setSourceTemps(sourceTemps);
        report.setProcessTemps(processTemps);
        report.setTempTimes(tempTimes);
        report.setMaxTemp(getConnectorManager().getHighestTemp());
        report.setMinTemp(getConnectorManager().getLowestTemp());
        report.setHardVersion(hardVersion.get());
        report.setStartTime(startTime);
        report.setEndTime(System.currentTimeMillis());
        report.setDeviceId(deviceId.get());
        report.setReportId(reportId.get());
        report.setProfileId(String.valueOf(Objects.requireNonNull(measureInfo.get()).getProfile().getProfileId()));
        report.setProfileName(Objects.requireNonNull(measureInfo.get()).getProfile().getRealname());
        if (getConnectorManager().isMQTTConnect()) {
            report.setDeviceType("MQTT");
        } else {
            report.setDeviceType(measureInfo.get().getDevice().getDeviceType().toString());
        }
        //上传json文件
        String jsonPath = Utils.getReportJsonPath(report.getStartTime());
        FileUtils.writeFile(jsonPath, Utils.createJsonSkipLitepal(report));
        Logger.w("生成报告json成功");
        report.setFilePath(jsonPath);
        report.setUserId(App.get().getApiUid());
        //用于离线报告的数据
        report.setPatchVersion(hardVersion.get());
        report.setPatchSerialNum(serialNumber.get());
        report.setPatchMacaddress(patchMacaddress.get());
        report.setPatchDeviceType(measureInfo.get().getDevice().getDeviceType().toString());
        report.setType(isBeforeMeasure ? 1 : 0);
        //保存报告，方便做离线缓存
        report.save();
        Logger.w("当前报告存储报告数量:" + LitePal.count(ReportBean.class));
        report.setFilePath(OSSUtils.uploadReportJson(jsonPath, report.getStartTime()));
        return report;
    }

    /**
     * 边测量边保存数据(每20s存一次)
     */
    private void startCacheReportData() {
        if (mTimer == null) {
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (stopTimer) {
                        mTimer.cancel();
                        mTimer = null;
                    } else {
                        saveReport3Json();
                    }
                }
            }, 10 * 1000, 30 * 1000);
        }
    }

    private void stopCacheReportTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    /**
     * 边测量边保存json文件到本地
     */
    private void saveReport3Json() {
        //准备json数据
        List<TempDataBean> allTemps = getAllTemps();
        List<Float> sourceTemps = new ArrayList<>();
        List<Float> processTemps = new ArrayList<>();
        List<Long> tempTimes = new ArrayList<>();

        List<Integer> states = new ArrayList<>();
        List<Integer> gestures = new ArrayList<>();
        List<String> algSdkVer = new ArrayList<>();

        for (TempDataBean tempData : allTemps) {
            if (tempData == null) {
                continue;
            }
            sourceTemps.add(tempData.getTemp());
            processTemps.add(tempData.getAlgorithmTemp());
            tempTimes.add(tempData.getTime());

            states.add(tempData.getMeasureStatus());
            gestures.add(tempData.getGesture());
            if (tempData.getAlgorithmVerType() == 0) {
                algSdkVer.add(getConnectorManager().getLocalAlgorithmVersion());
            } else {
                algSdkVer.add(dockerAlgVersion);
            }
        }

        long startTime;
        if (allTemps.size() > 0) {
            startTime = allTemps.get(0).getTime();
        } else {
            startTime = System.currentTimeMillis();
        }
        //此处不能获取之前的对象然后进行json操作，有可能出现ConcurrentModificationException异常
//        ReportBean report = LitePal.where("userId = ?", App.get().getApiUid()).where("startTime = ?", String.valueOf(startTime)).findFirst(ReportBean.class);
        LitePal.deleteAll(ReportBean.class, "userId = ? and startTime = ?", App.get().getApiUid(), String.valueOf(startTime));
        ReportBean report = new ReportBean();
        report.setAlgSdkVer(algSdkVer);
        report.setStates(states);
        report.setGestures(gestures);
        report.setSample(getConnectorManager().getAllSample());
        report.setConnectType(getConnectorManager().getAllConnectionType());
        report.setSourceTemps(sourceTemps);
        report.setProcessTemps(processTemps);
        report.setTempTimes(tempTimes);
        report.setMaxTemp(getConnectorManager().getHighestTemp());
        report.setMinTemp(getConnectorManager().getLowestTemp());
        report.setHardVersion(hardVersion.get());
        report.setStartTime(startTime);
        report.setEndTime(System.currentTimeMillis());
        report.setDeviceId(deviceId.get());
        report.setReportId(reportId.get());
        report.setProfileId(String.valueOf(Objects.requireNonNull(measureInfo.get()).getProfile().getProfileId()));
        report.setProfileName(Objects.requireNonNull(measureInfo.get()).getProfile().getRealname());
        if (getConnectorManager().isMQTTConnect()) {
            report.setDeviceType("MQTT");
        } else {
            report.setDeviceType(measureInfo.get().getDevice().getDeviceType().toString());
        }
        //上传json文件  更新本地文件
        String jsonPath = Utils.getReportJsonPath(report.getStartTime());
        FileUtils.writeFile(jsonPath, Utils.createJsonSkipLitepal(report));

        Logger.w("生成报告json成功");
        report.setFilePath(jsonPath);
        report.setUserId(App.get().getApiUid());
        //用于离线报告的数据
        report.setPatchVersion(hardVersion.get());
        report.setPatchSerialNum(serialNumber.get());
        report.setPatchMacaddress(patchMacaddress.get());
        report.setPatchDeviceType(measureInfo.get().getDevice().getDeviceType().toString());
        report.setType(isBeforeMeasure ? 1 : 0);
        if (report == null) {
            return;
        }
        //保存报告，方便做离线缓存
        boolean save = report.save();
        if (save) {
            Logger.w("当前保存时间: ", report.getStartTime(), " 当前报告存储报告数量:" + LitePal.count(ReportBean.class));
            ReportBean reportBean = LitePal.where("userId = ?", App.get().getApiUid()).where("startTime = ?", String.valueOf(startTime)).findFirst(ReportBean.class);
            Logger.w("报告详情：", reportBean == null ? "null" : reportBean.toString());
        } else {
            Logger.w("报告保存失败");
        }
    }

    public TempConnectorManager getConnectorManager() {
        return TempConnectorManager.getInstance(measureInfo.get().getDevice());
    }

    /**
     * 处理mqtt共享
     *
     * @param code 200请求 201回复 202实时温度 203结束测量 204取消共享 205开始测量
     */
//    private void doMQTTShare(int code) {
//        //共享和mqtt连接不用发布消息
//        if (isShare()
//                || getConnectorManager().isMQTTConnect()
//                || measureInfo.get().getProfile() == null
//                || measureInfo.get().getProfile().getProfileId() == 0
//                || !App.get().isLogined()
//                || !NetUtils.isConnected(App.get())
//                || !isConnected()
//                || BuildConfig.IS_INTERNAL) {
//            return;
//        }
//        mShareTemp.setCode(code);
//        mShareTemp.setCurrentTemp(currentTemp.get());
//        mShareTemp.setHighestTemp(highestTemp.get());
//        mShareTemp.setProfileId(measureInfo.get().getProfile().getProfileId());
//        mShareTemp.setSharedUid(Long.parseLong(App.get().getApiUid()));
//        //订阅一个topic用于回应
//        MQTTShareManager.getInstance().subscribe(getShareTopic(), new MQTTShareManager.MQTTShareListener() {
//            @Override
//            public void receiveMQTTData(ShareTempBean shareTemp) {
//                if (shareTemp == null) {
//                    return;
//                }
//                if (shareTemp.getCode() == 200
//                        && shareTemp.getProfileId() == measureInfo.get().getProfile().getProfileId()) {
//                    mShareTemp.setCode(201);
//                    mShareTemp.setCurrentTemp(getConnectorManager().getCurrentTemp());
//                    mShareTemp.setHighestTemp(getConnectorManager().getHighestTemp());
//                    MQTTShareManager.getInstance().publish(getShareTopic(), mShareTemp);
//                    Logger.w("发送201");
//                }
//            }
//        });
//        MQTTShareManager.getInstance().publish(getShareTopic(), mShareTemp);
//    }

    /**
     * 开启算法姿势占比计时器
     */
    private void startAlgorithmGestureTimer() {
        if (mAlgorithmGestureTimer == null) {
            mAlgorithmGestureTimer = new Timer();
            mAlgorithmGestureTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    totalAlgorithmCount = 0;
                    isInConditionAlgorithmGestureCount = 0;
                    inConditionPercent = 0f;
                }
            }, duration * 1000, duration * 1000);
        }
    }

    /**
     * 停止算法姿势计时器
     */
    private void stopAlgorithmGestureTimer() {
        if (mAlgorithmGestureTimer != null) {
            mAlgorithmGestureTimer.cancel();
            mAlgorithmGestureTimer = null;
        }
    }

    /**
     * 切换温度和提示定时器
     */
    private void startSwitchTempAndTipsTimer() {
        //不在温度范围内不切换
        if (!Utils.isTempInRange(currentTemp.get())) return;

        if (mSwitchTimer == null) {
            mSwitchTimer = new Timer();
            mSwitchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Logger.w("切换定时器");
                    if (isShowPreheating.get()) {
                        needShowPreheating.set(!needShowPreheating.get());
                    } else {
                        needShowTempLow.set(!needShowTempLow.get());
                    }
                }
            }, 0, 5000);
        }
    }

    private void stopSwitchTempAndTipsTimer() {
        isShowPreheating.set(true);
        needShowPreheating.set(false);
        needShowTempLow.set(false);
        if (mSwitchTimer != null) {
            Logger.w("切换定时器停止");
            mSwitchTimer.cancel();
            mSwitchTimer = null;
        }
    }

    /**
     * 处理app前后台情况
     */
    public void doScreenLockStatus(boolean isLock) {
        Logger.w("是否在后台（按home键或者锁屏）:", isLock);
        if (measureInfo.get().getDevice().getDeviceType() == DeviceType.P02) {
            return;
        }
        ConnectionType currentConnectionType = getConnectorManager().getConnectionType();
        if (currentConnectionType == ConnectionType.NET) {
            //网络连接不处理
            return;
        }

        //广播模式，切换到后台，如果30秒没收到数据就切换到蓝牙，切换到前台立马切换成广播==>现在统一改成熄屏等6秒直接切换成蓝牙，android机型太多，统一一下
        if (isLock) {
//            startCheckReceiveDataTimer();
            lockScreenOperation();
        } else {
            stopCheckReceiveDataTimer();
            getConnectorManager().switchConnectionType(ConnectionType.BROADCAST);
        }
    }


    /**
     * 熄屏或按下home键直接6秒切蓝牙
     */
    private void lockScreenOperation() {
        Logger.w("当前连接类型 ： ", connectionType.get().name());
        if (connectionType.get() == ConnectionType.BROADCAST) {//广播连接-》熄屏
            mainHandler.postDelayed(() -> {
                if (!App.get().isForeground()) {
                    getConnectorManager().switchConnectionType(ConnectionType.BLUETOOTH);
                }
            }, 6000);
        } else if (connectionType.get() == ConnectionType.NET) {//wifi连接-》熄屏

            if (mCheckReceiveDataTimer != null) return;
            mLastReceiveTempTime = System.currentTimeMillis();
            mCheckReceiveDataTimer = new Timer();

            mCheckReceiveDataTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Logger.w("是否收到数据定时器");
                    if (System.currentTimeMillis() - mLastReceiveTempTime >= 30000 && mLastReceiveTempTime != 0) {
                        //当30秒没收到数据则切换到蓝牙
                        mainHandler.post(() -> {
                            getDockerMacaddressByPatch(false);
                        });
                        stopCheckReceiveDataTimer();
                    }
                }
            }, 0, 5000);
        }
    }

    /**
     * 开启是否收到数据的定时器
     */
    private void startCheckReceiveDataTimer() {
        if (mCheckReceiveDataTimer != null) return;
        mLastReceiveTempTime = System.currentTimeMillis();
        mCheckReceiveDataTimer = new Timer();
        mCheckReceiveDataTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Logger.w("是否收到数据定时器");
                if (System.currentTimeMillis() - mLastReceiveTempTime >= 30000 && mLastReceiveTempTime != 0) {
                    //当30秒没收到数据则切换到蓝牙
                    mainHandler.post(() -> {
                        Logger.w("当前连接类型 ： ", connectionType.get().name());
                        ConnectionType type = null;
                        switch (Objects.requireNonNull(connectionType.get())) {
                            case BROADCAST:
                                isNeedCheck = false;
                                type = ConnectionType.BLUETOOTH;
                                break;
//                            case BLUETOOTH://当两个相同卡片会出现bug，第一个卡片切换蓝牙，第二个关掉蓝牙连接导致连接不上
//                                isNeedCheck = false;
//                                type = ConnectionType.BROADCAST;
//                                break;
                            case NET:
                                isNeedCheck = true;
                                type = ConnectionType.BROADCAST;
                                break;
                        }
                        if (type == null) {
                            return;
                        }
                        Logger.w("要切换的连接方式： ", type.name(), "  是否需要检测体温贴是否在线 : ", isNeedCheck);

                        if (isNeedCheck) {
                            getDockerMacaddressByPatch(false);
                        } else {
                            getConnectorManager().switchConnectionType(type);
                        }
                    });
                    stopCheckReceiveDataTimer();
                }
            }
        }, 0, 5000);
    }

    private void stopCheckReceiveDataTimer() {
        if (mCheckReceiveDataTimer != null) {
            Logger.w("收到数据定时器停止");
            mCheckReceiveDataTimer.cancel();
            mCheckReceiveDataTimer = null;
        }
    }

    /**
     * 获取贴当前被连的底座mac地址
     * isNeedReset  是否需要重新制定连接方式，主要是为了规避10分钟收不到数据的问题，因为重置连接的时候会重置时间
     */
    private void getDockerMacaddressByPatch(boolean isNeedReset) {
        MeasureCenter.checkPatchIsMeasuring(patchMacaddress.get(), new NetCallBack<DeviceOnlineBean>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(DeviceOnlineBean deviceOnline) {
                Logger.w("贴是否在线:", deviceOnline.isOnline(), ",patchMac: ", patchMacaddress.get(), ",dockerMac:", deviceOnline.getDockerMac());
                if (deviceOnline.isOnline()) {
                    if (isNeedReset) {
                        getConnectorManager().setDockerMacaddress(deviceOnline.getDockerMac())
                                .switchConnectionType(ConnectionType.NET);
                    }
                } else {// App准备切换成蓝牙连接时，向服务器请求是否能切换成蓝牙(可以切换成蓝牙)
                    boolean foreground = App.get().isForeground();
                    if (!foreground) {
                        getConnectorManager().setDockerMacaddress(deviceOnline.getDockerMac())
                                .switchConnectionType(ConnectionType.BLUETOOTH);
                    } else {
                        getConnectorManager().setDockerMacaddress(deviceOnline.getDockerMac())
                                .switchConnectionType(ConnectionType.BROADCAST);
                    }

                }
            }

            @Override
            public void onTimeOut() {
                Logger.w("网络状态不稳定，请求超时");
            }
        });
    }

    /**
     * 清空状态
     */
    private void clear() {
        if (mResetTempDisposed != null && !mResetTempDisposed.isDisposed()) {
            mResetTempDisposed.dispose();
        }
        connectStatus.set(3);
        currentTemp.set(0);
        algorithmTemp.set(0);
        originalTemp.set(0);
        highestTemp.set(0);
        deviceId.set("");
        reportId.set("");
        saveReport.set(null);
        isAddingReport = false;
        //取消订阅
        if (!getConnectorManager().isMQTTConnect()) {
            MQTTShareManager.getInstance().unsubscribe(getShareTopic());
        }
        stopAlgorithmGestureTimer();
    }

    /**
     * 设备是否连接
     */
    public boolean isConnected() {
        return connectStatus.get() == 2;
    }

    /**
     * 设备是否连接中
     */
    public boolean isConnecting() {
        return connectStatus.get() == 1;
    }

    /**
     * 设备是否自动断开连接
     */
    public boolean isDisconnect() {
        return connectStatus.get() == 0;
    }

    /**
     * 设备是否手动断开连接
     */
    public boolean isManualDisconnect() {
        return connectStatus.get() == 3;
    }

    /**
     * 获取测量时间
     */
    public long getMeasureTime() {
        if (getAllTemps().size() <= 0) {
            return 0;
        }
        return System.currentTimeMillis() - getAllTemps().get(0).getTime();
    }

    /**
     * 是否是p02设备
     */
    public boolean isP02() {
        return measureInfo.get().getDevice().getDeviceType() == DeviceType.P02;
    }

    public ConnectionType getConnectType() {
        return getConnectorManager().getConnectionType();
    }

    /**
     * 是否是共享测量
     */
    public boolean isShare() {
        return false;
    }

    private String getShareTopic() {
        if (TextUtils.isEmpty(patchMacaddress.get())) {
            return "";
        }
        return Utils.getShareTopic(patchMacaddress.get());
    }

    /**
     * 打开温馨提示对话框
     */
    public void openRemindTip() {
        TipFragment tipFragment = new TipFragment();
        tipFragment.show(((Activity) getContext()).getFragmentManager(), "tip");
    }

    public void setIsBeforeMeasure(boolean isBeforeMeasure) {
        this.isBeforeMeasure = isBeforeMeasure;
    }

    /**
     * 获取底座算法版本号
     */
    public void getDockerAlgorithmVersion(String dockerMac) {
        MeasureCenter.getDockerAlgorithmVersion(dockerMac, new NetCallBack<String>() {
            @Override
            public void noNet() {
                super.noNet();
            }

            @Override
            public void onSucceed(String json) {
                dockerAlgVersion = JSONUtils.getString(json, "algVersion");
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                Logger.w(resultPair.getData());
            }
        });
    }

}

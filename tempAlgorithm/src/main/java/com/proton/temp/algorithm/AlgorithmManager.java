package com.proton.temp.algorithm;

import android.text.TextUtils;

import com.proton.temp.algorithm.bean.AlgorithmData;
import com.proton.temp.algorithm.bean.TempImg;
import com.proton.temp.algorithm.bean.TempResult;
import com.proton.temp.algorithm.interfaces.AlgorithmListener;
import com.proton.temp.algorithm.interfaces.IAlgorithm;
import com.proton.temp.algorithm.utils.AlgorithmHelper;
import com.proton.temp.algorithm.utils.ProtonAlgorithm;
import com.wms.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangmengsi on 2017/7/21.
 * 算法管理类，不同固件用不同算法
 */
public class AlgorithmManager {
    private static HashMap<String, AlgorithmManager> mAlgorithmManager = new HashMap<>();
    private static String version;
    /**
     * 唯一标志
     */
    private String id;
    /**
     * 算法结果
     */
    private TempImg lastTempImg = new TempImg();
    private TempResult tempResult = new TempResult();
    /**
     * 预测算法处理器
     */
    private IAlgorithm algorithm = new ProtonAlgorithm();
    /**
     * 体温数据
     */
    private List<Float> mTempList = new ArrayList<>();
    private AlgorithmListener mAlgorithmListener;
    /**
     * 0---开始测量，1—测量中，2—结束测量
     */
    private int algorithmStatus;
    /**
     * 是否服用药物
     */
    private boolean isPill;
    private AlgorithmData algorithmData;

    static {
        //初始化日志
        Logger.newBuilder()
                .tag("temp_algorithm")
                .showThreadInfo(false)
                .methodCount(1)
                .saveFile(BuildConfig.DEBUG)
                .isDebug(BuildConfig.DEBUG)
                .build();
    }

    private AlgorithmManager(String id) {
        this.id = id;
    }

    /**
     * id只要是唯一标志就ok，设备mac地址等
     */
    public static AlgorithmManager getInstance(String id) {
        if (TextUtils.isEmpty(id)) {
            throw new IllegalStateException("id is null");
        }
        if (!mAlgorithmManager.containsKey(id)) {
            mAlgorithmManager.put(id, new AlgorithmManager(id));
        }
        return mAlgorithmManager.get(id);
    }

    public static String getVersion() {
        if (TextUtils.isEmpty(version)) {
            version = AlgorithmHelper.getVersion();
        }
        return version;
    }

    /**
     * 处理算法数据v1.0版本
     */
    public void doAlgorithm1_0(float currentTemp) {
        mTempList.add(currentTemp);
        Logger.w("算法处理前温度:", currentTemp);
        lastTempImg = algorithm.getTemp(mTempList, lastTempImg);
        if (lastTempImg != null) {
            currentTemp = lastTempImg.getCurrentTemp();
            doCallback(currentTemp, lastTempImg.getStatus(), 4, 0, -1);
            Logger.w("算法处理后温度:", currentTemp, ",id = ", id);
        }
    }

    /**
     * 处理算法数据v1.5版本
     *
     * @param currentTemp    当前温度
     * @param time           当前温度时间
     * @param sample         采样率
     * @param connectionType 连接方式 0 蓝牙 1广播
     */
    public void doAlgorithm1_5(float currentTemp, long time, int sample, int connectionType) {
        if (algorithmStatus == 2) {
            algorithmStatus = 0;
        }
        tempResult = algorithm.getTemp(currentTemp, time, getFlag(), algorithmStatus, isPill ? 1 : 0, sample, connectionType);
        //测量状态改为正在测量
        algorithmStatus = 1;
        if (tempResult != null) {
            currentTemp = tempResult.getCurrentTemp();
            doCallback(currentTemp, tempResult.getStatus(), tempResult.getSample(), tempResult.getPercent(), tempResult.getGesture());
        }
    }

    private void doCallback(float currentTemp, int measureStatus, int sample, int percent, int gesture) {
        if (currentTemp <= 0) return;
        if (mAlgorithmListener != null) {
            algorithmData = new AlgorithmData(currentTemp, measureStatus, sample, percent, gesture);
            mAlgorithmListener.onComplete(algorithmData);
        }
    }

    public AlgorithmListener getAlgorithmListener() {
        return mAlgorithmListener;
    }

    public void setAlgorithmListener(AlgorithmListener algorithmListener) {
        mAlgorithmListener = algorithmListener;
    }

    public AlgorithmManager setPill(boolean pill) {
        isPill = pill;
        return this;
    }

    public AlgorithmManager setAlgorithmStatus(int status) {
        this.algorithmStatus = status;
        algorithm.getTemp(-1, -1, getFlag(), status, 0, 1, -1);
        return this;
    }

    public void closeAlgorithm() {
        setAlgorithmStatus(2);
        if (mAlgorithmManager.containsKey(id)) {
            mAlgorithmManager.remove(id);
            mAlgorithmListener = null;
        }
    }

    /**
     * 算法所需的唯一标志
     */
    private int getFlag() {
        return id.hashCode();
    }
}

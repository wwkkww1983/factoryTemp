package com.proton.carepatchtemp.factory.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.proton.carepatchtemp.factory.bean.CalibrateBean;
import com.proton.carepatchtemp.factory.bean.CalibrateDetailBean;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.DeviceType;

import java.math.BigDecimal;

/**
 * 校准值计算工具类
 */
public class CalibrateUtil {
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 允许误差范围±0.1
     */
    private static float errorRange = 0.1f;

    /**
     * 无效校准值用于返回，随意设置的一个比较大的值
     */
    public static float INVALID_T = 20;

    /**
     * 设置误差范围
     *
     * @param errorRange
     */
    public void setErrorRange(float errorRange) {
        this.errorRange = errorRange;
    }

    /**
     * 获取第一次校准值
     *
     * @param waterTemp  水槽温度
     * @param stableTemp 体温贴稳定后显示温度
     * @return
     */
    public static float getTL(float waterTemp, float stableTemp) {
        return waterTemp - stableTemp - errorRange;
    }

    /**
     * 获取第二次校准值
     *
     * @param waterTemp  水槽温度
     * @param stableTemp 体温贴稳定后显示温度
     * @return
     */
    public static float getTH(float waterTemp, float stableTemp) {
        return waterTemp - stableTemp + errorRange;
    }

    /**
     * 最终校准值
     *
     * @param TL1
     * @param TH1
     * @param TL2
     * @param TH2
     * @return
     */
    public static float getT(float TL1, float TH1, float TL2, float TH2) {
        float TL = Math.max(TL1, TL2);
        float TH = Math.min(TH1, TH2);
        if (TL > TH) {
            return INVALID_T;
        } else {
            float t = TL + (TH - TL) / 2;
            BigDecimal b = new BigDecimal(t);
            float value = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            return value;
        }
    }

    /**
     * 获取请求对象，如果直接使用CalibrateBean这个上传可能出现很多无用的参数，因为CalibrateBean继承自LitePalSupport
     *
     * @param calibrateBean
     * @return
     */
    public static CalibrateDetailBean getCalibrateDetail(CalibrateBean calibrateBean) {
        CalibrateDetailBean detailBean = new CalibrateDetailBean();
        detailBean.setMac(calibrateBean.getMac());
        detailBean.setSn(calibrateBean.getSn());
        detailBean.setVersion(calibrateBean.getVersion());
        if (TextUtils.isEmpty(calibrateBean.getType())) {
            detailBean.setType(DeviceType.getDeviceTypeByTypeDesc(calibrateBean.getType()));
        }else {
            detailBean.setType(DeviceType.getDeviceTypeByTypeDesc(calibrateBean.getType()));
        }
        detailBean.setFirstStableState(calibrateBean.getFirstStableState());
        detailBean.setFirstStableTemp(calibrateBean.getFirstStableTemp());
        detailBean.setFirstSinkTemp(calibrateBean.getFirstSinkTemp());
        detailBean.setFirstAllowableError(calibrateBean.getFirstAllowableError());

        detailBean.setSecondStableState(calibrateBean.getSecondStableState());
        detailBean.setSecondStableTemp(calibrateBean.getSecondStableTemp());
        detailBean.setSecondSinkTemp(calibrateBean.getSecondSinkTemp());
        detailBean.setSecondAllowableError(calibrateBean.getSecondAllowableError());

        detailBean.setCalibrationStatus(calibrateBean.getCalibrationStatus());
        detailBean.setCalibration(calibrateBean.getCalibration());
        detailBean.setQualificationStatus(calibrateBean.getQualificationStatus());
        detailBean.setReportId(calibrateBean.getReportId());
        return detailBean;
    }

    /**
     * 判断体温贴是否合格
     */
    public static boolean judgeQualification(String mac) {
        MeasureViewModel measureViewModel = Utils.getMeasureViewmodel(mac);
        if (measureViewModel.calibrationStatus.get() != 2) {
            return false;
        }
        float currentError = measureViewModel.originalTemp.get() - measureViewModel.secondSinkTemp.get();
        if (Math.abs(currentError) > measureViewModel.secondAllowableError.get()) {
            return false;
        } else {
            return true;
        }
    }

}

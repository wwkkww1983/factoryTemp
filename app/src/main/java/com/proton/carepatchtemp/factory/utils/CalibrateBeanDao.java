package com.proton.carepatchtemp.factory.utils;

import android.arch.lifecycle.ViewModel;

import com.proton.carepatchtemp.factory.bean.CalibrateBean;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.DeviceType;
import com.wms.ble.utils.Logger;

import org.litepal.LitePal;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CalibrateBeanDao {

    /**
     * 保存第一个水槽的相关数据
     */
    public static void saveMonitor1Data() {
        Map<String, ViewModel> allMeasureViewModel = Utils.getAllMeasureViewModel();
        Set<Map.Entry<String, ViewModel>> entries = allMeasureViewModel.entrySet();
        Iterator<Map.Entry<String, ViewModel>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            MeasureViewModel measureViewModel = (MeasureViewModel) iterator.next().getValue();
            CalibrateBean calibrateBean = LitePal.where("mac = ? ", measureViewModel.patchMacaddress.get()).findFirst(CalibrateBean.class);
            if (calibrateBean == null) {
                calibrateBean = new CalibrateBean();
            }
            calibrateBean.setReportId(measureViewModel.reportId.get());
            calibrateBean.setMac(measureViewModel.patchMacaddress.get());
            calibrateBean.setSn(measureViewModel.serialNumber.get());
            calibrateBean.setVersion(measureViewModel.hardVersion.get());
            calibrateBean.setType(DeviceType.getDeviceTypeBroadcast(measureViewModel.measureInfo.get().getDevice().getDeviceType()));

            calibrateBean.setFirstStableState(measureViewModel.firstStableState.get());
            calibrateBean.setFirstSinkTemp(toInterger(measureViewModel.firstSinkTemp.get()));
            calibrateBean.setFirstStableTemp(toInterger(measureViewModel.originalTemp.get()));
            calibrateBean.save();
        }
    }


    /**
     * 保存第二个水槽的相关数据
     */
    public static void saveMonitor2Data() {
        Map<String, ViewModel> allMeasureViewModel = Utils.getAllMeasureViewModel();
        Set<Map.Entry<String, ViewModel>> entries = allMeasureViewModel.entrySet();
        Iterator<Map.Entry<String, ViewModel>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            MeasureViewModel measureViewModel = (MeasureViewModel) iterator.next().getValue();
            CalibrateBean calibrateBean = LitePal.where("mac = ? ", measureViewModel.patchMacaddress.get()).findFirst(CalibrateBean.class);
            if (calibrateBean == null) {
                calibrateBean = new CalibrateBean();
                calibrateBean.setMac(measureViewModel.patchMacaddress.get());
            }
            calibrateBean.setSecondStableState(measureViewModel.secondStableState.get());
            calibrateBean.setSecondStableTemp(toInterger(measureViewModel.originalTemp.get()));
            calibrateBean.setSecondSinkTemp(toInterger(measureViewModel.secondSinkTemp.get()));
            calibrateBean.save();
        }
    }

    /**
     * 保存校准值
     *
     * @param mac               体温贴mac
     * @param calibrationStatus 校准状态
     */
    public static void saveCalibrationValue(String mac, int calibrationStatus, float calibration) {
        CalibrateBean calibrateBean = LitePal.where("mac = ? ", mac).findFirst(CalibrateBean.class);
        calibrateBean.setCalibrationStatus(calibrationStatus);
        calibrateBean.setCalibration(toInterger(calibration));
        calibrateBean.save();
    }

    /**
     * 保存合格状态
     */
    public static void saveQualificationStatus(String mac, int qualificationStatus) {
        CalibrateBean calibrateBean = LitePal.where("mac = ? ", mac).findFirst(CalibrateBean.class);
        if (qualificationStatus == calibrateBean.getQualificationStatus()) {
            Logger.w("合格状态未发生改变，不需要重复存储");
            return;
        }
        calibrateBean.setQualificationStatus(qualificationStatus);
        calibrateBean.save();
    }

    /**
     * 保存误差范围
     *
     * @param mac
     * @param firstAllowableError
     * @param secondAllowableError
     */
    public static void saveAllowableError(String mac, float firstAllowableError, float secondAllowableError) {
        CalibrateBean calibrateBean = LitePal.where("mac = ? ", mac).findFirst(CalibrateBean.class);
        calibrateBean.setFirstAllowableError(toInterger(firstAllowableError));
        calibrateBean.setSecondAllowableError(toInterger(secondAllowableError));
        calibrateBean.save();
    }

    /**
     * 删除指定数据库数据
     * @param mac
     */
    public static void deleteMonitorLitepalData(String mac){
        LitePal.deleteAll(CalibrateBean.class,"mac = ?",mac);
    }


    /**
     * 将float扩大100倍，转为int类型
     *
     * @param t
     * @return
     */
    public static Integer toInterger(float t) {
        int value = (int) (t * 100);
        return value;
    }

}

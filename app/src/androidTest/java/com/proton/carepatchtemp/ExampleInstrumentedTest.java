package com.proton.carepatchtemp;

import android.content.Context;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.proton.carepatchtemp.utils.StatusBarUtil;
import com.wms.logger.Logger;
import com.wms.utils.DensityUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        Logger.w("test:", appContext.getFilesDir().getAbsolutePath());
        //HUAWEI
        Logger.w("手机型号:", Build.MANUFACTURER, "--", Build.MODEL);
    }

    @Test
    public void testBuglyAppid() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        String appId = appContext.getString(R.string.buglyAppid);
        if (appId.equals("fef8b1ea78")) {
            Logger.w("国际版");
        } else if (appId.equals("847567d215")) {
            Logger.w("国内debug");
        } else if (appId.equals("ae87846642")) {
            Logger.w("国内release");
        }
    }

    @Test
    public void getStatusHeight() {
        int statusHeight = -1;
        try {
            Context appContext = InstrumentationRegistry.getTargetContext();
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = appContext.getResources().getDimensionPixelSize(height);
            System.out.println("statusHeight:" + StatusBarUtil.getStatusBarHeight());
            System.out.println("statusHeight:" + DensityUtils.dip2px(appContext, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
//        String json = FileUtils.readFile("/sdcard/temp.json", "UTF-8").toString();
//        String temp = JSONUtils.getString(json, "rawData");
//        String sample = JSONUtils.getString(json, "sample");
//        String[] temps = temp.substring(1, temp.length() - 1).split(",");
//        String[] samples = sample.substring(1, sample.length() - 1).split(",");
//
//        int size = temps.length;
//
//        AlgorithmManager manager = AlgorithmManager.getInstance("111");
//        StringBuilder result = new StringBuilder();
//        manager.setAlgorithmListener(new AlgorithmListener() {
//            int count = 0;
//
//            @Override
//            public void onComplete(float processTemp, int measureStatus, int sample) {
//                super.onComplete(processTemp, measureStatus, sample);
//                count++;
//                result.append(processTemp).append(",");
//                if (count == size) {
//                    Logger.w("算法:" + result.toString());
//                }
//            }
//        });
//        manager.setAlgorithmStatus(0);
//        for (int i = 0; i < temps.length; i++) {
//            manager.doAlgorithm1_5(Float.parseFloat(temps[i]), Integer.parseInt(samples[i]));
//        }
//        manager.setAlgorithmStatus(2);
    }
}

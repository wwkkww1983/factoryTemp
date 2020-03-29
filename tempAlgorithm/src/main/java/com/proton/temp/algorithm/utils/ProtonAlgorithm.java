package com.proton.temp.algorithm.utils;

import com.proton.temp.algorithm.bean.TempImg;
import com.proton.temp.algorithm.bean.TempResult;
import com.proton.temp.algorithm.interfaces.IAlgorithm;

import java.util.List;

/**
 * Created by wangmengsi on 2018/3/23.
 */

public class ProtonAlgorithm implements IAlgorithm {
    @Override
    public TempImg getTemp(List<Float> temps, TempImg tempImg) {
        float tempsArr[] = new float[temps.size()];
        for (int i = 0; i < temps.size(); i++) {
            tempsArr[i] = temps.get(i);
        }
        return AlgorithmHelper.getTempOld(tempsArr, tempImg);
    }

    @Override
    public TempResult getTemp(float temp, long time, int flag, int state, int isPill, int sample, int connectionType) {
        return AlgorithmHelper.getTemp(temp, time, flag, state, isPill, sample, connectionType);
    }
}

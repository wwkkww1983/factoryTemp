package com.proton.carepatchtemp.viewmodel.managecenter;

import android.databinding.Observable;
import android.databinding.ObservableField;

import com.proton.carepatchtemp.constant.AppConfigs;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.SpUtils;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;

/**
 * Created by luochune on 2018/3/12.
 * 管理中心数据获取
 */

public class ManageCenterViewModel extends BaseViewModel {
    public ObservableField<Boolean> isCTempChoose = new ObservableField<>(false);
    public ObservableField<Boolean> isFTempChoose = new ObservableField<>(false);


    public void addTempChangeChoose() {
        isCTempChoose.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (isCTempChoose.get()) {
                    SpUtils.saveInt(AppConfigs.SP_KEY_TEMP_UNIT, AppConfigs.SP_VALUE_TEMP_C);
                }
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_UNIT));
            }
        });
        isFTempChoose.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (isFTempChoose.get()) {
                    SpUtils.saveInt(AppConfigs.SP_KEY_TEMP_UNIT, AppConfigs.SP_VALUE_TEMP_F);
                }
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.SWITCH_UNIT));
            }
        });

    }

}

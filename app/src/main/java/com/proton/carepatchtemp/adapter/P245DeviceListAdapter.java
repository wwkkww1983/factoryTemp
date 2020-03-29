package com.proton.carepatchtemp.adapter;

import android.content.Context;

import com.proton.carepatchtemp.net.bean.DeviceBean;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import java.util.List;

/**
 * Created by luochune on 2018/3/29.
 * p245设备适配器
 */

public class P245DeviceListAdapter extends CommonAdapter<DeviceBean> {
    public P245DeviceListAdapter(Context context, List<DeviceBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, DeviceBean deviceManageListBean) {

    }
}

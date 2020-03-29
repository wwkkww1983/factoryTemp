package com.proton.carepatchtemp.view;

import android.view.View;

import com.wms.adapter.CommonViewHolder;

/**
 * Created by luochune on 2018/3/22.
 */

public interface OnItemChildClickListener<T> {
    void onChildClick(CommonViewHolder holder, T bean, View view);
}

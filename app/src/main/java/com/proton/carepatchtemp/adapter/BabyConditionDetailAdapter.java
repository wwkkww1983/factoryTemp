package com.proton.carepatchtemp.adapter;

import android.content.Context;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import java.util.List;

/**
 * Created by luochune on 2018/4/20.
 * 宝宝症状详解适配类
 */

public class BabyConditionDetailAdapter extends CommonAdapter<String> {
    public BabyConditionDetailAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, String babyConditionStr) {
        //症状
        ((TextView) holder.getView(R.id.id_tv_babyCondition)).setText(babyConditionStr);
        TextView conditionDetailTv = holder.getView(R.id.id_tv_babyConditionDes);
        //症状详解
        if (babyConditionStr.equals(getString(R.string.string_hc))) {
            //寒颤
            conditionDetailTv.setText(R.string.string_hc_measure);
        } else if (babyConditionStr.equals(getString(R.string.string_szmsbl))) {
            //四肢末梢冰凉
            conditionDetailTv.setText(R.string.string_szmsbl_measure);
        } else if (babyConditionStr.equals(getString(R.string.string_yht))) {
            //咽喉疼
            conditionDetailTv.setText(R.string.string_yht_measure);
        } else if (babyConditionStr.equals(getString(R.string.string_tt))) {
            //头疼
            conditionDetailTv.setText(R.string.string_tt_measure);
        } else if (babyConditionStr.equals(getString(R.string.string_cough))) {
            //咳嗽
            conditionDetailTv.setText(R.string.string_cough_measure);
        } else if (babyConditionStr.equals(getString(R.string.string_pz))) {
            //皮疹
            conditionDetailTv.setText(R.string.string_pz_measure);
        } else if (babyConditionStr.equals(getString(R.string.string_ot))) {
            //呕吐
            conditionDetailTv.setText(R.string.string_ot_measure);
        } else if (babyConditionStr.equals(getString(R.string.string_fx))) {
            //腹泻
            conditionDetailTv.setText(R.string.string_fx_measure);
        } else if (babyConditionStr.equals(getString(R.string.string_hxjc))) {
            //呼吸急促
            conditionDetailTv.setText(R.string.string_hxjc_measure);
        } else if (babyConditionStr.equals(getString(R.string.string_jswm))) {
            //精神萎靡
            conditionDetailTv.setText(R.string.string_jswm_measure);
        } else if (babyConditionStr.equals(R.string.string_szcc)) {
            //四肢抽搐
            conditionDetailTv.setText(R.string.string_szcc_measure);
        }
    }

    private String getString(int resId) {
        return App.get().getResources().getString(resId);
    }
}

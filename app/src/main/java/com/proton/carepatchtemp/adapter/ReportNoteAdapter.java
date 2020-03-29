package com.proton.carepatchtemp.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.common.AdapterChildClickListener;
import com.proton.carepatchtemp.net.bean.NoteBean;
import com.proton.carepatchtemp.utils.DateUtils;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import java.util.List;

/**
 * Created by luochune on 2018/3/25.
 */

public class ReportNoteAdapter extends CommonAdapter<NoteBean> {

    private AdapterChildClickListener adapterChildClickListener;

    public ReportNoteAdapter(Context context, List<NoteBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, NoteBean noteBean) {
        if (noteBean == null) {
            return;
        }
        ImageView noteTypeIv = holder.getView(R.id.id_iv_note_type);
        switch (noteBean.getType()) {
            case 1:
                //服用药物
                noteTypeIv.setImageResource(R.drawable.img_notes_drug);
                break;
            case 2:
                //物理治疗
                noteTypeIv.setImageResource(R.drawable.img_notes_physical_therapy);
                break;
            case 3:
                //宝宝状态
                noteTypeIv.setImageResource(R.drawable.img_notes_physical_condition);
                break;
            case 7:
                //备注
                noteTypeIv.setImageResource(R.drawable.img_notes_remark);
                break;
        }
        //添加时间
        ((TextView) holder.getView(R.id.id_tv_note_time)).setText(DateUtils.dateStrToYMDHM(noteBean.getCreated()));
        //添加内容
        ((TextView) holder.getView(R.id.id_tv_note_content)).setText(noteBean.getContent());
        //滑动删除一条记录
        holder.getView(R.id.id_iv_delete_note).setOnClickListener(v -> {
            if (adapterChildClickListener != null) {
                adapterChildClickListener.onClick(holder, v);
            }
        });
    }

    public void setAdapterChildClickListener(AdapterChildClickListener adapterChildClickListener) {
        this.adapterChildClickListener = adapterChildClickListener;
    }

    private List<NoteBean> getNoteList() {
        return mDatas;
    }
}

package com.proton.carepatchtemp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.common.AdapterChildClickListener;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.bean.MessageBean;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import java.util.List;

/**
 * Created by luochune on 2018/4/3.
 */

public class MsgListAdapter extends CommonAdapter<MessageBean> {
    private AdapterChildClickListener adapterChildClickListener;
    private boolean isEdit = false;//是否编辑

    public MsgListAdapter(Context context, List<MessageBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, MessageBean msgBean) {
       /* holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a="";
            }
        });*/
        //是否批量删除
        if (isEdit) {
            ((SwipeMenuLayout) holder.getView(R.id.id_swipeLayout)).setSwipeEnable(false);
            holder.getView(R.id.id_iv_msg_choose).setVisibility(View.VISIBLE);
            if (msgBean.isChecked()) {
                ((ImageView) holder.getView(R.id.id_iv_msg_choose)).setImageDrawable(ContextCompat.getDrawable(App.get(), R.drawable.icon_rb_checked));
            } else {
                ((ImageView) holder.getView(R.id.id_iv_msg_choose)).setImageDrawable(ContextCompat.getDrawable(App.get(), R.drawable.icon_rb_unchecked));
            }
               /*   holder.getView(R.id.id_iv_msg_choose).setOnClickListener(v -> {
                        if(adapterChildClickListener!=null)
                        {
                            adapterChildClickListener.onClick(holder,v);
                        }
                    });*/
        } else {
            //展示列表
            ((SwipeMenuLayout) holder.getView(R.id.id_swipeLayout)).setSwipeEnable(true);
            holder.getView(R.id.id_iv_msg_choose).setVisibility(View.GONE);
        }
        //消息内容
        ((TextView) holder.getView(R.id.tv_msg_content)).setText(msgBean.getContents());
        //消息时间
        ((TextView) holder.getView(R.id.tv_msg_time)).setText(msgBean.getTime());
        //消息标题
        ((TextView) holder.getView(R.id.tv_msg_title)).setText(msgBean.getTitle());
        holder.getView(R.id.id_tv_delete).setOnClickListener(v -> {
            if (adapterChildClickListener != null) {
                adapterChildClickListener.onClick(holder, v);
            }
        });
        //消息内容点击事件(编辑状态才能点击)

        holder.getView(R.id.id_msg_content).setOnClickListener(v -> {
            if (adapterChildClickListener != null) {
                adapterChildClickListener.onClick(holder, v);
            }
        });
   /*     {
            if (adapterChildClickListener != null) {
                adapterChildClickListener.onClick(holder, v);
            }
        });
        ((SwipeMenuLayout)holder.getView(R.id.id_swipeLayout)).setOnClickListener(v->
        {
            int a=-1;

        });
        holder.getConvertView().setOnClickListener(v->
        {
            String a="";
        });*/
    }

    public void setAdapterChildClickListener(AdapterChildClickListener adapterChildClickListener) {
        this.adapterChildClickListener = adapterChildClickListener;
    }

    /**
     * 列表是否处于编辑状态
     */
    public void setEdit(boolean edit) {
        isEdit = edit;
        notifyDataSetChanged();
    }
}

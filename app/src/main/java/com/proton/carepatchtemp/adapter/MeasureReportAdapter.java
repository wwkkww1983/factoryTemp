package com.proton.carepatchtemp.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.bean.ReportListItemBean;
import com.proton.carepatchtemp.utils.FormatUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import java.util.List;

/**
 * Created by luochune on 2018/3/25.
 */

public class MeasureReportAdapter extends CommonAdapter<ReportListItemBean> {

    private boolean isEditMeasureReport = false;
    private AdapterChildClickListener adapterChildClickListener;

    public MeasureReportAdapter(Context context, List<ReportListItemBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, ReportListItemBean reportListItemBean) {
        if (reportListItemBean == null) {
            return;
        }

        if (reportListItemBean.getFilepath().startsWith("http")) {
            holder.getView(R.id.id_not_upload).setVisibility(View.GONE);
        } else {
            holder.getView(R.id.id_not_upload).setVisibility(View.VISIBLE);
        }

        //体温单位
        ((TextView) holder.getView(R.id.id_tv_temp_unit)).setText(Utils.getTempUnit());
        //是否收藏
        if (reportListItemBean.isCollect()) {
            //收藏
            holder.getView(R.id.id_iv_report_collect).setVisibility(View.VISIBLE);
        } else {
            //未收藏
            holder.getView(R.id.id_iv_report_collect).setVisibility(View.GONE);
        }
        //编辑测量报告
        if (isEditMeasureReport) {
            //  holder.mSwipeMenuLayout.setSwipeEnable(false);
            ImageView chooseIv = holder.getView(R.id.id_iv_report_choose);
            chooseIv.setVisibility(View.VISIBLE);
            if (reportListItemBean.isChecked()) {
                //选中
                chooseIv.setImageDrawable(ContextCompat.getDrawable(App.get(), R.drawable.icon_rb_checked));
            } else {
                //取消选中
                chooseIv.setImageDrawable(ContextCompat.getDrawable(App.get(), R.drawable.icon_rb_unchecked));
            }
        } else {
            /*TODO   holder.mSwipeMenuLayout.setSwipeEnable(true);
             */
            holder.getView(R.id.id_iv_report_choose).setVisibility(View.GONE);
        }
        //报告测试时间
        ((TextView) holder.getView(R.id.id_tv_report_sum_time)).setText(FormatUtils.formatTime(reportListItemBean.getEndtime() - reportListItemBean.getStarttime()));
        //头像
        ((SimpleDraweeView) holder.getView(R.id.id_sdv_report_avatar)).setImageURI(reportListItemBean.getProfileavatar());
        // 报告距离现在经过的时间(1.5以结束时间为准)
        ((TextView) holder.getView(R.id.id_tv_report_time)).setText(FormatUtils.getTimeElapse(reportListItemBean.getEndtime()));
        //宝宝姓名
        ((TextView) holder.getView(R.id.id_tv_report_babyname)).setText(reportListItemBean.getProfilename());
        //温度范围
        //在显示范围内再显示
        TextView temLevelTv = holder.getView(R.id.id_btn_report_temparea);
        TextView highestTempTv = holder.getView(R.id.id_tv_highest_temp);
        float maxTempFloat = -1;
        if (reportListItemBean.getData() != null) {
            maxTempFloat = Float.parseFloat(reportListItemBean.getData().getTemp_max());
        }
        //最高体温赋值
        highestTempTv.setText(UIUtils.currMaxTemp(maxTempFloat));
        //最高体温根据单位转换
        int tempLevel = UIUtils.tempLevel(maxTempFloat);
        switch (tempLevel) {
            case 0:
                //正常体温
                temLevelTv.setText(R.string.string_normal);
                temLevelTv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_report_normal_temp));
                break;
            case 1:
                //低热
                temLevelTv.setText(R.string.string_low_fever);
                temLevelTv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_report_low_temp));
                break;
            case 2:
                //中热
                temLevelTv.setText(R.string.string_middle_fever);
                temLevelTv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_report_middle_temp));
                break;
            case 3:
                //高热
                temLevelTv.setText(R.string.string_high_fever);
                temLevelTv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_report_high_temp));
                break;
            default:
                //非正常体温
                temLevelTv.setText(R.string.string_not_normal_temp);
                temLevelTv.setBackground(ContextCompat.getDrawable(mContext, R.drawable.shape_report_unusual_temp));
                break;
        }

        /**
         * 收藏和分享操作
         */
        holder.getView(R.id.id_iv_more_operationgs).setOnClickListener(v -> {
            if (adapterChildClickListener != null) {
                adapterChildClickListener.onAdapterChildClick(v, holder, reportListItemBean);
            }
        });
        //item点击事件
        holder.getView(R.id.id_lay_report_content).setOnClickListener(v -> {
            if (adapterChildClickListener != null) {
                adapterChildClickListener.onAdapterChildClick(v, holder, reportListItemBean);
            }
        });
        //删除
        holder.getView(R.id.id_tv_delete).setOnClickListener(v -> {
            if (adapterChildClickListener != null) {
                adapterChildClickListener.onAdapterChildClick(v, holder, reportListItemBean);
            }
        });
    }

    public void setAdapterChildClickListener(AdapterChildClickListener adapterChildClickListener) {
        this.adapterChildClickListener = adapterChildClickListener;
    }

    public boolean isEditMeasureReport() {
        return isEditMeasureReport;
    }

    public void setEditMeasureReport(boolean editMeasureReport) {
        isEditMeasureReport = editMeasureReport;
        notifyDataSetChanged();
    }

    public List<ReportListItemBean> getReportList() {
        return mDatas;
    }

    public interface AdapterChildClickListener {
        void onAdapterChildClick(View view, CommonViewHolder commonViewHolder, ReportListItemBean reportListItemBean);
    }
}

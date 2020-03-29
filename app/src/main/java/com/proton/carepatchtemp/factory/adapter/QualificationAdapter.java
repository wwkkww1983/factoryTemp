package com.proton.carepatchtemp.factory.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.factory.utils.CalibrateBeanDao;
import com.proton.carepatchtemp.factory.utils.CalibrateUtil;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.DeviceType;

import java.util.List;

public class QualificationAdapter extends RecyclerView.Adapter<QualificationAdapter.VH> {
    private Context context;
    private List<MeasureViewModel> datum;

    public QualificationAdapter(Context context, List<MeasureViewModel> datum) {
        this.context = context;
        this.datum = datum;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.item_factory_qualification_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {

        MeasureViewModel measureViewModel = datum.get(position);
        holder.txtCurrentTemp.setText(Utils.formatTempToStr(measureViewModel.originalTemp.get()) + "");
        float TL1 = CalibrateUtil.getTL(measureViewModel.firstSinkTemp.get(), measureViewModel.firstStableTemp.get());
        float TL2 = CalibrateUtil.getTL(measureViewModel.secondSinkTemp.get(), measureViewModel.secondStableTemp.get());
        float TH1 = CalibrateUtil.getTH(measureViewModel.firstSinkTemp.get(), measureViewModel.firstStableTemp.get());
        float TH2 = CalibrateUtil.getTH(measureViewModel.secondSinkTemp.get(), measureViewModel.secondStableTemp.get());
        float t = CalibrateUtil.getT(TL1, TH1, TL2, TH2);
        if (measureViewModel.calibrationStatus.get() == 1) {
            holder.txtQualificationStatus.setText("已校准");
            switchStableTextView(holder.txtQualificationStatus, true);
        } else {
            holder.txtQualificationStatus.setText("未校准");
            switchStableTextView(holder.txtQualificationStatus, false);
        }
        holder.txtDeviceType.setText(DeviceType.getDeviceTypeBroadcast(measureViewModel.measureInfo.get().getDevice().getDeviceType()) + ",");
        holder.txtMac.setText(String.format("MAC:%s", measureViewModel.patchMacaddress.get()));
        holder.txtSn.setText(String.format("SN:%s ,", measureViewModel.serialNumber.get()));
        holder.txtVer.setText(String.format("VER:%s", measureViewModel.hardVersion.get()));
        holder.txtAdditionalData1.setText(String.format("测温点1：%s℃(槽%s℃)", measureViewModel.firstStableTemp.get(), measureViewModel.firstSinkTemp.get()));
        holder.txtAdditionalData2.setText(String.format("测温点2：%s℃(槽%s℃)", measureViewModel.secondStableTemp.get(), measureViewModel.secondSinkTemp.get()));

        switchCalibrateSelect(measureViewModel, holder.txtQualified, holder.txtNotQualified, CalibrateUtil.judgeQualification(measureViewModel.patchMacaddress.get()));

        holder.txtQualified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCalibrateSelect(measureViewModel, holder.txtQualified, holder.txtNotQualified, true);
                measureViewModel.qualificationStatus.set(1);
                CalibrateBeanDao.saveQualificationStatus(measureViewModel.patchMacaddress.get(), measureViewModel.qualificationStatus.get());
            }
        });

        holder.txtNotQualified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                measureViewModel.qualificationStatus.set(2);
                switchCalibrateSelect(measureViewModel, holder.txtQualified, holder.txtNotQualified, false);
                CalibrateBeanDao.saveQualificationStatus(measureViewModel.patchMacaddress.get(), measureViewModel.qualificationStatus.get());
            }
        });

        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeCallBack!=null) {
                    closeCallBack.closeCard(measureViewModel.patchMacaddress.get());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == datum ? 0 : datum.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        public ImageView ivClose;
        public TextView txtCurrentTemp, txtDeviceType, txtMac, txtSn, txtVer, txtQualified, txtNotQualified, txtQualificationStatus, txtAdditionalData1, txtAdditionalData2;

        public VH(@NonNull View itemView) {
            super(itemView);
            ivClose = itemView.findViewById(R.id.id_close);
            txtCurrentTemp = itemView.findViewById(R.id.id_current_temp);
            txtDeviceType = itemView.findViewById(R.id.id_device_type);
            txtMac = itemView.findViewById(R.id.id_mac);
            txtSn = itemView.findViewById(R.id.id_sn);
            txtVer = itemView.findViewById(R.id.id_ver);
            txtAdditionalData1 = itemView.findViewById(R.id.id_additional_data1);
            txtAdditionalData2 = itemView.findViewById(R.id.id_additional_data2);
            txtQualified = itemView.findViewById(R.id.id_qualified);
            txtNotQualified = itemView.findViewById(R.id.id_not_qualified);
            txtQualificationStatus = itemView.findViewById(R.id.id_qualification_status);
        }
    }

    private void switchStableTextView(TextView txtQualified, boolean isQualified) {
        if (isQualified) {
            txtQualified.setBackgroundColor(ContextCompat.getColor(context, R.color.color_main));
            txtQualified.setEnabled(false);
            txtQualified.setClickable(false);
            txtQualified.setText("已校准");
            txtQualified.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            txtQualified.setBackgroundResource(R.drawable.add_new_device_bg);
            txtQualified.setEnabled(true);
            txtQualified.setClickable(true);
            txtQualified.setText("无法校准");
            txtQualified.setTextColor(ContextCompat.getColor(context, R.color.color_main));
        }
    }

    /**
     * 切换合格状态
     *
     * @param measureViewModel
     * @param isQualified      是否合格
     */
    private void switchCalibrateSelect(MeasureViewModel measureViewModel, TextView txtQualified, TextView txtNotQualified, boolean isQualified) {
        if (isQualified) {
            txtQualified.setBackgroundResource(R.drawable.solid_main_bg);
            txtQualified.setTextColor(ContextCompat.getColor(context, R.color.white));
            txtNotQualified.setBackgroundResource(R.drawable.stroke_main_bg);
            txtNotQualified.setTextColor(ContextCompat.getColor(context, R.color.color_main));
            measureViewModel.calibrateSelect.set(true);
        } else {
            txtQualified.setBackgroundResource(R.drawable.stroke_main_bg);
            txtQualified.setTextColor(ContextCompat.getColor(context, R.color.color_main));
            txtNotQualified.setBackgroundResource(R.drawable.solid_main_bg);
            txtNotQualified.setTextColor(ContextCompat.getColor(context, R.color.white));
            measureViewModel.calibrateSelect.set(false);
        }
    }

    private ItemClickCallBack closeCallBack;

    public void setCloseCallBack(ItemClickCallBack closeCallBack) {
        this.closeCallBack = closeCallBack;
    }

    public interface ItemClickCallBack {
        /**
         * 点击关闭按钮，关闭卡片
         *
         * @param mac mac地址
         */
        void closeCard(String mac);
    }

}

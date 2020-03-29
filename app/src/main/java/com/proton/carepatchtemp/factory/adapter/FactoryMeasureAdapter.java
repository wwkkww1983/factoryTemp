package com.proton.carepatchtemp.factory.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.factory.bean.CalibrateBean;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.DeviceType;

import org.litepal.LitePal;

import java.util.List;

public class FactoryMeasureAdapter extends RecyclerView.Adapter<FactoryMeasureAdapter.VH> {

    private Context context;
    private List<MeasureViewModel> datum;
    /**
     * 是否是第二次测温
     */
    private boolean isSecondMeasure;

    public FactoryMeasureAdapter(Context context, List<MeasureViewModel> datum) {
        this.context = context;
        this.datum = datum;
    }

    public void setIsSecondMeasure(boolean secondMeasure) {
        isSecondMeasure = secondMeasure;
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.item_factory_measure_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MeasureViewModel measureViewModel = datum.get(position);
        holder.txtCurrentTemp.setText(Utils.formatTempToStr(measureViewModel.originalTemp.get()) + "");
        holder.txtDeviceType.setText(DeviceType.getDeviceTypeBroadcast(measureViewModel.measureInfo.get().getDevice().getDeviceType()) + ",");
        holder.txtMac.setText(String.format("MAC:%s", measureViewModel.patchMacaddress.get()));
        holder.txtSn.setText(String.format("SN:%s ,", measureViewModel.serialNumber.get()));
        holder.txtVersion.setText(String.format("VER:%s", measureViewModel.hardVersion.get()));
        if (isSecondMeasure) {
            switchStableTextView(holder.txtStatus, measureViewModel.secondStableState.get() == 1);
            holder.txtAdditionalData.setText(String.format("测温点1：%s℃(槽%s℃)", measureViewModel.firstStableTemp.get(), measureViewModel.firstSinkTemp.get()));
            holder.txtAdditionalData.setVisibility(View.VISIBLE);

        } else {
            switchStableTextView(holder.txtStatus, measureViewModel.firstStableState.get() == 1);
            holder.txtAdditionalData.setVisibility(View.GONE);
        }

        holder.txtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSecondMeasure) {
                    if (measureViewModel.secondStableState.get() == 1) {
                        return;
                    }
                    measureViewModel.secondStableTemp.set(measureViewModel.originalTemp.get());
                    measureViewModel.secondStableState.set(1);
                    switchStableTextView(holder.txtStatus, true);
                    if (callBack != null) {
                        callBack.statusClickCallBack(position);
                    }
                } else {
                    if (measureViewModel.firstStableState.get() == 1) {
                        return;
                    }
                    measureViewModel.firstStableTemp.set(measureViewModel.originalTemp.get());
                    measureViewModel.firstStableState.set(1);
                    switchStableTextView(holder.txtStatus, true);
                    if (callBack != null) {
                        callBack.statusClickCallBack(position);
                    }
                }
            }
        });

        holder.txtDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.disconnectClickCallBack(position);
                }
            }
        });
    }

    private void switchStableTextView(TextView txtStable, boolean isStable) {
        if (isStable) {
            txtStable.setBackgroundResource(R.drawable.solid_main_bg);
            txtStable.setEnabled(false);
            txtStable.setClickable(false);
            txtStable.setText("稳定");
            txtStable.setTextColor(ContextCompat.getColor(context, R.color.white));
        } else {
            txtStable.setBackgroundResource(R.drawable.stroke_main_bg);
            txtStable.setEnabled(true);
            txtStable.setClickable(true);
            txtStable.setText("未稳定");
            txtStable.setTextColor(ContextCompat.getColor(context, R.color.color_main));
        }
    }

    @Override
    public int getItemCount() {
        return null == datum ? 0 : datum.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        private TextView txtCurrentTemp, txtDeviceType, txtMac, txtSn, txtVersion, txtStatus, txtDisconnect, txtAdditionalData;

        public VH(@NonNull View itemView) {
            super(itemView);
            txtCurrentTemp = itemView.findViewById(R.id.id_current_temp);
            txtDeviceType = itemView.findViewById(R.id.id_device_type);
            txtMac = itemView.findViewById(R.id.id_mac);
            txtSn = itemView.findViewById(R.id.id_sn);
            txtVersion = itemView.findViewById(R.id.id_ver);
            txtStatus = itemView.findViewById(R.id.id_status);
            txtDisconnect = itemView.findViewById(R.id.id_disconnect);
            txtAdditionalData = itemView.findViewById(R.id.id_additional_data);
        }
    }

    private ItemClickCallBack callBack;

    public void setCallBack(ItemClickCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * item点击监听回调
     */
    public interface ItemClickCallBack {
        /**
         * "稳点" 按钮点击回调
         *
         * @param position
         */
        void statusClickCallBack(int position);

        /**
         * "断开" 按钮点击回调
         *
         * @param position
         */
        void disconnectClickCallBack(int position);
    }
}

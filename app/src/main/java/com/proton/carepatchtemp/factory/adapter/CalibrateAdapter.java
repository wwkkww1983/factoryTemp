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
import com.proton.carepatchtemp.factory.utils.CalibrateBeanDao;
import com.proton.carepatchtemp.factory.utils.CalibrateUtil;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.temp.connector.bean.DeviceType;
import java.util.List;

public class CalibrateAdapter extends RecyclerView.Adapter<CalibrateAdapter.VH> {

    private Context context;
    private List<MeasureViewModel> datum;

    public CalibrateAdapter(Context context, List<MeasureViewModel> datum) {
        this.context = context;
        this.datum = datum;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new VH(LayoutInflater.from(context).inflate(R.layout.item_factory_calibrate_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MeasureViewModel measureViewModel = datum.get(position);
        float TL1 = CalibrateUtil.getTL(measureViewModel.firstSinkTemp.get(), measureViewModel.firstStableTemp.get());
        float TL2 = CalibrateUtil.getTL(measureViewModel.secondSinkTemp.get(), measureViewModel.secondStableTemp.get());
        float TH1 = CalibrateUtil.getTH(measureViewModel.firstSinkTemp.get(), measureViewModel.firstStableTemp.get());
        float TH2 = CalibrateUtil.getTH(measureViewModel.secondSinkTemp.get(), measureViewModel.secondStableTemp.get());
        float t = CalibrateUtil.getT(TL1, TH1, TL2, TH2);

        measureViewModel.calibration.set(t);
        CalibrateBeanDao.saveCalibrationValue(datum.get(position).patchMacaddress.get(), measureViewModel.calibrationStatus.get(), t);

        if (t != CalibrateUtil.INVALID_T) {
            holder.txtCalibration.setText(String.format("校准值：%s℃", Utils.formatTempToStr(t)));
            holder.txtCalibrate.setVisibility(View.VISIBLE);
        } else {
            holder.txtCalibration.setText("无校准值");
            holder.txtNotCalibrate.setText("无法校准");
            holder.txtCalibrate.setVisibility(View.GONE);
            switchStableTextView(holder.txtNotCalibrate, false);
        }
        holder.txtDeviceType.setText(DeviceType.getDeviceTypeBroadcast(measureViewModel.measureInfo.get().getDevice().getDeviceType()) + ",");
        holder.txtMac.setText(String.format(",MAC:%s,", measureViewModel.patchMacaddress.get()));
        holder.txtSn.setText(String.format("SN:%s ,", measureViewModel.serialNumber.get()));
        holder.txtVer.setText(String.format("VER:%s", measureViewModel.hardVersion.get()));
        holder.txtAdditionalData1.setText(String.format("测温点1：%s℃(槽%s℃)", measureViewModel.firstStableTemp.get(), measureViewModel.firstSinkTemp.get()));
        holder.txtAdditionalData2.setText(String.format("测温点2：%s℃(槽%s℃)", measureViewModel.secondStableTemp.get(), measureViewModel.secondSinkTemp.get()));
        holder.txtCalibrate.setOnClickListener(v -> switchCalibrateSelect(datum.get(position), holder.txtCalibrate, holder.txtNotCalibrate, true));
        holder.txtNotCalibrate.setOnClickListener(v -> switchCalibrateSelect(datum.get(position), holder.txtCalibrate, holder.txtNotCalibrate, false));
    }

    /**
     * 无法校准是按钮状态切换
     *
     * @param txtCalibrate
     * @param isCanCalibrate
     */
    private void switchStableTextView(TextView txtCalibrate, boolean isCanCalibrate) {
        if (!isCanCalibrate) {
            txtCalibrate.setBackgroundColor(ContextCompat.getColor(context, R.color.color_main));
            txtCalibrate.setTextColor(ContextCompat.getColor(context, R.color.white));
            txtCalibrate.setEnabled(false);
            txtCalibrate.setClickable(false);
            txtCalibrate.setText("无法校准");
        } else {
            txtCalibrate.setBackgroundResource(R.drawable.add_new_device_bg);
            txtCalibrate.setTextColor(ContextCompat.getColor(context, R.color.color_main));
            txtCalibrate.setEnabled(true);
            txtCalibrate.setClickable(true);
            txtCalibrate.setText("不校准");
        }
    }

    /**
     * 切换校准状态
     *
     * @param measureViewModel
     * @param txtCalibrate
     * @param txtNotCalibrate
     * @param isCanCalibrate
     */
    private void switchCalibrateSelect(MeasureViewModel measureViewModel, TextView txtCalibrate, TextView txtNotCalibrate, boolean isCanCalibrate) {
        if (isCanCalibrate) {
            txtCalibrate.setBackgroundResource(R.drawable.solid_main_bg);
            txtCalibrate.setTextColor(ContextCompat.getColor(context, R.color.white));
            txtNotCalibrate.setBackgroundResource(R.drawable.stroke_main_bg);
            txtNotCalibrate.setTextColor(ContextCompat.getColor(context, R.color.color_main));
            measureViewModel.calibrateSelect.set(true);
        } else {
            txtCalibrate.setBackgroundResource(R.drawable.stroke_main_bg);
            txtCalibrate.setTextColor(ContextCompat.getColor(context, R.color.color_main));
            txtNotCalibrate.setBackgroundResource(R.drawable.solid_main_bg);
            txtNotCalibrate.setTextColor(ContextCompat.getColor(context, R.color.white));
            measureViewModel.calibrateSelect.set(false);
        }
    }

    @Override
    public int getItemCount() {
        return null == datum ? 0 : datum.size();
    }

    public static class VH extends RecyclerView.ViewHolder {
        public TextView txtCalibration, txtDeviceType, txtMac, txtSn, txtVer, txtAdditionalData1, txtAdditionalData2, txtCalibrate, txtNotCalibrate;

        public VH(@NonNull View itemView) {
            super(itemView);
            txtCalibration = itemView.findViewById(R.id.id_calibration);
            txtDeviceType = itemView.findViewById(R.id.id_device_type);
            txtMac = itemView.findViewById(R.id.id_mac);
            txtSn = itemView.findViewById(R.id.id_sn);
            txtVer = itemView.findViewById(R.id.id_ver);
            txtAdditionalData1 = itemView.findViewById(R.id.id_additional_data1);
            txtAdditionalData2 = itemView.findViewById(R.id.id_additional_data2);
            txtCalibrate = itemView.findViewById(R.id.id_calibrate);
            txtNotCalibrate = itemView.findViewById(R.id.id_not_calibrate);
        }
    }

}

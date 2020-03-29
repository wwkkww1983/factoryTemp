package com.proton.carepatchtemp.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.device.DockerDetailActivity;
import com.proton.carepatchtemp.activity.device.PatchDetailActivity;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.net.bean.DeviceBean;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.UpdateFirmwareBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.DeviceCenter;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.proton.carepatchtemp.utils.StringUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.WarmDialog;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by luochune on 2018/3/29.
 * p03设备适配器
 */

public class DeviceListAdapter extends CommonAdapter<DeviceBean> {
    private String latestVersion;

    public DeviceListAdapter(Context context, List<DeviceBean> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(CommonViewHolder holder, DeviceBean device) {
        //设备管理图标
        if (!device.isDocker()) {
            switch (device.getDeviceType()) {
                case 2:
                    //普通版
                    ((ImageView) holder.getView(R.id.id_iv_devicePic)).setImageResource(R.drawable.img_carepatch_common);
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                    ((ImageView) holder.getView(R.id.id_iv_devicePic)).setImageResource(R.drawable.img_carepatch_simple);
                    break;
            }
        } else {
            ((ImageView) holder.getView(R.id.id_iv_devicePic)).setImageResource(R.drawable.icon_docker);
        }
        //设备名称
        ((TextView) holder.getView(R.id.id_tv_deviceName)).setText(device.getDeviceTypeName());
        //序列号
        ((TextView) holder.getView(R.id.id_device_mac)).setText(App.get().getResources().getString(R.string.string_device_mac, device.getBtaddress()));
        //设备删除
        holder.getView(R.id.id_tv_delete).setOnClickListener(v -> {
            final String[] stringItems = new String[]{(UIUtils.getString(R.string.string_confirm))};
            final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems, null);
            dialog.title(UIUtils.getString(R.string.string_delete_device));
            dialog.titleTextSize_SP(14F);
            dialog.show();
            dialog.setOnOperItemClickL((parent, view1, position, id) -> {
                switch (position) {
                    case 0:
                        //判断下当前设备是否在测量
                        if (!Utils.checkPatchIsMeasuring(device.getBtaddress())) {
                            deleteDevice(device.getId());
                        } else {
                            new WarmDialog(ActivityManager.currentActivity())
                                    .setTopText(R.string.string_warm_tips)
                                    .setContent(R.string.string_can_not_delete_measuring_device)
                                    .hideCancelBtn()
                                    .show();
                        }
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            });
        });
        //p245设备详情点击
        holder.getView(R.id.id_lay_p245content).setOnClickListener(v -> {
            if (!device.isDocker()) {
                mContext.startActivity(new Intent(mContext, PatchDetailActivity.class)
                        .putExtra("device", device));
            } else {
                mContext.startActivity(new Intent(mContext, DockerDetailActivity.class)
                        .putExtra("device", device));
            }
        });
        //升级提示,1.0.0和1.0.1不支持升级,1.0.2及以上需要升级
        if (!device.isDocker()) {
            UpdateFirmwareBean updateInfo = LitePal
                    .where("deviceType = ?", String.valueOf(device.getDeviceType()))
                    .findFirst(UpdateFirmwareBean.class);
            if (updateInfo != null) {
                latestVersion = updateInfo.getVersion();
                if (!TextUtils.isEmpty(latestVersion)) {
                    if (StringUtils.compareVersion(device.getVersion(), latestVersion) < 0 && device.getDeviceType() != 2) {
                        //该固件需要升级
                        holder.getView(R.id.id_iv_isNeedUpdate).setVisibility(View.VISIBLE);
                    } else {
                        holder.getView(R.id.id_iv_isNeedUpdate).setVisibility(View.GONE);
                    }
                }
            }
        } else {
            holder.getView(R.id.id_iv_isNeedUpdate).setVisibility(View.GONE);
        }
    }

    /**
     * 删除某个设备
     */
    private void deleteDevice(int deviceId) {
        DeviceCenter.deleteDevice(deviceId, new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ResultPair data) {
                //删除成功
                BlackToast.show(R.string.string_delete_success);
                EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.DEVICE_CHANGED));
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && !TextUtils.isEmpty(resultPair.getData())) {
                    BlackToast.show(resultPair.getData());
                } else {
                    BlackToast.show(R.string.string_delete_failed);
                }
            }
        });
    }
}

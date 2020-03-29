package com.proton.carepatchtemp.activity.user;

import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseActivity;
import com.proton.carepatchtemp.bean.AlarmBean;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.ActivityRemindBellSelectBinding;
import com.proton.carepatchtemp.utils.MediaManager;
import com.proton.carepatchtemp.utils.Utils;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;
import com.wms.adapter.recyclerview.OnItemClickListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class RemindBellSelectActivity extends BaseActivity<ActivityRemindBellSelectBinding> {

    private CommonAdapter mAdapter;
    private List<AlarmBean> alarmList = new ArrayList<>();

    @Override
    protected int inflateContentView() {
        return R.layout.activity_remind_bell_select;
    }

    @Override
    protected void initView() {
        super.initView();
        mAdapter = new CommonAdapter<AlarmBean>(mContext, alarmList, R.layout.item_remind_bell_layout) {
            @Override
            public void convert(CommonViewHolder holder, AlarmBean alarmBean) {
                holder.setText(R.id.txt_alarm_name, alarmBean.getName());
                holder.setVisible(R.id.iv_select, alarmBean.getIsSelected() == 1);
            }
        };
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.idRecyclerview.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, Object o, int position) {

                for (int i = 0; i < alarmList.size(); i++) {
                    if (i == position) {
                        alarmList.get(i).setIsSelected(1);
                    } else {
                        alarmList.get(i).setIsSelected(0);
                    }
                    alarmList.get(i).save();
                }
                mAdapter.setDatas(alarmList);
                mAdapter.notifyDataSetChanged();
                MediaManager.getInstance().setAlarmFileName(alarmList.get(position).getFileName());
                MediaManager.getInstance().playSound(true);
            }

            @Override
            public boolean onItemLongClick(ViewGroup parent, View view, Object o, int position) {
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
//        List<AlarmBean> all = LitePal.findAll(AlarmBean.class);
        List<AlarmBean> all = LitePal.where("uid = ?", App.get().getApiUid()).find(AlarmBean.class);
        if (all != null && all.size() > 0 && !TextUtils.isEmpty(all.get(0).getFileName())) {
            alarmList.addAll(all);
        } else {
            addAlarm();
        }
        mAdapter.notifyDataSetChanged();
    }

    private void addAlarm() {
        LitePal.deleteAll(AlarmBean.class, "uid = ?", App.get().getApiUid());
        if (alarmList != null) {
            alarmList.clear();
        }
        alarmList.add(new AlarmBean(App.get().getApiUid(), "铃声1", "alarmOne.mp3", 1));
        alarmList.add(new AlarmBean(App.get().getApiUid(), "铃声2", "alarmTwo.mp3", 0));
        alarmList.add(new AlarmBean(App.get().getApiUid(), "铃声3", "alarmThree.mp3", 0));
        LitePal.saveAll(alarmList);
    }

    @Override
    public String getTopCenterText() {
        return getString(R.string.string_the_bell_remind);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.cancelVibrateAndSound();
    }
}

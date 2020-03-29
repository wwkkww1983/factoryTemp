package com.proton.carepatchtemp.fragment.profile;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.Observable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.profile.AddProfileActivity;
import com.proton.carepatchtemp.activity.profile.ProfileEditActivity;
import com.proton.carepatchtemp.activity.report.SomOneMeasureReportActivity;
import com.proton.carepatchtemp.adapter.ProfileListAdapter;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.FragmentProfileBinding;
import com.proton.carepatchtemp.fragment.base.BaseViewModelFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.OnItemChildClickListener;
import com.proton.carepatchtemp.view.WarmDialog;
import com.proton.carepatchtemp.viewmodel.profile.ProfileViewModel;
import com.wms.adapter.CommonViewHolder;

import java.util.List;

/**
 * Created by luochune on 2018/3/21.
 * 档案管理
 */

public class ProfileFragment extends BaseViewModelFragment<FragmentProfileBinding, ProfileViewModel> implements OnItemChildClickListener<ProfileBean> {
    private ProfileListAdapter mAdapter;
    private OnReportOperateListener onReportOperateListener;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    protected ProfileViewModel getViewModel() {
        return ViewModelProviders.of(this).get(ProfileViewModel.class);
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_profile;
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        if (event.getEventType() == MessageEvent.EventType.LOGIN) {
            binding.idTopLayout.idTopRight.setVisibility(View.VISIBLE);
            initData();
        } else if (event.getEventType() == MessageEvent.EventType.PROFILE_CHANGE) {
            initData();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idTopLayout.idTopRight.setVisibility(View.VISIBLE);
        binding.idTopLayout.idTopRight.setText(R.string.string_new_file);
        binding.idTopLayout.idTitle.setText(getResources().getString(R.string.string_profile_manage));
        mAdapter = new ProfileListAdapter(getActivity(), null, R.layout.item_profile_list, this);
        binding.idIncludeRefresh.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        binding.idIncludeRefresh.idRecyclerview.setAdapter(mAdapter);
        Utils.notLoginViewHide(binding.idTopLayout.idTopRight);
        setListener();
    }


    private void setListener() {
        binding.idTopLayout.idToogleDrawer.setOnClickListener(v -> {
            //抽屉开关
            if (onReportOperateListener != null) {
                onReportOperateListener.toggleClick();
            }
        });
        //添加新档案
        binding.idTopLayout.idTopRight.setOnClickListener(v -> {
            //添加新档案
            startActivity(new Intent(getActivity(), AddProfileActivity.class));
        });
    }

    @Override
    protected void initData() {
        if (!App.get().isLogined()) {
            setLoadError();
            return;
        }
        super.initData();
        viewmodel.getProfileList();
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();
        initRefreshLayout(binding.idIncludeRefresh.idRefreshLayout, refreshLayout -> viewmodel.getProfileList());
        viewmodel.profileList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                //刷新处理
                //返回回来一定会有数据，加载状态和空数据在base处理过
                binding.idIncludeRefresh.idRefreshLayout.finishRefresh();
                List<ProfileBean> netList = viewmodel.profileList.get();
                if (netList != null && netList.size() > 0) {
                    mAdapter.setDatas(netList);
                }
            }

        });
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idIncludeRefresh.idRefreshLayout;
    }

    /**
     * 列表adapter子项点击
     */
    @Override
    public void onChildClick(CommonViewHolder holder, ProfileBean bean, View view) {
        switch (view.getId()) {
            case R.id.id_lay_profile_delete:
                //删除
                //判断下当前档案是否正在测量
                if (Utils.checkProfileIsMeasuring(bean.getProfileId())) {
                    new WarmDialog(ActivityManager.currentActivity())
                            .setTopText(R.string.string_warm_tips)
                            .setContent(R.string.string_can_not_delete_measuring_profile)
                            .hideCancelBtn()
                            .show();
                } else {
                    showDeleteDialog(bean);
                }
                break;
            case R.id.id_lay_profile_edit:
                startActivity(new Intent(getActivity(), ProfileEditActivity.class)
                        .putExtra("profileBean", bean));
                break;
            case R.id.id_lay_profile_report:
                //跳转到档案列表页只查看当前档案
                long profileId = bean.getProfileId();
                startActivity(new Intent(getActivity(), SomOneMeasureReportActivity.class).putExtra("profileId", profileId));
        }
    }

    private void showDeleteDialog(ProfileBean bean) {
        new WarmDialog(getActivity()).setTopText(R.string.string_title_delete_profile).setContent(R.string.string_delete_profile_msg).setConfirmText(R.string.string_confirm)
                .setConfirmListener(v -> viewmodel.deleteProfile(bean.getProfileId())).setCancelText(R.string.string_cancel).show();
    }

    @Override
    protected int generateEmptyLayout() {
        return R.layout.layout_empty;
    }

    @Override
    protected String getEmptyText() {
        return getResString(R.string.string_go_to_add_new_profile);
    }

    @Override
    protected int getNotLoginTips() {
        return R.string.string_not_login_can_not_view_profile;
    }

    public void setOnReportOperateListener(OnReportOperateListener onReportOperateListener) {
        this.onReportOperateListener = onReportOperateListener;
    }

    public interface OnReportOperateListener {
        void toggleClick();
    }
}

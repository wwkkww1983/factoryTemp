package com.proton.carepatchtemp.fragment.measure;

import android.arch.lifecycle.ViewModel;
import android.content.Intent;
import android.databinding.Observable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.HomeActivity;
import com.proton.carepatchtemp.activity.measure.AddNewDeviceActivity;
import com.proton.carepatchtemp.activity.profile.AddProfileActivity;
import com.proton.carepatchtemp.bean.MeasureBean;
import com.proton.carepatchtemp.bean.PushBean;
import com.proton.carepatchtemp.bean.ShareBean;
import com.proton.carepatchtemp.databinding.FragmentMeasureChooseProfileBinding;
import com.proton.carepatchtemp.fragment.base.BaseViewModelFragment;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.net.bean.ProfileBean;
import com.proton.carepatchtemp.utils.ActivityManager;
import com.proton.carepatchtemp.utils.IntentUtils;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.view.EllipsizeTextView;
import com.proton.carepatchtemp.view.WarmDialog;
import com.proton.carepatchtemp.viewmodel.measure.MeasureViewModel;
import com.proton.carepatchtemp.viewmodel.measure.ShareMeasureViewModel;
import com.proton.carepatchtemp.viewmodel.profile.ProfileViewModel;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wangmengsi on 2018/2/28.
 * 测量选择档案
 */

public class MeasureChooseProfileFragment extends BaseViewModelFragment<FragmentMeasureChooseProfileBinding, ProfileViewModel> {

    private OnChooseProfileListener onChooseProfileListener;
    private List<ProfileBean> mProfiles = new ArrayList<>();
    private List<ShareBean> mShares = new ArrayList<>();
    private WarmDialog mShareDialog;
    /**
     * 当前activity是否依附在AddNewDeviceActivity  1:是依附在addNewDeviceActivity   0：依附在homeActivity
     */
    private int isAttachAddNew;

    public static MeasureChooseProfileFragment newInstance() {
        return new MeasureChooseProfileFragment();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_measure_choose_profile;
    }

    @Override
    protected void fragmentInit() {
        super.fragmentInit();

        viewmodel.profileList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                initRecyclerView();
            }

            private void initRecyclerView() {
                mProfiles.clear();
                mProfiles.addAll(viewmodel.profileList.get());
                filterMeasuringProfile();
                initProfileRecycler();
            }
        });

        viewmodel.shareList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                mShares.clear();
                mShares.addAll(viewmodel.shareList.get());
                filterMeasuringShare();
                initShareRecycler();
            }
        });
    }

    private void initShareRecycler() {
        binding.idRefreshLayout.finishRefresh();
        if (CommonUtils.listIsEmpty(mShares)) {
            binding.idShareLayout.setVisibility(View.GONE);
            return;
        }
        binding.idShareLayout.setVisibility(View.VISIBLE);
        binding.idShareRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        binding.idShareRecyclerview.setAdapter(new CommonAdapter<ShareBean>(mContext, mShares, R.layout.item_choose_share) {
            @Override
            public void convert(CommonViewHolder holder, ShareBean shareBean) {
                EllipsizeTextView nameText = holder.getView(R.id.id_name);
                nameText.setText(shareBean.getRealName());

                holder.setText(R.id.id_age, getResources().getQuantityString(R.plurals.string_sui, shareBean.getAge(), shareBean.getAge()));
                SimpleDraweeView avatarImg = holder.getView(R.id.id_avatar);
                avatarImg.setImageURI(shareBean.getAvatar());

                holder.getView(R.id.id_rootview).setOnClickListener(v -> {
                    if (onChooseProfileListener != null) {
                        onChooseProfileListener.onClickShare(shareBean);
                    }
                });
            }
        });
    }

    private void initProfileRecycler() {
        binding.idRefreshLayout.finishRefresh();
        if (CommonUtils.listIsEmpty(mProfiles)) {
            binding.idProfileLayout.setVisibility(View.GONE);
            return;
        }
        binding.idProfileLayout.setVisibility(View.VISIBLE);
        binding.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        binding.idRecyclerview.setAdapter(new CommonAdapter<ProfileBean>(mContext, mProfiles, R.layout.item_choose_profile) {
            @Override
            public void convert(CommonViewHolder holder, ProfileBean profileBean) {
                EllipsizeTextView nameText = holder.getView(R.id.id_name);
                nameText.setText(profileBean.getRealname());
                holder.setText(R.id.id_age, profileBean.getAge());
                SimpleDraweeView avatarImg = holder.getView(R.id.id_avatar);
                avatarImg.setImageURI(profileBean.getAvatar());

                holder.setText(R.id.id_macadress, TextUtils.isEmpty(profileBean.getMacaddress()) ? getString(R.string.string_not_bind_patch) : getString(R.string.string_has_bind_patch) + Utils.getShowMac(profileBean.getMacaddress()));

                holder.getView(R.id.id_measure).setOnClickListener(v -> {
                    if (onChooseProfileListener != null) {
                        onChooseProfileListener.onClickProfile(profileBean);
                    }
                });


                holder.getView(R.id.id_rebind).setOnClickListener(v -> {
                            if (getActivity().getClass().getSimpleName().equalsIgnoreCase(AddNewDeviceActivity.class.getSimpleName())) {
                                isAttachAddNew = 1;
                            } else {
                                isAttachAddNew = 0;
                            }
                            profileBean.setIsAttachAddNew(isAttachAddNew);
                            IntentUtils.goToScanQRCode(mContext, profileBean);
                        }
                );

                holder.getView(R.id.id_lay_profile_edit).setOnClickListener(v -> IntentUtils.goToEditProfile(mContext, profileBean));
            }
        });
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idAddProfile.setOnClickListener(v -> {
            if (getActivity().getClass().getSimpleName().equalsIgnoreCase(AddNewDeviceActivity.class.getSimpleName())) {
                isAttachAddNew = 1;
            } else {
                isAttachAddNew = 0;
            }
            //新增档案
            getActivity().startActivityForResult(new Intent(getActivity(), AddProfileActivity.class)
                            .putExtra("needScanQRCode", true)
                            .putExtra("isAttachAddNew", isAttachAddNew)
                    , 10000);
        });

        initRefreshLayout(binding.idRefreshLayout, refreshlayout -> {
            viewmodel.getProfileList(true);
            if (!BuildConfig.IS_INTERNAL) {
                viewmodel.getSharedList();
            }
        });
    }

    @Override
    protected ProfileViewModel getViewModel() {
        return new ProfileViewModel();
    }

    @Override
    protected void initData() {
        super.initData();
        viewmodel.getProfileList();
        if (!BuildConfig.IS_INTERNAL) {
            viewmodel.getSharedList();
        }
    }

    @Override
    protected int generateEmptyLayout() {
        return 0;
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idRecyclerview;
    }

    public void setOnChooseProfileListener(OnChooseProfileListener onChooseProfileListener) {
        this.onChooseProfileListener = onChooseProfileListener;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            filterMeasuringProfile();
            filterMeasuringShare();
        }
    }

    /**
     * 过滤当前正在测量的分享
     */
    private void filterMeasuringShare() {
        mShares.clear();
        if (viewmodel.shareList != null
                && !CommonUtils.listIsEmpty(viewmodel.shareList.get())) {
            mShares.addAll(viewmodel.shareList.get());
            Map<String, ViewModel> viewmodels = Utils.getAllMeasureViewModel();
            if (viewmodels != null && viewmodels.size() > 0) {
                for (String key : viewmodels.keySet()) {
                    if (viewmodels.get(key) instanceof ShareMeasureViewModel) {
                        //测量的viewmodel
                        MeasureBean measureBean = ((MeasureViewModel) viewmodels.get(key)).measureInfo.get();
                        if (measureBean == null) continue;
                        Iterator<ShareBean> iterator = mShares.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().getProfileId() == measureBean.getProfile().getProfileId()) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }

            initShareRecycler();
        }
    }

    /**
     * 过滤当前正在测量的档案
     */
    private void filterMeasuringProfile() {
        mProfiles.clear();
        if (viewmodel.profileList != null
                && !CommonUtils.listIsEmpty(viewmodel.profileList.get())) {
            mProfiles.addAll(viewmodel.profileList.get());
            Map<String, ViewModel> viewmodels = Utils.getAllMeasureViewModel();
            if (viewmodels != null && viewmodels.size() > 0) {
                for (String key : viewmodels.keySet()) {
                    if (viewmodels.get(key) instanceof MeasureViewModel) {
                        //测量的viewmodel
                        MeasureBean measureBean = ((MeasureViewModel) viewmodels.get(key)).measureInfo.get();
                        if (measureBean == null) continue;
                        Iterator<ProfileBean> iterator = mProfiles.iterator();
                        while (iterator.hasNext()) {
                            if (iterator.next().getProfileId() == measureBean.getProfile().getProfileId()) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }

            initProfileRecycler();
        }
    }

    @Override
    protected boolean isRegistEventBus() {
        return true;
    }

    @Override
    public void onMessageEvent(MessageEvent event) {
        super.onMessageEvent(event);
        MessageEvent.EventType type = event.getEventType();
        if (type == MessageEvent.EventType.PROFILE_CHANGE
                || type == MessageEvent.EventType.BIND_DEVICE_SUCCESS
                || type == MessageEvent.EventType.UNBIND_DEVICE_SUCCESS) {
            //档案编辑了或者档案绑定了贴
            viewmodel.getProfileList(true);
        } else if (type == MessageEvent.EventType.PUSH_SHARE_CANCEL
                || type == MessageEvent.EventType.MQTT_SHARE_CANCEL) {
            if (mShareDialog != null) {
                mShareDialog.dismiss();
            }
            //共享变化
            viewmodel.getSharedList();
        } else if (type == MessageEvent.EventType.PUSH_SHARE || type == MessageEvent.EventType.PUSH_SHARE_START_MEASURE) {
            viewmodel.getSharedList();
            showShareWarmDialog(event);
        }
    }

    private void showShareWarmDialog(MessageEvent event) {
        PushBean pushBean = (PushBean) event.getObject();
        String content;
        if (event.getEventType() == MessageEvent.EventType.PUSH_SHARE) {
            content = getString(R.string.string_some_one_sharing_data_with_you);
        } else {
            content = getString(R.string.string_some_one_start_measure);
        }

        if (Utils.needRecreateDialog(mShareDialog)) {
            mShareDialog = new WarmDialog(ActivityManager.currentActivity())
                    .setTopText(R.string.string_share_tips);
        }

        mShareDialog
                .setContent(String.format(content, pushBean.getName()))
                .setConfirmListener(v -> {
                    if (onChooseProfileListener != null) {
                        ActivityManager.finishOthersActivity(HomeActivity.class);
                        ShareBean shareBean = new ShareBean();
                        shareBean.setSex(pushBean.getSex());
                        shareBean.setAvatar(pushBean.getAvatar());
                        shareBean.setDockerMacaddress(pushBean.getDockerMacaddress());
                        shareBean.setMacaddress(pushBean.getPatchMacAddress());
                        shareBean.setRealName(pushBean.getName());
                        shareBean.setIsMeasuring(1);
                        shareBean.setId(String.valueOf(pushBean.getShareUid()));
                        shareBean.setProfileId(pushBean.getProfileId());
                        onChooseProfileListener.onClickShare(shareBean);
                    }
                });

        if (!mShareDialog.isShowing()) {
            mShareDialog.show();
        }
    }

    public interface OnChooseProfileListener {
        void onClickProfile(ProfileBean profile);

        void onClickShare(ShareBean shareBean);
    }
}

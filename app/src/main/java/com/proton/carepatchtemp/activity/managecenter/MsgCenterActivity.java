package com.proton.carepatchtemp.activity.managecenter;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.Observable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.activity.base.BaseViewModelActivity;
import com.proton.carepatchtemp.activity.common.AdapterChildClickListener;
import com.proton.carepatchtemp.adapter.MsgListAdapter;
import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.databinding.FragmentMessageCenterBinding;
import com.proton.carepatchtemp.net.bean.MessageBean;
import com.proton.carepatchtemp.utils.Utils;
import com.proton.carepatchtemp.viewmodel.systemmessage.MessageCenterListViewModel;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.wms.adapter.CommonViewHolder;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class MsgCenterActivity extends BaseViewModelActivity<FragmentMessageCenterBinding, MessageCenterListViewModel> implements AdapterChildClickListener {

    private List<MessageBean> messageList = new ArrayList<MessageBean>();
    private MsgListAdapter messageAdapter;

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_message_center;
    }

    @Override
    protected void init() {
        super.init();
        binding.idIncludeRefresh.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        initRefreshLayout(binding.idIncludeRefresh.idRefreshLayout, refreshlayout ->
        {
            viewmodel.isStaredRefreshing.set(true);
            viewmodel.getMessageList(true, "1");
        }, null);
        messageAdapter = new MsgListAdapter(mContext, messageList, R.layout.item_msg_center);
        messageAdapter.setAdapterChildClickListener(this);
        binding.idIncludeRefresh.idRecyclerview.setAdapter(messageAdapter);
    }

    @Override
    protected void initView() {
        super.initView();
        binding.idIncludeTop.idTvRightOperate.setVisibility(View.VISIBLE);
        binding.idIncludeTop.idTvRightOperate.setText(getResources().getString(R.string.string_edit));
    }

    @Override
    protected void initData() {
        if (!App.get().isLogined()) {
            setLoadError();
            Utils.notLoginViewHide(binding.idIncludeTop.idTvRightOperate);
            return;
        }
        super.initData();
        viewmodel.getMessageList(true, "1");
    }

    @Override
    protected void setListener() {
        super.setListener();
        //消息列表数据监听
        viewmodel.messagBeanListObserve.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (!CommonUtils.listIsEmpty(viewmodel.messagBeanListObserve.get())) {
                    messageAdapter.setDatas(viewmodel.messagBeanListObserve.get());
                    setLoadSuccess();
                    binding.idIncludeTop.idTvRightOperate.setEnabled(true);
                    binding.idIncludeTop.idTvRightOperate.setTextColor(getResColor(R.color.color_blue_30));
                } else {
                    setLoadEmpty();
                    //不能继续编辑操作
                    binding.idLayDelete.setVisibility(View.GONE);
                    binding.idIncludeTop.idTvRightOperate.setText(getResources().getString(R.string.string_edit));
                    binding.idIncludeTop.idTvRightOperate.setEnabled(false);
                    binding.idIncludeTop.idTvRightOperate.setTextColor(getResColor(R.color.color_gray_cc));
                }
            }
        });
        //监听刷新
        viewmodel.isStaredRefreshing.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (viewmodel.isStaredRefreshing.get() == false) {
                    binding.idIncludeRefresh.idRefreshLayout.finishRefresh();
                }
            }
        });
        //监听加载
        viewmodel.isStartedLoadMore.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (viewmodel.isStartedLoadMore.get() == false) {
                    binding.idIncludeRefresh.idRefreshLayout.finishLoadmore();
                }
            }
        });
        //消息全部编辑
        binding.idIncludeTop.idTvRightOperate.setOnClickListener(v ->
        {
            String tvStr = ((TextView) v).getText().toString();
            if (tvStr.equals(getResources().getString(R.string.string_edit))) {
                //批量删除
                messageAdapter.setEdit(true);
                binding.idLayDelete.setVisibility(View.VISIBLE);
                binding.idIncludeTop.idTvRightOperate.setText(getResources().getString(R.string.string_cancel));
            } else if (tvStr.equals(getResources().getString(R.string.string_cancel))) {
                //展示
                messageAdapter.setEdit(false);
                binding.idLayDelete.setVisibility(View.GONE);
                binding.idIncludeTop.idTvRightOperate.setText(getResources().getString(R.string.string_edit));
            }

        });
        viewmodel.msgSelectedIdObserveList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                if (viewmodel.msgSelectedIdObserveList.get().size() > 0) {
                    binding.idLayDelete.setBackgroundColor(App.get().getResources().getColor(R.color.color_orange_fc));
                } else {
                    binding.idLayDelete.setBackgroundColor(App.get().getResources().getColor(R.color.color_gray_b2));
                }
            }
        });
        //批量删除
        binding.idLayDelete.setOnClickListener(v -> {
            viewmodel.deleteSelectedMsgs();
        });
    }

    @Override
    public String getTopCenterText() {
        return getResources().getString(R.string.string_message_center);
    }

    @Override
    protected String getEmptyText() {
        return getResString(R.string.string_msg_empty);
    }

    @Override
    protected int getNotLoginTips() {
        return R.string.string_not_login_can_not_view_msg;
    }

    @Override
    protected MessageCenterListViewModel getViewModel() {
        return ViewModelProviders.of(this).get(MessageCenterListViewModel.class);
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idIncludeRefresh.idRefreshLayout;
    }

    @Override
    public void onClick(CommonViewHolder commonViewHolder, View view) {
        switch (view.getId()) {
            case R.id.id_tv_delete:
                final String[] stringItems = {getString(R.string.string_confirm)};
                final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
                dialog.title(getString(R.string.string_delete_msg_warm));
                dialog.titleTextSize_SP(14F);
                dialog.show();
                dialog.setOnOperItemClickL((parent, view1, position, id) -> {
                    switch (position) {
                        case 0:
                            //确定删除
                            ((SwipeMenuLayout) commonViewHolder.getView(R.id.id_swipeLayout)).quickClose();
                            viewmodel.deleteMsg(viewmodel.messagBeanListObserve.get().get(commonViewHolder.getItemPosition()));
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((SwipeMenuLayout) commonViewHolder.getView(R.id.id_swipeLayout)).quickClose();
                    }
                });
                break;
            case R.id.id_msg_content:
                if (commonViewHolder.getView(R.id.id_iv_msg_choose).getVisibility() == View.VISIBLE) {
                    String tvStr = binding.idIncludeTop.idTvRightOperate.getText().toString();
                    if (tvStr.equals(getResources().getString(R.string.string_cancel))) {
                        //列表展示状态
                        //编辑报告删除状态
                        viewmodel.msgItemClick(commonViewHolder.getItemPosition());
                        messageAdapter.notifyItemRangeChanged(commonViewHolder.getItemPosition(), 1);
                    }
                } else {
                    //自动进入编辑状态
                    messageAdapter.setEdit(true);
                    binding.idLayDelete.setVisibility(View.VISIBLE);
                    binding.idIncludeTop.idTvRightOperate.setText(getResources().getString(R.string.string_cancel));
                }

                break;
        }
    }
}

package com.proton.carepatchtemp.fragment.home;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.databinding.FragmentMessageCenterBinding;
import com.proton.carepatchtemp.fragment.base.BaseLazyFragment;
import com.proton.carepatchtemp.net.bean.MessageBean;
import com.proton.carepatchtemp.net.bean.MessageDataBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.viewmodel.systemmessage.MessageCenterListViewModel;
import com.wms.adapter.CommonViewHolder;
import com.wms.adapter.recyclerview.CommonAdapter;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangmengsi on 2018/2/28.
 * 消息中心
 * TODO 消息片段将会被删除
 */

public class MessageCenterFragment extends BaseLazyFragment<FragmentMessageCenterBinding> {

    private MessageCenterListViewModel messageCenterListViewModel;
    private List<MessageBean> messageList = new ArrayList<MessageBean>();
    private CommonAdapter<MessageBean> messageAdapter;
    //消息列表请求回调
    private NetCallBack<MessageDataBean> mMessageCenterCallback = new NetCallBack<MessageDataBean>() {

        @Override
        public void noNet() {
            super.noNet();
            //刷新情况处理
            setLoadError();
            binding.idIncludeRefresh.idRefreshLayout.finishRefresh();
        }

        @Override
        public void onSubscribe() {
            super.onSubscribe();
        }

        @Override
        public void onSucceed(MessageDataBean data) {
            messageList.clear();
            binding.idIncludeRefresh.idRefreshLayout.finishRefresh();
            List<MessageBean> msgList = data.getContent();
            messageList.addAll(msgList);
            messageAdapter.notifyDataSetChanged();
            if (CommonUtils.listIsEmpty(msgList)) {
                setLoadEmpty();
            } else {
                setLoadSuccess();
            }
        }

        @Override
        public void onFailed(ResultPair resultPair) {
            setLoadError();
            binding.idIncludeRefresh.idRefreshLayout.finishRefresh();
        }
    };

    public static MessageCenterFragment newInstance() {
        return new MessageCenterFragment();
    }

    @Override
    protected int inflateContentView() {
        return R.layout.fragment_message_center;
    }

    @Override
    protected void fragmentInit() {
        messageCenterListViewModel = ViewModelProviders.of(this).get(MessageCenterListViewModel.class);
        binding.idIncludeRefresh.idRecyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        // initRefreshLayout(binding.idIncludeRefresh.idRefreshLayout, refreshlayout -> messageCenterListViewModel.getMessageList(true, mMessageCenterCallback), null);
        messageAdapter = new CommonAdapter<MessageBean>(mContext, messageList, R.layout.item_msg_center) {
            @Override
            public void convert(CommonViewHolder holder, MessageBean msgBean) {
               /*TODO 是否批量删除 if (isBulkDeletion) {
                    holder.mSwipeMenuLayout.setSwipeEnable(false);
                    holder.mCbMsgSelect.setVisibility(View.VISIBLE);
                    holder.mCbMsgSelect.setChecked(mMsg.isChecked());
                    holder.mCbMsgSelect.setOnClickListener(v -> {
                        if (mDelMsgList.contains(mMsg)) {
                            mMsg.setChecked(false);
                            holder.mCbMsgSelect.setChecked(false);
                            mDelMsgList.remove(mMsg);
                            mDeleteList.remove(mMsg.getId() + "");
                        } else {
                            mMsg.setChecked(true);
                            holder.mCbMsgSelect.setChecked(true);
                            mDelMsgList.add(mMsg);
                            mDeleteList.add(mMsg.getId());
                        }
                        LogUtils.d(mDeleteList);
                        LogUtils.d(mDelMsgList);
                        LogUtils.e("mDeleteList = " + mDeleteList.toString().replace("[", "").replace("]", ""));
                    });
                } else {
                    holder.mSwipeMenuLayout.setSwipeEnable(true);
                    holder.mCbMsgSelect.setVisibility(View.GONE);
                }*/
                //消息内容
                ((TextView) holder.getView(R.id.tv_msg_content)).setText(msgBean.getContents());
                //消息时间
                ((TextView) holder.getView(R.id.tv_msg_time)).setText(msgBean.getTime());
                //消息标题
                ((TextView) holder.getView(R.id.tv_msg_title)).setText(msgBean.getTitle());
                //TODO 消息删除
             /*   holder.mIvDelete.setOnClickListener(v -> {

                    final String[] stringItems = {mContext.getString(R.string.string_confirm)};
                    final ActionSheetDialog dialog = new ActionSheetDialog(mContext, stringItems,holder.itemView);
                    dialog.title(mContext.getString(R.string.string_delete_msg_warm));
                    dialog.titleTextSize_SP(14F);
                    dialog.show();
                    dialog.setOnOperItemClickL((parent, view1, position1, id) -> {
                        switch (position1) {
                            case 0:
                                holder.mSwipeMenuLayout.quickClose();
                                MsgCenter.msgdelete(((Msg) datas.get(position)).getId());
                                datas.remove(position);
                                notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }
//            TastyUtils.makeTextShort(stringItems[position], TastyToast.SUCCESS);
                        dialog.dismiss();
                    });
                });*/
            }
        };
        binding.idIncludeRefresh.idRecyclerview.setAdapter(messageAdapter);
    }

    @Override
    protected void initView() {
        super.initView();
        //消息列表数据监听
       /* messageCenterListViewModel.messagBeanList.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {

            }
        });*/
    }

    @Override
    protected void initData() {
        super.initData();
        // messageCenterListViewModel.getMessageList(true, mMessageCenterCallback);
    }

    @Override
    protected View getEmptyAndLoadingView() {
        return binding.idIncludeRefresh.idRefreshLayout;
    }
}

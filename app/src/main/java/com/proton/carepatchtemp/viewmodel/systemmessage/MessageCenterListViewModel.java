package com.proton.carepatchtemp.viewmodel.systemmessage;

import android.databinding.ObservableField;

import com.proton.carepatchtemp.R;
import com.proton.carepatchtemp.net.bean.MessageBean;
import com.proton.carepatchtemp.net.bean.MessageDataBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.net.center.ManageCenter;
import com.proton.carepatchtemp.utils.BlackToast;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.UIUtils;
import com.proton.carepatchtemp.viewmodel.BaseViewModel;
import com.sinping.iosdialog.dialog.widget.ActionSheetDialog;
import com.wms.logger.Logger;
import com.wms.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import static com.proton.carepatchtemp.net.center.DataCenter.parseResult;

/**
 * Created by luochune on 2018/3/14.
 */

public class MessageCenterListViewModel extends BaseViewModel {

    public ObservableField<Boolean> isStaredRefreshing = new ObservableField<>();//是否开启了列表刷新状态
    public ObservableField<Boolean> isStartedLoadMore = new ObservableField<>();//是否开启了列表加载状态
    public ObservableField<List<MessageBean>> messagBeanListObserve = new ObservableField<List<MessageBean>>(new ArrayList<MessageBean>());//消息列表
    public ObservableField<List<String>> msgSelectedIdObserveList = new ObservableField<>(new ArrayList<String>());//消息列表选中集合（存储消息id）
    private MsgListNetCallBack msgListNetCallBack = new MsgListNetCallBack(true);

    private static MessageDataBean parseMsgList(String json) throws Exception {
        Logger.json(json);
        ResultPair resultPair = parseResult(json);
        if (resultPair.isSuccess()) {
         /*   Type type = new TypeToken<MessageDataBean>() {
            }.getType();*/
            return JSONUtils.getObj(resultPair.getData(), MessageDataBean.class);
        } else {
            throw new ParseResultException(resultPair.getData());
        }
    }

    /**
     * Msg
     * 获取消息列表
     * "type", "1"//1,体温 2，心电 3，医生端 4，微信
     *
     * @param isNeedRefresh 是否需要刷新
     */
    public void getMessageList(boolean isNeedRefresh, String type) {
        msgListNetCallBack.setRefresh(isNeedRefresh);
        ManageCenter.getMsgList(type, msgListNetCallBack);
    }

    /**
     * 报告加载状态改变
     *
     * @param isRefresh 刚结束刷新或者加载状态
     */
    private void modifydataLoadingSatus(boolean isRefresh) {
        if (isRefresh) {
            isStaredRefreshing.set(false);
        } else {
            isStartedLoadMore.set(false);
        }
    }

    /**
     * 删除某条消息
     *
     * @param msgBean 消息实体对象
     */
    public void deleteMsg(MessageBean msgBean) {
        ManageCenter.msgdelete(msgBean.getId(), new NetCallBack<ResultPair>() {
            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ResultPair data) {
                //删除消息成功
                List<MessageBean> msgBeanList = new ArrayList<>();
                messagBeanListObserve.get().remove(msgBean);
                msgBeanList.addAll(messagBeanListObserve.get());
                messagBeanListObserve.set(msgBeanList);
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData());
                }
            }
        });
    }

    /**
     * 删除选中的消息
     */
    public void deleteSelectedMsgs() {
        List<String> allMsgsIdList = msgSelectedIdObserveList.get();
        if (allMsgsIdList != null && allMsgsIdList.size() > 0) {
            final String[] stringItems = new String[]{(UIUtils.getString(R.string.string_confirm))};
            final ActionSheetDialog dialog = new ActionSheetDialog(getContext(), stringItems, null);
            dialog.title(UIUtils.getString(R.string.string_delete_msg_warm));
            dialog.titleTextSize_SP(14F);
            dialog.show();
            dialog.setOnOperItemClickL((parent, view1, position, id) -> {
                switch (position) {
                    case 0:
                        sureDeleteMsgs(allMsgsIdList);
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            });
        } else {
            BlackToast.show(R.string.string_choose_atLeast);
        }
    }

    /**
     * 确定删除消息
     */
    private void sureDeleteMsgs(List<String> allMsgsIdList) {
        ManageCenter.deleteSomeMsg(allMsgsIdList, new NetCallBack<ResultPair>() {

            @Override
            public void noNet() {
                super.noNet();
                BlackToast.show(R.string.string_no_net);
            }

            @Override
            public void onSucceed(ResultPair data) {
                //报告删除成功
                BlackToast.show(R.string.string_delete_success);
                msgSelectedIdObserveList.set(new ArrayList<String>());
                //TODO 目前通过刷新列表方式展示最后数据
                getMessageList(true, "1");
            }

            @Override
            public void onFailed(ResultPair resultPair) {
                super.onFailed(resultPair);
                if (resultPair != null && resultPair.getData() != null) {
                    BlackToast.show(resultPair.getData() + "");
                } else {
                    BlackToast.show(R.string.string_delete_failed);
                }
            }
        });
    }

    /**
     * 报告列表选中某一项
     */
    public void msgItemClick(int position) {

        //全部列表
        List<MessageBean> allMsgList = messagBeanListObserve.get();
        MessageBean messageBean = allMsgList.get(position);
        List<String> msgSelectedList = msgSelectedIdObserveList.get();
        boolean isOldChecked = messageBean.isChecked();
        if (CommonUtils.listIsEmpty(msgSelectedList)) {
            msgSelectedList = new ArrayList<String>();
        }
        if (messageBean.isChecked()) {
            //清除当前报告Id选择
            msgSelectedList.remove(messageBean.getId());
            //需要重新赋值给变量才能收到变化
            List<String> newMsgSelecteBeanList = new ArrayList<String>();
            newMsgSelecteBeanList.addAll(msgSelectedList);
            msgSelectedIdObserveList.set(newMsgSelecteBeanList);
        } else {
            //添加当前报告id
            msgSelectedList.add(messageBean.getId());
            List<String> newMsgSelecteBeanList = new ArrayList<String>();
            newMsgSelecteBeanList.addAll(msgSelectedList);
            msgSelectedIdObserveList.set(newMsgSelecteBeanList);
        }
        messageBean.setChecked(!isOldChecked);
        allMsgList.set(position, messageBean);
        messagBeanListObserve.set(allMsgList);

    }

    //消息列表请求回调
    private class MsgListNetCallBack extends NetCallBack<MessageDataBean> {

        boolean isRefresh = true;

        public MsgListNetCallBack(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }

        @Override
        public void noNet() {
            super.noNet();
            //刷新情况处理
            status.set(Status.NO_NET);
            modifydataLoadingSatus(isRefresh);
        }

        @Override
        public void onSubscribe() {
            super.onSubscribe();
        }

        @Override
        public void onSucceed(MessageDataBean data) {
            modifydataLoadingSatus(isRefresh);
            List<MessageBean> msgList = data.getContent();

            if (isRefresh) {
                messagBeanListObserve.set(data.getContent());
            } else {
                //加载
                if (CommonUtils.listIsEmpty(data.getContent())) {
                    BlackToast.show(R.string.string_no_moredata);
                    return;
                }
                ArrayList<MessageBean> newMsgList = new ArrayList<>();//重新定义一个变量是让observe通知到响应
                newMsgList.addAll(messagBeanListObserve.get());
                newMsgList.addAll(data.getContent());
                messagBeanListObserve.set(newMsgList);
            }

        }

        @Override
        public void onFailed(ResultPair resultPair) {
            status.set(Status.Fail);
            modifydataLoadingSatus(isRefresh);
        }

        public void setRefresh(boolean refresh) {
            isRefresh = refresh;
        }
    }


}

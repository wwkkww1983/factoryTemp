package com.proton.carepatchtemp.net.center;

import com.proton.carepatchtemp.net.RetrofitHelper;
import com.proton.carepatchtemp.net.bean.MessageDataBean;
import com.proton.carepatchtemp.net.callback.NetCallBack;
import com.proton.carepatchtemp.net.callback.NetSubscriber;
import com.proton.carepatchtemp.net.callback.ParseResultException;
import com.proton.carepatchtemp.net.callback.ResultPair;
import com.proton.carepatchtemp.utils.JSONUtils;
import com.proton.carepatchtemp.utils.StringUtils;
import com.wms.logger.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luochune on 2018/3/20.
 */

public class ManageCenter extends DataCenter {
    /**
     * 意见反馈
     */
    public static void sendFeedBack(String feedBackStr, NetCallBack<ResultPair> feedBackNetCallBack) {
        Map<String, String> feedBackMap = new HashMap<String, String>();
        feedBackMap.put("content", feedBackStr);
        RetrofitHelper.getManagerCenterApi().feedBack(feedBackMap).map(json ->
        {
            Logger.json(json);
            ResultPair feedBackResultPair = DataCenter.parseResult(json);
            return feedBackResultPair;
        }).compose(threadTrans())
                .subscribe(new NetSubscriber<ResultPair>(feedBackNetCallBack) {
                    @Override
                    public void onNext(ResultPair resultPair) {
                        feedBackNetCallBack.onSucceed(resultPair);
                    }
                });
    }

    /**
     * 获取消息列表
     *
     * @param type 消息类型 //1,体温 2，心电 3，医生端 4，微信
     */
    public static void getMsgList(String type, NetCallBack<MessageDataBean> msgListNetCallBack) {
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("type", type);
        RetrofitHelper.getManagerCenterApi().getMsgList(paramMap).map(json ->
        {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                return JSONUtils.getObj(resultPair.getData(), MessageDataBean.class);
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<MessageDataBean>(msgListNetCallBack) {
            @Override
            public void onNext(MessageDataBean messageDataBean) {
                msgListNetCallBack.onSucceed(messageDataBean);
            }
        });
    }

    /**
     * 删除某条消息
     *
     * @param id 消息id
     */
    public static void msgdelete(String id, NetCallBack<ResultPair> msgDeleteNetCallBack) {
        HashMap<String, Object> msgDeleteMap = new HashMap<String, Object>();
        msgDeleteMap.put("messageid", id);
        RetrofitHelper.getManagerCenterApi().deleteMsg(msgDeleteMap).map(json ->
        {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(msgDeleteNetCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                msgDeleteNetCallBack.onSucceed(resultPair);
            }
        });
    }

    public static void deleteSomeMsg(List<String> ids, NetCallBack<ResultPair> deleteNumMsgsNetCallBack) {
        String msgIdStr = StringUtils.join(ids, ",");
        HashMap<String, String> msgMap = new HashMap<String, String>();
        msgMap.put("messageids", msgIdStr);
        RetrofitHelper.getManagerCenterApi().deleteMsgNums(msgMap).map(json -> {
            Logger.json(json);
            ResultPair resultPair = parseResult(json);
            if (resultPair.isSuccess()) {
                return resultPair;
            } else {
                throw new ParseResultException(resultPair.getData());
            }
        }).compose(threadTrans()).subscribe(new NetSubscriber<ResultPair>(deleteNumMsgsNetCallBack) {
            @Override
            public void onNext(ResultPair resultPair) {
                deleteNumMsgsNetCallBack.onSucceed(resultPair);
            }
        });
    }

}


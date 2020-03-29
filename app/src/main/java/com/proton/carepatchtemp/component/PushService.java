package com.proton.carepatchtemp.component;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.proton.carepatchtemp.bean.PushBean;
import com.proton.carepatchtemp.net.bean.MessageEvent;
import com.proton.carepatchtemp.utils.EventBusManager;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;
import com.wms.logger.Logger;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * Created by wangmengsi on 2018/3/15.
 */
public class PushService extends UmengMessageService {
    @Override
    public void onMessage(Context context, Intent intent) {
        Logger.w("收到推送了");
        if (!App.get().isLogined()) return;
        String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        if (TextUtils.isEmpty(message)) return;
        UMessage msg;
        try {
            msg = new UMessage(new JSONObject(message));
            Logger.d("推送内容:" + msg.extra);
            Gson gson = new Gson();
            PushBean pushBean = gson.fromJson(gson.toJson(msg.extra), PushBean.class);
            if (pushBean != null) {
                if ("DEVICE_CANCELSHARE".equalsIgnoreCase(pushBean.getType())) {
                    Logger.w("推送取消共享");
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.PUSH_SHARE_CANCEL, pushBean));
                } else if ("DEVICE_SHARE".equalsIgnoreCase(pushBean.getType())) {
                    Logger.w("推送共享");
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.PUSH_SHARE, pushBean));
                } else if ("DEVICE_STARTRECORDS".equalsIgnoreCase(pushBean.getType())) {
                    Logger.w("推送开始测量");
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.PUSH_SHARE_START_MEASURE, pushBean));
                } else if ("HOME_MESSAGE".equalsIgnoreCase(pushBean.getType())) {
                    Logger.w("推送首页消息");
                    EventBusManager.getInstance().post(new MessageEvent(MessageEvent.EventType.HOME_GET_MSG));
                }
            }
            //消息打开统计
            UTrack.getInstance(context).trackMsgClick(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

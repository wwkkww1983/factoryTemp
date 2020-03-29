package com.proton.carepatchtemp.utils;

import com.wms.logger.Logger;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 王梦思 on 2016/6/27 0027.
 * <p/>
 * EventBus统一管理
 */
public class EventBusManager {

    private static EventBusManager mInstance = new EventBusManager();
    private static EventBus mEventBus;

    private EventBusManager() {
    }

    public static EventBusManager getInstance() {
        if (mEventBus == null) {
            mEventBus = EventBus.getDefault();
        }
        return mInstance;
    }

    /**
     * 注册event
     */
    public <T> void register(T obj) {
        Logger.i("event register: obj=" + obj.toString());
        mEventBus.register(obj);
    }

    /**
     * 解除注册
     */
    public <T> void unregister(T obj) {
        //解除注册
        if (mEventBus.isRegistered(obj)) {
            Logger.i("event unregister: obj=" + obj.toString());
            mEventBus.unregister(obj);
        }
    }

    /**
     * post event
     */
    public <T> void post(T event) {
        Logger.i("eventbus post: event=" + event.toString());
        mEventBus.post(event);
    }
}

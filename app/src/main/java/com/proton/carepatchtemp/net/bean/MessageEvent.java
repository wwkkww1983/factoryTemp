package com.proton.carepatchtemp.net.bean;

/**
 * Created by wangmengsi on 2018/1/19.
 */

public class MessageEvent {

    private EventType eventType;
    private String msg;
    private String msg2;
    private Object object;

    public Object getObject2() {
        return object2;
    }

    public void setObject2(Object object2) {
        this.object2 = object2;
    }

    private Object object2;

    public MessageEvent(EventType type) {
        this.eventType = type;
    }

    public MessageEvent(EventType type, String msg) {
        this.eventType = type;
        this.msg = msg;
    }

    public MessageEvent(EventType type, String msg, String msg2) {
        this.eventType = type;
        this.msg = msg;
        this.msg2 = msg2;
    }

    public MessageEvent(EventType type, String msg, Object object) {
        this.eventType = type;
        this.msg = msg;
        this.object = object;
    }

    public MessageEvent(EventType type, Object object) {
        this.eventType = type;
        this.object = object;
    }

    public MessageEvent(EventType type, Object object, Object object2) {
        this.eventType = type;
        this.object = object;
        this.object2 = object2;
    }


    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg2() {
        return msg2;
    }

    public void setMsg2(String msg2) {
        this.msg2 = msg2;
    }

    public enum EventType {
        /**
         * 切换设备
         */
        SWITCH_DEVICE,
        /**
         * 切换温度单位
         */
        SWITCH_UNIT,
        /**
         * 是否登录
         */
        LOGIN,
        /**
         * wifi连接成功
         */
        WIFI_CONNECT_SUCCESS,
        /**
         * MQTT取消共享
         */
        MQTT_SHARE_CANCEL,
        /**
         * 添加设备
         */
        DEVICE_CHANGED,
        /**
         * 添加报告
         */
        ADD_REPORT,
        /**
         * MQTT共享温度
         */
        SHARE_TEMP,
        /**
         * 档案改变
         */
        PROFILE_CHANGE,
        /**
         * 删除档案
         */
        PROFILE_DELETE,
        /**
         * 取消共享
         */
        PUSH_SHARE_CANCEL,
        /**
         * 共享
         */
        PUSH_SHARE,
        /**
         * 分享设备开始测量
         */
        PUSH_SHARE_START_MEASURE,
        /**
         * 网络变化
         */
        NET_CHANGE,
        /**
         * 删除档案
         */
        DELETE_PROFILE,
        /**
         * 更新所有报告(收藏报告的取消收藏和删除需要刷新全部报告列表)
         */
        UPDATE_ALL_REPORT,
        /**
         * 更新收藏报告
         */
        UPDATE_COLLECT_REPORT,
        /**
         * 档案从无恢复到了有
         */
        HAVE_PROFILE,
        /**
         * 更新头像
         */
        UPDATE_AVATER,
        /**
         * 添加测量卡片
         */
        ADD_MEASURE_ITEM,
        /**
         * 设备绑定成功
         */
        DEVICE_BIND_SUCCESS,
        /**
         * 扫描完成
         */
        SCAN_COMPLETE,
        /**
         * 绑定设备成功
         */
        BIND_DEVICE_SUCCESS,
        /**
         * 解绑设备成功
         */
        UNBIND_DEVICE_SUCCESS,
        /**
         * 新设备界面点击重新绑定
         */
        ADD_NEW_DEVICE_BIND,
        /**
         * 获取首页消息
         */
        HOME_GET_MSG,
        /**
         * 固件更新成功
         */
        FIREWARE_UPDATE_SUCCESS,

        /**
         * 实时测量输入指令
         */
        INPUT_INSTRUCTION,
        /**
         * 重置密码成功
         */
        RESET_PWD_SUCCESS,

        /**
         * app是否在前台
         */
        APP_ISFOREGROUND,
        /**
         * 修改了报警时间间隔
         */
        MODIFIY_WARM_DURATION,

        /**
         * 体温校准温度发生改变刷新
         */
        TEMP_CHANGE,
        /**
         * 设备数量刷新
         */
        DEVICE_ADD,
        /**
         * 体温贴校准切换fragment
         */
        SWITCH_FACTORY_FRAGMENT,

        /**
         * 稳定状态发生变化时候的更新
         */
        STABLE_CHANGE,

        /**
         * 误差发生变化
         */
        ERROR_CHANGE,

        /**
         * 校准所有贴
         */
        CALIBRATE_ALL_PATCH,
        /**
         * 验证合格页面刷新
         */
        QUALIFICATION_REFRESH,

    }
}

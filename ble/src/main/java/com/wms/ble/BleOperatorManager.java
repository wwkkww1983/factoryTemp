package com.wms.ble;

import android.annotation.SuppressLint;
import android.content.Context;

import com.wms.ble.operator.IBleOperator;
import com.wms.ble.operator.WmsBleOperator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by 王梦思 on 2017/7/8.
 */
@SuppressLint("StaticFieldLeak")
public class BleOperatorManager {
    private static volatile IBleOperator sInstance;
    private static Context mContext;
    private static IBleOperator mTarget;

    public static void init(Context context) {
        mContext = context;
        mTarget = new WmsBleOperator(mContext);
    }

    private BleOperatorManager() {
    }

    public static void init(Context context, IBleOperator target) {
        if (target == null) {
            throw new IllegalArgumentException("target can not be null");
        }
        mTarget = target;
        mContext = context;
    }

    public static IBleOperator getInstance() {
        if (sInstance == null) {
            synchronized (BleOperatorManager.class) {
                if (sInstance == null) {
                    if (mContext == null) {
                        throw new IllegalArgumentException("You should initialize BleOperatorManager before using,You can initialize in your Application class");
                    }
                    sInstance = (IBleOperator) Proxy.newProxyInstance(mTarget.getClass().getClassLoader(), mTarget.getClass().getInterfaces(), new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            return method.invoke(mTarget, args);
                        }
                    });
                }
            }
        }
        return sInstance;
    }
}

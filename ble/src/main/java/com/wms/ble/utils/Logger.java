package com.wms.ble.utils;

import android.util.Log;

import com.wms.ble.BuildConfig;

/**
 * Created by proton on 2015/12/16.
 */
public class Logger {

    private static final String LOG_TAG = "proton_connector";
    private static final boolean isDebug = BuildConfig.DEBUG;

    //info-----begin
    public static void i(Object msg) {
        if (isDebug) {
            log(Type.INFO, msg);
        }
    }

    public static void i(Object msg1, Object msg2) {
        if (isDebug) {
            log(Type.INFO, msg1, msg2);
        }
    }

    public static void i(Object msg1, Object msg2, Object msg3) {
        if (isDebug) {
            log(Type.INFO, msg1, msg2, msg3);
        }
    }

    public static void i(Object msg1, Object msg2, Object msg3, Object msg4) {
        if (isDebug) {
            log(Type.INFO, msg1, msg2, msg3, msg4);
        }
    }

    public static void i(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5) {
        if (isDebug) {
            log(Type.INFO, msg1, msg2, msg3, msg4, msg5);
        }
    }

    public static void i(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6) {
        if (isDebug) {
            log(Type.INFO, msg1, msg2, msg3, msg4, msg5, msg6);
        }
    }

    public static void i(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7) {
        if (isDebug) {
            log(Type.INFO, msg1, msg2, msg3, msg4, msg5, msg6, msg7);
        }
    }

    public static void i(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7, Object msg8) {
        if (isDebug) {
            log(Type.INFO, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8);
        }
    }
    //info-----end

    //error-----begin
    public static void e(Object msg) {
        if (isDebug) {
            log(Type.ERROR, msg);
        }
    }

    public static void e(Object msg1, Object msg2) {
        if (isDebug) {
            log(Type.ERROR, msg1, msg2);
        }
    }

    public static void e(Object msg1, Object msg2, Object msg3) {
        if (isDebug) {
            log(Type.ERROR, msg1, msg2, msg3);
        }
    }

    public static void e(Object msg1, Object msg2, Object msg3, Object msg4) {
        if (isDebug) {
            log(Type.ERROR, msg1, msg2, msg3, msg4);
        }
    }

    public static void e(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5) {
        if (isDebug) {
            log(Type.ERROR, msg1, msg2, msg3, msg4, msg5);
        }
    }

    public static void e(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6) {
        if (isDebug) {
            log(Type.ERROR, msg1, msg2, msg3, msg4, msg5, msg6);
        }
    }

    public static void e(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7) {
        if (isDebug) {
            log(Type.ERROR, msg1, msg2, msg3, msg4, msg5, msg6, msg7);
        }
    }

    public static void e(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7, Object msg8) {
        if (isDebug) {
            log(Type.ERROR, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8);
        }
    }
    //error-----end

    //VERBOSE-----begin
    public static void v(Object msg) {
        if (isDebug) {
            log(Type.VERBOSE, msg);
        }
    }

    public static void v(Object msg1, Object msg2) {
        if (isDebug) {
            log(Type.VERBOSE, msg1, msg2);
        }
    }

    public static void v(Object msg1, Object msg2, Object msg3) {
        if (isDebug) {
            log(Type.VERBOSE, msg1, msg2, msg3);
        }
    }

    public static void v(Object msg1, Object msg2, Object msg3, Object msg4) {
        if (isDebug) {
            log(Type.VERBOSE, msg1, msg2, msg3, msg4);
        }
    }

    public static void v(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5) {
        if (isDebug) {
            log(Type.VERBOSE, msg1, msg2, msg3, msg4, msg5);
        }
    }

    public static void v(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6) {
        if (isDebug) {
            log(Type.VERBOSE, msg1, msg2, msg3, msg4, msg5, msg6);
        }
    }

    public static void v(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7) {
        if (isDebug) {
            log(Type.VERBOSE, msg1, msg2, msg3, msg4, msg5, msg6, msg7);
        }
    }

    public static void v(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7, Object msg8) {
        if (isDebug) {
            log(Type.VERBOSE, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8);
        }
    }
    //VERBOSE-----end

    //DEBUG-----begin
    public static void d(Object msg) {
        if (isDebug) {
            log(Type.DEBUG, msg);
        }
    }

    public static void d(Object msg1, Object msg2) {
        if (isDebug) {
            log(Type.DEBUG, msg1, msg2);
        }
    }

    public static void d(Object msg1, Object msg2, Object msg3) {
        if (isDebug) {
            log(Type.DEBUG, msg1, msg2, msg3);
        }
    }

    public static void d(Object msg1, Object msg2, Object msg3, Object msg4) {
        if (isDebug) {
            log(Type.DEBUG, msg1, msg2, msg3, msg4);
        }
    }

    public static void d(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5) {
        if (isDebug) {
            log(Type.DEBUG, msg1, msg2, msg3, msg4, msg5);
        }
    }

    public static void d(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6) {
        if (isDebug) {
            log(Type.DEBUG, msg1, msg2, msg3, msg4, msg5, msg6);
        }
    }

    public static void d(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7) {
        if (isDebug) {
            log(Type.DEBUG, msg1, msg2, msg3, msg4, msg5, msg6, msg7);
        }
    }

    public static void d(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7, Object msg8) {
        if (isDebug) {
            log(Type.DEBUG, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8);
        }
    }
    //DEBUG-----end

    //warm-----begin
    public static void w(Object msg) {
        if (isDebug) {
            log(Type.WARM, msg);
        }
    }

    public static void w(Object msg1, Object msg2) {
        if (isDebug) {
            log(Type.WARM, msg1, msg2);
        }
    }

    public static void w(Object msg1, Object msg2, Object msg3) {
        if (isDebug) {
            log(Type.WARM, msg1, msg2, msg3);
        }
    }

    public static void w(Object msg1, Object msg2, Object msg3, Object msg4) {
        if (isDebug) {
            log(Type.WARM, msg1, msg2, msg3, msg4);
        }
    }

    public static void w(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5) {
        if (isDebug) {
            log(Type.WARM, msg1, msg2, msg3, msg4, msg5);
        }
    }

    public static void w(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6) {
        if (isDebug) {
            log(Type.WARM, msg1, msg2, msg3, msg4, msg5, msg6);
        }
    }

    public static void w(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7) {
        if (isDebug) {
            log(Type.WARM, msg1, msg2, msg3, msg4, msg5, msg6, msg7);
        }
    }

    public static void w(Object msg1, Object msg2, Object msg3, Object msg4, Object msg5, Object msg6, Object msg7, Object msg8) {
        if (isDebug) {
            log(Type.WARM, msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8);
        }
    }
    //warm-----end

    private static void log(Type type, Object... msgs) {
        StringBuilder log = new StringBuilder();
        for (Object msg : msgs) {
            log.append(msg.toString());
        }
        if (type == Type.WARM) {
            Log.w(LOG_TAG, log.toString());
        }
    }

    private enum Type {
        INFO, WARM, VERBOSE, DEBUG, ERROR
    }
}

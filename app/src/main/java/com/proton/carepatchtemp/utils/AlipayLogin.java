package com.proton.carepatchtemp.utils;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.AuthTask;
import com.wms.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 王梦思 on 2018/9/21.
 * <p/>
 */
public class AlipayLogin {
    /**
     * 支付宝支付业务：入参app_id
     */
    private static final String APPID = "2018092161461774";
    /**
     * 支付宝账户登录授权业务：入参pid值，可在下面链接找到
     * <p/>
     * https://openhome.alipay.com/platform/keyManage.htm?keyType=partner
     */
    private static final String PID = "2088122646494563";
    /**
     * 支付宝账户登录授权业务：入参target_id值
     */
    private static final String TARGET_ID = String.valueOf(System.currentTimeMillis());
    /**
     * 商户私钥，pkcs8格式
     * 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个
     * 如果商户两个都设置了，优先使用 RSA2_PRIVATE
     * RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE
     * 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成
     * <p>
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    private static final String RSA2_PRIVATE = "";
    private static final String RSA_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCvaNeqWh3zD3WXZUvSt8IRqFdGS7m/S7gM/uaBoI/Aq4FyJg0B+vzADEhef2LF+uKbneS9jBAQRXc81waqoYj9sFv2mbzx0xrOGbXzEpdlLZjYPUXuPDNIMtajUZWCx6+XG2bC9d2TzOBs9D5sUa6bhmn5HUgNhf+Qlp+3EX2fAW41FTwrdI27E0G5S+E0VnccRCDrTzFCz1GM61eKm1yg/WpLPRfY1sDs+oQpz6CWcdNb7EMF7d/4kig2WO4G99H7L3IV9Z/QXc76r4nqOgJA31oPgcokHvKXmam3m1csGCGUkcqQorEoZ4XV477bUdaCNJjw49MXDQuAuBePCPsXAgMBAAECggEAach9f2OXejTRgrr5Dhv5srJl56PVYhMk7NlwY65T4yrLBu8Bmzck5CDEKBEEnUbfEGAZSWJFcbqi3RMEFzURq4SHAWHXu7LHEJIa03tbyA3Ghmn1WWX+u0TFgnq6AsOwFh3VTDAAL5k3ox1s4+Aiv/JAyJYrFNTBug3MRhgLPS8cdt+tjenFdJGGPanUFOByvvxL9PaSzYWEK9efvv1lQrYYUSJ7pzxhoRKLga7RNDLT5qI7rhkBWQA8Kdg3DHYx0yWmZMaJ5sve72UCw2IyU306uaFnZ3tebrxS8DD7qTQSo/feXqVtRn/hzB1CvfU5filgAhuUx4ba4/car1ikQQKBgQDjD38RLxXfDCrTjGvAbUfXz0JmwszWGC1BYaw2bnbGPmOybznBLxe/2dTz/mbpvP8t4ZebQtmZ0BmWEAv/4XCyjo2jIORugZJBZrNp/LKsxVTuLknUrDu4b1HkOlcktyLhWcb0JsqvVO2yqaAQkMUAfhLYksC//6W6EMl2cD6YNwKBgQDFxBVxY7zv0PCCO+2b3Cm7YiD196eH0ERaOI1bjnwX5ZtZrCDKuwb0StFHDZD4ljoRKlyey55Ez4+DIN0YS8zeKfcY5Goj4oDsi7AYBecbuHfwwToGXkCNRCJSeDicOqAuCOoOQBsSr9L5yKTfbRfujKdMT9aCAcqZO217X92EIQKBgF6XSBI6kVv4T6cNqp3haLqJB1qT8I2ISOIh9IJAho6T0yi0mVTXb4dnEgYLh4f8Sjos7uGpeclQHjibusQAgNiB9pNs0n0O0YUZacjPkwfStUz8T5mfnsl45p6zhzYubQOASRfRSUK9mMCDVcFQ7iEEZImAqXfS4pGfCK6kmA8FAoGBAMNKn9YqJLA5BtfEcB5Cah91hpD+LzypiGYDhGvOJ3K9tQv3MyafS/2/SCw/FFK2nr7Gs1HIRxEyWkimEk7kw3j7vduh68Jl8OQmCQ2f/plcL2zakv8o4dveVXpU56o3JmhvOxo5acKpR9CLt2w+4awazzZHfM9DD5sDmViE0U2hAoGBALaAC8B7icz6fK88cFyVPkiCwM4jOW6ckna+HLY9AoCSbHNrg+wnw+mSC5+wJx5iaVE2/zasjdwPaJuJ/CBEUaJsftft3V6NdbpVDPVb+EJ0CPgZOOfbRkACax2/0izvf+DZMA5qhamANan/WvlcjaYdABPgH7YXk9+kk84r+CwR";

    public static void login(Activity activity, AlipayLoginCallback callback) {
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> authInfoMap = buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
        String info = buildOrderParam(authInfoMap);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = getSign(authInfoMap, privateKey, rsa2);
        final String authInfo = info + "&" + sign;
        new Thread(() -> {
            // 构造AuthTask 对象
            AuthTask authTask = new AuthTask(activity);
            // 调用授权接口，获取授权结果
            Map<String, String> result = authTask.authV2(authInfo, true);
            Logger.w("result:" + result);
            if (callback != null) {
                String resultStatus = result.get("resultStatus");
                String[] resultValue = result.get("result").split("&");
                String authCode = "";
                String resultCode = "";
                for (String value : resultValue) {
                    if (value.startsWith("auth_code")) {
                        authCode = removeBrackets(getValue("auth_code=", value));
                        continue;
                    }
                    if (value.startsWith("result_code")) {
                        resultCode = removeBrackets(getValue("result_code=", value));
                    }
                }
                if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(resultCode, "200")) {
                    callback.onSuccess(authCode);
                } else if (TextUtils.equals(resultStatus, "6001")) {
                    callback.onCancel();
                } else {
                    callback.onFail();
                }
            }
        }).start();
    }

    private static String removeBrackets(String str) {
        if (!TextUtils.isEmpty(str)) {
            if (str.startsWith("\"")) {
                str = str.replaceFirst("\"", "");
            }
            if (str.endsWith("\"")) {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }

    private static String getValue(String header, String data) {
        return data.substring(header.length(), data.length());
    }

    /**
     * 构造授权参数列表
     */
    private static Map<String, String> buildAuthInfoMap(String pid, String app_id, String target_id, boolean rsa2) {
        Map<String, String> keyValues = new HashMap<String, String>();
        // 商户签约拿到的app_id，如：2013081700024223
        keyValues.put("app_id", app_id);
        // 商户签约拿到的pid，如：2088102123816631
        keyValues.put("pid", pid);
        // 服务接口名称， 固定值
        keyValues.put("apiname", "com.alipay.account.auth");
        // 商户类型标识， 固定值
        keyValues.put("app_name", "mc");
        // 业务类型， 固定值
        keyValues.put("biz_type", "openservice");
        // 产品码， 固定值
        keyValues.put("product_id", "APP_FAST_LOGIN");
        // 授权范围， 固定值
        keyValues.put("scope", "kuaijie");
        // 商户唯一标识，如：kkkkk091125
        keyValues.put("target_id", target_id);
        // 授权类型， 固定值
        keyValues.put("auth_type", "AUTHACCOUNT");
        // 签名类型
        keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");
        return keyValues;
    }

    /**
     * 构造支付订单参数信息
     *
     * @param map 支付订单参数
     */
    private static String buildOrderParam(Map<String, String> map) {
        List<String> keys = new ArrayList<>(map.keySet());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            sb.append(buildKeyValue(key, value, true));
            sb.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        sb.append(buildKeyValue(tailKey, tailValue, true));

        return sb.toString();
    }

    /**
     * 拼接键值对
     */
    private static String buildKeyValue(String key, String value, boolean isEncode) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append("=");
        if (isEncode) {
            try {
                sb.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                sb.append(value);
            }
        } else {
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * 对支付参数信息进行签名
     *
     * @param map 待签名授权信息
     */
    private static String getSign(Map<String, String> map, String rsaKey, boolean rsa2) {
        List<String> keys = new ArrayList<>(map.keySet());
        // key排序
        Collections.sort(keys);

        StringBuilder authInfo = new StringBuilder();
        for (int i = 0; i < keys.size() - 1; i++) {
            String key = keys.get(i);
            String value = map.get(key);
            authInfo.append(buildKeyValue(key, value, false));
            authInfo.append("&");
        }

        String tailKey = keys.get(keys.size() - 1);
        String tailValue = map.get(tailKey);
        authInfo.append(buildKeyValue(tailKey, tailValue, false));

        String oriSign = SignUtils.sign(authInfo.toString(), rsaKey, rsa2);
        String encodedSign = "";

        try {
            encodedSign = URLEncoder.encode(oriSign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "sign=" + encodedSign;
    }

    public static class SignUtils {
        private static final String ALGORITHM = "RSA";
        private static final String SIGN_ALGORITHMS = "SHA1WithRSA";
        private static final String SIGN_SHA256RSA_ALGORITHMS = "SHA256WithRSA";
        private static final String DEFAULT_CHARSET = "UTF-8";

        private static String getAlgorithms(boolean rsa2) {
            return rsa2 ? SIGN_SHA256RSA_ALGORITHMS : SIGN_ALGORITHMS;
        }

        static String sign(String content, String privateKey, boolean rsa2) {
            try {
                PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey));
//                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM, "BC");--》会抛出异常
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
                PrivateKey priKey = keyFactory.generatePrivate(priPKCS8);
                java.security.Signature signature = java.security.Signature.getInstance(getAlgorithms(rsa2));
                signature.initSign(priKey);
                signature.update(content.getBytes(DEFAULT_CHARSET));
                byte[] signed = signature.sign();
                return Base64.encode(signed);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static final class Base64 {
        private static final int BASELENGTH = 128;
        private static final int LOOKUPLENGTH = 64;
        private static final int TWENTYFOURBITGROUP = 24;
        private static final int EIGHTBIT = 8;
        private static final int SIXTEENBIT = 16;
        private static final int FOURBYTE = 4;
        private static final int SIGN = -128;
        private static char PAD = '=';
        private static byte[] base64Alphabet = new byte[BASELENGTH];
        private static char[] lookUpBase64Alphabet = new char[LOOKUPLENGTH];

        static {
            for (int i = 0; i < BASELENGTH; ++i) {
                base64Alphabet[i] = -1;
            }
            for (int i = 'Z'; i >= 'A'; i--) {
                base64Alphabet[i] = (byte) (i - 'A');
            }
            for (int i = 'z'; i >= 'a'; i--) {
                base64Alphabet[i] = (byte) (i - 'a' + 26);
            }

            for (int i = '9'; i >= '0'; i--) {
                base64Alphabet[i] = (byte) (i - '0' + 52);
            }

            base64Alphabet['+'] = 62;
            base64Alphabet['/'] = 63;

            for (int i = 0; i <= 25; i++) {
                lookUpBase64Alphabet[i] = (char) ('A' + i);
            }

            for (int i = 26, j = 0; i <= 51; i++, j++) {
                lookUpBase64Alphabet[i] = (char) ('a' + j);
            }

            for (int i = 52, j = 0; i <= 61; i++, j++) {
                lookUpBase64Alphabet[i] = (char) ('0' + j);
            }
            lookUpBase64Alphabet[62] = '+';
            lookUpBase64Alphabet[63] = '/';

        }

        private static boolean isWhiteSpace(char octect) {
            return (octect == 0x20 || octect == 0xd || octect == 0xa || octect == 0x9);
        }

        private static boolean isPad(char octect) {
            return (octect == PAD);
        }

        private static boolean isData(char octect) {
            return (octect < BASELENGTH && base64Alphabet[octect] != -1);
        }

        public static String encode(byte[] binaryData) {

            if (binaryData == null) {
                return null;
            }

            int lengthDataBits = binaryData.length * EIGHTBIT;
            if (lengthDataBits == 0) {
                return "";
            }

            int fewerThan24bits = lengthDataBits % TWENTYFOURBITGROUP;
            int numberTriplets = lengthDataBits / TWENTYFOURBITGROUP;
            int numberQuartet = fewerThan24bits != 0 ? numberTriplets + 1
                    : numberTriplets;
            char encodedData[];

            encodedData = new char[numberQuartet * 4];

            byte k, l, b1, b2, b3;

            int encodedIndex = 0;
            int dataIndex = 0;

            for (int i = 0; i < numberTriplets; i++) {
                b1 = binaryData[dataIndex++];
                b2 = binaryData[dataIndex++];
                b3 = binaryData[dataIndex++];

                l = (byte) (b2 & 0x0f);
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                        : (byte) ((b1) >> 2 ^ 0xc0);
                byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4)
                        : (byte) ((b2) >> 4 ^ 0xf0);
                byte val3 = ((b3 & SIGN) == 0) ? (byte) (b3 >> 6)
                        : (byte) ((b3) >> 6 ^ 0xfc);

                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[(l << 2) | val3];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[b3 & 0x3f];
            }

            if (fewerThan24bits == EIGHTBIT) {
                b1 = binaryData[dataIndex];
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2)
                        : (byte) ((b1) >> 2 ^ 0xc0);
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[k << 4];
                encodedData[encodedIndex++] = PAD;
                encodedData[encodedIndex++] = PAD;
            } else if (fewerThan24bits == SIXTEENBIT) {
                b1 = binaryData[dataIndex];
                b2 = binaryData[dataIndex + 1];
                l = (byte) (b2 & 0x0f);
                k = (byte) (b1 & 0x03);

                byte val1 = ((b1 & SIGN) == 0) ? (byte) (b1 >> 2) : (byte) ((b1) >> 2 ^ 0xc0);
                byte val2 = ((b2 & SIGN) == 0) ? (byte) (b2 >> 4) : (byte) ((b2) >> 4 ^ 0xf0);

                encodedData[encodedIndex++] = lookUpBase64Alphabet[val1];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[val2 | (k << 4)];
                encodedData[encodedIndex++] = lookUpBase64Alphabet[l << 2];
                encodedData[encodedIndex++] = PAD;
            }

            return new String(encodedData);
        }

        public static byte[] decode(String encoded) {

            if (encoded == null) {
                return null;
            }

            char[] base64Data = encoded.toCharArray();
            int len = removeWhiteSpace(base64Data);
            if (len % FOURBYTE != 0) {
                return null;
            }

            int numberQuadruple = (len / FOURBYTE);

            if (numberQuadruple == 0) {
                return new byte[0];
            }

            byte decodedData[];
            byte b1 = 0, b2, b3 = 0, b4 = 0;
            char d1 = 0, d2 = 0, d3 = 0, d4 = 0;

            int i = 0;
            int encodedIndex = 0;
            int dataIndex = 0;
            decodedData = new byte[(numberQuadruple) * 3];

            for (; i < numberQuadruple - 1; i++) {

                if (!isData((d1 = base64Data[dataIndex++]))
                        || !isData((d2 = base64Data[dataIndex++]))
                        || !isData((d3 = base64Data[dataIndex++]))
                        || !isData((d4 = base64Data[dataIndex++]))) {
                    return null;
                }// if found "no data" just return null

                b1 = base64Alphabet[d1];
                b2 = base64Alphabet[d2];
                b3 = base64Alphabet[d3];
                b4 = base64Alphabet[d4];

                decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
            }

            if (!isData((d1 = base64Data[dataIndex++]))
                    || !isData((d2 = base64Data[dataIndex++]))) {
                return null;// if found "no data" just return null
            }

            b1 = base64Alphabet[d1];
            b2 = base64Alphabet[d2];

            d3 = base64Data[dataIndex++];
            d4 = base64Data[dataIndex++];
            if (!isData((d3)) || !isData((d4))) {// Check if they are PAD characters
                if (isPad(d3) && isPad(d4)) {
                    if ((b2 & 0xf) != 0)// last 4 bits should be zero
                    {
                        return null;
                    }
                    byte[] tmp = new byte[i * 3 + 1];
                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                    tmp[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
                    return tmp;
                } else if (!isPad(d3) && isPad(d4)) {
                    b3 = base64Alphabet[d3];
                    if ((b3 & 0x3) != 0)// last 2 bits should be zero
                    {
                        return null;
                    }
                    byte[] tmp = new byte[i * 3 + 2];
                    System.arraycopy(decodedData, 0, tmp, 0, i * 3);
                    tmp[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                    tmp[encodedIndex] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                    return tmp;
                } else {
                    return null;
                }
            } else {
                b3 = base64Alphabet[d3];
                b4 = base64Alphabet[d4];
                decodedData[encodedIndex++] = (byte) (b1 << 2 | b2 >> 4);
                decodedData[encodedIndex++] = (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
                decodedData[encodedIndex++] = (byte) (b3 << 6 | b4);
            }

            return decodedData;
        }

        private static int removeWhiteSpace(char[] data) {
            if (data == null) {
                return 0;
            }

            int newSize = 0;
            int len = data.length;
            for (int i = 0; i < len; i++) {
                if (!isWhiteSpace(data[i])) {
                    data[newSize++] = data[i];
                }
            }
            return newSize;
        }
    }

    public interface AlipayLoginCallback {
        /**
         * 登录成功
         *
         * @param authCode 授权码
         */
        void onSuccess(String authCode);

        /**
         * 登录失败
         */
        void onFail();

        /**
         * 等录取消
         */
        void onCancel();
    }
}

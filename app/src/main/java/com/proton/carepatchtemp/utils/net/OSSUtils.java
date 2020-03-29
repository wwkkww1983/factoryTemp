package com.proton.carepatchtemp.utils.net;

import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.proton.carepatchtemp.BuildConfig;
import com.proton.carepatchtemp.bean.AliyunToken;
import com.proton.carepatchtemp.component.App;
import com.wms.logger.Logger;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Random;

/**
 * oss工具类
 */
public class OSSUtils {
    public static final int DATA_TEMP = 2;
    private static final int DATA_AVATAR = 0;
    private static final int DATA_REPORT = 1;
    private static OSS oss;

    public static void initOss() {
        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {

            @Override
            public OSSFederationToken getFederationToken() {
                AliyunToken aliyunToken = App.get().aliyunToken;
                return new OSSFederationToken(aliyunToken.getAccessKeyId(), aliyunToken.getAccessKeySecret(), aliyunToken.getSecurityToken(), aliyunToken.getExpiration());
            }
        };
        //初始化ossclient
        oss = new OSSClient(App.get(), getEndPoint(), credentialProvider);
    }

    /**
     * 上传图片
     *
     * @param filepath 本地文件地址
     */
    public static String uploadImg(String filepath) {
        String key = getImageUploadKey() + ".jpg";
        PutObjectRequest put = new PutObjectRequest(getBucket(DATA_AVATAR), key, filepath);
        try {
            PutObjectResult a = oss.putObject(put);
            Logger.w(a.getETag() + ":" + a.getStatusCode());
            String filePath = oss.presignConstrainedObjectURL(getBucket(DATA_AVATAR), key, 30 * 60);
            Logger.w("头像上传路径:" + filePath);
            return filePath;
        } catch (ClientException | ServiceException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getEndPoint() {
        if (BuildConfig.IS_INTERNAL) {
            return "http://oss-us-west-1.aliyuncs.com";
        } else {
            return "http://oss-cn-hangzhou.aliyuncs.com";
        }
    }

    public static String getImageUploadKey() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR) + File.separator + (new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1)) + File.separator + "vu" + System.currentTimeMillis() + getRandomStr();
    }

    private static String getRandomStr() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    public static String getBucket(int type) {
        try {
            if (BuildConfig.IS_INTERNAL) {
                return "oversea-temp";
            } else {
                if (type == DATA_AVATAR) {
                    return "vdpics";
                } else if (type == DATA_REPORT) {
                    return "sts-temp-report";
                }
                return "rawtemp";
            }
        } catch (Throwable e) {
        }
        return "";
    }

    public static String getSaveUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        if (App.get().aliyunToken == null) return url;

        try {
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
        Logger.w("url = " + url);
        return url;
    }

    public static String getRealUrl(String url) {
        if (TextUtils.isEmpty(url) || url.equals("null") || url.length() < getEndPoint().length()) {
            return null;
        }

        if (App.get().aliyunToken == null) return url;

        try {
            if (!url.contains("?") && url.contains("aliyuncs.com")) {
                String key = url.substring(url.indexOf("aliyuncs.com/") + "aliyuncs.com/".length());
                String bucket = url.substring(url.indexOf("//") + "//".length(), url.indexOf("."));
                String endPoint = url.substring(url.indexOf("//") + "//".length() + bucket.length() + 1, url.indexOf("aliyuncs.com/") + "aliyuncs.com".length());
                Logger.w("key = " + key + ",bucket = " + bucket + ",endPoint = " + endPoint);
                try {
                    url = oss.presignConstrainedObjectURL(bucket, key, 30 * 60);
                    url = url.replace(getEndPoint().substring(url.indexOf("//") + "//".length()), endPoint);
                } catch (ClientException e) {
                    e.printStackTrace();
                    return "";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
        Logger.w("url = " + url);
        return url;
    }

    public static String uploadReportJson(String filePath, long startTime) {
        String key = getReportJsonKey() + startTime + ".json";
        PutObjectRequest put = new PutObjectRequest(getBucket(DATA_REPORT), key, filePath);
        try {
            PutObjectResult a = oss.putObject(put);
            return oss.presignConstrainedObjectURL(getBucket(DATA_REPORT), key, 30 * 60);
        } catch (ClientException | ServiceException e) {
            com.wms.logger.Logger.w(e.toString());
        }
        return "";
    }

    public static String getReportJsonKey() {
        Calendar cal = Calendar.getInstance();
        return (BuildConfig.DEBUG ? "debug" : "release") +
                File.separator + "android" +
                File.separator + cal.get(Calendar.YEAR) +
                File.separator + (new DecimalFormat("00").format(cal.get(Calendar.MONTH) + 1)) +
                File.separator + (new DecimalFormat("00").format(cal.get(Calendar.DAY_OF_MONTH))) +
                File.separator + App.get().getApiUid() + File.separator;
    }

    /**
     * 上传分享的pdf文件
     */
    public static String uploadSharePdf(byte[] dataStr, String key) {
        key = key + ".pdf";
        PutObjectRequest put = new PutObjectRequest(getBucket(DATA_TEMP), key, dataStr);
        try {
            PutObjectResult putObjectResult = oss.putObject(put);
            return oss.presignConstrainedObjectURL(getBucket(DATA_TEMP), key, 30 * 60);
        } catch (ClientException | ServiceException e) {
            e.printStackTrace();
        }

        return "";
    }
}

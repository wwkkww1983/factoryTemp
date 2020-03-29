package com.proton.carepatchtemp.utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.proton.carepatchtemp.component.App;
import com.proton.carepatchtemp.utils.net.OSSUtils;
import com.proton.temp.connector.bean.DeviceType;
import com.wms.logger.Logger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Api on 2016/10/18.
 */

public class FileUtils {
    public static String temp = getDirectoryP("temp");
    public static String report = getDirectoryP("report");
    public static String json_filepath = getDirectoryP("json_filepath");
    public static String pdf_filepath = getDirectoryP("pdf_path");
    public static String data_cache = getDirectoryP("DataCache");
    public static String firmWare = getDirectoryP("firm_ware");
    private static String avatar = App.get().getFilesDir().getAbsolutePath() + File.separator + "avatar_update.jpg";


    /**
     * 读取缓存的总数据
     */
    public static double readCacheData() {
        //读取缓存数据
        double cacheSize = 0;
        double externalCacheSize = 0;
        try {
            cacheSize = getFolderSize(App.get().getCacheDir());
        } catch (Exception e) {
            e.printStackTrace();
            cacheSize = 0;
        }
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                externalCacheSize = getFolderSize(App.get().getExternalCacheDir());
            }
        } catch (Exception e) {
            e.printStackTrace();
            externalCacheSize = 0;
        }
        return cacheSize + externalCacheSize;
    }

    public static boolean isFileExist(String filePath, String fileName) {
        try {
            String path = filePath + File.separator + fileName;
            File file = new File(path);
            return file.exists();
        } catch (Exception e) {
            Log.e("isFileExist", "some thing worng");
            return false;
        }
    }


    public static boolean deleteFile(String filePath, String fileName) {
        try {
            String path = filePath + File.separator + fileName;
            File file = new File(path);
            if (isFileExist(filePath, fileName)) {
                file.delete();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 下载文件
     *
     * @param urlStr   下载地址
     * @param savePath 保存文件目录
     */
    public static String downLoadFromUrl(String urlStr, String savePath) {
        if (TextUtils.isEmpty(urlStr)) return null;
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) return null;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (conn == null) return null;
        //设置超时间为3秒
        conn.setConnectTimeout(15 * 1000);

        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = null;
        try {
            inputStream = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果为null 直接返回
        if (inputStream == null) {
            return "";
        }
        //获取自己数组
        byte[] getData = new byte[0];
        try {
            getData = readInputStream(inputStream);
        } catch (IOException e) {
            getData = null;
            e.printStackTrace();
            return "";
        }

        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File file = new File(savePath, new File(OSSUtils.getSaveUrl(urlStr)).getName());
        Logger.w("下载文件保存路径:" + file.getAbsolutePath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(getData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger.w(url + " download success");

        return new String(getData);
    }

    /**
     * 下载文件
     *
     * @param urlStr   下载地址
     * @param savePath 保存文件目录
     */
    public static String downloadFile(String urlStr, String savePath, String fileName) {
        if (TextUtils.isEmpty(urlStr)) return null;
        URL url = null;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) return null;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (conn == null) return null;
        //设置超时间为3秒
        conn.setConnectTimeout(15 * 1000);

        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = null;
        try {
            inputStream = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //如果为null 直接返回
        if (inputStream == null) {
            return "";
        }
        //获取自己数组
        byte[] getData = new byte[0];
        try {
            getData = readInputStream(inputStream);
        } catch (IOException e) {
            getData = null;
            e.printStackTrace();
            return "";
        }

        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        File file = new File(savePath, fileName);
        Logger.w("下载文件保存路径:" + file.getAbsolutePath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(getData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger.w(url + " download success");

        return new String(getData);
    }

    /**
     * 从输入流中获取字节数组
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


    public static String readJSONFile(String path) throws IOException {
        FileInputStream fis = new FileInputStream(path);
        Logger.w(path);
        InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        inputStreamReader.close();

        return stringBuilder.toString();
    }

    public static String getDirectoryP(String folderName) {
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
            if (App.get().getExternalFilesDir(folderName) != null) {
                return App.get().getExternalFilesDir(folderName).getAbsolutePath();
            } else {
                return "";
            }
        } else {
            return App.get().getFilesDir().getAbsolutePath() + File.separator + folderName;
        }
    }

    /**
     * 把一个文件转化为字节
     *
     * @return byte[]
     */
    public static byte[] getByte(File file) {
        try {
            byte[] bytes = null;
            if (file != null) {
                InputStream is = new FileInputStream(file);
                int length = (int) file.length();
                if (length > Integer.MAX_VALUE) {
                    System.out.println("this file is max ");
                    return null;
                }
                bytes = new byte[length];
                int offset = 0;
                int numRead = 0;
                while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                    offset += numRead;
                }
                //如果得到的字节长度和file实际的长度不一致就可能出错了
                if (offset < bytes.length) {
                    System.out.println("file length is error");
                    return null;
                }
                is.close();
            }
            return bytes;

        } catch (Exception e) {
            Logger.w(e.getMessage());
        }
        return null;
    }

    /**
     * 获取文件夹大小
     *
     * @param file File实例
     * @return long
     */
    public static long getFolderSize(java.io.File file) {

        long size = 0;
        try {
            if (null == file) {
                return size;
            }
            java.io.File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);

                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下文件及目录
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static String getTemp() {
        return temp;
    }

    public static String getJson_filepath() {
        return json_filepath;
    }

    public static String getPdf_filepath(){
        return pdf_filepath;
    }

    public static String getReport() {
        return report;
    }

    public static String getDataCache() {
        return data_cache;
    }

    public static String getAvatar() {
        return avatar;
    }

    public static String getFirewareDir(DeviceType deviceType) {
        return FileUtils.firmWare + File.separator + deviceType.toString() + File.separator;
    }

    public static String getFireware(DeviceType deviceType, String version) {
        return getFirewareDir(deviceType) + version;
    }
}

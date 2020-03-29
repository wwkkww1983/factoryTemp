package com.proton.carepatchtemp;

import com.proton.temp.connector.bean.TempDataBean;
import com.proton.temp.connector.bluetooth.utils.BleUtils;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        Map<String, Long> maps = new HashMap<>();
        maps.put("wms", 10L);
        maps.put("wms", 15L);
        maps.put("wms", 13L);
        maps.put("wms", 14L);
        maps.remove("wms");
        System.out.println("maps:" + maps.get("wms") + ",mapsize:" + maps.size());

        List<String> hasSubscribe = new ArrayList<>();
        hasSubscribe.add("wms");
        hasSubscribe.remove(null);
        System.out.println("list:" + hasSubscribe.size());
    }

    @Test
    public void testJava() {
        List<String> test = new ArrayList<>();
        test.add("1");
        test.add("2");
        test.add("3");
        test.add("4");

        test.remove(null);
        test.remove("5");
        System.out.println(test.size());
    }

    @Test
    public void test1() {
        List<Integer> mActivitys = new ArrayList<>();
        mActivitys.add(0);
        mActivitys.add(1);
        mActivitys.add(2);
        mActivitys.add(3);
        mActivitys.add(4);

        List<Integer> keepClazzs = new ArrayList<>();
        keepClazzs.add(0);
        keepClazzs.add(2);


        List<Integer> allClazzs = new ArrayList<>();
        allClazzs.addAll(mActivitys);

        allClazzs.removeAll(keepClazzs);

        Iterator<Integer> iterator = mActivitys.iterator();
        Integer activity;
        while (iterator.hasNext()) {
            activity = iterator.next();
            for (Integer clazz : allClazzs) {
                if (!clazz.equals(activity)) {
                    iterator.remove();
                }
            }
        }

        for (Integer integer : mActivitys) {
            System.out.println(integer);
        }
    }

    @Test
    public void test() {
        List<TempDataBean> cacheTemps = new ArrayList<>();
        cacheTemps.add(new TempDataBean(0, 23.5f, 24));
        cacheTemps.add(new TempDataBean(0, 24.5f, 24));
        cacheTemps.add(new TempDataBean(0, 25.5f, 24));
        cacheTemps.add(new TempDataBean(0, 25.5f, 24));
        cacheTemps.add(new TempDataBean(0, 25.5f, 24));

        System.out.println(parseBssid2Mac("240ac40f8608"));
    }

    private String parseBssid2Mac(String bssid) {
        StringBuilder macbuilder = new StringBuilder();
        for (int i = 0; i < bssid.length() / 2; i++) {
            macbuilder.append(bssid, i * 2, i * 2 + 2).append(":");
        }
        macbuilder.delete(macbuilder.length() - 1, macbuilder.length());
        return macbuilder.toString();
    }

    @Test
    public void testBleTime() {
        List<TempDataBean> temps = new ArrayList<>();
        temps.add(new TempDataBean(1f, 1.1f, 1, 100, 0, 30));
        temps.add(new TempDataBean(2f, 2.1f, 2, 101, 0, 30));
        temps.add(new TempDataBean(3f, 3.1f, 3, 102, 0, 30));
        temps.add(new TempDataBean(4f, 4.1f, 4, 103, 0, 30));
        List<TempDataBean> processTemps = BleUtils.getTempTime(temps);
        for (TempDataBean temp : processTemps) {
            System.out.println(temp.getAlgorithmTemp() + "," + new SimpleDateFormat("HH:mm:ss").format(temp.getTime()) + "," + temp.getPackageNumber());
        }
    }

    interface A {

    }

    interface B {

    }

    interface C extends A, B {

    }

    class Fu {
        int num = 0;

        Fu() {
            show();
        }

        void show() {
            System.out.println("fu...show...." + num);
        }
    }

    class Zi extends Fu {
        int num = 8;

        void show() {
            System.out.println("zi...show...." + num);
        }
    }

}
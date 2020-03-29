# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\eclipse\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-ignorewarnings
-dontwarn
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep class com.proton.temp.connector.interfaces.Connector {
    public *;
}

-keep class com.proton.temp.connector.bluetooth.BleConnector {
    public *;
}

-keep class com.proton.temp.connector.bluetooth.callback.OnReceiverBleDataListener {
     *;
}

-keep class com.proton.temp.connector.bluetooth.callback.OnScanListener {
     *;
}

-keep class com.proton.temp.connector.bluetooth.utils.AppUtils {
     *;
}

-keep class com.proton.temp.connector.bean.DockerDataBean {
     *;
}

-keep class * extends android.app.Service {
     *;
}

-keep public class com.proton.temp.connector.bean.TempDataBean {
   public *;
}

-keep public class com.proton.temp.connector.TempConnectorManager {
   public *;
}

-keep public class com.proton.temp.connector.mqtt.MQTTManager {
   public *;
   private static com.proton.temp.connector.bean.MQTTConfig *;
}

-keep public class com.proton.temp.connector.coap.CoapService {
   public *;
}

-keep public class com.proton.temp.connector.bean.DeviceBean {
   public *;
}

-keep public class com.proton.temp.connector.bean.MQTTConfig {
   *;
}

-keep public class com.proton.temp.connector.bean.DeviceType {
   public *;
}

-keep public class com.proton.temp.connector.bean.ConnectionType {
   public *;
}

-keep public class com.proton.temp.connector.utils.Utils {
   public *;
}

-keep public class com.proton.temp.connector.interfaces.AlgorithmStatusListener {
   public *;
}

-keep public class com.proton.temp.connector.interfaces.ConnectStatusListener {
   public *;
}

-keep public class com.proton.temp.connector.utils.FirewareUpdateManager {
   public *;
}

-keep public class com.proton.temp.connector.utils.FirewareUpdateManager$OnFirewareUpdateListener {
   *;
}

-keep public class com.proton.temp.connector.utils.FirewareUpdateManager$UpdateFailType {
   *;
}

-keep public class com.proton.temp.connector.utils.FirewareUpdateManager$FirewareAdapter {
   *;
}

-keep public class com.proton.temp.connector.interfaces.ConnectionTypeListener {
   public *;
}

-keep public class com.proton.temp.connector.interfaces.DataListener {
#    public void receiveRawTemp(float);
#    public void receiveCurrentTemp(float);
#    public void receiveCurrentTemp(float, long);
#    public void receiveBattery(java.lang.Integer);
#    public void receiveCharge(boolean);
#    public void receiveSerial(java.lang.String);
#    public void receiveHardVersion(java.lang.String);
#    public void receiveCacheTotal(java.lang.Integer);
#    public void receiveCacheTemp(java.util.List);
    public *;
}

-keep public class org.eclipse.** {
    *;
}

-keep public class com.proton.temp.connector.bluetooth.utils.BleUtils {
    *;
}

-keep public class com.proton.temp.connector.utils.ConnectorSetting {
    *;
}

-keep public class com.proton.temp.connector.BuildConfig {
    *;
}
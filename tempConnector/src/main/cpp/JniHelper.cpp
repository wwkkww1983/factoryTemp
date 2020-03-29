#include <jni.h>

extern "C" {
JNIEXPORT void JNICALL
Java_com_proton_temp_connector_mqtt_MQTTManager_initDefaultMQTTConfig(JNIEnv *env,
                                                                      jobject instance) {
    jclass configClass = env->FindClass("com/proton/temp/connector/bean/MQTTConfig");
    jmethodID configClint = env->GetMethodID(configClass, "<init>", "()V");
    jobject configObj = env->NewObject(configClass, configClint);
    jfieldID serverUrlField = env->GetFieldID(configClass, "serverUrl", "Ljava/lang/String;");
    jfieldID usernameField = env->GetFieldID(configClass, "username", "Ljava/lang/String;");
    jfieldID pwdField = env->GetFieldID(configClass, "password", "Ljava/lang/String;");
    env->SetObjectField(configObj, serverUrlField, env->NewStringUTF("tcp://47.100.92.19:1883"));
    env->SetObjectField(configObj, usernameField, env->NewStringUTF("proton"));
    env->SetObjectField(configObj, pwdField, env->NewStringUTF("proton123"));

    jclass mqttManagerClass = env->FindClass("com/proton/temp/connector/mqtt/MQTTManager");
    jfieldID mqttConfigField = env->GetStaticFieldID(mqttManagerClass, "mMQTTConfig",
                                                     "Lcom/proton/temp/connector/bean/MQTTConfig;");
    env->SetStaticObjectField(mqttManagerClass, mqttConfigField, configObj);
}
}
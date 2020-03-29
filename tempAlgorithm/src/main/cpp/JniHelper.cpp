#include <jni.h>
#include <sys/time.h>
#include <android/log.h>
#include <TempAlg.h>
#include <TempAlg1_5.h>

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,"JNIHelper",__VA_ARGS__)

static bool IS_DEBUG;

extern "C" {

long getCurrentTime() {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

JNIEXPORT void JNICALL
Java_com_proton_temp_algorithm_utils_AlgorithmHelper_init(JNIEnv *env, jclass type,
                                                          jboolean isDebug) {
    IS_DEBUG = isDebug;
}

JNIEXPORT jstring JNICALL
Java_com_proton_temp_algorithm_utils_AlgorithmHelper_getVersion(JNIEnv *env, jclass type) {
    return env->NewStringUTF(getVersion());
}

JNIEXPORT jobject JNICALL
Java_com_proton_temp_algorithm_utils_AlgorithmHelper_getTempOld(JNIEnv *env, jclass type,
                                                                jfloatArray temps,
                                                                jobject lastTempImg) {

    jclass lastTempImgClass = env->GetObjectClass(lastTempImg);
    jfieldID handleField = env->GetFieldID(lastTempImgClass, "handle", "Z");
    jfieldID startInd = env->GetFieldID(lastTempImgClass, "startInd", "I");
    jfieldID stickCountField = env->GetFieldID(lastTempImgClass, "stickCount", "I");
    jfieldID declineField = env->GetFieldID(lastTempImgClass, "decline", "I");
    jfieldID predIndField = env->GetFieldID(lastTempImgClass, "predInd", "I");
    jfieldID currentTempField = env->GetFieldID(lastTempImgClass, "currentTemp", "F");
    jfieldID predictedTempField = env->GetFieldID(lastTempImgClass, "predictedTemp", "F");
    jfieldID firstStartField = env->GetFieldID(lastTempImgClass, "firstStart", "I");

    jboolean handleValue = env->GetBooleanField(lastTempImg, handleField);
    jint startIndValue = env->GetIntField(lastTempImg, startInd);
    jint stickCountValue = env->GetIntField(lastTempImg, stickCountField);
    jint declineValue = env->GetIntField(lastTempImg, declineField);
    jint predIndValue = env->GetIntField(lastTempImg, predIndField);
    jfloat currentTempValue = env->GetFloatField(lastTempImg, currentTempField);
    jfloat predictedTempValue = env->GetFloatField(lastTempImg, predictedTempField);
    jint firstStartValue = env->GetIntField(lastTempImg, firstStartField);

    TempImg tempImg = TempImg();
    tempImg.decline = declineValue;
    tempImg.handle = handleValue;
    tempImg.startInd = startIndValue;
    tempImg.predInd = predIndValue;
    tempImg.predTemp = predictedTempValue;
    tempImg.stabT = currentTempValue;
    tempImg.stickN = stickCountValue;
    tempImg.firstStart = firstStartValue;

    jint size = env->GetArrayLength(temps);
    long startTime = getCurrentTime();
    if (IS_DEBUG) {
        LOGW("算法开始1.0,size = %d,currentTemp = %f,predTempValue = %f", size,
             currentTempValue, predictedTempValue);
    }
    vector<float> tempsDoubles(size);
    env->GetFloatArrayRegion(temps, 0, size, &tempsDoubles[0]);
    TempImg resultTemp = getTemp(tempsDoubles, tempImg.stabT, tempImg.predTemp, tempImg.startInd,
                                 tempImg.predInd, tempImg.stickN, tempImg.decline, tempImg.handle,
                                 tempImg.firstStart);

    if (IS_DEBUG) {
        LOGW("算法结束1.0,耗时%ld,measureStatus = %d,currentTemp = %f,predTempValue = %f",
             (getCurrentTime() - startTime), resultTemp.status, resultTemp.stabT,
             resultTemp.predTemp);
    }
    jclass tempImgClass = env->FindClass("com/proton/temp/algorithm/bean/TempImg");
    jmethodID tempImgClint = env->GetMethodID(tempImgClass, "<init>", "()V");
    jobject tempImgObj = env->NewObject(tempImgClass, tempImgClint);

    env->SetFloatField(tempImgObj, env->GetFieldID(tempImgClass, "currentTemp", "F"),
                       resultTemp.stabT);
    env->SetFloatField(tempImgObj, env->GetFieldID(tempImgClass, "predictedTemp", "F"),
                       resultTemp.predTemp);
    env->SetIntField(tempImgObj, env->GetFieldID(tempImgClass, "startInd", "I"),
                     resultTemp.startInd);
    env->SetIntField(tempImgObj, env->GetFieldID(tempImgClass, "stickCount", "I"),
                     resultTemp.stickN);
    env->SetIntField(tempImgObj, env->GetFieldID(tempImgClass, "decline", "I"), resultTemp.decline);
    env->SetIntField(tempImgObj, env->GetFieldID(tempImgClass, "predInd", "I"), resultTemp.predInd);
    env->SetBooleanField(tempImgObj, env->GetFieldID(tempImgClass, "handle", "Z"),
                         (jboolean) resultTemp.handle);
    env->SetIntField(tempImgObj, env->GetFieldID(tempImgClass, "firstStart", "I"),
                     resultTemp.firstStart);
    env->SetIntField(tempImgObj, env->GetFieldID(tempImgClass, "status", "I"), resultTemp.status);
    return tempImgObj;
}

JNIEXPORT jobject JNICALL
Java_com_proton_temp_algorithm_utils_AlgorithmHelper_getTemp(JNIEnv *env,
                                                             jclass type,
                                                             jfloat temp,
                                                             jlong time,
                                                             jint flag,
                                                             jint state,
                                                             jint isPill,
                                                             jint sample,
                                                             jint connectType) {

    long startTime = getCurrentTime();
    if (IS_DEBUG) {
        LOGW("算法开始1.5:temp = %lf,state = %d,connectionType = %d,sample=%d", temp, state,
             connectType,
             sample);
    }
    if (sample <= 0) {
        sample = 1;
    }
    ProResult result = appHandle(temp, time, static_cast<int16_t>(flag),
                                 static_cast<int16_t>(state),
                                 static_cast<int16_t>(isPill), static_cast<int16_t>(sample),
                                 static_cast<int16_t>(connectType));
    jclass tempResultClass = env->FindClass("com/proton/temp/algorithm/bean/TempResult");

    jmethodID tempResultClint = env->GetMethodID(tempResultClass, "<init>", "()V");
    jobject tempResultObj = env->NewObject(tempResultClass, tempResultClint);
    if (IS_DEBUG) {
        LOGW("算法结束1.5:temp = %lf,status = %d,gesture = %d,耗时:%ld", result.stabTemp, result.status,
             result.gesture, (getCurrentTime() - startTime));
    }
    env->SetFloatField(tempResultObj, env->GetFieldID(tempResultClass, "currentTemp", "F"),
                       result.stabTemp);
    env->SetIntField(tempResultObj, env->GetFieldID(tempResultClass, "status", "I"),
                     result.status);
    env->SetIntField(tempResultObj, env->GetFieldID(tempResultClass, "sample", "I"), result.samp);
    env->SetIntField(tempResultObj, env->GetFieldID(tempResultClass, "gesture", "I"),
                     result.gesture);
    return tempResultObj;
}
}
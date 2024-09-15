// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("plantclassification_v2");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("plantclassification_v2")
//      }
//    }

#include <android/asset_manager_jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <jni.h>
#include <algorithm>
#include <string>
#include <vector>
#include "ClassName.h"

// ncnn
#include "layer.h"
#include "net.h"
#include "benchmark.h"


static ncnn::Net PlantRecognition;
static ncnn::Net InsectRecognition;

struct Obj{
    float prob;
    int idx;
};


bool cmp(const Obj& a,const Obj& b){
    return a.prob>b.prob;
}
static void Softmax(std::vector<Obj>& output,const ncnn::Mat& in){
    int num = in.w;
    float sum = 0.f;
    for (int i = 0; i < num; ++i) {
        sum+=exp(in[i]);
    }
    for (int i = 0; i < num; ++i) {
        float prob = exp(in[i])/sum;
        Obj item;
        item.prob = prob;
        item.idx = i;
        output.push_back(item);
    }
    std::sort(output.begin(),output.end(), cmp);


}

extern "C" {
static jclass objCls = NULL;
static jmethodID constructorId;
static jfieldID idxId;
static jfieldID probId;
static jfieldID chineseNameId;
static jfieldID latinNameId;

JNIEXPORT jboolean JNICALL
Java_com_example_plantclassification_1v2_recognitionModel_PlantRecognitionModel_init(JNIEnv *env,jobject thiz,jobject assetManager) {
    //模型初始化
    AAssetManager *mgr = AAssetManager_fromJava(env, assetManager);
    {
        int ret = PlantRecognition.load_param(mgr, "quarrying_plantid_model.param");
        if (ret != 0) {
            __android_log_print(ANDROID_LOG_DEBUG, "model", "load_param failed");
            return JNI_FALSE;
        }
    }
    {
        int ret = PlantRecognition.load_model(mgr, "quarrying_plantid_model.bin");
        if (ret != 0) {
            __android_log_print(ANDROID_LOG_DEBUG, "model", "load_model failed");
            return JNI_FALSE;
        }
    }
    jclass localClass = env->FindClass(
            "com/example/plantclassification_v2/recognitionModel/PlantRecognitionModel$PlantObj");
    objCls = reinterpret_cast<jclass>(env->NewGlobalRef(localClass));
    constructorId = env->GetMethodID(objCls, "<init>", "()V");

    idxId = env->GetFieldID(objCls, "idx", "I");
    probId = env->GetFieldID(objCls, "prob", "F");
    chineseNameId = env->GetFieldID(objCls, "chineseName", "Ljava/lang/String;");
    latinNameId = env->GetFieldID(objCls, "latinName", "Ljava/lang/String;");

    return JNI_TRUE;

}
JNIEXPORT jobjectArray JNICALL
Java_com_example_plantclassification_1v2_recognitionModel_PlantRecognitionModel_detect(JNIEnv *env,jobject thiz,jobject bitmap) {
    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env, bitmap, &info);
    const int width = info.width;
    const int height = info.height;

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
        return NULL;
    const int target_size = 224;

    int w = width;
    int h = height;
    float scale = 1.0f;
    if (w > h) {
        scale = (float) target_size / w;
        w = target_size;
        h = h * scale;
    } else {
        scale = (float) target_size / h;
        h = target_size;
        w = w * scale;
    }
    ncnn::Mat in_pad = ncnn::Mat::from_android_bitmap_resize(env, bitmap, ncnn::Mat::PIXEL_RGB, w,
                                                             h);
    //图像处理
    const float norm_vals[3] = {1 / 0.229f / 255.f, 1 / 0.224f / 255.f, 1 / 0.225f / 255.f};
    const float mean_vals[3] = {0.485f * 255.f, 0.456f * 255.f, 0.406f * 255.f};
    in_pad.substract_mean_normalize(mean_vals, norm_vals);

    ncnn::Extractor ex = PlantRecognition.create_extractor();
    ex.input("input.1", in_pad);
    ncnn::Mat preds;
    ex.extract("477", preds);
    std::vector<Obj> out;
    Softmax(out, preds);
    int resultSize = 5;
    jobjectArray jObjArray = env->NewObjectArray(resultSize, objCls, NULL);
    for (int i = 0; i < resultSize; ++i) {
        jobject jObj = env->NewObject(objCls, constructorId);

        env->SetIntField(jObj, idxId, out[i].idx);
        env->SetFloatField(jObj, probId, out[i].prob);
        env->SetObjectField(jObj, chineseNameId, env->NewStringUTF(plantChineseName[out[i].idx]));
        env->SetObjectField(jObj, latinNameId, env->NewStringUTF(plantLatinName[out[i].idx]));

        env->SetObjectArrayElement(jObjArray, i, jObj);
    }

    return jObjArray;

}
static jclass insectObjCls = NULL;
static jmethodID insectConstructorId;
static jfieldID insectIdxId;
static jfieldID insectProbId;
static jfieldID insectChineseNameId;
static jfieldID insectLatinNameId;
JNIEXPORT jboolean JNICALL
Java_com_example_plantclassification_1v2_recognitionModel_InsectIdentificationModel_init(
        JNIEnv *env, jobject thiz, jobject assetManager) {
    AAssetManager* mgr = AAssetManager_fromJava(env,assetManager);
    {
        int ret = InsectRecognition.load_param(mgr,"quarrying_insect_identifier.param");
        if (ret!=0){
            __android_log_print(ANDROID_LOG_DEBUG, "model", "load_param failed");
            return JNI_FALSE;
        }
    }
    {
        int ret = InsectRecognition.load_model(mgr, "quarrying_insect_identifier.bin");
        if (ret!=0) {
            __android_log_print(ANDROID_LOG_DEBUG, "model", "load_model failed");
            return JNI_FALSE;
        }

    }
    jclass localClass = env->FindClass("com/example/plantclassification_v2/recognitionModel/InsectIdentificationModel$Obj");
    insectObjCls = reinterpret_cast<jclass>(env->NewGlobalRef(localClass));
    insectConstructorId = env->GetMethodID(insectObjCls, "<init>", "()V");

    insectIdxId = env->GetFieldID(insectObjCls,"idx","I");
    insectProbId= env->GetFieldID(insectObjCls,"prob","F");
    insectChineseNameId = env->GetFieldID(insectObjCls,"chineseName","Ljava/lang/String;");
    insectLatinNameId = env->GetFieldID(insectObjCls,"latinName","Ljava/lang/String;");

    return JNI_TRUE;
}
JNIEXPORT jobjectArray JNICALL
Java_com_example_plantclassification_1v2_recognitionModel_InsectIdentificationModel_detect(
        JNIEnv *env, jobject thiz, jobject bitmap) {
    AndroidBitmapInfo info;
    AndroidBitmap_getInfo(env,bitmap,&info);
    const int width = info.width;
    const int height = info.height;

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
        return NULL;

    const int target_size = 224;

    int w = width;
    int h = height;
    float scale = 1.0f;
    if (w > h)
    {
        scale = (float)target_size / w;
        w = target_size;
        h = h * scale;
    }
    else
    {
        scale = (float)target_size / h;
        h = target_size;
        w = w * scale;
    }
    ncnn::Mat in_pad = ncnn::Mat::from_android_bitmap_resize(env, bitmap, ncnn::Mat::PIXEL_RGB, w, h);
    const float norm_vals[3] = {1/0.229f/255.f, 1/0.224f/255.f, 1/0.225f/255.f};
    const float mean_vals[3] = {0.485f*255.f, 0.456f*255.f, 0.406f*255.f};
    in_pad.substract_mean_normalize(mean_vals, norm_vals);

    ncnn::Extractor ex = InsectRecognition.create_extractor();
    ex.input("input.1",in_pad);
    ncnn::Mat preds;
    ex.extract("477",preds);
    std::vector<Obj> out;
    Softmax(out,preds);
    int resultSize = 5;
    jobjectArray jObjArray = env->NewObjectArray(resultSize,insectObjCls,NULL);
    for (int i = 0; i < resultSize; ++i) {
        jobject jObj = env->NewObject(insectObjCls,insectConstructorId);
        env->SetIntField(jObj,insectIdxId,out[i].idx);
        env->SetFloatField(jObj,insectProbId,out[i].prob);
        env->SetObjectField(jObj,insectChineseNameId,env->NewStringUTF(insectChineseName[out[i].idx]));
        env->SetObjectField(jObj,insectLatinNameId,env->NewStringUTF(insectLatinName[out[i].idx]));
        env->SetObjectArrayElement(jObjArray,i,jObj);
    }

    return jObjArray;
}
}


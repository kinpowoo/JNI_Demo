//
// Created by DeskTop29 on 2018/9/21.
//

#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <android/log.h>

#define LOG_TAG "LocalRefOverflowActivity"
#define LOG_I(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)
#define LOG_E(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#ifdef __cplusplus
extern "C" {
#endif


jobjectArray getStrings(JNIEnv *env, jobject obj, jint count, jstring sample){
    jobjectArray string_arr;
    jclass cls_string = NULL;
    jmethodID mid_string_init;
    jobject obj_str = NULL;
    const char *c_str_sample = NULL;
    char buff[256];
    int i;

    // 保证至少可以创建3个局部引用（str_array，cls_string，obj_str）
    if ((*env)->EnsureLocalCapacity(env, 3) != JNI_OK) {
        return NULL;
    }

    c_str_sample = (*env)->GetStringUTFChars(env,sample,NULL);
    if(c_str_sample == NULL){
        return NULL;
    }


    cls_string = (*env)->FindClass(env,"java/lang/String");
    if(cls_string == NULL){
        printf("not found class String. \n");
        return NULL;
    }

    mid_string_init = (*env)->GetMethodID(env,cls_string,"<init>","()V");
    if(mid_string_init == NULL){
        (*env)->DeleteLocalRef(env,cls_string);
        printf("find string init construct func failed. \n");
        return NULL;
    }

    obj_str = (*env)->NewObject(env,cls_string,mid_string_init);
    if(obj_str == NULL){
        (*env)->DeleteLocalRef(env,cls_string);
        printf("String obj create failed. \n");
        return NULL;
    }

    string_arr = (*env)->NewObjectArray(env,count,cls_string,obj_str);
    if(string_arr == NULL){
        (*env)->DeleteLocalRef(env,cls_string);
        (*env)->DeleteLocalRef(env,obj_str);
        printf("created string array obj failed. \n");
        return NULL;
    }


    for(i = 0;i<count;i++){
        memset(buff,0, sizeof(buff));
        sprintf(buff,c_str_sample,i);
        jstring str = (*env)->NewStringUTF(env,buff);
        (*env)->SetObjectArrayElement(env,string_arr,i,str);
        (*env)->DeleteLocalRef(env,str);
    }

    //释放java VM内存
    (*env)->ReleaseStringUTFChars(env,sample,c_str_sample);

    (*env)->DeleteLocalRef(env,cls_string);
    (*env)->DeleteLocalRef(env,obj_str);
    return string_arr;
}


const JNINativeMethod g_methods[] ={
        {"getStrings","(ILjava/lang/String;)[Ljava/lang/String;",(void*)getStrings}
};

static jclass g_cls_LocalRefActivity = NULL;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved){
    LOG_I("JNI_OnLoad method call begin");
    JNIEnv* env = NULL;
    jclass cls = NULL;
    if((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // 查找要加载的本地方法Class引用
    cls = (*env)->FindClass(env, "com/szsszwl/jnipro/LocalRefOverflowActivity");
    if(cls == NULL) {
        return JNI_ERR;
    }
    // 将class的引用缓存到全局变量中
    g_cls_LocalRefActivity = (*env)->NewWeakGlobalRef(env, cls);

    (*env)->DeleteLocalRef(env, cls);   // 手动删除局部引用是个好习惯

    // 将java中的native方法与本地函数绑定
    (*env)->RegisterNatives(env, g_cls_LocalRefActivity,g_methods, sizeof(g_methods)/sizeof(JNINativeMethod));
    LOG_I("JNI_OnLoad method call end");

    return JNI_VERSION_1_6;
}



JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm,void *reserved){
    LOG_I("JNI_OnUnload method call begin");
    JNIEnv *env = NULL;
    if((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }
    (*env)->UnregisterNatives(env, g_cls_LocalRefActivity); // so被卸载的时候解除注册
    (*env)->DeleteWeakGlobalRef(env, g_cls_LocalRefActivity);
}









#ifdef __cplusplus
}
#endif
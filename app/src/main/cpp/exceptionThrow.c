//
// Created by DeskTop29 on 2018/9/21.
//
#include <jni.h>
#include <stdlib.h>
#include <android/log.h>


#define LOG_TAG "ExceptionThrowActivity"
#define LOG_I(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG, __VA_ARGS__)
#define LOG_E(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


#ifdef __cplusplus
extern "C" {
#endif


void doit(JNIEnv *env,jobject jobj){
    jclass cls_exception = NULL;
    jmethodID wrong_func_mid;
    jmethodID normal_func_mid;


    cls_exception = (*env)->GetObjectClass(env,jobj);
    if(cls_exception == NULL){
        printf("get class error \n");
        return;
    }

    wrong_func_mid = (*env)->GetMethodID(env,cls_exception,"arithmeticCal","()V");
    if(wrong_func_mid == NULL){
        printf("get wrong func method id error \n");
        (*env)->DeleteLocalRef(env,cls_exception);
        return;
    }

    (*env)->CallVoidMethod(env,jobj,wrong_func_mid);

    //调用会抛出异常的方法后，对JNIEnv进行check，看是否检测到错误
    if((*env)->ExceptionCheck(env)){
        //打印错误信息
        (*env)->ExceptionDescribe(env);
        //清除错误，让程序继续往下执行
        (*env)->ExceptionClear(env);

        //(*env)->DeleteLocalRef(env,cls_exception);      //如果不继续往下执行,删除本地引用

        //抛出java Exception类，并给出异常信息,这将中止Java VM运行
        (*env)->ThrowNew(env,(*env)->FindClass(env,"java/lang/Exception"),"JNI方法抛出异常");

        // return;   //如果此时return,程序将不会往下执行
    }

    normal_func_mid = (*env)->GetStaticMethodID(env,cls_exception,"normalCallback","()V");
    if(normal_func_mid == NULL){
        printf("get NORMAL func method id error \n");
        (*env)->DeleteLocalRef(env,cls_exception);
        return;
    }

    (*env)->CallStaticVoidMethod(env,cls_exception,normal_func_mid);

    //最后删除引用
    (*env)->DeleteLocalRef(env,cls_exception);
}


static JNINativeMethod n_methods[] = {
        {"doit","()V",(void *)doit}
};

//静态全局变量
static jclass exception_cls;


//System.loadLibrary() 时会被调用
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm,void* reserved){
    jclass e_cls;

    JNIEnv *env = NULL;

    if((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    e_cls = (*env)->FindClass(env,"com/szsszwl/jnipro/ExceptionThrowActivity");
    if(e_cls == NULL){
        LOG_I("find class Exception throws Activity failed");
        return JNI_ERR;
    }

    //将本地引用赋给全局引用
    exception_cls = (*env)->NewWeakGlobalRef(env,e_cls);
    //将本地引用删除
    (*env)->DeleteLocalRef(env,e_cls);

    //在这之后就不能用 e_cls 这个本地引用了
    (*env)->RegisterNatives(env,exception_cls,n_methods, sizeof(n_methods)/ sizeof(JNINativeMethod));

    LOG_I("JNI_OnLoad method call end");
    return JNI_VERSION_1_6;
}


// .so library unload
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm,void* reserved){
    LOG_I("JNI_OnUnload method called");
    JNIEnv *env = NULL;
    if((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }

    //反注册本地方法
    (*env)->UnregisterNatives(env,exception_cls);

    //删除全局引用
    (*env)->DeleteWeakGlobalRef(env,exception_cls);
}


#ifdef __cplusplus
}
#endif
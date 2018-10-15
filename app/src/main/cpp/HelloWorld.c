
#include <jni.h>
#include <stdio.h>
#include <string.h>
#include "HelloWorld.h"

JNIEXPORT void JNICALL Java_com_szsszwl_jnipro_HelloWorld_print
  (JNIEnv *env, jobject obj)
{
  printf("Hello World!\n");
  return;
}


JNIEXPORT jstring JNICALL Java_com_szsszwl_jnipro_HelloWorld_getLine
        (JNIEnv *env, jobject obj,jstring prompt){

    char buf[128];
    char* str;
    //(*env)->GetStringUTFRegion(env,prompt,0,(*env)->GetStringUTFLength(env,prompt),str);
    str = (*env)->GetStringUTFChars(env,prompt,JNI_FALSE);
    if (str == NULL) {
        return NULL; /* OutOfMemoryError already thrown */
    }

    char* appendStr = "我是被追加的";

    //合并字符串到缓冲数组上
    sprintf(buf, "%s%s",str,appendStr);

    char *substr = strchr(buf,'i');

    //生成新的UTF-8 String 类型字符串
    jstring newStr = (*env)->NewStringUTF(env,substr);


    //释放内存
    (*env)->ReleaseStringUTFChars(env, prompt, str);

    return newStr;
}
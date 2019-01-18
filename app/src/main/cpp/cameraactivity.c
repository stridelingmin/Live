#include <jni.h>
#include <stdio.h>
#include <string.h>

JNIEXPORT jbyteArray JNICALL
Java_cn_stride1025_live_Native_YUV420spToYUV420p(JNIEnv *env, jobject instance, jint width,
                                                 jint height, jbyteArray data_) {
    jbyte *yuv420p = (*env)->GetByteArrayElements(env, data_, NULL);

    jbyteArray yuv420spRet = (*env)->NewByteArray(env, (*env)->GetArrayLength(env, data_));
    jbyte *yuv420sp = (*env)->GetByteArrayElements(env, yuv420spRet, NULL);
    int i, j;
    int y_size = width * height;

    unsigned char *y = yuv420sp;
    unsigned char *vu = yuv420sp + y_size;

    unsigned char *y_tmp = yuv420p;
    unsigned char *u_tmp = yuv420p + y_size;
    unsigned char *v_tmp = yuv420p + y_size * 5 / 4;

// y
    memcpy(y, y_tmp, y_size);

// v,u
    for (j = 0, i = 0; j < y_size / 2; j += 2, i++) {
        vu[j] = v_tmp[i];
        vu[j + 1] = u_tmp[i];
    }

    (*env)->ReleaseByteArrayElements(env, data_, yuv420p, 0);
    (*env)->ReleaseByteArrayElements(env, yuv420spRet, yuv420sp, 0);
    return yuv420spRet;
}
void saveFile(jbyteArray jbyteArray1){

}
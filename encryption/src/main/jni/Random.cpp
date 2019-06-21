#include <openssl/rand.h>
#include <jni.h>

extern "C"
JNIEXPORT jbyteArray JNIEXPORT
Java_com_sudox_encryption_Encryption_generateBytes(JNIEnv *env, __unused jclass type, jint count) {
    unsigned char buf[count];
    RAND_bytes(buf, count);

    jbyteArray res = env->NewByteArray(count);
    env->SetByteArrayRegion(res, 0, count, reinterpret_cast<jbyte *>(buf));
    return res;
}
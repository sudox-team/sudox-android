#include <scipher/sha/SHA.h>
#include <crypto++/sha.h>
#include <jni.h>
#include <string>

using namespace CryptoPP;
using namespace std;

/**
 * Вычисляет SHA-224 хэш переданного массива байтов.
 *
 * @param data - массив байтов
 * @param length - длина массива байтов
 */
string calculateSHA224(unsigned char *data, unsigned int length) {
    string digest;

    // Calculating hash ...
    SHA224 hash;
    hash.Update(data, length);

    // Getting the result
    digest.resize(hash.DigestSize());
    hash.Final(reinterpret_cast<byte *>(&digest[0]));

    // Return size
    return digest;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_sudox_encryption_Encryption_calculateSHA224(JNIEnv *env, jclass type, jbyteArray data) {
    auto dataLength = static_cast<unsigned int>(env->GetArrayLength(data));
    auto dataNative = new unsigned char[dataLength];

    // Convert to C++ type
    env->GetByteArrayRegion(data, 0, dataLength, reinterpret_cast<jbyte *>(dataNative));

    // Calculate hash & map to jByteArray
    string hash = calculateSHA224(dataNative, dataLength);
    size_t size = hash.size();
    jbyteArray jArray = env->NewByteArray(size);
    env->SetByteArrayRegion(jArray, 0, size, reinterpret_cast<const jbyte *>(hash.c_str()));

    // Clean memory & return result ...
    delete[] dataNative;
    return jArray;
}



package com.sudox.protocol.helper

import android.util.Base64
import java.security.SecureRandom

// Генерирует рандомный Base-64
fun randomBase64String(length: Int): String {
    val bytes = ByteArray(length)

    // Генерируем рандомные байты
    SecureRandom().nextBytes(bytes)

    // Переводим в Base-64
    return Base64.encodeToString(bytes, Base64.DEFAULT)
            .replace("\n", "")
}

// Расшифровывает Base64 в байты
fun decodeBase64(input: ByteArray): ByteArray? {
    return try {
        Base64.decode(input, Base64.NO_PADDING)
    } catch (e: Exception) {
        null
    }
}

// Расшифровывает Base-64 строку
fun decodeBase64String(input: String): ByteArray? {
    return decodeBase64(input.toByteArray())
}

// Зашифровывает байты в Base-64
fun encodeBase64(input: ByteArray): ByteArray {
    return Base64.encode(input, Base64.NO_WRAP)
}

// Зашифровывает строку в Base-64
fun encodeBase64String(input: String): ByteArray {
    return encodeBase64(input.toByteArray())
}

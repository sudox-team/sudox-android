package com.sudox.messenger.api.auth

import com.sudox.events.EventEmitter
import com.sudox.messenger.api.ApiResult

internal const val AUTH_EXCHANGE_DENIED_EVENT = "exchange_not_accepted"
internal const val AUTH_EXCHANGE_ACCEPTED_EVENT = "exchange_accepted"

abstract class AuthApi {

    val authEventEmitter = EventEmitter()

    abstract fun start(phone: String): ApiResult<Boolean>
    abstract fun confirmPhone(phone: String, phoneCode: String, publicKey: ByteArray): ApiResult<Int>
    abstract fun confirmPhone(phone: String, phoneCode: String): ApiResult<Boolean>
    abstract fun finish(phone: String, nickname: String, hash: ByteArray): ApiResult<String>
    abstract fun finish(phone: String, hash: ByteArray): ApiResult<String>
}
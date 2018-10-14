package com.sudox.android.data.models.auth.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AuthSignUpDTO : JsonModel() {

    // For sending ...
    lateinit var email: String
    lateinit var hash: String
    lateinit var code: String
    lateinit var name: String
    lateinit var nickname: String

    // For receiving ...
    lateinit var id: String
    lateinit var secret: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("email", email)
            put("hash", hash)
            put("code", code.toInt())
            put("name", name)
            put("nickname", nickname)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        id = jsonObject.optString("id")
        secret = jsonObject.optString("secret")
    }
}
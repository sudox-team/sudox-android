package com.sudox.android.data.models.auth.dto

import com.sudox.protocol.models.JsonModel
import org.json.JSONObject

class AuthCodeDTO : JsonModel() {

    // For sending
    lateinit var phone: String

    // For receive
    lateinit var hash: String
    var status: Int = -1

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("phone", phone)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {
        hash = jsonObject.optString("hash")
        status = jsonObject.optInt("reg")
    }
}
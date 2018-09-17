package com.sudox.android.common.models

import com.sudox.protocol.model.JsonModel
import org.json.JSONObject

class AuthImportDTO : JsonModel() {

    lateinit var id: String
    lateinit var secret: String

    override fun toJSON(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("secret", secret)
        }
    }

    override fun fromJSON(jsonObject: JSONObject) {}
}
package com.wonddak.loacell.assetData

import io.github.aakira.napier.Napier
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object Translate {
    private const val TAG = "Translate"
    private var data: List<JsonElement> = emptyList()

    fun addData(data: List<JsonElement>) {
        Napier.d(tag = TAG) { "Translate Data Add : $data" }
        this.data = data
    }

    private val cacheKey: MutableMap<String, String> = mutableMapOf()

    fun getTranslate(key: String): String {
        if (cacheKey.containsKey(key)) {
            return cacheKey[key]!!
        }
        for (datum in data) {
            val jsonKey = datum.jsonObject["key"]?.jsonPrimitive?.content
            if (jsonKey == key) {
                val translate = datum.jsonObject["ko"]?.jsonPrimitive?.content ?: key
                cacheKey[key] = translate
                return translate
            }
        }
        cacheKey[key] = key
        return key
    }
}
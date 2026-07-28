package com.wonddak.loacell.network.firebase

import com.wonddak.loacell.network.firebase.model.FBData
import com.wonddak.loacell.network.firebase.model.FBRequest
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FBApi {

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        install(Resources)
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.d(tag = "FBApi") { message }
                }
            }
            level = LogLevel.ALL
        }
        expectSuccess = true
        defaultRequest {
            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "application/json")
            }
        }
    }

    suspend fun getData(request: FBRequest): FBData {
        val response = httpClient.post {
            url {
                protocol = URLProtocol.HTTPS
                host = "getuserinfos-aknb6doirq-uc.a.run.app"
            }
            setBody(request)
        }
        return response.body()
    }

    suspend fun getAssetData(): Map<String, Int> {
        val response = httpClient.post {
            url {
                protocol = URLProtocol.HTTPS
                host = "getassetversion-aknb6doirq-uc.a.run.app"
            }
        }
        val data = response.bodyAsText()
        return json.decodeFromString<Map<String, Int>>(data)
    }
}
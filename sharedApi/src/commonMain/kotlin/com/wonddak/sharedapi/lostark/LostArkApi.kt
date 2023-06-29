package com.wonddak.sharedapi.lostark

import com.wonddak.sharedapi.ApiResult
import com.wonddak.sharedapi.LostArkResult
import com.wonddak.sharedapi.lostark.armories.EquipmentItem
import com.wonddak.sharedapi.lostark.armories.ProfilesItem
import com.wonddak.sharedapi.lostark.model.CharacterInfo
import com.wonddak.sharedapi.lostark.resource.Armories
import com.wonddak.sharedapi.safeRequest
import com.wonddak.sharedapi.toError
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.encodeURLPath
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class LostArkApi {
    companion object {
        const val API_KEY =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyIsImtpZCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyJ9.eyJpc3MiOiJodHRwczovL2x1ZHkuZ2FtZS5vbnN0b3ZlLmNvbSIsImF1ZCI6Imh0dHBzOi8vbHVkeS5nYW1lLm9uc3RvdmUuY29tL3Jlc291cmNlcyIsImNsaWVudF9pZCI6IjEwMDAwMDAwMDAxOTg4MzgifQ.PaE7BfPP2E7kl94kIEs4xHJ6jfZrH6URxNTGSUQbRNROiysUzPfIIVazL5sS3KZ80ry29nvQh8ZbnjHOT1OrwazZNqfu7u5vQweb3hhyBSbV2lCKsgkBA3ruZclvAoYV8rIokKb2QpRSHkDO0vicRyhFR7QtYal-3_NkZ2XW56Qq6pssMTerRbIBA4KtiWAm2gSaLraDJeizrS7v7C3ou5lkUpZic_5PefIIyRS9bDDRJq2N1PkVh9ASLoYVnKgXlBCNmDONAhUDg1hsxTGh00lExQCI6KSGNpYGkpi4YtaOPnhNyFBbC-1d4byozg1WyTjSMIXLlUqsyhRSWYFylw"
        const val API_BASE = "developer-lostark.game.onstove.com"
    }


    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Resources)
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        expectSuccess = true
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = API_BASE
            }
            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "application/json")
                append("authorization", "bearer $API_KEY")
            }
        }
    }

    suspend fun getCharacterInfo(characterName: String): LostArkResult<List<CharacterInfo>> {
        val result: ApiResult<List<CharacterInfo>> = httpClient.safeRequest {
            url.path("characters/${characterName.encodeURLPath()}/siblings")
        }
        return when (result) {
            is ApiResult.Success -> LostArkResult.Success(data = result.data)
            is ApiResult.ErrorOnlyMsg -> LostArkResult.FailOnlyMsg(result.message)
            is ApiResult.Error -> {
                when (val code = result.response.status.value) {
                    503 -> {
                        LostArkResult.Fail(code, "로스트아크 서버가 점검중 입니다.")
                    }
                    else -> {
                        LostArkResult.Fail(code, result.message.toError())
                    }
                }
            }
            is ApiResult.Exception -> LostArkResult.Fail(0,result.e.message.toError())
            is ApiResult.Loading -> LostArkResult.Fail(0,"")
        }
    }

    suspend fun getArmoriesProfiles(characterName: String): ProfilesItem {
        return httpClient.get(Armories.Character.Profiles(Armories.Character(characterName = characterName)))
            .body()
    }

    suspend fun getArmoriesEquipment(characterName: String): List<EquipmentItem> {
        return httpClient.get(Armories.Character.Equipment(Armories.Character(characterName = characterName)))
            .body()
    }

}
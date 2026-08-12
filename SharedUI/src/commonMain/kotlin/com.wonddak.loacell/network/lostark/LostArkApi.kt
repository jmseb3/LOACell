package com.wonddak.loacell.network.lostark

import com.wonddak.loacell.network.ApiResult
import com.wonddak.loacell.network.LostArkResult
import com.wonddak.loacell.network.lostark.LostArkApi.Companion.API_BASE
import com.wonddak.loacell.network.lostark.model.CharacterInfo
import com.wonddak.loacell.network.lostark.model.EventInfo
import com.wonddak.loacell.network.safeRequest
import com.wonddak.loacell.network.toError
import com.wonddak.loacell.util.Config
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.encodeURLPath
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Clock

@SingleIn(AppScope::class)
@Inject
class LostArkApi(
    private val config: Config
) {
    private val module = LostArkApiModule()
    private val eventCacheMutex = Mutex()

    companion object {
        const val API_KEY =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyIsImtpZCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyJ9.eyJpc3MiOiJodHRwczovL2x1ZHkuZ2FtZS5vbnN0b3ZlLmNvbSIsImF1ZCI6Imh0dHBzOi8vbHVkeS5nYW1lLm9uc3RvdmUuY29tL3Jlc291cmNlcyIsImNsaWVudF9pZCI6IjEwMDAwMDAwMDAxOTg4MzgifQ.PaE7BfPP2E7kl94kIEs4xHJ6jfZrH6URxNTGSUQbRNROiysUzPfIIVazL5sS3KZ80ry29nvQh8ZbnjHOT1OrwazZNqfu7u5vQweb3hhyBSbV2lCKsgkBA3ruZclvAoYV8rIokKb2QpRSHkDO0vicRyhFR7QtYal-3_NkZ2XW56Qq6pssMTerRbIBA4KtiWAm2gSaLraDJeizrS7v7C3ou5lkUpZic_5PefIIyRS9bDDRJq2N1PkVh9ASLoYVnKgXlBCNmDONAhUDg1hsxTGh00lExQCI6KSGNpYGkpi4YtaOPnhNyFBbC-1d4byozg1WyTjSMIXLlUqsyhRSWYFylw"
        const val API_BASE = "developer-lostark.game.onstove.com"
        private const val EVENT_CACHE_DURATION_MILLIS = 24 * 60 * 60 * 1_000L
    }

    suspend fun getCharacterInfo(characterName: String): LostArkResult<List<CharacterInfo>> {
        return module.getCharacterInfo(
            characterName = characterName,
            token = config.tokenKey.first() ?: API_KEY,
        )
    }

    suspend fun getEvents(): LostArkResult<List<EventInfo>> = eventCacheMutex.withLock {
        val now = Clock.System.now().toEpochMilliseconds()
        config.getEventCache()?.let { cache ->
            if (now - cache.updatedAt < EVENT_CACHE_DURATION_MILLIS) {
                runCatching {
                    Json.decodeFromString<List<EventInfo>>(cache.events)
                }.getOrNull()?.let { events ->
                    return@withLock LostArkResult.Success(events)
                }
                config.clearEventCache()
            }
        }

        when (val result = module.getEvents(token = config.tokenKey.first() ?: API_KEY)) {
            is LostArkResult.Success -> {
                config.updateEventCache(
                    events = Json.encodeToString(result.data),
                    updatedAt = now,
                )
                result
            }
            else -> result
        }
    }

    suspend fun validateToken(token: String): LostArkResult<List<CharacterInfo>> =
        module.getCharacterInfo(
            characterName = "아이오에스티떡상가즈아",
            token = token,
        )
}

class LostArkApiModule {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Resources)
        expectSuccess = true
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = API_BASE
            }
            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "application/json")
            }
        }
    }

    fun close() {
        httpClient.close()
    }

    @Throws(Throwable::class)
    suspend fun getCharacterInfo(
        characterName: String,
        token: String,
    ): LostArkResult<List<CharacterInfo>> {
        val result: ApiResult<List<CharacterInfo>> = httpClient.safeRequest {
            url.path("characters/${characterName.encodeURLPath()}/siblings")
            headers.append(HttpHeaders.Authorization, "bearer $token")
        }
        return when (result) {
            is ApiResult.Success -> LostArkResult.Success(data = result.data)
            is ApiResult.ErrorOnlyMsg -> LostArkResult.FailOnlyMsg(result.message)
            is ApiResult.Error -> {
                when (val code = result.response.status.value) {
                    401 -> {
                        LostArkResult.Fail(code, "정상적인 토큰이 아닙니다.")
                    }

                    503 -> {
                        LostArkResult.Fail(code, "로스트아크 서버가 점검중 입니다.")
                    }

                    else -> {
                        LostArkResult.Fail(code, result.message.toError())
                    }
                }
            }

            is ApiResult.Exception -> LostArkResult.Fail(0, result.e.message.toError())
            is ApiResult.Loading -> LostArkResult.Fail(0, "")
        }
    }

    @Throws(Throwable::class)
    suspend fun getEvents(token: String): LostArkResult<List<EventInfo>> {
        val result: ApiResult<List<EventInfo>> = httpClient.safeRequest {
            url.path("news/events")
            headers.append(HttpHeaders.Authorization, "bearer $token")
        }
        return when (result) {
            is ApiResult.Success -> LostArkResult.Success(data = result.data)
            is ApiResult.ErrorOnlyMsg -> LostArkResult.FailOnlyMsg(result.message)
            is ApiResult.Error -> {
                when (val code = result.response.status.value) {
                    401 -> LostArkResult.Fail(code, "정상적인 토큰이 아닙니다.")
                    503 -> LostArkResult.Fail(code, "로스트아크 서버가 점검중 입니다.")
                    else -> LostArkResult.Fail(code, result.message.toError())
                }
            }

            is ApiResult.Exception -> LostArkResult.Fail(0, result.e.message.toError())
            is ApiResult.Loading -> LostArkResult.Fail(0, "")
        }
    }
}

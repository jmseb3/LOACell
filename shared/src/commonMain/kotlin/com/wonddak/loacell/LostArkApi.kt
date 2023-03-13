package com.wonddak.loacell

import com.wonddak.loacell.model.CharacterInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import io.ktor.utils.io.charsets.*
import kotlinx.serialization.json.Json

class LostArkApi {
    companion object {
        const val API_KEY = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyIsImtpZCI6IktYMk40TkRDSTJ5NTA5NWpjTWk5TllqY2lyZyJ9.eyJpc3MiOiJodHRwczovL2x1ZHkuZ2FtZS5vbnN0b3ZlLmNvbSIsImF1ZCI6Imh0dHBzOi8vbHVkeS5nYW1lLm9uc3RvdmUuY29tL3Jlc291cmNlcyIsImNsaWVudF9pZCI6IjEwMDAwMDAwMDAxOTg4MzgifQ.PaE7BfPP2E7kl94kIEs4xHJ6jfZrH6URxNTGSUQbRNROiysUzPfIIVazL5sS3KZ80ry29nvQh8ZbnjHOT1OrwazZNqfu7u5vQweb3hhyBSbV2lCKsgkBA3ruZclvAoYV8rIokKb2QpRSHkDO0vicRyhFR7QtYal-3_NkZ2XW56Qq6pssMTerRbIBA4KtiWAm2gSaLraDJeizrS7v7C3ou5lkUpZic_5PefIIyRS9bDDRJq2N1PkVh9ASLoYVnKgXlBCNmDONAhUDg1hsxTGh00lExQCI6KSGNpYGkpi4YtaOPnhNyFBbC-1d4byozg1WyTjSMIXLlUqsyhRSWYFylw"
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
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = API_BASE
            }
            headers {
                append("Content-Type","application/json")
                append("accept","application/json")
                append("authorization","bearer $API_KEY")
            }
        }
    }

    @Throws(Throwable::class)
    suspend fun getCharacterInfo(characterName: String): List<CharacterInfo> {
        return httpClient.get("characters/${characterName.encodeURLPath()}/siblings").body()
    }

}
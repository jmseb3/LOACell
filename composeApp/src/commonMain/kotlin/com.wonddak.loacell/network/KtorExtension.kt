package com.wonddak.loacell.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

suspend inline fun <reified T> HttpClient.safeRequest(
    block: HttpRequestBuilder.() -> Unit,
): ApiResult<T> =
    try {
        val response = request { block() }
        ApiResult.Success(response.body())
    } catch (e: RedirectResponseException) {
        // 3xx ~ error
        ApiResult.Error(
            response = e.response,
            message = e.message
        )
    } catch (e: ClientRequestException) {
        // 4xx ~ error
        val resetTime = runCatching { e.response.headers["retry-after"] }.getOrNull()
        if (resetTime == null) {
            ApiResult.Error(
                response = e.response,
                message = e.message
            )
        } else {
            ApiResult.ErrorOnlyMsg("${resetTime}초 후 다시 시도해 주세요.")
        }
    } catch (e: ServerResponseException) {
        // 5xx ~ error
        ApiResult.Error(
            response = e.response,
            message = e.message
        )
    } catch (e: JsonConvertException) {
        ApiResult.ErrorOnlyMsg("검색 결과가 없습니다.")
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }


suspend inline fun <reified T> HttpClient.safeFlowRequest(
    delaySeconds: Long = 1000L,
    crossinline block: HttpRequestBuilder.() -> Unit,
): Flow<ApiResult<T>> = flow {
    emit(ApiResult.Loading)
    delay(delaySeconds)
    emit(safeRequest(block = block))
}


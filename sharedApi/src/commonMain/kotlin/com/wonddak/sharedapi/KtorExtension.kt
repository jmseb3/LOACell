package com.wonddak.sharedapi

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
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
            code = e.response.status.value,
            message = e.message
        )
    } catch (e: ClientRequestException) {
        // 4xx ~ error
        ApiResult.Error(
            code = e.response.status.value,
            message = e.message
        )
    } catch (e: ServerResponseException) {
        // 5xx ~ error
        ApiResult.Error(
            code = e.response.status.value,
            message = e.message
        )
    } catch (e: Exception) {
        ApiResult.Exception(e)
    }


suspend inline fun <reified T> HttpClient.safeFlowRequest(
    delaySeconds: Long = 1000L,
    crossinline block: HttpRequestBuilder.() -> Unit,
): Flow<ApiResult<T>> = flow {
    emit(com.wonddak.sharedapi.ApiResult.Loading)
    delay(delaySeconds)
    emit(safeRequest(block = block))
}


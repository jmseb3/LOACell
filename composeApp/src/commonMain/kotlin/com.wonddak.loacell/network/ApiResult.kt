package com.wonddak.loacell.network

import io.ktor.client.statement.HttpResponse

fun String?.toError(): String {
    return this ?: "unknown error"
}

sealed class LostArkResult<out T> {
    data class Success<out T>(val data: T) : LostArkResult<T>()
    data class Fail(val code: Int, val message: String) : LostArkResult<Nothing>()
    data class FailOnlyMsg(val message: String) : LostArkResult<Nothing>()
}

inline fun <reified T : Any> LostArkResult<T>.onSuccess(action: (data: T) -> Unit): LostArkResult<T> {
    if (this is LostArkResult.Success) action(data)
    return this
}

inline fun <reified T : Any> LostArkResult<T>.onFail(action: (code: Int, message: String) -> Unit): LostArkResult<T> {
    if (this is LostArkResult.Fail) action(code, message)
    return this
}

inline fun <reified T : Any> LostArkResult<T>.onFailOnlyMsg(action: (message: String) -> Unit): LostArkResult<T> {
    if (this is LostArkResult.FailOnlyMsg) action(message)
    return this
}

inline fun <reified T : Any> LostArkResult<T>.onFailMsg(action: (message: String) -> Unit): LostArkResult<T> {
    if (this is LostArkResult.FailOnlyMsg) action(message) else if (this is LostArkResult.Fail) action(
        "$message($code)"
    )
    return this
}


sealed class ApiResult<out T> {
    //로딩시 (최초값으로 사용하기)
    object Loading : ApiResult<Nothing>() // 상태값이 바뀌지 않는 서브 클래스의 경우 object 를 사용하는 것을 권장

    // 성공적으로 수신할 경우 body 데이터를 반환
    data class Success<out T>(val data: T) : ApiResult<T>()

    // 오류 메시지가 포함된 응답을 성공적으로 수신한 경우
    data class Error(val response: HttpResponse, val message: String?) : ApiResult<Nothing>()
    data class ErrorOnlyMsg(val message: String) : ApiResult<Nothing>()

    //예외 발생시
    data class Exception(val e: Throwable) : ApiResult<Nothing>()
}

//편하게 쓰려고 구현
// inline function .. 반복 개체생성이 안됨
// reified : 인라인(inline) 함수와 reified 키워드를 함께 사용하면 T type에 대해서 런타임에 접근할 수 있게 해줌.
inline fun <reified T : Any> ApiResult<T>.onLoading(action: () -> Unit) {
    if (this is ApiResult.Loading) action()
}

inline fun <reified T : Any> ApiResult<T>.onSuccess(action: (data: T) -> Unit) {
    if (this is ApiResult.Success) action(data)
}

inline fun <reified T : Any> ApiResult<T>.onError(action: (response: HttpResponse, message: String?) -> Unit) {
    if (this is ApiResult.Error) action(response, message)
}

inline fun <reified T : Any> ApiResult<T>.onException(action: (e: Throwable) -> Unit) {
    if (this is ApiResult.Exception) action(e)
}
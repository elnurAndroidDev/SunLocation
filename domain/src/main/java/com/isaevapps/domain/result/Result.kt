package com.isaevapps.domain.result

typealias RootError = Error

sealed interface Result<out D, out E: RootError> {
    val dataOrNull: D?
        get() = (this as? Success)?.data

    val errorOrNull: E?
        get() = (this as? Error)?.error

    data class Success<out D, out E: RootError>(val data: D) : Result<D, E>
    data class Error<out D, out E: RootError>(val error: E) : Result<D, E>
}
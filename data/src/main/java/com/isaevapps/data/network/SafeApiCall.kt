package com.isaevapps.data.network

import com.isaevapps.domain.result.NetworkError
import com.isaevapps.domain.result.Result
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> T,
    map: (T) -> R
): Result<R, NetworkError> {
    return try {
        val response = apiCall()
        Result.Success(map(response))
    } catch (e: IOException) {
        Result.Error(NetworkError.NO_INTERNET)
    } catch (e: HttpException) {
        Result.Error(NetworkError.SERVER_ERROR)
    } catch (e: SocketTimeoutException) {
        Result.Error(NetworkError.TIMEOUT)
    } catch (e: Exception) {
        Result.Error(NetworkError.UNKNOWN)
    }
}
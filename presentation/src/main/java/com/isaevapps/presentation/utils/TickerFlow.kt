package com.isaevapps.presentation.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration

fun tickerFlow(period: Duration): Flow<Unit> = flow {
    while (true) {
        emit(Unit)
        delay(period)
    }
}
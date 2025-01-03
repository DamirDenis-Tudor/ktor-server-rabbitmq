package io.github.damir.denis.tudor.ktor.server.rabbitmq.dsl.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.withContext

suspend fun <T> failover( block : () -> T ): T{
    return withContext(Dispatchers.IO) {
        return@withContext flow {
            emit(block())
        }.retry{ e->
            e is Exception
        }.first()
    }
}
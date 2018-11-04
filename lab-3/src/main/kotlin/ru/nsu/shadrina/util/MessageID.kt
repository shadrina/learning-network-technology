package ru.nsu.shadrina.util

data class MessageID(
        val guid: Int,
        var ttl: Int = MAX_ATTEMPTS * TTL
)
package ru.nsu.shadrina.util

data class MessageInfo(
        val sentMessage: String,
        val receivers: MutableList<Subscriber>,
        var ttl: Int = TTL,
        var attempts: Int = 0
)
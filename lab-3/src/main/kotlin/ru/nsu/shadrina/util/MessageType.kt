package ru.nsu.shadrina.util

open class EnumCompanion<T, V>(private val valueMap: Map<T, V>) {
    fun fromString(type: T) = valueMap[type]
}

enum class MessageType(val value: String) {
    CHILD_BIRTH("0"),
    NEW_MESSAGE("1"),
    CONFIRMATION("2");

    companion object: EnumCompanion<String, MessageType>(MessageType.values().associateBy(MessageType::value))
}

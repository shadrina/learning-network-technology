package ru.nsu.shadrina.util

import java.util.*

fun rand(from: Int, to: Int) : Int {
    val random = Random()
    return random.nextInt(to - from) + from
}

const val GUID_FROM = 0
const val GUID_TO = 10000000

const val TTL = 5
const val TIMEOUT = 1000L
const val MAX_ATTEMPTS = 5
const val MAX_PACKET_SIZE = 1024
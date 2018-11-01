package ru.nsu.shadrina.tree

import ru.nsu.shadrina.util.*
import ru.nsu.shadrina.util.MessageType.*

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.concurrent.LinkedBlockingQueue

class Receiver(
        private val socket: DatagramSocket,
        private val subscribers: LinkedBlockingQueue<Subscriber>,
        private val messagesInfo: LinkedBlockingQueue<MessageInfo>,
        private val messagesToSend: LinkedBlockingQueue<Message>,
        private val history: LinkedBlockingQueue<MessageID>,
        private val lossPercentage: Int
) : Thread() {

    private fun sendConfirmation(payload: String, confirmationReceiver: Subscriber) {
        val confirmation = "2:$payload"
        messagesToSend.put(Message(confirmation, confirmationReceiver))
    }

    override fun run() {
        loop@ while (true) {
            val data = ByteArray(MAX_PACKET_SIZE)
            val packet = DatagramPacket(data, data.size)
            socket.receive(packet)

            val sender = Subscriber(packet.address, packet.port)
            val stringMessage = String(packet.data).replace("\u0000", "")

            if (rand(0, 99) < lossPercentage) continue@loop

            val info = stringMessage.split(":")
            val type = MessageType.fromString(info[0])
            val guid = info[3].toInt()
            if (type != CONFIRMATION && history.any { it.guid == guid }) {
                sendConfirmation("${info[1]}:${info[2]}:${info[3]}", sender)
                continue@loop
            }
            history.put(MessageID(guid))

            val payload = "${info[1]}:${info[2]}:${info[3]}"

            when (type) {
                CHILD_BIRTH -> {
                    subscribers.put(sender)

                    sendConfirmation(payload, sender)
                }
                NEW_MESSAGE -> {
                    println("${info[1]}: ${info[2]}")
                    subscribers.forEach {
                        if (it != sender) messagesToSend.put(Message(stringMessage, it))
                    }
                    val receivers = subscribers.toMutableList()
                    receivers.removeIf { it == sender }
                    messagesInfo.put(MessageInfo(stringMessage, receivers))

                    sendConfirmation(payload, sender)
                }
                CONFIRMATION -> messagesInfo
                        .firstOrNull { it.sentMessage.contains(payload) }
                        ?.receivers
                        ?.removeIf { it == sender }
            }
        }
    }
}

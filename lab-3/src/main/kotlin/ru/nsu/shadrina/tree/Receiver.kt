package ru.nsu.shadrina.tree

import ru.nsu.shadrina.util.Message
import ru.nsu.shadrina.util.MessageInfo
import ru.nsu.shadrina.util.Subscriber
import ru.nsu.shadrina.util.rand
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.concurrent.LinkedBlockingQueue

class Receiver(
        private val socket: DatagramSocket,
        private val subscribers: LinkedBlockingQueue<Subscriber>,
        private val messagesInfo: LinkedBlockingQueue<MessageInfo>,
        private val messagesToSend: LinkedBlockingQueue<Message>,
        private val lossPercentage: Int
) : Thread() {
    override fun run() {
        loop@ while (true) {
            val data = ByteArray(1024)
            val packet = DatagramPacket(data, data.size)
            socket.receive(packet)

            val sender = Subscriber(packet.address, packet.port)
            val stringMessage = String(packet.data)
            val info = stringMessage.split(":")

            when (info[0].toInt()) {
                0 -> subscribers.put(sender)
                1 -> {
                    if (rand(0, 99) < lossPercentage) continue@loop

                    println("${info[1]}: ${info[2]}")
                    subscribers.forEach {
                        if (it != sender) messagesToSend.put(Message(stringMessage, it))
                    }
                    val receivers = subscribers.toMutableList()
                    receivers.removeIf { it == sender }
                    val payload = "${info[1]}:${info[2]}:${info[3]}"
                    messagesInfo.put(MessageInfo("1:$payload", receivers))

                    val confirmation = "2:$payload"
                    messagesToSend.put(Message(confirmation, sender))
                }
                2 -> messagesInfo
                        .firstOrNull {
                            val payload = "${info[1]}:${info[2]}:${info[3]}"
                            val msg = "1:$payload".replace("\u0000", "")
                            val msgToCompare = it.sentMessage.replace("\u0000", "")

                            msg == msgToCompare
                        }?.receivers
                        ?.removeIf { it == sender }
            }
        }
    }
}

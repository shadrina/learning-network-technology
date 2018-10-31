package ru.nsu.shadrina.tree

import ru.nsu.shadrina.util.Message
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.concurrent.LinkedBlockingQueue

class Sender(
        private val socket: DatagramSocket,
        private val messagesToSend: LinkedBlockingQueue<Message>
) : Thread() {
    override fun run() {
        while (true) {
            val message = messagesToSend.take()
            val data = message.message.toByteArray()
            val addressee = message.addressee

            val packet = DatagramPacket(data, data.size, addressee.ip, addressee.port)
            socket.send(packet)
        }
    }
}

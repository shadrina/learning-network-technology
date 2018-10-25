import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.Random

fun rand(from: Int, to: Int) : Int {
    val random = Random()
    return random.nextInt(to - from) + from
}

class Receiver(
        private val myName: String,
        private val socket: DatagramSocket,
        private val subscribers: MutableList<Subscriber>,
        private val messagesInfo: MutableList<MessageInfo>,
        private val messagesToSend: MutableList<Message>,
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
                0 -> subscribers.add(sender)
                1 -> {
                    if (rand(0, 99) < lossPercentage) continue@loop

                    println("${info[1]}: ${info[2]}")
                    subscribers.forEach {
                        if (it != sender) messagesToSend.add(Message(stringMessage, it))
                    }
                    val receivers = subscribers.toMutableList()
                    receivers.removeIf { it == sender }
                    messagesInfo.add(MessageInfo("1:${info[1]}:${info[2]}", receivers))

                    val confirmation = "2:${info[1]}:${info[2]}"
                    messagesToSend.add(Message(confirmation, sender))
                }
                2 -> messagesInfo
                        .firstOrNull {
                            val msg = "1:${info[1]}:${info[2]}".replace("\u0000", "")
                            val msgToCompare = it.sentMessage.replace("\u0000", "")

                            msg == msgToCompare
                        }?.receivers
                        ?.removeIf { it == sender }

            }
        }
    }
}

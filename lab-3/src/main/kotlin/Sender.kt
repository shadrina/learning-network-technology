import java.net.DatagramPacket
import java.net.DatagramSocket

class Sender(
        private val socket: DatagramSocket,
        private val messagesToSend: MutableList<Message>
) : Thread() {
    override fun run() {
        while (true) if (messagesToSend.isNotEmpty()) {
            while (messagesToSend.isNotEmpty()) {
                val message = messagesToSend.first()
                messagesToSend.removeAt(0)
                val data = message.message.toByteArray()
                val addressee = message.addressee

                val packet = DatagramPacket(data, data.size, addressee.ip, addressee.port)
                socket.send(packet)
            }
        }
    }
}

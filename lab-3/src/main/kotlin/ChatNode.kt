import java.net.DatagramSocket
import java.net.InetAddress

data class Subscriber(val ip: InetAddress, val port: Int)
data class Message(val message: String, val addressee: Subscriber)
data class MessageInfo(
        val sentMessage: String,
        val receivers: MutableList<Subscriber>,
        var ttl: Int = 5
)

class ChatNode(
        name: String,
        myPort: Int,
        lossPercentage: Int,
        parentIp: InetAddress? = null,
        parentPort: Int? = null
) {
    private val subscribers = mutableListOf<Subscriber>()

    init {
        val socket = DatagramSocket(myPort, InetAddress.getLocalHost())
        println("Running on ${socket.localAddress}:${socket.localPort}")

        val messagesToSend = mutableListOf<Message>()
        val messagesInfo = mutableListOf<MessageInfo>()

        if (parentIp != null && parentPort != null) {
            val parent = Subscriber(parentIp, parentPort)
            subscribers.add(parent)
            val message = "0:$name"
            messagesToSend.add(Message(message, parent))
        }

        val senderThread = Sender(socket, messagesToSend)
        // val managerThread = Manager(messagesInfo, messagesToSend, subscribers)
        val receiverThread = Receiver(
                name,
                socket,
                subscribers,
                messagesInfo,
                messagesToSend,
                lossPercentage
        )

        senderThread.start()
        // managerThread.start()
        receiverThread.start()

        while (true) {
            val userMessage = readLine()
            val message = "1:$name:$userMessage"
            messagesInfo.add(MessageInfo(message, subscribers.toMutableList()))
            subscribers.forEach {
                messagesToSend.add(Message(message, it))
            }
        }

        senderThread.join()
        // managerThread.join()
        receiverThread.join()

        socket.close()
    }
}
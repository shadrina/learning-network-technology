package ru.nsu.shadrina.tree

import ru.nsu.shadrina.util.*
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.LinkedBlockingQueue

class ChatNode(
        private val name: String,
        myPort: Int,
        lossPercentage: Int,
        private val parentIp: InetAddress? = null,
        private val parentPort: Int? = null
) {
    private val subscribers = LinkedBlockingQueue<Subscriber>()
    private val messagesToSend = LinkedBlockingQueue<Message>()
    private val messagesInfo = LinkedBlockingQueue<MessageInfo>()
    private val history = LinkedBlockingQueue<MessageID>()

    init {
        val socket = DatagramSocket(myPort, InetAddress.getLocalHost())
        println("Running on ${socket.localAddress}:${socket.localPort}")

        registerParent()

        val senderThread = Sender(socket, messagesToSend)
        val managerThread = Manager(messagesInfo, messagesToSend, subscribers, history)
        val receiverThread = Receiver(
                socket,
                subscribers,
                messagesInfo,
                messagesToSend,
                history,
                lossPercentage
        )

        senderThread.start()
        managerThread.start()
        receiverThread.start()

        waitForMessages()

        senderThread.join()
        managerThread.join()
        receiverThread.join()

        socket.close()
    }

    private fun registerParent() {
        if (parentIp != null && parentPort != null) {
            val parent = Subscriber(parentIp, parentPort)
            subscribers.put(parent)
            val guid = rand(GUID_FROM, GUID_TO)
            val message = "0:$name:I am your child!:$guid"
            messagesToSend.put(Message(message, parent))
            messagesInfo.put(MessageInfo(message, mutableListOf(parent)))
        }
    }

    private fun waitForMessages() {
        while (true) {
            val userMessage = readLine()
            val guid = rand(GUID_FROM, GUID_TO)
            val message = "1:$name:$userMessage:$guid"
            messagesInfo.put(MessageInfo(message, subscribers.toMutableList()))
            subscribers.forEach {
                messagesToSend.put(Message(message, it))
            }
        }
    }
}

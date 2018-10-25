const val TIMEOUT = 1000L

class Manager(
        private val messagesInfo: MutableList<MessageInfo>,
        private val messagesToSend: MutableList<Message>,
        private val subscribers: MutableList<Subscriber>
) : Thread() {
    override fun run() {
        while (true) {
            Thread.sleep(TIMEOUT)
            messagesInfo.removeIf { it.receivers.size == 0 }
            messagesInfo.forEach { it.ttl-- }
            messagesInfo
                    .filter { it.ttl == 0 }
                    .forEach { messageInfo ->
                        messageInfo.receivers.forEach {receiver ->
                            messagesToSend.add(Message(messageInfo.sentMessage, receiver))
                        }
                        messageInfo.ttl = 5
                    }
        }
    }
}

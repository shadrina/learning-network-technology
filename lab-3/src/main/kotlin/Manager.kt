const val TTL = 3
const val TIMEOUT = 1000L
const val MAX_ATTEMPTS = 5

class Manager(
        private val messagesInfo: MutableList<MessageInfo>,
        private val messagesToSend: MutableList<Message>,
        private val subscribers: MutableList<Subscriber>
) : Thread() {
    override fun run() {
        while (true) {
            Thread.sleep(TIMEOUT)
            messagesInfo.removeIf {messagesInfo ->
                if (messagesInfo.attempts == MAX_ATTEMPTS) {
                    subscribers.removeIf { subscriber ->
                        messagesInfo.receivers.firstOrNull { it == subscriber } != null
                    }
                }
                messagesInfo.receivers.size == 0 || messagesInfo.attempts == MAX_ATTEMPTS
            }
            messagesInfo.forEach { it.ttl-- }
            messagesInfo
                    .filter { it.ttl == 0 }
                    .forEach { messageInfo ->
                        messageInfo.receivers.forEach {receiver ->
                            messagesToSend.add(Message(messageInfo.sentMessage, receiver))
                        }
                        messageInfo.attempts++
                        messageInfo.ttl = TTL
                    }
        }
    }
}

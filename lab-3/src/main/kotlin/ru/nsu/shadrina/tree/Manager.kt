package ru.nsu.shadrina.tree

import ru.nsu.shadrina.util.*
import java.util.concurrent.LinkedBlockingQueue

class Manager(
        private val messagesInfo: LinkedBlockingQueue<MessageInfo>,
        private val messagesToSend: LinkedBlockingQueue<Message>,
        private val subscribers: LinkedBlockingQueue<Subscriber>,
        private val history: LinkedBlockingQueue<MessageID>
) : Thread() {
    override fun run() {
        while (true) {
            Thread.sleep(TIMEOUT)
            history.takeWhile { it.ttl == 0 }
            history.forEach { it.ttl-- }
            messagesInfo.takeWhile { messagesInfo ->
                if (messagesInfo.attempts == MAX_ATTEMPTS) {
                    subscribers.takeWhile { subscriber ->
                        messagesInfo.receivers.firstOrNull { it == subscriber } != null
                    }
                }
                messagesInfo.receivers.size == 0 || messagesInfo.attempts == MAX_ATTEMPTS
            }

            messagesInfo
                    .map{ it.apply { ttl-- } }
                    .filter { it.ttl == 0 }
                    .forEach { messageInfo ->
                        messageInfo.receivers.forEach {receiver ->
                            messagesToSend.put(Message(messageInfo.sentMessage, receiver))
                        }
                        messageInfo.attempts++
                        messageInfo.ttl = TTL
                    }
        }
    }
}

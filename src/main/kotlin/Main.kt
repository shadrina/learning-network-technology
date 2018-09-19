import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.MulticastSocket

const val MULTICAST_GROUP_ADDRESS = "224.11.11.11"

const val TTL = 10
const val MULTICAST_PORT = 60000
const val MY_PORT = 51111

data class MulticastGroupMemberInfo(val address: InetAddress, var timeRemaining: Int = TTL)

class ServerRunnable(private val datagramSocket: DatagramSocket) : Runnable {
    override fun run() {
        try {
            while (true) {
                val msg = ""
                val msgPacket = DatagramPacket(
                        msg.toByteArray(),
                        msg.toByteArray().size,
                        InetAddress.getByName(MULTICAST_GROUP_ADDRESS),
                        MULTICAST_PORT
                )
                datagramSocket.send(msgPacket)
                Thread.sleep(5000)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
        }
    }
}

class ClientRunnable(private val multicastSocket: MulticastSocket) : Runnable {
    val others = mutableListOf<MulticastGroupMemberInfo>()
    override fun run() {
        while (true) {
            val buf = ByteArray(1024)
            val packet = DatagramPacket(buf, buf.size)
            multicastSocket.receive(packet)
            if (!others.any { it.address == packet.address }) {
                println("New member ${packet.address}:${packet.port}")
                others.add(MulticastGroupMemberInfo(packet.address))
                others.forEach { println(it.address) }
                println()
            } else {
                others.forEach { if (it.address == packet.address) it.timeRemaining++ }
            }
            others.forEach { it.timeRemaining-- }
            others.removeIf { it.timeRemaining == 0 }
        }
    }

}

fun main(args: Array<String>) {
    val datagramSocket = DatagramSocket(MY_PORT, InetAddress.getByName(args[0]))
    val multicastSocket = MulticastSocket(MULTICAST_PORT)
    val addr = InetAddress.getByName(MULTICAST_GROUP_ADDRESS)
    multicastSocket.joinGroup(addr)

    val clientRunnable = ClientRunnable(multicastSocket)
    val serverRunnable = ServerRunnable(datagramSocket)

    val clientThread = Thread(clientRunnable)
    val serverThread = Thread(serverRunnable)

    clientThread.start()
    serverThread.start()

    clientThread.join()
    serverThread.join()

    multicastSocket.leaveGroup(addr)
    multicastSocket.close()
    datagramSocket.close()
}
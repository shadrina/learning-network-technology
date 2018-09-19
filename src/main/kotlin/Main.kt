import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.MulticastSocket

const val TTL = 10
const val MULTICAST_PORT = 60000
const val MY_PORT = 51111

data class MulticastGroupMemberInfo(val address: InetAddress, var timeRemaining: Int = TTL)

class ServerRunnable(
        private val datagramSocket: DatagramSocket,
        private val multicastAddress: InetAddress) : Runnable {
    override fun run() {
        try {
            while (true) {
                val msg = ""
                val msgPacket = DatagramPacket(
                        msg.toByteArray(),
                        msg.toByteArray().size,
                        multicastAddress,
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
    val myAddress = InetAddress.getByName(args.getOrNull(0))
    if (!myAddress.isLoopbackAddress) {
        println("Not a loopback address")
        return
    }
    val multicastAddress = InetAddress.getByName(args.getOrNull(1))
    if (!multicastAddress.isMulticastAddress) {
        println("Not a multicast address")
        return
    }

    val datagramSocket = DatagramSocket(MY_PORT, myAddress)
    val multicastSocket = MulticastSocket(MULTICAST_PORT)
    multicastSocket.joinGroup(multicastAddress)

    val clientRunnable = ClientRunnable(multicastSocket)
    val serverRunnable = ServerRunnable(datagramSocket, multicastAddress)

    val clientThread = Thread(clientRunnable)
    val serverThread = Thread(serverRunnable)

    clientThread.start()
    serverThread.start()

    clientThread.join()
    serverThread.join()

    multicastSocket.leaveGroup(multicastAddress)
    multicastSocket.close()
    datagramSocket.close()
}
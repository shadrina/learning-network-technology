import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.MulticastSocket

const val MULTICAST_GROUP_ADDRESS = "224.11.11.11"
const val MULTICAST_PORT = 60000
const val MY_PORT = 51111

class ServerRunnable(val datagramSocket: DatagramSocket) : Runnable {
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
                Thread.sleep(1000)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        } catch (ex: InterruptedException) {
            ex.printStackTrace()
        }
    }
}

class ClientRunnable(val multicastSocket: MulticastSocket) : Runnable {
    override fun run() {
        while (true) {
            val buf = ByteArray(1024)
            val packet = DatagramPacket(buf, buf.size)
            multicastSocket.receive(packet)
            println("Received data from: " + packet.address.toString() + ":" + packet.port);
        }
    }

}

fun main(args: Array<String>) {
    val datagramSocket = DatagramSocket(MY_PORT)
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
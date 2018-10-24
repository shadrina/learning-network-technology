import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.Random

const val TTL = 10
const val MULTICAST_PORT = 24242
const val MY_PORT = 50505

data class MulticastGroupMemberInfo(val address: InetAddress, var timeRemaining: Int = TTL)

class ServerRunnable(
        private val datagramSocket: DatagramSocket,
        private val multicastAddress: InetAddress
) : Runnable {
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
                Thread.sleep(20000)
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
            println("Got!")
            if (!others.any { it.address == packet.address }) {
                println("New member ${packet.address}:${packet.port}")
                others.add(MulticastGroupMemberInfo(packet.address))
                others.forEach { println(it.address) }
                println()
            } else {
                others.forEach { if (it.address == packet.address) it.timeRemaining = TTL }
            }
            others.forEach { it.timeRemaining-- }

            if (others.any { it.timeRemaining == 0 }) {
                others.forEach { if (it.timeRemaining == 0) println(it.address.toString() + " is leaving!") }
                others.filter { it.timeRemaining != 0 }.forEach { println(it.address) }
                println()
            }
            others.removeIf { it.timeRemaining == 0 }
        }
    }
}

fun main(args: Array<String>) {
    val random = Random()
    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }
    val number = rand(1, 255)
    val myAddress = InetAddress.getByName("127.0.0.$number")
    if (!myAddress.isLoopbackAddress) {
        println("Not a loopback address")
        return
    }
    val multicastAddress = InetAddress.getByName(args.getOrNull(0))
    if (!multicastAddress.isMulticastAddress) {
        println("Not a multicast address")
        return
    }


    val datagramSocket = DatagramSocket(MY_PORT, myAddress)
    val multicastSocket = MulticastSocket(MULTICAST_PORT)
    println(multicastSocket.port)
    multicastSocket.joinGroup(multicastAddress)

    val clientThread = Thread(ClientRunnable(multicastSocket))
    val serverThread = Thread(ServerRunnable(datagramSocket, multicastAddress))

    clientThread.start()
    serverThread.start()

    clientThread.join()
    serverThread.join()

    multicastSocket.leaveGroup(multicastAddress)
    multicastSocket.close()
    datagramSocket.close()
}
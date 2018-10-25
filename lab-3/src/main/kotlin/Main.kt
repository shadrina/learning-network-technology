import java.net.InetAddress

fun main(args: Array<String>) {
    val (name, port, lossPercentage) = args
    val parentIp = args.getOrNull(3)
    val parentPort = args.getOrNull(4)
    ChatNode(
            name,
            port.toInt(),
            lossPercentage.toInt(),
            if (parentIp != null) InetAddress.getByName(parentIp) else null,
            parentPort?.toInt()
    )
}

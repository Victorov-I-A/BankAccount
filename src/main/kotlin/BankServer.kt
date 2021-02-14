import java.io.BufferedReader
import java.net.ServerSocket
import java.net.SocketException


class BankServer {
    fun startServer() {
        try {
            val dataBase = BankAccount()

            ServerSocket(9999).use { server -> // создаём сервер
                println("Server is running on port: ${server.localPort}")

                while (true) {
                    val sender = server.accept()
                    if (sender.isConnected) {
                        println("Client connected: ${sender.isConnected} -> ${sender.inetAddress.hostAddress}:${sender.localPort}")

                        val message = BufferedReader(sender.getInputStream().reader()).readLine().split(" ")
                        println("Server get message: $message")

                        if (message[0] == "close") {
                            ClientThread(dataBase, message, sender)
                            break
                        } else {
                            ClientThread(dataBase, message, sender)
                        }
                    }
                }
            }
            println("Server was closed")
        } catch (e: SocketException) {
            println("Error: server was not closed or there was another problem")
            e.printStackTrace()
        }
    }
}
import java.io.BufferedReader
import java.net.ServerSocket
import java.net.SocketException


class BankServer {
    fun startServer() {
        try {
            val dataBase = BankAccounts()

            ServerSocket(9999).use { server -> // создаём сервер
                println("Server is running on port: ${server.localPort}")

                while (true) {
                    val sender = server.accept() //создаём новый сокет клиенту
                    if (sender.isConnected) {
                        println("Client connected: ${sender.isConnected} -> " +
                                "${sender.inetAddress.hostAddress}:${sender.localPort}")
                        //принимаем сообщение от клиента
                        val message = BufferedReader(sender.getInputStream().reader()).readLine().split(" ")
                        println("Server get message: $message")

                        if (message[0] == "close" && message.size == 1) { //проверяем на запрос закрытия сервера
                            ClientThread(dataBase, message, sender)
                            break
                        } else {
                            ClientThread(dataBase, message, sender)
                        }
                    }
                }
            }
            println("Server is closed")
        } catch (e: SocketException) {
            println("Error: server was not closed or there was another problem")
            e.printStackTrace()
        }
    }
}
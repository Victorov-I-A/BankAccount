import java.net.Socket
import java.net.SocketException


class ClientThread(private val dataBase: BankAccount,
                   private val message: List<String>,
                   private val sender: Socket): Thread() {

    init { this.start() }

    override fun run() {
        val answer = messageHandler()

        if (sender.isConnected) { // проверяем соединение с клиентом
            sender.getOutputStream().write(answer.toByteArray()) // отправляем ответ клиенту
            println("Server send message: $answer")
            try {
                sender.close() //закрываем клиентский сокет
            } catch (e: SocketException) {
                println("Error: socket was not closed")
                e.printStackTrace()
            }
        }
    }

    private fun messageHandler(): String { // обрабатываем сообщение на ошибки, отправляем запрос в базу данных
        if (message[0] == "close" && message.size == 1) { // проверяем на закрытие сервера
            return "Server is closed"
        }
        if (message.size > 2) { // проверяем количество аргументов
            return "Error: too many arguments"
        }
        if (message.size < 2) { // проверяем количество аргументов
                return "Error: arguments are not enough"
        }
        if (!message[1].matches(Regex("""[0-9]+"""))) { // проверяем наличие числа в запросе
            return "Error: deposit is not found"
        }
        return when (message[0]) { // обрабатываем команду из запроса
            "get" -> dataBase.doDeposit(-message[1].toInt())

            "put" -> dataBase.doDeposit(message[1].toInt())

            else -> "Error: command is not found"
        }
    }
}
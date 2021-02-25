import java.net.Socket
import java.net.SocketException


class ClientThread(private val dataBase: BankAccounts,
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
        return when {
            message.size == 1 && message[0] == "close" -> { //проверяем на закрытие сервера
                dataBase.upload()
                "Server is closed"
            }
            message.size == 3 && message[2] == "check" -> { //отвечаем на запрос
                val answer = dataBase.use(message[0], message[1], 0)

                if (answer.first == 1) {
                    "On account: ${answer.second}"
                } else {
                    "Error: wrong card number or PIN"
                }
            }
            message.size > 4 -> "Error: too many arguments" //проверяем на превышение кол-ва аргументов

            message.size < 4 -> "Error: arguments are not enough" //проверяем на недостаток кол-ва аргументов

            !message[3].matches(Regex("""[0-9]+""")) -> "Error: deposit is not found" //проверяем на наличие депозита

            message[2] == "get" -> { //отвечаем на запрос
                val answer = dataBase.use(message[0], message[1], -message[3].toInt())
                when (answer.first) {
                    -1 -> "Error: wrong card number or PIN"

                    0 -> "Warning: not enough money. On account: ${answer.second}"

                    else  -> "Success of the operation. On account: ${answer.second}"
                }
            }
            message[2] == "put" -> { //отвечаем на запрос
                val answer = dataBase.use(message[0], message[1], message[3].toInt())
                when (answer.first) {
                    -1 -> "Error: wrong card number or PIN"

                    0 -> "Warning: too mach money. On account: ${answer.second}"

                    else  -> "Success of the operation. On account: ${answer.second}"
                }
            }
            else -> "Error: command is not found" //ничего не произошло -> команда неправильная
        }
    }
}
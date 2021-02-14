import java.io.File
import kotlin.Int.Companion.MIN_VALUE


class BankAccount {

    @Synchronized
    fun doDeposit(deposit: Int): String { //убрать в польз. поток и синхронизировать два метода?
        val previousNumber = download()

        return when (val updateNumber = previousNumber + deposit) { // проверка результата на больше/меньше
            in 100_001..Int.MAX_VALUE -> {
                upload(previousNumber)
                "Error: too mach money on account"
            }
            in -1 downTo MIN_VALUE -> {
                upload(previousNumber)
                "Error: not enough money on account"
            }
            else -> {
                upload(updateNumber)
                "Success of the operation"
            }
        }
    }

    private fun download(): Int {
        return File("dataBase.txt")
            .bufferedReader().readLine()
            .replace("_", "")
            .toInt()
    }

    private fun upload(number: Int) {
        val bufferedWriter = File("dataBase.txt").bufferedWriter()
        bufferedWriter.write(number.toString())
        bufferedWriter.close()
        //File("dataBase.txt").bufferedWriter().write(number.toString())
    }
}
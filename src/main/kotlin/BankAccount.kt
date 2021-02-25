import java.io.File
import kotlin.Int.Companion.MIN_VALUE


class BankAccount {

    @Synchronized
    fun use(card: String, pin: String, deposit: Int = 0): String {
        val bankAccounts = download()
        var useAccount: List<String>? = null

        for (account in bankAccounts) {
            if (account[0] == card && account[1] == pin) {
                useAccount = account
            }
        }
        if (useAccount == null) {
            upload(bankAccounts)
            return "Error: wrong card number or PIN"
        }

        return when (val updateNumber = useAccount[2].toInt() + deposit) { // проверка результата на больше/меньше
            in 100_001..Int.MAX_VALUE -> {
                upload(bankAccounts)
                "Error: too mach money. On account ${useAccount[2]}"
            }
            in -1 downTo MIN_VALUE -> {
                upload(bankAccounts)
                "Error: not enough money. On account ${useAccount[2]}"
            }
            else -> {
                bankAccounts.remove(useAccount)
                bankAccounts.add(listOf(card, pin, updateNumber.toString()))
                upload(bankAccounts)
                "Success of the operation. On account $updateNumber"
            }
        }
    }

    private fun download(): MutableList<List<String>> { // выгружаем данные из БД
        val oldData = mutableListOf<List<String>>()
        for (line in File("dataBase.txt").bufferedReader().readLines()) {
            oldData.add(line.split("_"))
        }
        return oldData
    }

    private fun upload(newData: MutableList<List<String>>) {
        val bufferedWriter = File("dataBase.txt").bufferedWriter()
        for (line in newData) {
            bufferedWriter.write(line[0] + "_" + line[1] + "_" + line[2])
            bufferedWriter.newLine()
        }
        bufferedWriter.close()
    }
}
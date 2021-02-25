import java.io.File


class BankAccounts {
    private val data: MutableList<List<String>> //храним данные из бд

    init {
        data = download() //при создании объекта выгружаем данные из бд
    }

    private fun download(): MutableList<List<String>> {  //загрузка данных из бд
        val oldData = mutableListOf<List<String>>()
        for (line in File("dataBase.txt").bufferedReader().readLines()) {
            oldData.add(line.split("_"))
        }
        return oldData
    }

    @Synchronized
        fun use(card: String, pin: String, deposit: Int = 0): Pair<Int, Int> { //исполняем запрос, формируем ответ обработчику
        val useAccount = data.find { it[0] == card && it[1] == pin } ?: return Pair(-1, 0)
        val newValue: Int = useAccount[2].toInt() + deposit

        if (newValue in 0..100_000) {
            data.remove(useAccount)
            data.add(listOf(card, pin, newValue.toString()))
            return Pair(1, newValue)
        }

        return Pair(0, newValue)
    }

    @Synchronized
    fun upload() { //выгрузка данных в бд
        val bufferedWriter = File("dataBase.txt").bufferedWriter()
        for (line in data) {
            bufferedWriter.write(line[0] + "_" + line[1] + "_" + line[2])
            bufferedWriter.newLine()
        }
        bufferedWriter.close()
    }
}
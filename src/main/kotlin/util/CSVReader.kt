package util

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

object CSVReader {

    fun getCSVColumnsByIndex(filepath: String, indexOfColumns: Int = 47): List<String> {
        val list = mutableSetOf<String>()
        csvReader().open(filepath) {
            readAllAsSequence().drop(1).forEach { row ->
                list.add(row[85])
            }
        }
        println(list.size)
        return list.toList()
    }
}
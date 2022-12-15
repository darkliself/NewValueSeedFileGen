package util

import com.monitorjbl.xlsx.StreamingReader
import java.io.File
import java.io.FileInputStream
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

object FileReader {

    fun pickFile(extension: String): String {
        val jfc = JFileChooser(FileSystemView.getFileSystemView().homeDirectory)
        jfc.currentDirectory = File(System.getProperty("user.dir"))
        val returnValue = jfc.showOpenDialog(null)
        return if (returnValue == JFileChooser.APPROVE_OPTION) {
            val selectedFile = jfc.selectedFile
            if (selectedFile.absolutePath.endsWith(extension)) return selectedFile.absolutePath.toString() else ""
        } else ""
    }

    fun getCategoryIdWithSG(filePath: String): MutableMap<String, MutableList<String>> {
        val listOfCategories = mutableMapOf<String, MutableList<String>>()
        val staplesDumpFile = FileInputStream(File(filePath))
        val workBook = StreamingReader.builder()
            .rowCacheSize(100) // number of rows to keep in memory (defaults to 10)
            .bufferSize(4096) // buffer size to use when reading InputStream to file (defaults to 1024)
            .open(staplesDumpFile) // Input

        val fullDump = mutableListOf<MutableList<String>>()
        workBook.getSheetAt(0).forEach { row ->
            val tmp = mutableListOf<String>()
            row.forEach { cell ->
                tmp.add(cell.stringCellValue)
            }
            fullDump.add(tmp)
        }
        fullDump.drop(1).forEach { row ->
          // val tmp = mutableListOf<String>()
            if  (listOfCategories.containsKey(row[1]) && !listOfCategories[row[1]]?.contains(row[3])!!) {
                listOfCategories[row[1]]?.add(row[3])
            } else {
                listOfCategories[row[1]] = mutableListOf(row[3])
            }
//            row.forEach {
//                tmp.add(it)
//            }
//            listOfCategories[row[1]] = tmp
        }
        listOfCategories.forEach {
            println(it)
        }
        return listOfCategories
    }
    fun generateListForFileOutputStream(filePath: String, map: Map<String, List<String>>): Map<String, List<String>> {
        val staplesDumpFile = FileInputStream(File(filePath))
        val result = mutableMapOf<String, MutableList<String>>()
        val workBook = StreamingReader.builder()
            .rowCacheSize(100) // number of rows to keep in memory (defaults to 10)
            .bufferSize(4096) // buffer size to use when reading InputStream to file (defaults to 1024)
            .open(staplesDumpFile) // Input

        val fullDump = mutableListOf<MutableList<String>>()
        workBook.getSheetAt(0).forEach { row ->
            val tmp = mutableListOf<String>()
            row.forEach { cell ->
                tmp.add(cell.stringCellValue)
            }
            fullDump.add(tmp)
        }
        // println(fullDump)
        result["headers"] = mutableListOf(
            "{L=0}",
            "{I=0}",
            "NAME",
            "EXPRESSION",
            "EXPRESSION_STATUS",
            "#8",
            "#9",
            "#12",
            "#45",
            "#10",
            "#13",
            "#14",
            "#48"
        )
        fullDump.forEach { row ->
            if (map.containsKey(row[4]) && map[row[4]]!!.contains(row[9])) {
                if (result.containsKey(row[4]+row[9])) {
                    result[row[4]+row[9]]?.set(10, "${result[row[4]+row[9]]!![10]}|${row[14]}")
                } else {
                    result[row[4]+row[9]] = mutableListOf(
                        row[3],
                        row[4],
                        row[8],
                        "Add(R(\"cnet_common_${row[9]}\"));",
                        "ready",
                        row[13].replace("N", "OPTIONAL").replace("Y", "REQUIRED"),
                        row[10].replace("number", "DECIMAL")
                            .replace("text", "TEXT")
                            .replace("List Of Values", "LIST"),
                        row[9],
                        "1",
                        "0",
                        row[14]?: "",
                        row[16]?: "",
                    )
                }
            }
        }
        result.let { copy ->
            copy.forEach {
                result[it.key]?.add(it.value[10].split("|").size.toString())
            }
        }
        result["headers"]!![result["headers"]!!.size - 1] = ""

        result.forEach {
            println(it)
        }
        result.size

        // common file generator
//        val commonPattenResult = mutableMapOf<String, MutableList<String>>()
//
//        result.forEach {
//            if (commonPattenResult.containsKey("SP-${it.key.split("SP-").last()}")) {
//
//            } else {
//                val commonKey = "SP-${it.key.split("SP-").last()}"
//                commonPattenResult[commonKey] = it.value
//                commonPattenResult[commonKey]?.set(0, "COMMON")
//                commonPattenResult[commonKey]?.set(1, "COMMON_SECTIONS")
//                commonPattenResult[commonKey]?.set(3, "")
//            }
//        }

        return result
    }
}

fun main() {
    val file = File("/../../../Staples Style Guides.xlsx")
    println(file.absoluteFile.toString())
}
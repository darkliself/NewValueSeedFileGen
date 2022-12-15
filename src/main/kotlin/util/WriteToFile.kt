package util

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

object FileWriter {
    fun write(rowsToWrite: Map<String, List<String>>) {
        val myWorkBook = XSSFWorkbook()
        val myWorkList = myWorkBook.createSheet("seed_request")
        var row = 0
        var column = 0
        myWorkList.createRow(0)
        rowsToWrite.forEach { (_, value) ->
            myWorkList.createRow(row)
            value.forEach {
                myWorkList.getRow(row).createCell(column).setCellValue(it)
                column++
            }
            row++
            column = 0
        }
        val file = File("new_values_seed_file.xlsx")
        val output = FileOutputStream(file)
        // seedFilePath = file.absolutePath
        myWorkBook.write(output)
        myWorkBook.close()
    }
}
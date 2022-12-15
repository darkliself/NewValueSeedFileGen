// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import util.CSVReader
import util.FileReader
import util.FileWriter

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    var newValuesList: Map<String, List<String>>? = null
    var planIODumpFile: Map<String, List<String>>? = null
    var listOfCategoriesOnPlanIo: List<String>? = null
    var filteredCategories: Map<String, List<String>>? = null
    MaterialTheme {

        Column {
            Button(onClick = {
                val src = FileReader.pickFile(".xlsx")
                println(src)
              newValuesList = FileReader.getCategoryIdWithSG(src)
            }) {
                Text("Open file with new values")
            }
            Button(onClick = {
                val src = FileReader.pickFile(".csv")
                println(src)
                // ProdCatLev4-135391203220757
                listOfCategoriesOnPlanIo = CSVReader.getCSVColumnsByIndex(src)
                println(listOfCategoriesOnPlanIo)
            }) {
                Text("Open plan.io dump file of Staples BB")
            }
            Button(onClick = {
                val src = FileReader.pickFile(".xlsx")
                println(src)
            }) {
                Text("Open client Staple dump file")
            }
            Button(onClick = {
                if (newValuesList != null && listOfCategoriesOnPlanIo != null) {
                    filteredCategories = takeWhatWeNeed(newValuesList!!, listOfCategoriesOnPlanIo!!)
                    println(filteredCategories)
                    println(filteredCategories!!.size)
                    val t = FileReader.generateListForFileOutputStream(FileReader.pickFile(".xlsx"), filteredCategories!!)
                    FileWriter.write(t)
                }
            }) {
                Text("make test")
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}


fun takeWhatWeNeed(map: Map<String, List<String>>, list: List<String>): Map<String, List<String>> {
    val result = mutableMapOf<String, List<String>>()
    map.forEach {
        if (list.contains(it.key)) {
            result[it.key] = it.value
        }
    }
    return result
}
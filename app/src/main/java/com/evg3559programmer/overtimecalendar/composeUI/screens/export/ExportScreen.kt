package com.evg3559programmer.overtimecalendar.composeUI.screens.export

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.evg3559programmer.overtimecalendar.R
import com.evg3559programmer.overtimecalendar.composeUI.screens.fragments.minuteToHours
import com.evg3559programmer.overtimecalendar.di.WorkDay
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.DuplicateHeaderMode
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.WorkbookUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.time.LocalDate


/*
Список работ:
- 1pr балл
*/



@Composable
fun ExportScreen(list: List<WorkDay>) {

//    val list = listOf<WorkDay>(WorkDay(0,2023,1,10, 240, "чибрикит", "#FFFFFF"),
//        WorkDay(1,2023,1,12, 290, "крепость", "#FFFFFF"),
//        WorkDay(2,2023,1,15, 360, "Ладно", "#FFFFFF"),
//        WorkDay(3,2023,1,117, 140, "пуля", "#FFFFFF") )

    val context = LocalContext.current
    val contextResource = LocalContext.current.resources
    var exportStateText by remember { mutableStateOf("") }
    var exportStateTextStyle by remember { mutableStateOf(TextStyle(fontSize = 14.sp, color = Color(0xFF4D4545))) }
    var exportError by remember { mutableStateOf("") }
    //val scope = rememberCoroutineScope()
    val exportList = list
    val scrollState = rememberScrollState()
    val extension = remember { mutableStateOf(extensionState.exportCSV.string) }       //выбор формата файла
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit){
        startDate = minmaxDate("min", list)
        endDate = minmaxDate("max", list)
    }


    LaunchedEffect(exportStateText){
        when (exportStateText){
            contextResource.getString(R.string.NoSave_export) -> exportStateTextStyle =TextStyle(fontSize = 14.sp, color = Color(0xFF1009D3), fontWeight = FontWeight.Normal)               //не сохранилось
            contextResource.getString(R.string.Success_export) -> exportStateTextStyle =TextStyle(fontSize = 14.sp, color = Color(0xFF60CF1E), fontWeight = FontWeight.Bold)
            contextResource.getString(R.string.Error_export) -> exportStateTextStyle =TextStyle(fontSize = 14.sp, color = Color(0xFFFC1717), fontWeight = FontWeight.Bold )
            else -> exportStateTextStyle =TextStyle(fontSize = 14.sp, color = Color(0xFF4D4545), fontWeight = FontWeight.Normal)
        }
    }

    Column(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        //добавить разные варианты дат.
        val exportCSV = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) {
            if (it != null) {
                try {
                    context.contentResolver.openOutputStream(it)?.bufferedWriter()?.apply {
                        val csvPrinter = CSVPrinter(this.append('\ufeff'), CSVFormat.Builder.create()                //'\ufeff' для поддержки русского языка "метки порядка байтов"
                            .setDelimiter(';')
                            .setRecordSeparator("\r\n")
                            .setIgnoreEmptyLines(false)
                            .setDuplicateHeaderMode(DuplicateHeaderMode.ALLOW_ALL)
                            .setHeader(contextResource.getString(R.string.Date_export), contextResource.getString(R.string.TimeWorked_export), contextResource.getString(R.string.Comment_export))                                     //названия столбцов
                            .setAllowMissingColumnNames(true)
                            .build())

                        //val printer = CSVPrinter(PrintWriter("nlp.csv", "UTF-8"), CSVFormat.EXCEL.withDelimiter("|"[0]))

                        for (day in exportList) {
                            val date = try { LocalDate.of(day.year,day.month, day.day) }
                            catch (e:Exception){ LocalDate.of(day.year,day.month, 1) }
                            val wHours = minuteToHours(day.worked).hours
                            val wMin = minuteToHours(day.worked).minutes
                            val worked = "$wHours:${ if (wMin == 0) "00" else {if (wMin < 10) "0$wMin" else wMin}}"

                            val comment = day.comment
                            val color = day.color

                            csvPrinter.printRecord("$date", worked, comment)
                        }
                        csvPrinter.printRecord("")
                        val summ = exportList.sumOf {it.worked }
                        val wHours = minuteToHours(summ).hours
                        val wMin = minuteToHours(summ).minutes
                        val worked = "$wHours:${ if (wMin == 0) "00" else {if (wMin < 10) "0$wMin" else wMin}}"
                        csvPrinter.printRecord(contextResource.getString(R.string.TotalTable_export), worked, contextResource.getString(R.string.TotalShifts_export, exportList.size) )


                        csvPrinter.flush()
                        csvPrinter.close()
                        val folder = it.lastPathSegment?.substringAfterLast(":")
                        exportStateText = contextResource.getString(R.string.Success_export)
                        exportError = "$folder"
                    }

                } catch (e: Exception) {
                    exportStateText = contextResource.getString(R.string.Error_export)
                    exportError = " ${e.message}"
                }
            }else {
                exportStateText = contextResource.getString(R.string.NoSave_export)
                exportError = ""
            }
        }

        val exportXLS = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/vnd.ms-excel")) {
            if (it != null) {
                try {
                    context.contentResolver.openOutputStream(it)?.bufferedWriter()?.apply {
                        val folder = it.lastPathSegment?.substringAfterLast(":")

                        val wb = XSSFWorkbook()
                        val safeName = WorkbookUtil.createSafeSheetName("Listing")
                        val sheet3: Sheet = wb.createSheet(safeName)
                        val file = File(folder.toString())

                    //    wb.write(this)


//                        //file path
//                            val file = File(it)
//                            val wbSettings = WorkbookSettings()
//                            //wbSettings.setLocale(Locale(Locale.ENGLISH .getLanguage(), Locale.ENGLISH.getCountry()))
//                            val workbook: WritableWorkbook
//                            workbook = Workbook.createWorkbook(file, wbSettings)
//
//                            //Excel sheetA first sheetA
//                            val sheetA: WritableSheet = workbook.createSheet("sheet A", 0)
//
//                            // column and row titles
//                            sheetA.addCell(Label(0, 0, "sheet A 1"))
//                            sheetA.addCell(Label(1, 0, "sheet A 2"))
//                            sheetA.addCell(Label(0, 1, "sheet A 3"))
//                            sheetA.addCell(Label(1, 1, "sheet A 4"))
//
//                            //Excel sheetB represents second sheet
//                            val sheetB: WritableSheet = workbook.createSheet("sheet B", 1)
//
//                            // column and row titles
//                            sheetB.addCell(Label(0, 0, "sheet B 1"))
//                            sheetB.addCell(Label(1, 0, "sheet B 2"))
//                            sheetB.addCell(Label(0, 1, "sheet B 3"))
//                            sheetB.addCell(Label(1, 1, "sheet B 4"))
//
//                            // close workbook
//                            workbook.write()
//                            workbook.close()

                        for (day in exportList) {
                            val date = try { LocalDate.of(day.year,day.month, day.day) }
                            catch (e:Exception){ LocalDate.of(day.year,day.month, 1) }
                            val worked = day.worked
                            val comment = day.comment
                            val color = day.color

                        }

                        val summ = exportList.sumOf {it.worked }
                        val wHours = minuteToHours(summ).hours
                        val wMin = minuteToHours(summ).minutes
                        val worked = "$wHours:${ if (wMin == 0) "00" else {if (wMin < 10) "0$wMin" else wMin}}"


                        exportStateText = contextResource.getString(R.string.Success_export)
                        exportError = "$folder"
                    }

                } catch (e: Exception) {
                    exportStateText = contextResource.getString(R.string.Error_export)
                    exportError = " ${e.message}"
                }
            }else {
                exportStateText = contextResource.getString(R.string.NoSave_export)
                exportError = ""
            }
        }

        Row(modifier = Modifier) {
            Text(text = stringResource(id = R.string.DateTableStart_export))            //начальная дата
            Text(text = "$startDate")
        }
        Row(modifier = Modifier) {
            Text(text = stringResource(id = R.string.DateTableEnd_export))              //конечная дата
            Text(text = "$endDate")
        }
        Row(modifier = Modifier) {
            Text(text = stringResource(id = R.string.NumberTable_export, list.size ))               //количество записей
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp))
        LaunchedEffect(Unit){
            exportStateText = contextResource.getString(R.string.ChoosePlace_export)
        }

        //кнопка экспорт
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
            RButton(extension)
            Button(modifier = Modifier,
                onClick = {
                    exportStateText = contextResource.getString(R.string.InProccess_export)
                    when (extension.value) {
                        extensionState.exportCSV.string -> exportCSV.launch(contextResource.getString(R.string.FileNameCSV_export))
                        extensionState.exportXLS.string -> exportXLS.launch(contextResource.getString(R.string.FileNameXLS_export))
                        else -> exportStateText = contextResource.getString(R.string.FileNameErr_export)
                    }
                }) {
                Text(text = stringResource(id = R.string.ExportButton_export))
            }
        }

        Text(text = stringResource(id = R.string.Result_export), modifier = Modifier.align(Alignment.Start))                //результат работы
        Text(text = exportStateText + exportError, style = exportStateTextStyle, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp))

        Column(modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxWidth()) {
            TablePreview(exportList)
        }
    }

}

sealed class extensionState (val string: String){
    object exportCSV: extensionState("csv")
    object exportXLS: extensionState("xls")
}

@Composable
fun TablePreview(exportList: List<WorkDay>) {
    Row(modifier = Modifier
        .border(BorderStroke(width = 0.dp, color = Color.Black))
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround) {
        Text(text = stringResource(id = R.string.Date_export),    modifier = Modifier
            .padding(5.dp)
            .weight(1f))
        Text(text = stringResource(id = R.string.TimeWorked_export),  modifier = Modifier
            .padding(5.dp)
            .weight(1f))
        Text(text = stringResource(id = R.string.Comment_export), modifier = Modifier
            .padding(5.dp)
            .weight(2f))
    }
    
    for (day in exportList){
        val date = try { LocalDate.of(day.year,day.month, day.day) }
        catch (e:Exception){ LocalDate.of(day.year,day.month, 1) }
        val wHours = minuteToHours(day.worked).hours
        val wMin = minuteToHours(day.worked).minutes
        val worked = "$wHours:${ if (wMin == 0) "00" else {if (wMin < 10) "0$wMin" else wMin}}"

        val comment = day.comment
        val color = day.color

        LineTable(date,worked,comment)
    }
    val summ = exportList.sumOf { it.worked }
    val summHours = minuteToHours(summ).hours
    val summMin = minuteToHours(summ).minutes
    val summWorked = "$summHours:${ if (summMin == 0) "00" else {if (summMin < 10) "0$summMin" else summMin}}"
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp))

    Row(modifier = Modifier
        .border(BorderStroke(width = 0.dp, color = Color.Black))
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround) {
        Text(text = "В сумме",    modifier = Modifier
            .padding(5.dp)
            .weight(1f))
        Text(text = summWorked,  modifier = Modifier
            .padding(5.dp)
            .weight(1f))
        Text(text = "", modifier = Modifier
            .padding(5.dp)
            .weight(2f))
    }
}

@Composable
fun LineTable(date: LocalDate, worked: String, comment: String) {
    Row(modifier = Modifier
        .border(BorderStroke(width = 0.dp, color = Color.Black))
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround) {
        Text(text = "$date",    modifier = Modifier
            .padding(5.dp)
            .weight(1f))
        Text(text = worked,  modifier = Modifier
            .padding(5.dp)
            .weight(1f))
        Text(text = comment, modifier = Modifier
            .padding(5.dp)
            .weight(2f))
    }
}

fun minmaxDate(value: String, list: List<WorkDay>): LocalDate{
    when (value){
        "min" -> {
            var minYear = 9999
            var minMonth = 12
            var minDay = 31
            for (d in list){
                if (d.year <= minYear){
                    minYear = d.year
                    if (d.month <= minMonth){
                        minMonth = d.month
                        if (d.day <= minDay){
                            minDay = d.day
                        }
                    }
                }
            }
            return try { LocalDate.of(minYear,minMonth, minDay) }
            catch (e:Exception){
                Log.d("MyTag", "min $minYear.$minMonth,$minDay")
                LocalDate.now()
            }
        }
        "max" -> {
            var maxYear = 2018
            var maxMonth = 1
            var maxDay = 1
            for (d in list){
                if (d.year >= maxYear){
                    maxYear = d.year
                    if (d.month >= maxMonth){
                        maxMonth = d.month
                        if (d.day >= maxDay){
                            maxDay = d.day
                        }
                    }
                }
            }
            return try { LocalDate.of(maxYear,maxMonth, maxDay) }
            catch (e:Exception){
                Log.d("MyTag", "max $maxYear.$maxMonth.$maxDay")
                LocalDate.now()
            }
        }

    else -> return LocalDate.now()
    }

}

@Composable
fun RButton(extension: MutableState<String>) {

    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.Center,
    ) {
        Row (verticalAlignment = Alignment.CenterVertically){
           RadioButton(
               selected = extension.value == extensionState.exportCSV.string,
               onClick = { extension.value = extensionState.exportCSV.string})
            Text(text = extensionState.exportCSV.string)
        }
        Row (verticalAlignment = Alignment.CenterVertically){
           RadioButton(
               selected = extension.value == extensionState.exportXLS.string,
               onClick = { extension.value = extensionState.exportXLS.string })
            Text(text = extensionState.exportXLS.string)
        }

    }

}


@Preview(showBackground = true, showSystemUi = true, locale = "RU")
@Composable
fun composepreviewExport (){
    val list = listOf<WorkDay>(WorkDay(0,2023,1,20, 240, "чибрикит", "#FFFFFF"),
        WorkDay(1,2023,1,2, 290, "крепость", "#FFFFFF"),
        WorkDay(2,2023,1,3, 360, "Ладно", "#FFFFFF"),
        WorkDay(1,2023,1,4, 290, "крепость", "#FFFFFF"),
        WorkDay(2,2023,1,5, 360, "Ладно", "#FFFFFF"),
        WorkDay(1,2023,1,14, 290, "крепость не согласная с оставшимися", "#FFFFFF"),
        WorkDay(2,2023,1,7, 360, "Ладно", "#FFFFFF"),
        WorkDay(1,2023,1,8, 290, "крепость", "#FFFFFF"),
        WorkDay(2,2023,1,9, 360, "Ладно", "#FFFFFF"),
        WorkDay(2,2023,1,10, 360, "Ладно", "#FFFFFF"),
        WorkDay(1,2023,1,11, 290, "крепость", "#FFFFFF"),
        WorkDay(2,2023,1,12, 360, "Ладно", "#FFFFFF"),
        WorkDay(1,2023,1,8, 290, "крепость", "#FFFFFF"),
        WorkDay(2,2023,1,16, 360, "Ладно", "#FFFFFF"),
        WorkDay(1,2023,1,17, 290, "крепость", "#FFFFFF"),
        WorkDay(2,2023,1,18, 360, "Ладно", "#FFFFFF"),
        WorkDay(3,2023,1,19, 140, "пуля", "#FFFFFF") )

    val navController = rememberNavController()
    ExportScreen(list)
}


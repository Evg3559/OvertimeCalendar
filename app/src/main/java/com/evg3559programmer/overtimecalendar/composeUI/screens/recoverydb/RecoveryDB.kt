package com.evg3559programmer.overtimecalendar.composeUI.screens.recoverydb

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.evg3559programmer.overtimecalendar.DataStore.AppSettings
import com.evg3559programmer.overtimecalendar.di.WorkDay
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream

@Composable
fun RecoveryDB(navController: NavHostController, dataStore: DataStore<AppSettings>) {
   val contextResource = LocalContext.current.resources
   val context = LocalContext.current
   val recoverydbVM: recoverydbVM = hiltViewModel()
   val processed = remember { mutableStateOf(false) }
   val migrated = remember { mutableStateOf(false) }
   val errored = remember { mutableStateOf(false) }
   val scope = rememberCoroutineScope()
   val textError = remember { mutableStateOf("") }
   val username = remember { mutableStateOf("") }
   val tablename = remember { mutableStateOf("") }
   val fileIS = remember {  mutableStateOf(false)  }
   LaunchedEffect(Unit){
      val file: File = context.getDatabasePath("mydatabase.db")
      if (file.exists()){
         fileIS.value = true
      } else {
         fileIS.value = false
      }
   }


   Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
      Row(     //восстановление бд базы данных
         modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
      ) {
         Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
            Text(text = if (fileIS.value) "Файл старой базы данных найден" else "Файл старой базы данных не найден",
               modifier = Modifier.padding(start = 10.dp, end = 10.dp),fontSize = 17.sp )
            Text(text = "В старой базе данных хранятся данные, которые использовались до версии 1.34 приложения. Это сентябрь 2021",
               modifier = Modifier.padding(start = 10.dp, end = 10.dp),fontSize = 17.sp )
            Text(text = "Не восстанавливайте таблицу, если вы это уже делали.",
               modifier = Modifier.padding(start = 10.dp, end = 10.dp),fontSize = 17.sp )
         }
      }
      if (errored.value) Row(
         modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
      ) {
         Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
            Text(text = textError.value,
               modifier = Modifier.padding(start = 10.dp, end = 10.dp),fontSize = 17.sp )
         }
      }
      Row(modifier = Modifier
         .fillMaxWidth()
         .padding(top = 10.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically){
         Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Card(backgroundColor = Color(0xFFAFACAC), shape = RoundedCornerShape(1.dp), border = BorderStroke(0.dp, Color(0x55BBBCBE)), modifier = Modifier
               .padding(top = 8.dp, end = 5.dp)
               .clickable {
                  //Восстановить
                  if (!processed.value) {
                     if (!migrated.value) {
                        scope.launch {
                           try {
                              RecoverDB(recoverydbVM, context, processed, migrated, tablename, username)

                           } catch (e: Exception) {
                              errored.value = true
                              processed.value = false
                              textError.value = e.toString()
                           }

                        }
                     }
                  }

               }) {
               Column() {
                  if (fileIS.value) {
                     if (!processed.value) {
                        if (!migrated.value) {
                           Text( text = "Восстановить",
                              modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                              fontSize = 13.sp, maxLines = 1, color = Color.Black
                           )
                        }
                     } else {
                        Text( text = "В процессе",
                           modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                           fontSize = 13.sp, maxLines = 1, color = Color.Yellow )
                     }
                     if (errored.value) {
                        Text( text = "Ошибка",
                           modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                           fontSize = 13.sp, maxLines = 1, color = Color.Red
                        )
                     }
                     if (migrated.value) {
                        Text( text = "Сделано",
                           modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                           fontSize = 13.sp, maxLines = 1, color = Color.Green )
                     }
                  }
               }
               }
            }
         }

      if (migrated.value) Row(     //успешность операции
             modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
          ) {
             Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                Text(text = "База данных ${tablename.value} пользователя ${username.value} восстановлена",
                   modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                style = TextStyle(fontSize = 17.sp, color = Color(0xFF015F0D)))
             }
          }
      }
   }


suspend fun RecoverDB(
   recoverydbVM: recoverydbVM,
   context: Context,
   processed: MutableState<Boolean>,
   migrated: MutableState<Boolean>,
   tablename: MutableState<String>,
   username: MutableState<String>
) {
   processed.value = true
   data class DayworkOld(
      val year: Int, val month: Int, val day: Int,
      val worked:Int, val comments: String, val IDsql:Int, val color: String){ }

   class DBManager(context: Context) {
      inner class DBHelper(context: Context): SQLiteOpenHelper(context, "mydatabase.db",null, 1) {
         override fun onCreate(db: SQLiteDatabase?) {
            db?.execSQL("CREATE TABLE IF NOT EXISTS Anonymous (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                    "year INTEGER, " +
                    "month INTEGER, " +
                    "day INTEGER, " +
                    "worked TEXT, " +
                    "comments TEXT, " +
                    "color TEXT)")

            db?.execSQL("CREATE TABLE IF NOT EXISTS Configuration(" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                    "version TEXT, " +
                    "language TEXT, " +
                    "number_launchers INTEGER, " +
                    "name TEXT)")
            db?.execSQL("CREATE TABLE IF NOT EXISTS Users(" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "overworked INTEGER, " +
                    "cost_of_hours INTEGER, " +
                    "time_prime TEXT, " +
                    "use_table TEXT)")
         }

         override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
         }
      }
      val dbHelper = DBHelper(context)
      var db: SQLiteDatabase? = null
      val user = ""

      fun OpenDB(){
         db = dbHelper.writableDatabase
      }
      fun default_users():String{              ////узнаем имя базы данных по умолчанию
         val db = dbHelper.writableDatabase
         var data:String = "no"
         val projection = arrayOf("name")
         val selection = "${BaseColumns._ID} = ?"  //выбирваемый объект
         val selectionArgs = arrayOf("1")           //аргумент

         try {
            val cursor = db.query(
               "Configuration",        // Таблица для запроса
               projection,               // Массив возвращаемых столбцов (pass null to get all)
               selection,                  // Столбцы в предложении where
               selectionArgs,              // Значения в предложении where
               null,               // не группировать строки
               null,                // не фильтровать по группам строк
               null                   // Порядок сортировки
            )
            with(cursor){
               while (this.moveToNext()){
                  val name_User = getString(cursor.getColumnIndexOrThrow("name"))
                  data=name_User.toString()
               }
            }
            cursor?.close()
         }catch (e: IllegalArgumentException) {
            data = "no"
         }
         dbHelper.close()
         return if (data.length < 4) "no" else data

      }
      fun table_name(user:String):String{              ////узнаем имя базы данных по умолчанию
         val COL_NAME_USE_TABLE = "use_table"
         val COL_NAME_USER = "name"
         val TABLE_NAME = "Users"

         val db = dbHelper.readableDatabase
         var data:String = "no"
         val projection = arrayOf(COL_NAME_USE_TABLE)
         val selection = "${COL_NAME_USER} = ?"  //выбирваемый объект
         val selectionArgs = arrayOf(user)           //аргумент

         try {
            val cursor = db.query(
               TABLE_NAME,        // Таблица для запроса
               projection,               // Массив возвращаемых столбцов (pass null to get all)
               selection,                  // Столбцы в предложении where
               selectionArgs,              // Значения в предложении where
               null,               // не группировать строки
               null,                // не фильтровать по группам строк
               null                   // Порядок сортировки
            )
            with(cursor){
               while (this.moveToNext()){
                  val _table = getString(cursor.getColumnIndexOrThrow(COL_NAME_USE_TABLE))
                  data=_table.toString()
               }
            }
            cursor?.close()
         }catch (e: IllegalArgumentException) {
            data = "error"
         }
         dbHelper.close()
         return data
      }

      fun readDB_AllDaywork(table_name:String): ArrayList<DayworkOld>{   //принять данные о годе и месяце
         val dataList = ArrayList<DayworkOld>()
         val db = dbHelper.readableDatabase
         val sortOrder = "month" // +" DESC" для обратной сортировки
         val cursor = db.query(
            table_name,                 // Таблица для запроса
            null,               // Массив возвращаемых столбцов (pass null to get all)
            null,                  // Столбцы в предложении where
            null,              // Значения в предложении where
            null,               // не группировать строки
            null,                // не фильтровать по группам строк
            sortOrder                   // Порядок сортировки
         )
         with(cursor){
            while (moveToNext()){
               val _Year = getString(cursor.getColumnIndexOrThrow("year"))
               val _Month = getString(cursor.getColumnIndexOrThrow("month"))
               val _Day = getString(cursor.getColumnIndexOrThrow("day"))
               val _Worked = getString(cursor.getColumnIndexOrThrow("worked"))
               val _Comment = getString(cursor.getColumnIndexOrThrow("comments"))
               val _Id = getString(cursor.getColumnIndexOrThrow(BaseColumns._ID))
               val _Color = getString(cursor.getColumnIndexOrThrow("color"))
               dataList.add(
                  DayworkOld(
                     _Year!!.toInt(),
                     _Month!!.toInt(),
                     _Day!!.toInt(),
                     _Worked!!.toInt(),
                     _Comment.toString(),
                     _Id!!.toInt(),
                     _Color.toString())
               )
            }
         }
         cursor.close()
         dbHelper.close()
         return dataList
      }

   }
   val myDBManager = DBManager(context)
   val userOld = myDBManager.default_users()
   delay(40)
   val table_name_user = myDBManager.table_name(userOld)
   delay(40)
   val oldDB = myDBManager.readDB_AllDaywork(table_name_user)
   for (day in oldDB){
      recoverydbVM.addDay(WorkDay(0, day.year, day.month, day.day, day.worked, day.comments, day.color))
   }
   tablename.value = table_name_user
   username.value = userOld
   processed.value = false
   migrated.value = true

}
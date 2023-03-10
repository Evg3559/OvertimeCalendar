package com.evg3559programmer.overtimecalendar.di.modules.daysworkDB

import androidx.compose.ui.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.evg3559programmer.overtimecalendar.di.modules.daysworkDB.DaysworkEntity.Companion.TABLE_NAME

@Entity (tableName = TABLE_NAME)
class DaysworkEntity(
   @PrimaryKey(autoGenerate = true)
   @ColumnInfo(name = T_ID)        var _id: Long,
   @ColumnInfo(name = T_YEAR)      var year: Int,
   @ColumnInfo(name = T_MONTH)     var month: Int,
   @ColumnInfo(name = T_DAY)       var day: Int,
   @ColumnInfo(name = T_WORKED)    var worked:Int,       //в минутах!
   @ColumnInfo(name = T_COMMENT)   var comment: String,
   @ColumnInfo(name = T_COLOR)     var color: String,
   //@ColumnInfo(name = T_COST, defaultValue = "0")      var cost: Int
) {
   companion object{
      const val TABLE_NAME = "Dayswork"
      const val T_ID = "ID"
      const val T_YEAR = "year"
      const val T_MONTH= "month"
      const val T_DAY = "day"
      const val T_WORKED= "worked"
      const val T_COMMENT = "comment"
      const val T_COLOR = "color"
      //const val T_COST = "cost"
   }
}
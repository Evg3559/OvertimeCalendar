package com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB.ConfigurationEntity.Companion.TABLE_NAME

@Entity (tableName = TABLE_NAME)
class ConfigurationEntity(
   @PrimaryKey (autoGenerate = true)
   @ColumnInfo (name = T_ID)           var _id:Long = 0,
   @ColumnInfo (name = T_USER)         var user_default:String,
   @ColumnInfo (name = T_NUMOFLAUNCH)  var number_of_laucnhers:Int,
   @ColumnInfo (name = T_LANGUAGE)     var language: String,
   @ColumnInfo (name = T_RESERVED)     var reserved:String
)
{
   companion object{
      const val TABLE_NAME = "Configuration"
      const val T_ID = "ID"
      const val T_USER= "user_default"
      const val T_NUMOFLAUNCH = "number_of_launchers"
      const val T_LANGUAGE = "language"
      const val T_RESERVED = "reserved"
   }
}
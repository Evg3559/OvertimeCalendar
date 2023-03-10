package com.evg3559programmer.overtimecalendar.di.modules.UserDB

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.evg3559programmer.overtimecalendar.di.modules.UserDB.UserEntity.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
class UserEntity(
   @PrimaryKey(autoGenerate = true)
   @ColumnInfo(name = T_ID)          var _id:Long = 0,
   @ColumnInfo(name = T_USER)        var name_user:String,
   @ColumnInfo(name = T_POINTS)      var points:Int,           //считать баллы или часы. 1 - баллы, 0 - часы
   @ColumnInfo(name = T_COSTOFHOURS) var cost_of_hours:Int,          //стоимость одного часа работы рассчитывать от оклада -- пока не использовать
   @ColumnInfo(name = T_PRIME)       var prime:Int,               //количество. Сколько раз можно использовать премиум функции.
   @ColumnInfo(name = T_MIGRATED)    var migrated:Boolean        //Мигрирована ли таблица на рум?

) {

   companion object{
      const val TABLE_NAME = "Users"
      const val T_ID= "ID"
      const val T_USER = "name_user"
      const val T_POINTS = "points"
      const val T_COSTOFHOURS = "COSTOFHOURS"
      const val T_PRIME = "prime"
      const val T_MIGRATED = "migrated"
   }
}
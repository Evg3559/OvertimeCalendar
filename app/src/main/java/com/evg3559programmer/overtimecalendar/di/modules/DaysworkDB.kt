package com.evg3559programmer.overtimecalendar.di.modules

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB.ConfigurationDao
import com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB.ConfigurationEntity
import com.evg3559programmer.overtimecalendar.di.modules.UserDB.UserDBDao
import com.evg3559programmer.overtimecalendar.di.modules.UserDB.UserEntity
import com.evg3559programmer.overtimecalendar.di.modules.daysworkDB.DaysworkDao
import com.evg3559programmer.overtimecalendar.di.modules.daysworkDB.DaysworkEntity

@Database(
   entities = [DaysworkEntity::class, ConfigurationEntity::class, UserEntity::class],
   version = 1,
   exportSchema = true)
abstract class DaysworkDB : RoomDatabase() {

   abstract fun daysworkDao(): DaysworkDao
   abstract fun configurationDao(): ConfigurationDao
   abstract fun userDBDao(): UserDBDao


   companion object {
      const val DB_NAME = "daysworkdb.db"
   }
}
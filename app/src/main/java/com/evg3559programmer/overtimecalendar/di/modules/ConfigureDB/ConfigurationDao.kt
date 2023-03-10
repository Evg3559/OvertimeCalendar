package com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConfigurationDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun addconfig(configuration: ConfigurationEntity)

   @get:Query("SELECT * FROM ${ConfigurationEntity.TABLE_NAME}")
   val read_config:List<ConfigurationEntity>

   @Query("DELETE FROM ${ConfigurationEntity.TABLE_NAME}")
   suspend fun deleteAllConfigurations()

   @Query("SELECT ${ConfigurationEntity.T_NUMOFLAUNCH} FROM ${ConfigurationEntity.TABLE_NAME}")
   suspend fun getCountStart():Int

   @Query("UPDATE ${ConfigurationEntity.TABLE_NAME} SET ${ConfigurationEntity.T_NUMOFLAUNCH} = :count")
   suspend fun countStartPlus(count:Int)

   @Query("UPDATE ${ConfigurationEntity.TABLE_NAME} SET ${ConfigurationEntity.T_USER} = :newName")
   suspend fun renameUser (newName: String)
}
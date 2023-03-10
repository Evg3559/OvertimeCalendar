package com.evg3559programmer.overtimecalendar.di.modules.daysworkDB

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DaysworkDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertDay(roomdayswork: DaysworkEntity)

   @Query("SELECT * FROM ${DaysworkEntity.TABLE_NAME} WHERE ${DaysworkEntity.T_YEAR} = :year AND ${DaysworkEntity.T_MONTH} = :month ORDER BY ${DaysworkEntity.T_DAY} ASC")
   suspend fun findDaysInMonth(month: Int, year: Int): List<DaysworkEntity>

   @Query("SELECT * FROM ${DaysworkEntity.TABLE_NAME} WHERE ${DaysworkEntity.T_ID} = :id")
   suspend fun findDaysByID(id:Long): DaysworkEntity

   @get:Query("SELECT * FROM ${DaysworkEntity.TABLE_NAME} ORDER BY ${DaysworkEntity.T_DAY} ASC")
   val findAllDays: LiveData<List<DaysworkEntity>>

   @Query("SELECT * FROM ${DaysworkEntity.TABLE_NAME} ORDER BY ${DaysworkEntity.T_DAY} ASC")
   fun findAllDaysFlow(): Flow<List<DaysworkEntity>>

   @Query("SELECT * FROM ${DaysworkEntity.TABLE_NAME} WHERE ${DaysworkEntity.T_ID} = :ID")
   suspend fun interactiveDB(ID:Int): DaysworkEntity

   @Query("SELECT * FROM ${DaysworkEntity.TABLE_NAME} WHERE ${DaysworkEntity.T_YEAR} = :year AND ${DaysworkEntity.T_MONTH} = :month AND ${DaysworkEntity.T_DAY} = :day")
   suspend fun interactiveDB(year: Int,month:Int,day:Int): List<DaysworkEntity>

   @Update
   suspend fun editDay(day: DaysworkEntity)

   @Query("UPDATE ${DaysworkEntity.TABLE_NAME} SET " +
           "${DaysworkEntity.T_WORKED} = :worked, " +
           "${DaysworkEntity.T_COMMENT} = :comment, " +
           "${DaysworkEntity.T_COLOR} = :color " +
           "WHERE ${DaysworkEntity.T_YEAR} = :year AND ${DaysworkEntity.T_MONTH} = :mont AND ${DaysworkEntity.T_DAY} = :day")
   suspend fun editDayWithoutID(year: Int,mont:Int,day:Int, worked:Int, comment: String, color: String)

   @Query("UPDATE ${DaysworkEntity.TABLE_NAME} SET " +
           "${DaysworkEntity.T_WORKED} = :worked, " +
           "${DaysworkEntity.T_COMMENT} = :comment, " +
           "${DaysworkEntity.T_COLOR} = :color, " +
           "${DaysworkEntity.T_YEAR} = :year, " +
           "${DaysworkEntity.T_MONTH} = :month, " +
           "${DaysworkEntity.T_DAY} = :day " +
           "WHERE ${DaysworkEntity.T_ID} = :id")
   suspend fun editDayWithID(id: Long, year: Int,month:Int,day:Int, worked:Int, comment: String, color: String)

   @Query ("UPDATE ${DaysworkEntity.TABLE_NAME} SET ${DaysworkEntity.T_WORKED} = :worked WHERE ${DaysworkEntity.T_ID} = :id")
   suspend fun saveWorked(id: Long, worked: Int)

   @Query ("UPDATE ${DaysworkEntity.TABLE_NAME} SET ${DaysworkEntity.T_COMMENT} = :comment WHERE ${DaysworkEntity.T_ID} = :id")
   suspend fun saveComment(id: Long, comment: String)

   @Query ("UPDATE ${DaysworkEntity.TABLE_NAME} SET ${DaysworkEntity.T_COLOR} = :color WHERE ${DaysworkEntity.T_ID} = :id")
   suspend fun saveColor(id: Long, color: String)

   @Query("DELETE FROM ${DaysworkEntity.TABLE_NAME} WHERE ${DaysworkEntity.T_ID} like :id")
   suspend fun deleteDay(id: Long)

   @Delete
   suspend fun deletedays(list: List<DaysworkEntity>)

   @Query("DELETE FROM ${DaysworkEntity.TABLE_NAME}")
   suspend fun deleteAll()

   @Query("SELECT ${DaysworkEntity.T_ID} FROM ${DaysworkEntity.TABLE_NAME}")
   suspend fun getQuantityShifts():List<Long>

   @Query("SELECT ${DaysworkEntity.T_WORKED} FROM ${DaysworkEntity.TABLE_NAME}")
   suspend fun getWorkedAllTime():List<Int>

   @Query("SELECT ${DaysworkEntity.T_COLOR} FROM ${DaysworkEntity.TABLE_NAME} ORDER BY ${DaysworkEntity.T_ID} DESC LIMIT 90")
   suspend fun LastColor():List<String>

   @Query("SELECT ${DaysworkEntity.T_COMMENT} FROM ${DaysworkEntity.TABLE_NAME} ORDER BY ${DaysworkEntity.T_ID} DESC LIMIT 92")
   suspend fun LastComments():List<String>

   @Query("SELECT ${DaysworkEntity.T_WORKED} FROM ${DaysworkEntity.TABLE_NAME} ORDER BY ${DaysworkEntity.T_ID} DESC LIMIT 92")
   suspend fun LastWorkeds():List<Int>

   @Query("SELECT ${DaysworkEntity.T_WORKED} FROM ${DaysworkEntity.TABLE_NAME} WHERE ${DaysworkEntity.T_MONTH} = :month AND ${DaysworkEntity.T_YEAR} = :year")
   suspend fun getWorkedMonth(month:Int, year: Int):List<Int>
}
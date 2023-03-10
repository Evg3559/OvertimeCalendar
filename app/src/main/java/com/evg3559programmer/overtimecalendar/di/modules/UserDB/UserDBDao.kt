package com.evg3559programmer.overtimecalendar.di.modules.UserDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDBDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   suspend fun insertUser(user: UserEntity)

   @get:Query("SELECT * FROM ${UserEntity.TABLE_NAME}")
   val getUsers: LiveData<List<UserEntity>>

   @Query ("SELECT ${UserEntity.T_USER} FROM ${UserEntity.TABLE_NAME}")
   suspend fun getUserName():String

   @Query("DELETE FROM ${UserEntity.TABLE_NAME}")
   suspend fun deleteAllUsers()

   @Query("SELECT ${UserEntity.T_PRIME} FROM ${UserEntity.TABLE_NAME}")
   suspend fun getPrPoints():Int

   @Query("UPDATE ${UserEntity.TABLE_NAME} SET ${UserEntity.T_PRIME} = :str")
   suspend fun setPrPoints(str: Int)

   @Query("UPDATE ${UserEntity.TABLE_NAME} SET ${UserEntity.T_MIGRATED} = :newstate WHERE ${UserEntity.T_USER} = :user")
   suspend fun migratedChanged(newstate:Boolean, user: String)

   @Query("UPDATE ${UserEntity.TABLE_NAME} SET ${UserEntity.T_USER} = :newName WHERE ${UserEntity.T_USER} = :oldName ")
   suspend fun renameUser (oldName: String, newName: String)
}
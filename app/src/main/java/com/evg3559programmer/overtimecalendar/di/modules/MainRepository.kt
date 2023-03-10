package com.evg3559programmer.overtimecalendar.composeUI

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.evg3559programmer.overtimecalendar.di.AppConfig
import com.evg3559programmer.overtimecalendar.di.UserApp
import com.evg3559programmer.overtimecalendar.di.WorkDay
import com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB.ConfigurationDao
import com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB.ConfigurationEntity
import com.evg3559programmer.overtimecalendar.di.modules.UserDB.UserDBDao
import com.evg3559programmer.overtimecalendar.di.modules.UserDB.UserEntity
import com.evg3559programmer.overtimecalendar.di.modules.daysworkDB.DaysworkDao
import com.evg3559programmer.overtimecalendar.di.modules.daysworkDB.DaysworkEntity
import com.evg3559programmer.overtimecalendar.di.statistickaMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import kotlin.Exception


class MainRepository @Inject constructor (
   private val daysworkDao: DaysworkDao,
   private val userDBDao: UserDBDao,
   private val configurationDao: ConfigurationDao,
   private val cacheMapper: CacheMapper
) {
   private val pageLimit = 240

   suspend fun SaveDay(wd: WorkDay):String{
      return try {
         daysworkDao.insertDay(cacheMapper.mapWriteToEntity(wd))
         "Ok"
      }catch (e:Exception){
         "Error $e"
      }
   }
   suspend fun ReplaceDay(wd: WorkDay):String{
      return try {
         daysworkDao.editDay(cacheMapper.mapEditToEntity(wd))
         "Ok"
      }catch (e:Exception){
         "Error $e"
      }
   }

   suspend fun LastColor():List<Color>{
      val c = cacheMapper.mapStringColorFromEntityToColors(daysworkDao.LastColor())
      return c
   }

   suspend fun LastComments() = daysworkDao.LastComments()

   suspend fun LastWorkeds() = daysworkDao.LastWorkeds()

   suspend fun saveWorked(id:Int, worked:Int){
      daysworkDao.saveWorked(id.toLong(),worked)
   }

   suspend fun saveComment(id:Int, comment:String){
      daysworkDao.saveComment(id.toLong(), comment)
   }

   suspend fun saveColor(id:Int, color:String){
      daysworkDao.saveColor(id.toLong(), color)
   }

   suspend fun ReplaceDayWithoutID(wd: WorkDay):String{
      return try {
         daysworkDao.editDayWithoutID(wd.year,wd.month,wd.day,wd.worked,wd.comment,wd.color)
         "Ok"
      }catch (e:Exception){
         "Error $e"
      }
   }

   fun getDaysAll():Flow<List<WorkDay>> = daysworkDao.findAllDaysFlow().map { cacheMapper.mapDayFromEntityList(it) }


   suspend fun getDaysInMonth(month: Int, year: Int): List<WorkDay> = cacheMapper.mapDayFromEntityList(daysworkDao.findDaysInMonth(month, year))


   //нужно помнить, что из бд может выйти лист, это вызовит ошибку, поэтому нужно запросить лист и потом взять его первый элемент
   suspend fun getDaysOne(year: Int, month: Int, day: Int): WorkDay? {
      val getDay = daysworkDao.interactiveDB(year,month,day)
      return if (getDay.isNotEmpty()) cacheMapper.mapDayFromEntity(getDay[0]) else null
   }

   suspend fun getDaysOne2(selectedDay: LocalDate): WorkDay {
      val getDay = daysworkDao.interactiveDB(selectedDay.year,selectedDay.monthValue,selectedDay.dayOfMonth)
      return if (getDay.isNotEmpty()) cacheMapper.mapDayFromEntity(getDay[0]) else WorkDay(0,0,0,0,0,"#empty#", "#FFFFFF")
   }

    fun findAllDays ():LiveData<List<WorkDay>>{
      return cacheMapper.mapDayFromEntityListLiveData(daysworkDao.findAllDays)
   }

   //suspend fun getDaysFromMonth(month: Int, year: Int): Flow<StateList<List<WorkDay>>> = flow {
//      emit(StateList.Loading)
//      try {
//         val getlist = daysworkDao.findDaysInMonth(year, month)
//         if (getlist.isNotEmpty()){
//            emit(StateList.Success(cacheMapper.mapDayFromEntityList(getlist)))
//         } else {
//            emit(StateList.NoItems)
//         }
//      } catch (e:Exception){
//         emit(StateList.Error(e))
//      }
//   }

   suspend fun getMinuteofMonth(month: Int, year: Int): statistickaMonth {
      val getmin = daysworkDao.getWorkedMonth(month, year)
      return statistickaMonth(getmin.sum(), getmin.size)
   }
   suspend fun getMinuteofAllTime(): statistickaMonth {
      val getminAll = daysworkDao.getWorkedAllTime()
      return statistickaMonth(getminAll.sum(), getminAll.size)
   }
   suspend fun getUserName():String{
      val user = userDBDao.getUserName()
      return user
   }
   suspend fun setUserName(oldName: String, newName: String):Boolean{
      return try {
         userDBDao.renameUser(oldName, newName)
         configurationDao.renameUser(newName)
         true
      } catch (e:Exception){
         false
      }
   }
   suspend fun getCountStarting():Int{
      return configurationDao.getCountStart()
   }
   suspend fun countStartPlus(count: Int){
      configurationDao.countStartPlus(count)
   }


   suspend fun DeleteUser():Boolean{
      return try{
         userDBDao.deleteAllUsers()
         configurationDao.deleteAllConfigurations()
         daysworkDao.deleteAll()
         true
      }catch (e:Exception){
         false
      }
   }
   suspend fun DeleteShiftFormID(id:Int){
      daysworkDao.deleteDay(id.toLong())
   }

   suspend fun DeleteShiftFormIDnew(list: List<WorkDay>){
      daysworkDao.deletedays(cacheMapper.mapEditToEntityList(list))
   }


   suspend fun DeleteShifts(){
      daysworkDao.deleteAll()
   }
   suspend fun getPrPont():Int{
      return userDBDao.getPrPoints()
   }
   suspend fun writePrPont(int: Int){
         userDBDao.setPrPoints(int)
      }

   suspend fun saveConfig(conf: ConfigurationEntity) {
      configurationDao.addconfig(conf)
   }

   suspend fun saveUser(user: UserEntity) {
      userDBDao.insertUser(user)
   }


}




class CacheMapper @Inject constructor(): EntityMapper <DaysworkEntity, UserEntity, ConfigurationEntity, WorkDay, UserApp, AppConfig>{

   override fun mapDayFromEntity(entity:DaysworkEntity):WorkDay{
      return WorkDay(_id = entity._id.toInt(), year = entity.year, month = entity.month, day = entity.day,
      worked = entity.worked, comment = entity.comment, color = if (entity.color != "#empty#") entity.color else "#FFFFFF")
   }

   override fun mapUserFromEntity(entity:UserEntity):UserApp{
      return UserApp(_id = entity._id.toInt(), name_user = entity.name_user, points = entity.points,
      cost_of_hours = entity.cost_of_hours, prime = entity.prime, migrated = entity.migrated)
   }

   override fun mapColorFromEntity(entity:String):Color{
      return try { Color(entity.toColorInt())
      } catch (e:Exception){
         Color.White
      }
   }

   override fun mapAppConfigFromEntity(entity:ConfigurationEntity):AppConfig{
      return AppConfig(_id = entity._id, user_default = entity.user_default, number_of_laucnher = entity.number_of_laucnhers,
      language = entity.language, reserved = entity.reserved)
   }

   override fun mapWriteToEntity(day: WorkDay): DaysworkEntity{
      return DaysworkEntity(_id = 0, year = day.year, month = day.month, day = day.day,
      worked = day.worked,comment = day.comment, color = if (day.color != "#empty#") day.color else "#FFFFFF")           //, cost = 0
   }

   override fun mapEditToEntity(day: WorkDay): DaysworkEntity{
      return DaysworkEntity(_id = day._id.toLong(), year = day.year, month = day.month, day = day.day,
      worked = day.worked,comment = day.comment, color = day.color)     //, cost = 0
   }

   fun mapDayFromEntityList(entities: List<DaysworkEntity>): List<WorkDay>{
      return entities.map {mapDayFromEntity(it)}
   }

   fun mapEditToEntityList(entities: List<WorkDay>): List<DaysworkEntity>{
      return entities.map {mapEditToEntity(it)}
   }

   override fun mapDayFromEntityLiveData(entity:DaysworkEntity):WorkDay{
      return WorkDay(_id = entity._id.toInt(), year = entity.year, month = entity.month, day = entity.day,
         worked = entity.worked, comment = entity.comment, color = entity.color)
   }

   fun mapDayFromEntityListLiveData(entities: LiveData<List<DaysworkEntity>>): LiveData<List<WorkDay>>{
      return entities.map {mapDayFromEntityList(it)}
   }

   fun mapStringColorFromEntityToColors(entities: List<String>):List<Color>{
      return entities.map {mapColorFromEntity(it)}
   }

}


interface EntityMapper <DaysworkEntity, UserEntity, ConfigurationEntity, WorkDay, UserApp, AppConfig>{

   fun mapDayFromEntity(entity: DaysworkEntity): WorkDay
   fun mapColorFromEntity(entity: String): Color
   fun mapUserFromEntity(entity: UserEntity): UserApp
   fun mapAppConfigFromEntity(entity: ConfigurationEntity): AppConfig
   fun mapWriteToEntity (day: WorkDay): DaysworkEntity
   fun mapEditToEntity (day: WorkDay): DaysworkEntity
   fun mapDayFromEntityLiveData (entity: DaysworkEntity): WorkDay
}

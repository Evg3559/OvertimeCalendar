package com.evg3559programmer.overtimecalendar.composeUI.screens.ss

import androidx.lifecycle.ViewModel
import com.evg3559programmer.overtimecalendar.composeUI.MainRepository
import com.evg3559programmer.overtimecalendar.di.WorkDay
import com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB.ConfigurationEntity
import com.evg3559programmer.overtimecalendar.di.modules.UserDB.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.annotation.Nullable
import javax.inject.Inject


@HiltViewModel
class SplScrVM @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

   suspend fun getCountStarting(): Int {
      return try {
         mainRepository.getCountStarting()
      } catch (e:Exception){
         0
      }
   }

   suspend fun insertDay(day: WorkDay){
      mainRepository.SaveDay(day)
   }
   suspend fun insertConfiguration(conf: ConfigurationEntity){
      mainRepository.saveConfig(conf)
   }
   suspend fun insertUser(user: UserEntity){
      mainRepository.saveUser(user)
   }
   suspend fun countStartPlus(){
      mainRepository.countStartPlus(mainRepository.getCountStarting() + 1)
   }


}
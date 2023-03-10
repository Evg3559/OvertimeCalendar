package com.evg3559programmer.overtimecalendar.composeUI.screens.mainmenu

import androidx.lifecycle.*
import com.evg3559programmer.overtimecalendar.composeUI.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
   private val mainRepository: MainRepository,
   //private val savedStateHandle: SavedStateHandle
   ): ViewModel() {

   private val _deletedUser:MutableLiveData<Boolean> = MutableLiveData(false)
   val deletedUser: LiveData<Boolean> = _deletedUser
   private val _todayDate: MutableLiveData<String> = MutableLiveData("")
   val todayDate: LiveData<String>
      get() = _todayDate
   private val _workedTime: MutableLiveData<Int> = MutableLiveData(0)
   val workedTime: LiveData<Int>
      get() = _workedTime
   private val _workedShifts: MutableLiveData<Int> = MutableLiveData(0)
   val workedShifts: LiveData<Int>
      get() = _workedShifts
   private val _workedTimeAllTime: MutableLiveData<Int> = MutableLiveData(0)
   val workedTimeAllTime: LiveData<Int>
      get() = _workedTimeAllTime
   private val _workedShiftsAllTime: MutableLiveData<Int> = MutableLiveData(0)
   val workedShiftsAllTime: LiveData<Int>
      get() = _workedShiftsAllTime
   private val _userName: MutableLiveData<String> = MutableLiveData("Anonim")
   val userName: LiveData<String>
      get() = _userName

   fun changeLookDate(date: LocalDate){
      viewModelScope.launch {
         //_todayDate.postValue("${date.dayOfMonth}.${date.monthValue}.${date.year}")
         _todayDate.postValue(LocalDate.now().toString())
         val stat = mainRepository.getMinuteofMonth(date.monthValue, date.year)
         val statAllTime = mainRepository.getMinuteofAllTime()
         _workedTime.postValue(stat.minutes)
         _workedShifts.postValue(stat.numberShifts)
         _workedTimeAllTime.postValue(statAllTime.minutes)
         _workedShiftsAllTime.postValue(statAllTime.numberShifts)
         _userName.postValue(mainRepository.getUserName())
   }
}
   fun userDeleted(da:Boolean){
      _deletedUser.postValue(da)
   }

   private val _turnCard3 = MutableLiveData<Boolean>().apply {
      value = false
   }
   val turnCard3: LiveData<Boolean> = _turnCard3
   fun setTurned3(state:Boolean){
      _turnCard3.postValue(state)
   }
   private val _turnCard4 = MutableLiveData<Boolean>().apply {
      value = false
   }
   val turnCard4: LiveData<Boolean> = _turnCard4
   fun setTurned4(state:Boolean){
      _turnCard4.postValue(state)
   }

   private val _turnedCard3 = MutableLiveData<Boolean>().apply {
      value = false
   }
   val turnedCard3: LiveData<Boolean> = _turnedCard3
   fun turned3(state:Boolean){
      _turnedCard3.postValue(state)
   }

   private val _turnedCard4 = MutableLiveData<Boolean>().apply {
      value = false
   }
   val turnedCard4: LiveData<Boolean> = _turnedCard4
   fun turned4(state:Boolean){
      _turnedCard4.postValue(state)
   }

   private val _scrHeight = MutableLiveData<Float>().apply{
      value = 650f
   }
   val scrHeight: LiveData<Float>  = _scrHeight
   fun setScrHeight(height:Float) = _scrHeight.postValue(height)

   private val _correctFont = MutableLiveData<Int>(0)
   private val _correctPadding = MutableLiveData<Int>(0)
   val correctFont: LiveData<Int> = _correctFont
   val correctPadding: LiveData<Int> = _correctPadding
   fun setCorrectFont(set: Int) = _correctFont.postValue(set)
   fun setCorrectPadding(set: Int) = _correctPadding.postValue(set)
}
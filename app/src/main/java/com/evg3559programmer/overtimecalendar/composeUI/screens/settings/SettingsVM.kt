package com.evg3559programmer.overtimecalendar.composeUI.screens.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.*
import com.evg3559programmer.overtimecalendar.composeUI.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsVM @Inject constructor(
   private val mainRepository: MainRepository ) :ViewModel() {

   private val _prPoint: MutableLiveData<Int> = MutableLiveData(0)
   val prPoint: LiveData<Int> = _prPoint
   private val _errorConnecting: MutableLiveData<Boolean> = MutableLiveData(false)
   val errorConnecting: LiveData<Boolean> = _errorConnecting
   private val _visibleAdvButton: MutableLiveData<Boolean> = MutableLiveData(false)
   val visibleAdvButton: LiveData<Boolean> = _visibleAdvButton
   private val _deletedUser: MutableLiveData<Boolean> = MutableLiveData(false)
   val deletedUser: LiveData<Boolean> = _deletedUser

   private val _userName: MutableLiveData<String> = MutableLiveData("Anonym")
   val userName: LiveData<String>
      get()=_userName

   fun UserName() = viewModelScope.launch{
         _userName.postValue(mainRepository.getUserName())
   }


   fun setUserName(oldName: String, newName: String) = viewModelScope.launch {
      mainRepository.setUserName(oldName, newName)
      UserName()
   }

   fun DeleteUser(){
      viewModelScope.launch {
         if (mainRepository.DeleteUser()) _deletedUser.postValue(true) else _deletedUser.postValue(false)
      }
   }

   fun DeleteShifts(){
      viewModelScope.launch {
         mainRepository.DeleteShifts()
      }
   }

   fun errorConnecting(state:Boolean) = _errorConnecting.postValue(state)
   fun changeVisibleAdvButton(state:Boolean) {
      _visibleAdvButton.postValue(state) }

   fun getPrPoint() = viewModelScope.launch {
          _prPoint.postValue(mainRepository.getPrPont())
   }

   fun writePrPoint(int:Int) = viewModelScope.launch {
      mainRepository.writePrPont(int + prPoint.value!!)
      getPrPoint()
   }

   private val _senderr: MutableLiveData<Boolean> = MutableLiveData(true)
   val senderr: LiveData<Boolean> = _deletedUser

//   fun writeSendError(bool: Boolean){
//   }

//   fun getSendError(bool: Boolean) {
//   }
}
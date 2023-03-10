package com.evg3559programmer.overtimecalendar.composeUI.screens.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evg3559programmer.overtimecalendar.composeUI.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutVM @Inject constructor(
   private val mainRepository: MainRepository
):ViewModel() {
   private val _countStarting: MutableLiveData<Int> = MutableLiveData(0)
   val countStarting: LiveData<Int>
   get() = _countStarting

   fun getCountStarting() = viewModelScope.launch{
      _countStarting.postValue(mainRepository.getCountStarting())
   }


}
package com.evg3559programmer.overtimecalendar.composeUI.screens.calendarscreen

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.*
import com.evg3559programmer.overtimecalendar.composeUI.MainRepository
import com.evg3559programmer.overtimecalendar.di.StateDialog
import com.evg3559programmer.overtimecalendar.di.StateEditing
import com.evg3559programmer.overtimecalendar.di.WorkDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class CalendarVM @Inject constructor(
   private val mainRepository: MainRepository
): ViewModel() {

   private val mutableStateDialog = MutableStateFlow<StateDialog>(StateDialog.None)
   val stateDialog = mutableStateDialog.asStateFlow()
   fun SelectStateDialog(state: StateDialog){
      mutableStateDialog.value = state
   }

   private val mutableState = MutableStateFlow<StateEditing>(StateEditing.None)
   val stateEdition = mutableState.asStateFlow()
   fun SelectState(state: StateEditing){
      mutableState.value = state
   }

   private val _daysWork = mainRepository.getDaysAll().stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(4000),
      initialValue = listOf()
   )
   val daysWork: LiveData<List<WorkDay>> = _daysWork.asLiveData()

   suspend fun getDayFromDate (date: LocalDate):WorkDay {
      return mainRepository.getDaysOne2(date)
   }

   fun deleteDayFromID(id:Int) = viewModelScope.launch{
      mainRepository.DeleteShiftFormID(id)
   }

   fun saveWorked(id: Int, worked:Int) = viewModelScope.launch {
      mainRepository.saveWorked(id, worked)
   }
   fun saveComment(id: Int, comment:String) = viewModelScope.launch {
      mainRepository.saveComment(id, comment)
   }
   fun saveColor(id: Int, color:String) = viewModelScope.launch {
      mainRepository.saveColor(id, color)
   }

   private val _colorAdditional = MutableLiveData<List<Color>>().apply { value = listOf<Color>() }      //последние использованные цвета из бд
   val colorAdditional: LiveData<List<Color>> = _colorAdditional

   fun LastColors() = viewModelScope.launch {
      val colors = mainRepository.LastColor().distinct()

      if (colors.size > 11) {
         _colorAdditional.postValue(colors.subList(0, 11))
      } else {
         _colorAdditional.postValue(colors)
      }
   }

}
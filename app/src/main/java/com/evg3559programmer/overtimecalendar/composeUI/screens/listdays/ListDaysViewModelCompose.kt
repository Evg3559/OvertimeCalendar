package com.evg3559programmer.overtimecalendar.composeUI.screens.listdays

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.*
import com.evg3559programmer.overtimecalendar.composeUI.MainRepository
import com.evg3559programmer.overtimecalendar.di.StateDialog
import com.evg3559programmer.overtimecalendar.di.WorkDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class ListDaysViewModelCompose @Inject constructor(
      private val mainRepository: MainRepository,
      //private val savedStateHandle: SavedStateHandle        //сделать сохранение данных при долгой работе в фоне
): ViewModel()  {

   val MAX_PAGES: Int = 240   //240 месяцев будет доступно всего от текущей даты

   private val mutableStateDialog = MutableStateFlow<StateDialog>(StateDialog.None)
   val stateDialog = mutableStateDialog.asStateFlow()
   fun SelectStateDialog(state: StateDialog){
      mutableStateDialog.value = state
   }

//   private val _daysList: MutableLiveData<List<WorkDay>> = MutableLiveData()
//   val daysList: LiveData<List<WorkDay>> = _daysList

      val getDays: LiveData<List<WorkDay>> = mainRepository.findAllDays()


   private val _lookDate: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())
   val lookDate: LiveData<LocalDate>
      get() = _lookDate
   private val _workedTime: MutableLiveData<Int> = MutableLiveData(0)
   val workedTime: LiveData<Int>
      get() = _workedTime
   private val _workedShifts: MutableLiveData<Int> = MutableLiveData(0)
   val workedShifts: LiveData<Int>
      get() = _workedShifts

   //просто список дней по дате
   suspend fun daysListFromMonth(month: Int, year: Int):List<WorkDay> = mainRepository.getDaysInMonth(month,year)


   fun changeLookDate(currentPage: Int) = viewModelScope.launch {
      val cnt: Int = currentPage - (MAX_PAGES/2)
      val date = LocalDate.now().plusMonths(cnt.toLong())
      _lookDate.postValue(date)

//      val stat = mainRepository.getMinuteofMonth(date.monthValue, date.year)
//      _workedTime.postValue(stat.minutes)
//      _workedShifts.postValue(stat.numberShifts)
      }

   //нужно для обновления отработанных часов в карточке.
   fun updLookDate(minutes: Int, numberShifts: Int) {
      _workedTime.postValue(minutes)
      _workedShifts.postValue(numberShifts)

//      val stat = mainRepository.getMinuteofMonth(_lookDate.value!!.monthValue, _lookDate.value!!.year)
//      _workedTime.postValue(stat.minutes)
//      _workedShifts.postValue(stat.numberShifts)
   }


   fun deleteDayFromID(id:Int) = viewModelScope.launch{
      mainRepository.DeleteShiftFormID(id)
   }

//   fun deleteListDayFromID(listID: List<Int>) = viewModelScope.launch(Dispatchers.IO){
//      for(id in listID) {
//         mainRepository.DeleteShiftFormID(id)
//      }
//   }
   ///удалить список выделенных дней
   fun deleteListDayFromID(list: List<WorkDay>) = viewModelScope.launch{
//      Log.d("MyTag - deleteListDayFromID", "list - ${list.size}")
//      for(d in list) {
//         Log.d("MyTag - deleteListDayFromID", "for(d in list) - ${d._id}")
//         mainRepository.DeleteShiftFormID(d._id)
//      }
      mainRepository.DeleteShiftFormIDnew(list)
   }
   //сохранить время
   fun saveWorked(id: Int, worked:Int) = viewModelScope.launch {
      mainRepository.saveWorked(id, worked)
   }
   //сохранить время во всем списке
   fun saveWorkedListing(listID: List<WorkDay>, worked:Int) = viewModelScope.launch {
      for (d in listID) {
         mainRepository.saveWorked(d._id, worked)
      }
   }
   //сохранить коммент
   fun saveComment(id: Int, comment:String) = viewModelScope.launch {
      mainRepository.saveComment(id, comment)

   }
   //сохранить цвет
   fun saveColor(id: Int, colorString:String) = viewModelScope.launch {
      mainRepository.saveColor(id, colorString)
   }
   //сохранить комент во всем списке
   fun saveCommentListing(listID: List<WorkDay>, comment:String) = viewModelScope.launch {
      for(d in listID) {
         mainRepository.saveComment(d._id, comment)}
   }
   //сохранить цвет во всем списке
   fun saveColorListing(listID: List<WorkDay>, color:String) = viewModelScope.launch {
      for(d in listID) {mainRepository.saveColor(d._id, color)}
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

   suspend fun LastComments():List<String>{
      Log.d("Mytag", "Загрузка комментариев")
      return mainRepository.LastComments().distinct()
   }

   suspend fun LastWorkeds():List<Int>{
      return mainRepository.LastWorkeds().distinct()
   }
}





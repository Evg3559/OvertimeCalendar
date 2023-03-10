package com.evg3559programmer.overtimecalendar.composeUI.screens.addEdit

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.*
import com.evg3559programmer.overtimecalendar.composeUI.MainRepository
import com.evg3559programmer.overtimecalendar.di.Shifts
import com.evg3559programmer.overtimecalendar.di.StateEditing
import com.evg3559programmer.overtimecalendar.di.WorkDay
import com.evg3559programmer.overtimecalendar.di.workDays
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddDayVM @Inject constructor(
   private val mainRepository: MainRepository,
):ViewModel() {

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

   //постараться избавиться от LiveData в сторону Flow
   val daysWork:LiveData<List<WorkDay>> = _daysWork.asLiveData()

   private val _daywork: MutableLiveData<WorkDay> = MutableLiveData(WorkDay(0,0,0,0,0,"",  "#FFFFFFFF"))
      val daywork: LiveData<WorkDay> = _daywork

                                             //: Flow<StateList<WorkDay>>
//   suspend fun Daywork(selectedDay:LocalDate) = viewModelScope.launch(){
//      val day = mainRepository.getDaysOne2(selectedDay)
//      _daywork.postValue(day) ?: WorkDay(0,1,1,1,0,"","#FFFFFF")
//   }


   fun SaveDay(wd: WorkDay) = viewModelScope.launch {
      val ready1: Deferred<String> = async(start = CoroutineStart.LAZY) {mainRepository.SaveDay(wd)}
      if (ready1.await() == "Ok") {
         mutableState.value = StateEditing.SuccessSave
      }else{
         mutableState.value = StateEditing.Error(ready1.getCompleted())
      }
   }

   fun ReplaceDay(wd:WorkDay) = viewModelScope.launch {
      val ready1: Deferred<String> = async(start = CoroutineStart.LAZY) {mainRepository.ReplaceDay(wd)}
      if (ready1.await() == "Ok") {
         mutableState.value = StateEditing.SuccessSave
      }else{
         mutableState.value = StateEditing.Error(ready1.getCompleted())
      }
   }

   suspend fun getDayFromDate (date:LocalDate):WorkDay {
      return mainRepository.getDaysOne2(date)
   }

   ///занятость
   private val _isBusy = MutableLiveData<Boolean>().apply {
      value = false
   }
   val isBusy: LiveData<Boolean> = _isBusy
   fun isBusy(busy: Boolean) = _isBusy.postValue(busy)

   //список булевых значений
   private val _sheetCycle = MutableLiveData<List<Boolean>>().apply{ value = listOf(true) }
   private val _sheetCycleCache: MutableList<Boolean> = ArrayList()
   val sheetCycle: LiveData<List<Boolean>> = _sheetCycle

   fun removeSheetCycleLast(){
      _sheetCycleCache.removeLast()
      _sheetCycle.postValue(_sheetCycleCache)
      //finalShifts()
   }

   fun setSheetCycle(selected: List<Boolean>) {
      _sheetCycleCache.clear()
      _sheetCycleCache.addAll(selected)
      _sheetCycle.postValue(_sheetCycleCache)
      //finalShifts()
   }

   fun changeSheetCycle(item: Int, selected: Boolean) {
      _sheetCycleCache[item] = selected
      _sheetCycle.postValue(_sheetCycleCache)
      // finalShifts()
   }

   fun addSheetCycle(selected: Boolean) {
      _sheetCycleCache.add(selected)
      _sheetCycle.postValue(_sheetCycleCache)
      // finalShifts()
   }

   //количество заполняемых дней
   private val _numberShifts = MutableLiveData<Int>().apply {
      value = 1
   }
   val numberShifts: LiveData<Int> = _numberShifts
   private var _numberShiftsCahce = _numberShifts.value
   fun setNumberShifts(int:Int){
      _numberShiftsCahce = int
      _numberShifts.postValue(_numberShiftsCahce)
      // finalShifts()
   }

   //пальцем выбранный день
   private val _daySelected = MutableLiveData<LocalDate>().apply {
      value = LocalDate.now()
   }
   var _daySelectedCache: LocalDate = LocalDate.now()
   val daySelected: LiveData<LocalDate> = _daySelected
   //выбор стартового дня
   fun daySelect(date: LocalDate) {
      _daySelectedCache = date
      _daySelected.postValue(_daySelectedCache)
      //finalShifts()
   }


   private val _listWorkedDays = MutableLiveData<List<workDays>>().apply{ value = ArrayList() }       //список дат добавляемых рабочих дней
   val listWorkedDays: LiveData<List<workDays>> = _listWorkedDays                                        //список дат добавляемых рабочих дней переходит в sizelist и из нее добавляются дни
   private val _listCoincidenceWorkedDays = MutableLiveData<List<workDays>>().apply{ value = ArrayList() }       //список совпавших существующих дат
   val listCoincidenceWorkedDays: LiveData<List<workDays>> = _listCoincidenceWorkedDays                           //список совпавших дат
   private val _finalShifts = MutableLiveData<List<Shifts>>().apply{ value = listOf<Shifts>()  }         //итоговый список смен
   val finalShifts: LiveData<List<Shifts>> = _finalShifts                                                //итоговый список смен

   fun finalShifts(){
      val _listworkeddays: MutableList<workDays> = ArrayList()
      val _ListShifts: MutableList<Shifts> = ArrayList()
      val _ListCoincidenceshifts: MutableList<workDays> = ArrayList()
      var ticker = 0
      for (i in 0 until numberShifts.value!!){
         _ListShifts.add(Shifts(daySelected.value!!.plusDays(i.toLong()), sheetCycle.value!![ticker]))
         ticker++
         if (ticker >= sheetCycle.value!!.size){ticker = 0}
      }
      _finalShifts.postValue(_ListShifts)
      viewModelScope.launch() {
         _ListShifts.forEach {
            val findDay = mainRepository.getDaysOne(it.date.year,it.date.monthValue,it.date.dayOfMonth)

            if (it.worked) {    //если день рабочий, то ... если день существует, то пропустить    sizelist++
               if (findDay == null){
                  _listworkeddays.add(workDays(it.date.dayOfMonth, it.date.monthValue, it.date.year))
               }else {
                  _ListCoincidenceshifts.add(workDays(it.date.dayOfMonth, it.date.monthValue, it.date.year))
                  //if (rewriteDay.value!!){
                  // _listworkeddays.add(workDays(it.date.dayOfMonth, it.date.monthValue, it.date.year))
                  // }
               }
            }
         }
         _listWorkedDays.postValue(_listworkeddays)
         _listCoincidenceWorkedDays.postValue(_ListCoincidenceshifts)
      }

   }

   private val _listDayswork = MutableLiveData<List<WorkDay>>().apply{
      value = listOf<WorkDay>()
   }
   val listDayswork: LiveData <List<WorkDay>> = _listDayswork
   fun getListDayswork() {
      viewModelScope.launch(Dispatchers.IO) {
         _listDayswork.postValue(mainRepository.findAllDays().value)
      }
   }


   private val _hoursDays = MutableLiveData<Int>().apply { value = 0 }      //отработанное время
   private val _minutesDays = MutableLiveData<Int>().apply { value = 0 }         //минуты
   private val _commentsDays = MutableLiveData<String>().apply { value = ""  }         //комментарий
   private val _colorsDay = MutableLiveData<String>().apply { value = "FFFFFF" }          //цвет дней
   private val _rewriteDay = MutableLiveData<Boolean>().apply { value = false }          //заменять дни
   private val _prPoint = MutableLiveData<Int>().apply { value = 0 }         //премиум баллы
   val hoursDays: LiveData<Int> = _hoursDays
   val minutesDays: LiveData<Int> = _minutesDays
   val commentsDays: LiveData<String> = _commentsDays
   val colorsDays: LiveData<String> = _colorsDay
   val rewriteDay: LiveData<Boolean> = _rewriteDay
   val prPoint: LiveData<Int> = _prPoint
   fun setHours (int:Int){
      _hoursDays.postValue(int)
   }
   fun setMinutes (int:Int){
      _minutesDays.postValue(int)
   }
   fun setComments (str:String){
      _commentsDays.postValue(str)
   }
   fun setColors (str: String){
      _colorsDay.postValue(str)
   }
   fun setRewriteDay (str:Boolean){
      _rewriteDay.postValue(str)
   }
   //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
   fun getPrPoint() = viewModelScope.launch (Dispatchers.IO){
      _prPoint.postValue(mainRepository.getPrPont())
   }
   fun setPrPoint(prPoint: Int) = viewModelScope.launch (Dispatchers.IO){
      mainRepository.writePrPont(prPoint)
   }


   fun addDaysToDB(listdays:List<workDays>,  Coincidencelist:List<workDays>,
                   hours:Int, minutes:Int,
                   comments:String, colors:String) = viewModelScope.launch (Dispatchers.IO){
      val min = hours*60+minutes
      /////!!!!!!срочно переделать на mRepository!!!!!!!!!
      isBusy(true)
      listdays.forEach {
         //mRoomDaysworkViewModel.insertDay(RoomDayswork(0, it.Year, it.month, it.day, min, comments, colors))
         mainRepository.SaveDay(WorkDay(0, it.Year, it.month, it.day, min, comments, colors))
      }
      if (rewriteDay.value!!) {
         Log.d("MyTag", "rewriteDay ${rewriteDay.value}")
         Coincidencelist.forEach {
            //mRoomDaysworkViewModel.editDayWithoutID(it.Year, it.month, it.day, min, comments, colors)
            mainRepository.ReplaceDayWithoutID(WorkDay(0, it.Year, it.month, it.day, min, comments, colors))

         }
      }
      isBusy(false)
   }

   private val _openDialog = MutableLiveData<Boolean>().apply { value = false  }
   val openDialog: LiveData<Boolean> = _openDialog
   fun openDialog(bool:Boolean) = _openDialog.postValue(bool)


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
      return mainRepository.LastComments().distinct()
   }

   suspend fun LastWorkeds():List<Int>{
      return mainRepository.LastWorkeds().distinct()
   }

}


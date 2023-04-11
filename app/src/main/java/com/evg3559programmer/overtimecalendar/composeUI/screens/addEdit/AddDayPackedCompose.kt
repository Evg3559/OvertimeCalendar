package com.evg3559programmer.overtimecalendar.composeUI.screens.addEdit


import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.evg3559programmer.overtimecalendar.R
import com.evg3559programmer.overtimecalendar.di.MyColors
import com.evg3559programmer.overtimecalendar.di.WorkDay
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth


@Composable
fun CycleShifts(viewmodel: AddDayVM) {
   val context = LocalContext.current
   val selected = remember { mutableStateListOf(true) }
   //val selected = viewmodel.sheetCycle.observeAsState().value!!

   selected.let {
      viewmodel.setSheetCycle(it.toList())                                       //изменение цикла смен
      viewmodel.finalShifts()
   }

   Column() {
      Box(
         modifier = Modifier
            .padding(top = 15.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth()
      ) {
         Text(text = stringResource(id = R.string.dialShifts_packAdd), fontSize = 18.sp)               //Наберите цикл смен
      }
      Row(
         verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .padding(start = 15.dp, top = 10.dp, end = 15.dp)
            .fillMaxWidth()
      ) {
         Box(
            modifier = Modifier
               .size(15.dp)
               .background(Color(0xFFFF5722))
         ) {}
         Text(
            text = stringResource(id = R.string.workDay_packAdd), fontSize = 18.sp     //" - рабочий день"
         )
      }
      Row(
         verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .padding(start = 15.dp, top = 10.dp, end = 15.dp)
            .fillMaxWidth()
      ) {
         Box(
            modifier = Modifier
               .size(15.dp)
               .background(Color(0xFF8BC34A))
         ) {}
         Text(text = stringResource(id = R.string.weekendDay_packAdd), fontSize = 18.sp)        // - выходной день
      }

      Row(
         modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
      ) {
         for (i in selected.indices) {
               Card(
                  modifier = Modifier
                     .size(45.dp)
                     .padding(3.dp), elevation = 4.dp
               ) {
                  Box(modifier = Modifier
                     .fillMaxSize()
                     .background(if (selected[i]) Color(0xFFFC7D55) else Color(0xFF8BC34A))
                     .clickable {                                                                  //сам процесс клика по циклу смен
                        //  viewmodel.changeSheetCycle(i, !selected[i])
                        selected[i] = !selected[i]
                     }, contentAlignment = Alignment.Center) {
                     Text(text = "${i + 1}", fontSize = 16.sp, color = Color.White)
                  }
               }
         }

            Card(
               elevation = 4.dp, modifier = Modifier
                  .size(45.dp)
                  .padding(3.dp)
            ) {
               Box(contentAlignment = Alignment.Center, modifier = Modifier
                  .background(Color.LightGray)
                  .clickable {
                     if (selected.size < 32) selected.add(false)
                     //if (selected.size < 32) viewmodel.addSheetCycle(false)

                  }) {
                  Text(text = "+", color = Color.Black, fontSize = 21.sp)
               }
            }


            Card(
               elevation = 4.dp, modifier = Modifier
                  .size(45.dp)
                  .padding(3.dp)
            ) {
               Box(contentAlignment = Alignment.Center, modifier = Modifier
                  .background(Color.LightGray)
                  .clickable {
                     if (selected.size != 1) {
                        selected.removeLast()
                        //viewmodel.removeSheetCycleLast()
                     }
                  }) {
                  Text(text = "-", color = Color.Black, fontSize = 21.sp)
               }
            }

      }
   }
}

@Composable
fun TickerDays(viewmodel: AddDayVM) {
   val tickerObserver = viewmodel.sheetCycle.observeAsState().value!!.size
   val ticker = rememberSaveable { mutableStateOf(10) }
     ticker.let { viewmodel.setNumberShifts(ticker.value) }
   val showSlider = remember {mutableStateOf(false) }
   val sliderValue = remember { mutableStateOf(0)}
   val numberText = remember { mutableStateOf(ticker.value)}

   Column() {
      Box(
         modifier = Modifier
            .padding(top = 10.dp, start = 15.dp, end = 15.dp)
            .fillMaxWidth()
      ) {
         Text(text = stringResource(id = R.string.howManyFill_packAdd), fontSize = 18.sp)       //Сколько дней заполнить?
      }
      Row(
         verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)
      ) {
         Card(
            modifier = Modifier
               .size(40.dp)
               .padding(6.dp), elevation = 4.dp
         ) { //+
            Box(contentAlignment = Alignment.Center, modifier = Modifier
               .background(Color(0xFFE4E0DE))
               .clickable {
                  if (ticker.value > 0) {
                     ticker.value--
                     numberText.value = ticker.value
                     sliderValue.value = ticker.value
                  }
               }) {
               Text(text = "-", color = Color.Black, fontSize = 20.sp)
            }
         }

         Card(
            modifier = Modifier
               .size(width = 50.dp, height = 40.dp)
               .padding(6.dp), elevation = 4.dp
         ) {
            Box(
               contentAlignment = Alignment.Center, modifier = Modifier
                  .fillMaxSize()
                  .clickable {
                     showSlider.value = !showSlider.value
                  }
            ) {
               Text(text = "${numberText.value}", fontSize = 16.sp)
            }
         }

         Card(
            modifier = Modifier
               .size(40.dp)
               .padding(6.dp), elevation = 4.dp
         ) { //-
            Box(contentAlignment = Alignment.Center, modifier = Modifier
               .background(Color(0xFFE4E0DE))
               .clickable {
                  if (ticker.value < 62) {
                     ticker.value++
                     numberText.value = ticker.value
                     sliderValue.value = ticker.value
                  }
               }) {
               Text(text = "+", color = Color.Black, fontSize = 21.sp)
            }

         }
      }
      if (showSlider.value) Slider(modifier = Modifier.padding(start = 15.dp, end = 15.dp),
         value = sliderValue.value.toFloat(),
         onValueChange = {sliderValue.value = it.toInt()
            numberText.value = sliderValue.value},
         onValueChangeFinished = {
            ticker.value = sliderValue.value
            sliderValue.value = ticker.value
         },
         valueRange = 0f..65f,
         steps = 65,
         colors = SliderDefaults.colors(
            thumbColor = Color.Red,
            activeTrackColor = Color.LightGray
         ))
   }

}

@Composable
fun ViewCalendar(viewmodel: AddDayVM) {
   val selectedDay = viewmodel.daySelected.observeAsState().value ?: LocalDate.now()
   val fillsDays = remember { mutableStateListOf(selectedDay) }                     //существующие дни заполненные
   val numberShifts = viewmodel.numberShifts.observeAsState().value ?: 3
   val context = LocalContext.current
//   val mRoomDaysworkViewModel: RoomDaysworkViewModel = viewModel (
//      factory = RoomDaysworkViewModelFactory(context.applicationContext as Application)
//   )
//   viewmodel.getListDayswork (mRoomDaysworkViewModel) //пусто


   selectedDay.let {
      fillsDays.clear()
      val list: MutableList<LocalDate> = ArrayList()
      for (i in 0 until numberShifts){
         list.add (selectedDay.plusDays(i.toLong()))
      }
      fillsDays.addAll(list)
      viewmodel.finalShifts()
   }

   Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 30.dp)) {
      Box(
         contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()
      ) {
         Text(
            fontSize = 16.sp, text = stringResource(id = R.string.selectStartFilling_packAdd)         //Выберите дату начала заполнения
         )
      }
      SelectableCalendar(
         calendarState = rememberSelectableCalendarState(
            initialMonth = YearMonth.now(),
            initialSelection = fillsDays,
            initialSelectionMode = SelectionMode.Single,
         ),
         modifier = Modifier.animateContentSize(),
         showAdjacentMonths = true,
         firstDayOfWeek = DayOfWeek.MONDAY,
         today = LocalDate.now(),
         monthContainer = { MonthContainer(it) },
         dayContent = { DayContent(dayState = it, viewmodel) },
         daysOfWeekHeader = { WeekHeader(daysOfWeek = it) },
         monthHeader = { MonthHeader(monthState = it) },
      )
   }
}

@Composable
private fun DayContent(dayState: DayState<DynamicSelectionState>, viewmodel: AddDayVM) {
   val context = LocalContext.current
   val listDayswork = viewmodel.daysWork.observeAsState(listOf()).value                   //список смен в базе данных
//   val listDayswork = remember() {
//      derivedStateOf {
//         viewmodel.daysWork.value
//      }
//   }
   val isWorkDay = remember { mutableStateOf(false)   }
   val colorCyrcle = remember {mutableStateOf(Color.White)}
   val date = dayState.date
   val selectionState = dayState.selectionState
   var selectionColor: Color = Color.LightGray
   val currentDayColor: Color = Color.Blue
   val weekendDay = date.dayOfWeek.value == 7 || date.dayOfWeek.value == 6
   val isSelected = selectionState.isDateSelected(date)
   val daysWorked = viewmodel.finalShifts.observeAsState().value?.filter{ it.date == date }!!      //для закраски дня
//   val daysWorked by remember {
//      derivedStateOf {
//         viewmodel.finalShifts.value?.filter{ it.date == date }!!
//      }
//   }

   if (daysWorked.isNotEmpty()){       //закраска выделенного дня
      selectionColor =  if (daysWorked[0].worked) Color(0xFFFFA4A4) else Color(0xFFCDFF93)
   }
   if (listDayswork != null) {
      if (listDayswork.isNotEmpty()) {       //закраска кружком
         val _listWork = listDayswork.filter {
            (it.day == date.dayOfMonth) && (it.month == date.monthValue) && (it.year == date.year)
         }
         val workday = if (_listWork.isNotEmpty()) _listWork[0] else {
            WorkDay(0,0,0,0,0,"","#FFFFFFFF")
         }
         if ((workday.day == date.dayOfMonth) && (workday.month == date.monthValue) && (workday.year == date.year)) {
            isWorkDay.value = true
            colorCyrcle.value = Color(workday.color.toColorInt())
         }else {isWorkDay.value = false}
      }else {isWorkDay.value = false}
   } else {isWorkDay.value = false}


   Card(
      modifier = Modifier
         .aspectRatio(1f)
         .padding(2.dp),
      elevation = if (dayState.isFromCurrentMonth) 4.dp else 0.dp,
      border = if (dayState.isCurrentDay) BorderStroke(1.dp, currentDayColor) else null,
      contentColor = if (isSelected) selectionColor else contentColorFor(
         backgroundColor = MaterialTheme.colors.surface
      ),
      backgroundColor = if (isSelected) {selectionColor }  else {       //если выбран день иначе
                           if (weekendDay) { Color(0xFFFFF2F2)          //если выходной иначе простой день
                                          } else  {   Color(0xFFFFFFFF)
         }
      }
   ){
      Box(
         modifier = Modifier.clickable {
            viewmodel.daySelect(date)
         },
         contentAlignment = Alignment.Center,
      ) {
         if (isWorkDay.value) {
            Canvas(modifier = Modifier.fillMaxSize(), onDraw = {  //если такой день есть, рисуем круг цветом дня
            drawCircle(color = Color.Black,
               radius = size.maxDimension/3,
               center = center,
               style = Stroke(width = 8f))
            drawCircle(color = colorCyrcle.value,
               radius = size.maxDimension/3,
               center = center,
               style = Stroke(width = 7f))
         })}
         Text(text = date.dayOfMonth.toString(),
            color = if (dayState.isFromCurrentMonth) Color.Black else Color.LightGray)
      }
   }
}

@Composable
private fun WeekHeader(daysOfWeek: List<DayOfWeek>) {
   Row() {
      daysOfWeek.forEach { dayOfWeek ->
         Text(
            color = if (dayOfWeek.value == 6 || dayOfWeek.value == 7) Color(0xFFA50000) else (Color.Black),
            textAlign = TextAlign.Center,
            text = stringArrayResource(id = R.array.material_calendar_weeks_array)[dayOfWeek.value-1],  // dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ROOT),
            modifier = Modifier
               .weight(1f)
               .wrapContentHeight()
         )
      }
   }
}

@Composable
private fun MonthHeader(monthState: MonthState) {
   Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
   ) {
      IconButton(modifier = Modifier.testTag("Decrement"), onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }) {
         Image(
            imageVector = Icons.Default.KeyboardArrowLeft,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            contentDescription = "Previous",
         )
      }
      Text(
         modifier = Modifier.testTag("MonthLabel"),
         text = stringArrayResource(id = R.array.material_calendar_months_array)[monthState.currentMonth.monthValue-1],  //monthState.currentMonth.month.name.lowercase().replaceFirstChar { it.titlecase() },
         style = MaterialTheme.typography.h5
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(text = monthState.currentMonth.year.toString(), style = MaterialTheme.typography.h5)
      IconButton(modifier = Modifier.testTag("Increment"), onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }) {
         Image(
            imageVector = Icons.Default.KeyboardArrowRight,
            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            contentDescription = "Next",
         )
      }
   }
}

@Composable
private fun MonthContainer(content: @Composable (PaddingValues) -> Unit) {
   Box(
      content = { content(PaddingValues()) },
   )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HoursComment(viewmodel: AddDayVM) {
   val sizeColorBloks = 35.dp
   val sizeHeightColorBloks =  remember { mutableStateOf(45.dp) }
   val selectedColors = remember { mutableStateOf("#FFFFFFFF") }
   val hours = remember { mutableStateOf(0) }
   val minute = remember { mutableStateOf(0) }
   val comments = remember { mutableStateOf("") }
   val showSliderHours = remember { mutableStateOf(false)}
   val showSliderMinutes = remember { mutableStateOf(false)}
   val SliderHoursValue = remember { mutableStateOf(0)}
   val SliderMinutesValue = remember { mutableStateOf(0)}
   val checkedBox = remember { mutableStateOf(false)}             //заменить существующие дни
   val sizeCoincidencelist = viewmodel.listCoincidenceWorkedDays.observeAsState().value!!.size ?: 0          //количество совпавших добавляемых смен
   val context = LocalContext.current
   val focusRequesterMin = FocusRequester()
   val focusRequesterComment = FocusRequester()
   val composableScope = rememberCoroutineScope()
   val bringIntoViewRequester = remember { BringIntoViewRequester() }       // скролл к редактируемому элементу
   val bringIntoTextComment = remember { BringIntoViewRequester() }       // скролл к редактируемому элементу
   val dialogPicker = rememberMaterialDialogState()
   val colorAdditional = viewmodel.colorAdditional.observeAsState(listOf<Color>()).value


   SliderHoursValue.let { viewmodel.setHours(it.value) }
   SliderMinutesValue.let { viewmodel.setMinutes(it.value) }
   comments.let { viewmodel.setComments(it.value) }
   selectedColors.let {viewmodel.setColors(it.value)}
   checkedBox.let {viewmodel.setRewriteDay(it.value)
      viewmodel.finalShifts()}



   Column() {
      Text(text = stringResource(id = R.string.hoursPerDay_packAdd), fontSize = 18.sp, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp))              //Количество часов в день:

      Row(
         horizontalArrangement = Arrangement.Start,
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .padding(start = 15.dp, top = 2.dp, end = 15.dp)
            .fillMaxWidth()
      ) {
         OutlinedTextField(
            value = if (SliderHoursValue.value != 0) SliderHoursValue.value.toString() else "",
            onValueChange = { if (it != "") {
               try {
                  SliderHoursValue.value = it.toInt()
                  if (SliderHoursValue.value > 48) SliderHoursValue.value = 48
                  if (SliderHoursValue.value < -12) SliderHoursValue.value = -12} catch (e:Exception){SliderMinutesValue.value = 0}
            } else SliderHoursValue.value = 0
               if (it.length >= 2) focusRequesterMin.requestFocus()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions( onSend = {focusRequesterMin.requestFocus()}),
            singleLine = true,
            placeholder = { Text(text = "0") },
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier
               .width(60.dp)
               .bringIntoViewRequester(bringIntoViewRequester)
               .onFocusEvent {
                  if (it.isFocused) {
                     composableScope.launch {
                        delay(300)
                        bringIntoViewRequester.bringIntoView()
                     }
                  }
               },
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
         )
         Text(text = stringResource(id = R.string.hours), modifier = Modifier.clickable {          // - часов,
            showSliderHours.value = !showSliderHours.value
            showSliderMinutes.value = false
         }, style = TextStyle(color = if (showSliderHours.value) Color.Green else Color.Black))
         OutlinedTextField(
            value =  if (SliderMinutesValue.value != 0) SliderMinutesValue.value.toString() else "",
            onValueChange = { if (it != "") { try { SliderMinutesValue.value = it.toInt()
            if (SliderMinutesValue.value > 480) SliderMinutesValue.value = 480
            if (SliderMinutesValue.value < -60) SliderMinutesValue.value = -60 } catch (e: Exception){ SliderMinutesValue.value = 0 }
            } else SliderMinutesValue.value = 0 },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions( onSend = {focusRequesterComment.requestFocus()}),
            singleLine = true,
            placeholder = { Text(text = "0") },
            textStyle = TextStyle(fontSize = 16.sp),
            modifier = Modifier
               .width(60.dp)
               .focusRequester(focusRequesterMin)
               .bringIntoViewRequester(bringIntoViewRequester)
               .onFocusEvent {
                  if (it.isFocused) {
                     composableScope.launch {
                        delay(300)
                        bringIntoViewRequester.bringIntoView()
                     }
                  }
               },
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
         )
         Text(text = stringResource(id = R.string.minute), modifier = Modifier.clickable {                 // - минут
            showSliderHours.value = false
            showSliderMinutes.value = !showSliderMinutes.value
         }, style = TextStyle(color = if (showSliderMinutes.value) Color.Green else Color.Black))
      }

      if (showSliderHours.value) Slider(modifier = Modifier.padding(start = 15.dp, end = 15.dp),
         value = SliderHoursValue.value.toFloat(),
         onValueChange = {SliderHoursValue.value = it.toInt() },
         onValueChangeFinished = {},
         valueRange = 0f..49f,
         steps = 49,
         colors = SliderDefaults.colors(
            thumbColor = Color.Red,
            activeTrackColor = Color.LightGray
         ))
      if (showSliderMinutes.value) Slider(modifier = Modifier.padding(start = 15.dp, end = 15.dp),
         value = SliderMinutesValue.value.toFloat(),
         onValueChange = {SliderMinutesValue.value = it.toInt() },
         onValueChangeFinished = {},
         valueRange = 0f..60f,
         steps = 60,
         colors = SliderDefaults.colors(
            thumbColor = Color.Red,
            activeTrackColor = Color.LightGray
         ))

      OutlinedTextField(
         comments.value,
         {comments.value = it  },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences),
         singleLine = false,
         maxLines = 3,
         placeholder = { Text(text = stringResource(id = R.string.ifComment)) },
         textStyle = TextStyle(fontSize = 16.sp),
         modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 20.dp)
            .focusRequester(focusRequesterComment)
            .bringIntoViewRequester(bringIntoTextComment)
            .onFocusEvent {
               if (it.isFocused) {
                  composableScope.launch {
                     delay(300)
                     bringIntoTextComment.bringIntoView()
                  }
               }
            },
         colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.Black)
      )

      //цвета
      Text(text = stringResource(id = R.string.SelectColor), modifier = Modifier.padding(start = 20.dp))
      Row(horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .fillMaxWidth()
         .height(75.dp)
         .horizontalScroll(rememberScrollState())
         .padding(vertical = 5.dp, horizontal = 20.dp)) {
         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(if (selectedColors.value != "#FFFFFFFF") sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
            .padding(3.dp), elevation = 2.dp,
            border = if (selectedColors.value != "#FFFFFFFF") BorderStroke(1.dp,Color.Gray) else {BorderStroke(1.dp,Color.Red)}) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color.White)
               .clickable {
                  selectedColors.value = "#FFFFFFFF"
               }, contentAlignment = Alignment.Center ) {
               if (selectedColors.value == "#FFFFFFFF") Text(text = "✓")
            }
         }
         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(if (selectedColors.value != "#FFA7FFFF") sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
            .padding(3.dp), elevation = 2.dp,
            border = if (selectedColors.value != "#FFA7FFFF") BorderStroke(1.dp,Color.Gray) else {BorderStroke(1.dp,Color.Red)}) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color("#FFA7FFFF".toColorInt()))
               .clickable {
                  selectedColors.value = "#FFA7FFFF"
               }, contentAlignment = Alignment.Center ) {
               if (selectedColors.value == "#FFA7FFFF") Text(text = "✓")
            }
         }
         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(if (selectedColors.value != "#FFFFA199") sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
            .padding(3.dp), elevation = 2.dp,
            border = if (selectedColors.value != "#FFFFA199") BorderStroke(1.dp,Color.Gray) else {BorderStroke(1.dp,Color.Red)}) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color("#FFA199".toColorInt()))
               .clickable {
                  selectedColors.value = "#FFFFA199"
               }, contentAlignment = Alignment.Center ) {
               if (selectedColors.value == "#FFFFA199") Text(text = "✓")
            }
         }
         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(if (selectedColors.value != "#FFD2FAA4") sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
            .padding(3.dp), elevation = 2.dp,
            border = if (selectedColors.value != "#FFD2FAA4") BorderStroke(1.dp,Color.Gray) else {BorderStroke(1.dp,Color.Red)}) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color("#FFD2FAA4".toColorInt()))
               .clickable {
                  selectedColors.value = "#FFD2FAA4"
               }, contentAlignment = Alignment.Center ) {
               if (selectedColors.value == "#FFD2FAA4") Text(text = "✓")
            }
         }
         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(if (selectedColors.value != "#FFB7C2FF") sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
            .padding(3.dp), elevation = 2.dp,
            border = if (selectedColors.value != "#FFB7C2FF") BorderStroke(1.dp,Color.Gray) else {BorderStroke(1.dp,Color.Red)}) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color("#FFB7C2FF".toColorInt()))
               .clickable {
                  selectedColors.value = "#FFB7C2FF"
               }, contentAlignment = Alignment.Center ) {
               if (selectedColors.value == "#FFB7C2FF") Text(text = "✓")
            }
         }
         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(if (selectedColors.value != "#FFF7FF9E") sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
            .padding(3.dp), elevation = 2.dp,
            border = if (selectedColors.value != "#FFF7FF9E") BorderStroke(1.dp,Color.Gray) else {BorderStroke(1.dp,Color.Red)}) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color("#FFF7FF9E".toColorInt()))
               .clickable {
                  selectedColors.value = "#FFF7FF9E"
               }, contentAlignment = Alignment.Center ) {
               if (selectedColors.value == "#FFF7FF9E") Text(text = "✓")
            }
         }
         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(if (selectedColors.value != "#FFF3ADFF") sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
            .padding(3.dp), elevation = 2.dp,
            border = if (selectedColors.value != "#FFF3ADFF") BorderStroke(1.dp,Color.Gray) else {BorderStroke(1.dp,Color.Red)}) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color("#FFF3ADFF".toColorInt()))
               .clickable {
                  selectedColors.value = "#FFF3ADFF"
               }, contentAlignment = Alignment.Center ) {
               if (selectedColors.value == "#FFF3ADFF") Text(text = "✓")
            }
         }
         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(if (selectedColors.value != "#FFFFCE94") sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
            .padding(3.dp), elevation = 2.dp,
            border = if (selectedColors.value != "#FFFFCE94") BorderStroke(1.dp,Color.Gray) else {BorderStroke(1.dp,Color.Red)}) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color("#FFFFCE94".toColorInt()))
               .clickable {
                  selectedColors.value = "#FFFFCE94"
               }, contentAlignment = Alignment.Center ) {
               if (selectedColors.value == "#FFFFCE94") Text(text = "✓")
            }
         }

         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(sizeHeightColorBloks.value - 20.dp)
            .padding(3.dp), elevation = 2.dp,
            border = BorderStroke(1.dp,Color.Gray)) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color(selectedColors.value.toColorInt()))
               .clickable {
                  dialogPicker.show()
               },
               contentAlignment = Alignment.Center ) {

               if (selectedColors.value != "#FFFFCE94" &&
                  selectedColors.value != "#FFF3ADFF" &&
                  selectedColors.value != "#FFF7FF9E" &&
                  selectedColors.value != "#FFB7C2FF" &&
                  selectedColors.value != "#FFD2FAA4" &&
                  selectedColors.value != "#FFFFA199" &&
                  selectedColors.value != "#FFA7FFFF" &&
                  selectedColors.value != "#FFFFFFFF") {
                  Text(text = "✓") } else { Text(text = "?") }
            }
         }
      }
      if (sizeCoincidencelist > 0) {
         Text(text =  stringResource(id = R.string.matchesShifts_packAdd,sizeCoincidencelist),modifier = Modifier.padding(start = 10.dp, end = 5.dp))             //Совпадений с существущими сменами:

         Row(modifier = Modifier.padding(top = 0.dp, bottom = 0.dp, start = 5.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically ) {
            Checkbox(checked = checkedBox.value, onCheckedChange = {checkedBox.value = it})
            Text(text = stringResource(id = R.string.ReplaceDays_packAdd), modifier = Modifier.clickable { checkedBox.value = !checkedBox.value })    ///Заменить существующие дни
         }
         Row(modifier = Modifier.padding(top = 0.dp, bottom = 2.dp, start = 5.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically ) {
            Checkbox(checked = !checkedBox.value, onCheckedChange = {checkedBox.value = !it })
            Text(text = stringResource(id = R.string.SkipDays_packAdd), modifier = Modifier.clickable { checkedBox.value = !checkedBox.value })   //Пропустить существующие дни
         }
      } else {
         Text(text = stringResource(id = R.string.matchesNo_packAdd),modifier = Modifier.padding(start = 10.dp, end = 5.dp))                     //Совпадений с существующими сменами нет
      }

      viewmodel.LastColors()
      MaterialDialog(dialogState = dialogPicker) {
         colorChooser(colors = (MyColors.ColorsPicker+colorAdditional).distinct(), argbPickerState = ARGBPickerState.WithoutAlphaSelector, waitForPositiveButton = false,
            onColorSelected = {
               selectedColors.value = "#${Integer.toHexString(it.toArgb())}"
            })
         Text(text = "OK", fontSize = 20.sp,
            modifier = Modifier
               .padding(vertical = 10.dp, horizontal = 20.dp)
               .fillMaxWidth(1f)
               .clickable {
                  dialogPicker.hide()
               },
            textAlign = TextAlign.End)
      }
   }

}

@Composable
fun Buttons(viewmodel: AddDayVM){
   val isBusy = viewmodel.isBusy.observeAsState().value ?: false                        //в работе
   val sizelist = viewmodel.listWorkedDays.observeAsState().value!!.size ?: 0          //количество добавляемых смен
   val rewriteDay = viewmodel.rewriteDay.observeAsState().value ?: false
   val sizeCoincidencelist = viewmodel.listCoincidenceWorkedDays.observeAsState().value!!.size ?: 0          //количество совпавших добавляемых смен
   val prPoint = viewmodel.prPoint.observeAsState().value ?: 0                                    //премиум баллы
   var cost = 0                                                                         //итоговая стоимость
   val openDialog = viewmodel.openDialog.observeAsState().value ?: false                  //диалог
   val success = remember { mutableStateOf(false)}
   val error = remember { mutableStateOf(false)}
   val context = LocalContext.current

   viewmodel.getPrPoint()

   Column(modifier = Modifier
      .fillMaxWidth()
      .padding(start = 5.dp, top = 15.dp, end = 5.dp)) {
      Row(modifier = Modifier
         .fillMaxWidth()
         .padding(bottom = 0.dp, top = 10.dp, start = 5.dp, end = 10.dp),
         horizontalArrangement = Arrangement.End,
         verticalAlignment = Alignment.CenterVertically) {
         if (isBusy) {
            CircularProgressIndicator(modifier = Modifier.padding(end = 15.dp))
         }

         if (success.value) Icon(painterResource(id = R.drawable.ic_checkmark), contentDescription = "Ok", tint =  Color.Green)
         if (error.value) Icon(painterResource(id = R.drawable.ic_delete_icon), contentDescription = "Ok", tint =  Color.Red)
         Button(
            enabled = if (success.value) false else { (sizelist + if (rewriteDay) sizeCoincidencelist else 0) > 0 && !isBusy },
            colors = ButtonDefaults.buttonColors(Color.LightGray),
            onClick = {
               if (prPoint < cost) {
                  viewmodel.openDialog(true)             //диалог что поинтов не хватает, пойдем в долг?
               } else { InsertToDB(viewmodel, cost, success, error)}
            }) {
            Text(text = stringResource(id = R.string.toFill_packAdd), maxLines = 1)                //Заполнить
         }

      }
      Row(modifier = Modifier
         .padding(bottom = 25.dp, top = 0.dp, start = 5.dp, end = 10.dp)
         .fillMaxWidth(),horizontalArrangement = Arrangement.End,
         verticalAlignment = Alignment.CenterVertically
      ){
         Column(horizontalAlignment = Alignment.End) {
            Text(text = stringResource(id = R.string.youhavePrPoint_packAdd, prPoint),                      //У вас $prPoint премиум баллов"
               modifier = Modifier.padding(end = 5.dp)
            )
            cost =  if ((sizelist + if (rewriteDay) sizeCoincidencelist else 0)  <= 2) 0 else ((sizelist + if (rewriteDay) sizeCoincidencelist else 0) / 10) + 1

            Text(text = stringResource(id = R.string.youaddShifts_text_packAdd, sizelist + if (rewriteDay) sizeCoincidencelist else 0 ),modifier = Modifier.padding(start = 0.dp, end = 5.dp))           //Вы добавляете ${sizelist + if (rewriteDay) sizeCoincidencelist else 0 } смен
            Text(text = stringResource(id = R.string.costAdding_text_packAdd, cost),modifier = Modifier.padding(start = 0.dp, end = 5.dp))                                    //Стоимость добавления $cost премиум баллов
            }
         }

      Text(text = stringResource(id = R.string.addOneShift_text_packAdd),modifier = Modifier.padding(start = 15.dp, end = 10.dp))       //Добавление одной смены бесплатно,
      Text(text = stringResource(id = R.string.f3to10_text_packAdd), modifier = Modifier.padding(start = 15.dp, end = 10.dp))              //От 3 до 10 смен - 1 балл,
      Text(text = stringResource(id = R.string.f11to20_text_packAdd), modifier = Modifier.padding(start = 15.dp, end = 10.dp))                //От 11 до 20 смен- 2 балла,
      Text(text = stringResource(id = R.string.f21to30_text_packAdd), modifier = Modifier.padding(start = 15.dp, end = 10.dp))         //От 21 до 30 смен - 3 балла и т.д.

   }

   if (openDialog) {
      if ((prPoint - cost) < -5){
         NotificationDialog(onDismiss = {viewmodel.openDialog(false)},
         title = stringResource(id = R.string.somethingWrong_packAdd),                           //Что-то пошло не так
         text = stringResource(id = R.string.enough_prPoint_packAdd))              //У вас недостаточно премиум баллов
      } else {
      AlertDlg(onAccept = {
         viewmodel.openDialog(false)
         InsertToDB(viewmodel, cost, success, error)
      }, onDismiss = {viewmodel.openDialog(false)} )
   }
   }


}

private fun InsertToDB (viewmodel: AddDayVM, cost: Int, success: MutableState<Boolean>, error: MutableState<Boolean>){
   val listdays = viewmodel.listWorkedDays.value
   val Coincidencelist = viewmodel.listCoincidenceWorkedDays.value          //список совпавших дней в базе
   val hours = viewmodel.hoursDays.value ?: 0
   val minutes = viewmodel.minutesDays.value ?: 0
   val comments = viewmodel.commentsDays.value ?: ""
   val colors = viewmodel.colorsDays.value ?: "FFFFFF"
   val prPoint = viewmodel.prPoint.value ?: 0                                   //премиум баллы

   try {
      viewmodel.addDaysToDB(listdays!!,Coincidencelist!!, hours, minutes, comments.trim(), colors)
      success.value = true
   } catch (e: Exception ) {
      error.value = true
   } finally {
      if (!error.value) { viewmodel.setPrPoint( prPoint - cost)
      Log.d("MyTag", "error")}
   }

}

//сделать отказ, в случае, если pr-поинтов меньше 0
@Composable
fun AlertDlg(onAccept: () -> Unit, onDismiss: () -> Unit) {
   AlertDialog(onDismissRequest = { onDismiss() },
      title = { Text(text = stringResource(id = R.string.Insufficient_packAdd)) },            //Недостаточно средств
      text = { Text(text = stringResource(id = R.string.working_debt_Text_packAdd)) },           //Не хватает премиум баллов, работаем в долг?
      buttons = {
         Row(
            modifier = Modifier
               .padding(all = 8.dp)
               .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
         ) {
            Button(modifier = Modifier.wrapContentSize(), onClick = { onDismiss() }) {
               Text(stringResource(id = R.string.CancelButton_packAdd))          //Отмена
            }

            Button(modifier = Modifier.wrapContentSize(), onClick = { onAccept() }) {
               Text(stringResource(id = R.string.ToAcceptButton_packAdd))               //Принять
            }
         }
      })
}

@Composable
fun NotificationDialog(onDismiss: () -> Unit, title: String, text: String) {
   AlertDialog(onDismissRequest = { onDismiss() },
      title = { Text(text = title) },
      text = { Text(text = text) },
      buttons = {
         Row(
            modifier = Modifier
               .padding(all = 8.dp)
               .fillMaxWidth(), horizontalArrangement = Arrangement.End
         ) {
            Button(modifier = Modifier.wrapContentSize(), onClick = { onDismiss() }) {
               Text("Ok")
            }
         }
      })
}
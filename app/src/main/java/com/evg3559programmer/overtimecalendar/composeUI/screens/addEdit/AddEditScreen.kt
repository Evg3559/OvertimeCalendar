package com.evg3559programmer.overtimecalendar.composeUI.screens.addEdit

import android.content.Context
import android.os.Bundle
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.toColorInt
import androidx.datastore.core.DataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.evg3559programmer.overtimecalendar.DataStore.AppSettings
import com.evg3559programmer.overtimecalendar.R
import com.evg3559programmer.overtimecalendar.composeUI.screens.mainmenu.MainMenuViewModel
import com.evg3559programmer.overtimecalendar.di.MyColors
import com.evg3559programmer.overtimecalendar.di.StateEditing
import com.evg3559programmer.overtimecalendar.di.WorkDay
import com.evg3559programmer.overtimecalendar.di.WorkedTimeInHours
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import kotlinx.coroutines.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun AddEditScreen (navController: NavController, mainMenuVM: MainMenuViewModel, dataStore: DataStore<AppSettings>) {
   val addDayVM: AddDayVM = hiltViewModel()
   val extendedView = remember { mutableStateOf(false)}
   Column(modifier = Modifier
      .fillMaxWidth(1f)
      .verticalScroll(rememberScrollState())
   ){
   Row(modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
      Text(text = stringResource(id = R.string.Multiple_AddDay),
      modifier = Modifier.clickable { extendedView.value = !extendedView.value })
   Switch(checked = extendedView.value,
   onCheckedChange = {extendedView.value = it})
   }
      if (extendedView.value){
         ExtendedView(addDayVM)
      } else {

         SimpleView(mainMenuVM, addDayVM, dataStore)
      }
   }


}

@Composable
fun ExtendedView(addDayVM: AddDayVM) {

   LaunchedEffect(Unit){
      val params = Bundle()
      params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MyArt");
      params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Extended add days");
      Firebase.analytics.logEvent("MovementScreen", params)
   }
      CycleShifts(addDayVM)
      TickerDays(addDayVM)
      ViewCalendar(addDayVM)
      HoursComment(addDayVM)
      Buttons(addDayVM)

}

@Composable
fun SimpleView(mainMenuVM: MainMenuViewModel, addDayVM: AddDayVM, dataStore: DataStore<AppSettings> ){
   val stateEdition = addDayVM.stateEdition.collectAsState().value
   //смены
   val listWorkedDay = addDayVM.daysWork.observeAsState().value
   val choosing = remember { mutableStateOf(false) }
   val IDday = remember { mutableStateOf(0)}
   val selectedDate: MutableState<LocalDate> = remember { mutableStateOf(LocalDate.now())}
   val workedMinEntered =  remember { mutableStateOf(0)}            //значение введеное пользователем
   val commentEntered =  remember { mutableStateOf("")}
   val colorEntered =  remember { mutableStateOf("#FFFFFFFF")}
   val composableScope = rememberCoroutineScope()
   val dialogPicker = rememberMaterialDialogState()
   val colorAdditional = addDayVM.colorAdditional.observeAsState(listOf<Color>()).value
   val appSettings = dataStore.data.collectAsState(initial = AppSettings()).value
   val scope = rememberCoroutineScope()


   LaunchedEffect(Unit){
      val params = Bundle()
      params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MyArt");
      params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Simple add days");
      Firebase.analytics.logEvent("MovementScreen", params)
   }
   //выполняется один раз при запуске
//   LaunchedEffect(key1 = Unit, block = {
//      LoadDay(composableScope,IDday,workedMinEntered,commentEntered,colorEntered, addDayVM,selectedDate)
//
//   } )

   //демонстрационная строка
   Column(modifier = Modifier.fillMaxWidth(1f)){
      LaunchedEffect(Unit){
         selectedDate.value = LocalDate.now()
         choosing.value = true
         LoadDay(composableScope, IDday, workedMinEntered, commentEntered, colorEntered, addDayVM, selectedDate)    //загружаем день
      }
      SimpleViewCellDay(mainMenuVM, selectedDate, workedMinEntered, commentEntered, colorEntered, addDayVM )
      Row(modifier = Modifier
         .fillMaxWidth(1f)
         .padding(top = 10.dp, bottom = 5.dp, start = 0.dp, end = 0.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
         Column(horizontalAlignment = Alignment.CenterHorizontally) {
         IconButton(onClick = {  addDayVM.SelectState(StateEditing.ChooseDate) }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
            content = { Icon(painterResource(id = R.drawable.ic_calendar_clock), contentDescription = "Дата", tint = if (stateEdition == StateEditing.ChooseDate) Color.Green else Color.Black) })
            Text(text = stringResource(id = R.string.date))
         }
         Column(horizontalAlignment = Alignment.CenterHorizontally) {
         IconButton(onClick = {  addDayVM.SelectState(StateEditing.ChooseWorked) }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
            content = {Icon(painterResource(id = R.drawable.ic_timeworking), contentDescription = "Время", tint = if (stateEdition == StateEditing.ChooseWorked) Color.Green else Color.Black)})
            Text(text = stringResource(id = R.string.time))
         }
         Column(horizontalAlignment = Alignment.CenterHorizontally) {
         IconButton(onClick = {  addDayVM.SelectState(StateEditing.ChooseComment) }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
            content = {Icon(painterResource(id = R.drawable.ic_comment), contentDescription = "Комментарий", tint = if (stateEdition == StateEditing.ChooseComment) Color.Green else Color.Black)})
            Text(text = stringResource(id = R.string.comment))
         }
         Column(horizontalAlignment = Alignment.CenterHorizontally) {
         IconButton(onClick = {  addDayVM.SelectState(StateEditing.ChooseColor) }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
            content = { Icon(painterResource(id = R.drawable.ic_paintbrush), contentDescription = "Кисть", tint = if (stateEdition == StateEditing.ChooseColor) Color.Green else Color.Black) })
            Text(text = stringResource(id = R.string.color))
         }
      }
      Divider(modifier = Modifier
         .fillMaxWidth(1f)
         .padding(top = 6.dp, bottom = 6.dp, start = 0.dp, end = 0.dp), color = Color.DarkGray)
   }

   AnimatedVisibility(
      visible = stateEdition == StateEditing.None,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
   ) {NotChoosing()}
   AnimatedVisibility(
      visible = stateEdition == StateEditing.ChooseDate,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
   ) {Column(modifier = Modifier.fillMaxWidth(1f)) {ChooseDate(selectedDate, choosing,listWorkedDay,workedMinEntered, IDday, commentEntered, colorEntered, addDayVM, mainMenuVM)}}
   AnimatedVisibility(
      visible = stateEdition == StateEditing.ChooseWorked,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
   ) {if (!choosing.value) NotChoosing() else Column(modifier = Modifier.fillMaxWidth(1f)) {ChooseWorked(workedMinEntered, addDayVM)}}
   AnimatedVisibility(
      visible = stateEdition == StateEditing.ChooseComment,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
   ) {if (!choosing.value) NotChoosing() else Column(modifier = Modifier.fillMaxWidth(1f)) {
      ChooseComment(commentEntered, addDayVM, selectedDate, workedMinEntered, colorEntered, IDday, appSettings)}}
   AnimatedVisibility(
      visible = stateEdition == StateEditing.ChooseColor,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
   ) { Column(modifier = Modifier.fillMaxWidth(1f)) {
      addDayVM.LastColors()
      if (!choosing.value) NotChoosing() else ChooseColor(colorEntered, dialogPicker, commentEntered)}
   }
   AnimatedVisibility(
      visible = stateEdition == StateEditing.SuccessSave,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
   ) {SuccesSave(composableScope,IDday,workedMinEntered,commentEntered,colorEntered, addDayVM,selectedDate)}

   when (stateEdition){
      is StateEditing.Error -> { ErrorState(stateEdition.error) }
      else -> {}
   }



   if (choosing.value) {
      //кнопка
      Row(
         modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 18.dp, bottom = 4.dp, end = 15.dp, start = 15.dp),
         verticalAlignment = Alignment.Top,
         horizontalArrangement = Arrangement.SpaceBetween
      ) {
         val textButton = remember { mutableStateOf("Далее") }
         val enabledButton = remember { mutableStateOf(true) }
         when (stateEdition) {
            is StateEditing.None -> {
               textButton.value = stringResource(id = R.string.start_AddDay); enabledButton.value = true
            }
            is StateEditing.ChooseDate -> {
               textButton.value = stringResource(id = R.string.next_AddDay); enabledButton.value = true
            }
            is StateEditing.ChooseWorked -> {
               textButton.value =
                  if (IDday.value != 0) stringResource(id = R.string.changedEntry) else stringResource(id = R.string.save); enabledButton.value = true
            }
            is StateEditing.ChooseComment -> {
               textButton.value =
                  if (IDday.value != 0) stringResource(id = R.string.changedEntry) else stringResource(id = R.string.save); enabledButton.value = true
            }
            is StateEditing.ChooseColor -> {
               textButton.value =
                  if (IDday.value != 0) stringResource(id = R.string.changedEntry) else stringResource(id = R.string.save); enabledButton.value = true
            }  //сохранить, редактировать
            is StateEditing.SuccessSave -> {
               textButton.value = stringResource(id = R.string.success_AddDay); enabledButton.value = false
            }
            is StateEditing.Error -> {
               textButton.value = stringResource(id = R.string.error_AddDay); enabledButton.value = false
            }
         }
         Column() {     //чек бокс показывать подсказки
            AnimatedVisibility(
               visible = (stateEdition == StateEditing.ChooseWorked),
               enter = expandVertically() + fadeIn(),
               exit = shrinkVertically() + fadeOut()
            ) {
               Row(modifier = Modifier.padding(0.dp),
                  verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Start) {
                  Checkbox(checked = appSettings.hintHours, modifier = Modifier.padding(0.dp),
                     onCheckedChange = {
                        scope.launch {
                           dataStore.updateData {
                              it.copy(hintHours = !appSettings.hintHours)
                           }
                        }
                     })
                  IconButton(modifier = Modifier.testTag("HintComment"),
                     onClick = { scope.launch {
                        dataStore.updateData {
                           it.copy(hintHours = !appSettings.hintHours)
                        }
                     } }) {
                     Image(
                        imageVector = Icons.Outlined.Info,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                        contentDescription = "Previous",
                     )
                  }
               }
            }

            AnimatedVisibility(
               visible = (stateEdition == StateEditing.ChooseComment),
               enter = expandVertically() + fadeIn(),
               exit = shrinkVertically() + fadeOut()
            ) {
               Row(modifier = Modifier.padding(0.dp),
                  verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Start) {
                  Checkbox(checked = appSettings.hintComment, modifier = Modifier.padding(0.dp),
                          onCheckedChange = {
                              scope.launch {
                                 dataStore.updateData {
                                 it.copy(hintComment = !appSettings.hintComment)
                                 }
                              }
                          })
                  IconButton(modifier = Modifier.testTag("HintComment"),
                     onClick = { scope.launch {
                     dataStore.updateData {
                        it.copy(hintComment = !appSettings.hintComment)
                     }
               } }) {
                  Image(
                     imageVector = Icons.Outlined.Info,
                     colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                     contentDescription = "Previous",
                  )
               }
               }
            }
         }
         //сама кнопка
          Button(enabled = enabledButton.value, elevation = ButtonDefaults.elevation(4.dp), border = BorderStroke(1.dp, Color(0xFF85BFF8)),
             colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEBEBEB)),
               modifier = Modifier.wrapContentSize(),
               onClick = {
                  if (commentEntered.value == "#empty#") {
                     colorEntered.value = "#FFFFFFFF"
                  }
                  enabledButton.value = false
                  when (stateEdition) {
                     is StateEditing.None -> {
                        addDayVM.SelectState(StateEditing.ChooseDate)
                     }
                     is StateEditing.ChooseDate -> {
                        addDayVM.SelectState(StateEditing.ChooseWorked)
                     }
                     is StateEditing.ChooseWorked -> {
                        if (IDday.value == 0) {
                           addDayVM.SaveDay(
                              WorkDay(
                                 0,
                                 selectedDate.value.year,
                                 selectedDate.value.monthValue,
                                 selectedDate.value.dayOfMonth,
                                 workedMinEntered.value,
                                 if (commentEntered.value != "#empty#") commentEntered.value.trim() else "",
                                 colorEntered.value.toString()
                              )
                           )
                        } else {
                           addDayVM.ReplaceDay(
                              WorkDay(
                                 IDday.value,
                                 selectedDate.value.year,
                                 selectedDate.value.monthValue,
                                 selectedDate.value.dayOfMonth,
                                 workedMinEntered.value,
                                 commentEntered.value.trim(),
                                 colorEntered.value.toString()
                              )
                           )
                        }
                     }

                     is StateEditing.ChooseComment -> {
                        if (IDday.value == 0) {
                           addDayVM.SaveDay(
                              WorkDay(
                                 0,
                                 selectedDate.value.year,
                                 selectedDate.value.monthValue,
                                 selectedDate.value.dayOfMonth,
                                 workedMinEntered.value,
                                 commentEntered.value.trim(),
                                 colorEntered.value.toString()
                              )
                           )
                        } else {
                           addDayVM.ReplaceDay(
                              WorkDay(
                                 IDday.value,
                                 selectedDate.value.year,
                                 selectedDate.value.monthValue,
                                 selectedDate.value.dayOfMonth,
                                 workedMinEntered.value,
                                 commentEntered.value.trim(),
                                 colorEntered.value
                              )
                           )
                        }
                     }

                     is StateEditing.ChooseColor -> {
                        if (IDday.value == 0) {
                           addDayVM.SaveDay(
                              WorkDay(
                                 0,
                                 selectedDate.value.year,
                                 selectedDate.value.monthValue,
                                 selectedDate.value.dayOfMonth,
                                 workedMinEntered.value,
                                 commentEntered.value.trim(),
                                 colorEntered.value.toString()
                              )
                           )

                        } else {
                           addDayVM.ReplaceDay(
                              WorkDay(
                                 IDday.value,
                                 selectedDate.value.year,
                                 selectedDate.value.monthValue,
                                 selectedDate.value.dayOfMonth,
                                 workedMinEntered.value,
                                 commentEntered.value,
                                 colorEntered.value
                              )
                           )
                        }
                     }

                     is StateEditing.SuccessSave -> {}
                     is StateEditing.Error -> {}
                  }
               }) {
               Text(text = textButton.value, maxLines = 1)                //Начать, Далее, Заполнить, Редактировать
            }
         }
      }


   //список отработанного времени
   AnimatedVisibility(
      visible = (stateEdition == StateEditing.ChooseWorked) and appSettings.hintHours,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
   ) {
      val listWorked = remember { mutableListOf<Int>() }
      //список
      Column(modifier = Modifier
         .padding(top = 0.dp, bottom = 4.dp, end = 15.dp, start = 15.dp)) {
         listWorked.forEach { n ->
            ItemlistWorked(worked = n){
               workedMinEntered.value = it
            }
         }
      }
      LaunchedEffect(Unit){
         listWorked.addAll(addDayVM.LastWorkeds())
      }
   }

   //список комментариев
   AnimatedVisibility(
      visible = (stateEdition == StateEditing.ChooseComment) and appSettings.hintComment,
      enter = expandVertically() + fadeIn(),
      exit = shrinkVertically() + fadeOut()
   ) {
      val listComents = remember { mutableListOf<String>() }
      //список комментариев
      Column(modifier = Modifier
         .padding(top = 0.dp, bottom = 4.dp, end = 15.dp, start = 15.dp)) {
         listComents.forEach {str->
            ItemlistComments(str = str){
               commentEntered.value = it.toString()
            }
         }
      }

      LaunchedEffect(Unit){
         listComents.addAll(addDayVM.LastComments())
      }
   }

   MaterialDialog(dialogState = dialogPicker) {
      colorChooser(colors = (MyColors.ColorsPicker + colorAdditional).distinct(),
         argbPickerState = ARGBPickerState.WithoutAlphaSelector, waitForPositiveButton = false,
         initialSelection = (MyColors.ColorsPicker + colorAdditional).distinct().indexOfFirst { if (commentEntered.value != "#empty#") it == Color(colorEntered.value.toColorInt()) else it == Color.White },          //удаление лишних цветов в спике и поиск и выбор элемента в списке
         onColorSelected = {
            colorEntered.value = "#${Integer.toHexString(it.toArgb())}"
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

@Composable
fun ItemlistWorked(worked: Int, onClicked: (worked: Int) -> Unit){
   Card(modifier = Modifier
      .wrapContentSize(Alignment.TopCenter)
      .padding(top = 0.dp, bottom = 3.dp, end = 5.dp, start = 5.dp)
      // .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
      .defaultMinSize(minWidth = 60.dp)
      .clickable { onClicked(worked) },
      elevation = 4.dp,
      backgroundColor = Color(0xFFFCFCFC),
      border = BorderStroke(1.dp, Color(0xFF85BFF8))) {
      Text(text =  "${minuteToHours(worked).hours}:${(String.format("%02d", minuteToHours(worked).minutes))}",        //отработано в часах и минуте
         maxLines = 1, style = TextStyle(color = Color.Black, fontSize = 17.sp, textAlign = TextAlign.Center),
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(5.dp)
      )
   }

}


@Composable
fun ItemlistComments(str: String, onClicked: (str: String) -> Unit){
   Card(modifier = Modifier
      .wrapContentSize(Alignment.TopCenter)
      .padding(top = 0.dp, bottom = 5.dp, end = 15.dp, start = 15.dp)
      // .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
      .defaultMinSize(minWidth = 100.dp)
      .clickable { onClicked(str) },
      elevation = 4.dp,
      backgroundColor = Color(0xFFFCFCFC),
      border = BorderStroke(1.dp, Color(0xFF85BFF8))) {
      Text(text = str,
         maxLines = 2, style = TextStyle(color = Color.Black, fontSize = 15.sp, textAlign = TextAlign.Center),
         overflow = TextOverflow.Ellipsis,
         modifier = Modifier.padding(5.dp)
      )
   }

}

@Composable
fun NotChoosing(){
   Column(modifier = Modifier.fillMaxWidth(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
      Text(text = stringResource(id = R.string.Choose_AddDay), fontSize = 18.sp, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp))
   }
}

@Composable
fun ChooseDate(
   selectedDate: MutableState<LocalDate>,
   choosing: MutableState<Boolean>,
   listWorkedDay: List<WorkDay>?,
   workedMinEntered: MutableState<Int>,
   IDday: MutableState<Int>,
   commentEntered: MutableState<String>,
   colorEntered: MutableState<String>,
   addDayVM: AddDayVM,
   mainMenuVM: MainMenuViewModel
) {
//календарь
   Column(Modifier.fillMaxWidth(1f),horizontalAlignment = Alignment.CenterHorizontally) {
      Column(modifier = Modifier.fillMaxWidth(0.8f)){
         /// SimpleViewCalendar()
         SelectableCalendar(calendarState = rememberSelectableCalendarState(
            initialMonth = YearMonth.now(),
            initialSelectionMode = SelectionMode.Single
            ),
            modifier = Modifier.animateContentSize(),
            showAdjacentMonths = true,
            firstDayOfWeek = DayOfWeek.MONDAY,
            today = LocalDate.now(),
            monthContainer = { SimpleViewMonthContainer(it) },
            dayContent = { SimpleViewDayContent(dayState = it, selectedDate, choosing, listWorkedDay, workedMinEntered, IDday, commentEntered, colorEntered, addDayVM, mainMenuVM) },
            daysOfWeekHeader = { SimpleViewWeekHeader(daysOfWeek = it) },
            monthHeader = { SimpleViewMonthHeader(monthState = it) },
         )
      }
   }
}

@Composable
fun ChooseWorked(workedMinEntered: MutableState<Int>, addDayVM: AddDayVM) {
//   val SliderHoursValue = remember { mutableStateOf(minuteToHours(workedMinEntered.value).hours)}
//   val SliderMinutesValue = remember { mutableStateOf(minuteToHours(workedMinEntered.value).minutes)}
//   val showSliderHours = remember { mutableStateOf(false)}
//   val showSliderMinutes = remember { mutableStateOf(false)}
   val focusRequester = remember { FocusRequester() }
      val hourstext = remember { mutableStateOf("") }
      val minutestext = remember { mutableStateOf("") }
//   LaunchedEffect(SliderHoursValue.value,SliderMinutesValue.value){
//      workedMinEntered.value = SliderHoursValue.value * 60 + SliderMinutesValue.value
//   }

   Text(text = stringResource(id = R.string.hoursPerDay_packAdd), fontSize = 18.sp,
      modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp) )              //Количество часов в день:
   Row(horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .padding(start = 15.dp, top = 2.dp, end = 15.dp)
         .fillMaxWidth()
   ) {
      OutlinedTextField(      //часы
         //value = if  (workedMinEntered.value == 0) "" else minuteToHours(workedMinEntered.value).hours.toString(),
         value = if  (hourstext.value.isEmpty() and (minuteToHours(workedMinEntered.value).hours == 0)) "" else minuteToHours(workedMinEntered.value).hours.toString(),
         onValueChange = {hourstext.value = it
                        if (it == "000") hourstext.value = "00"
                        if (it != "") {
                           try {
                              workedMinEntered.value = it.toInt()*60 + minuteToHours(workedMinEntered.value).minutes

                              if (minuteToHours(workedMinEntered.value).hours > 48) workedMinEntered.value = 48*60 + minuteToHours(workedMinEntered.value).minutes
                              if (minuteToHours(workedMinEntered.value).hours < -24) workedMinEntered.value = -24*60 + minuteToHours(workedMinEntered.value).minutes
                           }
                           catch (e:Exception){workedMinEntered.value = 0 + minuteToHours(workedMinEntered.value).minutes}
                              } else { workedMinEntered.value = 0 + minuteToHours(workedMinEntered.value).minutes }
                           if (it.length >= 2) focusRequester.requestFocus()

                         },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
         keyboardActions = KeyboardActions( onNext = {focusRequester.requestFocus()}),
         modifier = Modifier.width(60.dp) ,
         singleLine = true,
         placeholder = { Text(text = "0") },
         textStyle = TextStyle(fontSize = 16.sp),
         colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
      )
      Text(text = stringResource(id = R.string.hours),
//          modifier = Modifier.clickable {          // - часов,
//         showSliderHours.value = !showSliderHours.value
//         showSliderMinutes.value = false
//    }
         )
      OutlinedTextField(      //минуты
         value = if  ((minuteToHours(workedMinEntered.value).minutes == 0) and (minutestext.value.isEmpty())) ""
            else  if ((minuteToHours(workedMinEntered.value).minutes == 0) and (minutestext.value.length > 0 )) { minutestext.value }
            else  minuteToHours(workedMinEntered.value).minutes.toString()
         ,
         onValueChange = {
                           if (it == "000") minutestext.value = "00" else minutestext.value = it
                           if (it != "") {
                                  try {
                                     if (it.toInt() in -59..59) {
                                        workedMinEntered.value = minuteToHours(workedMinEntered.value).hours*60 + it.toInt()
                                     } else {
                                        if (it.toInt() > 59) workedMinEntered.value = minuteToHours(workedMinEntered.value).hours*60 + 59
                                        if (it.toInt() < -59) workedMinEntered.value = minuteToHours(workedMinEntered.value).hours*60 - 59
                                     }
                                  }
                                   catch (e: Exception){ workedMinEntered.value = 0 + minuteToHours(workedMinEntered.value).hours*60  }
                              } else workedMinEntered.value = 0 + minuteToHours(workedMinEntered.value).hours*60
                         },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,imeAction = ImeAction.Next ),
         keyboardActions = KeyboardActions( onNext = {
            addDayVM.SelectState(StateEditing.ChooseComment)}),
         modifier = Modifier
            .width(60.dp)
            .focusRequester(focusRequester),
         singleLine = true,
         placeholder = { Text(text = "00") },
         textStyle = TextStyle(fontSize = 16.sp),
         colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
      )
      Text(text = stringResource(id = R.string.minute),
//         modifier = Modifier.clickable {                 // - минут
//         showSliderHours.value = false
//         showSliderMinutes.value = !showSliderMinutes.value
//      }
      )
   }

}

@Composable
fun ChooseComment(
   commentEntered: MutableState<String>, addDayVM: AddDayVM,
   selectedDate: MutableState<LocalDate>, workedMinEntered: MutableState<Int>,
   colorEntered: MutableState<String>, IDday: MutableState<Int>, appSettings: AppSettings
) {
   val focusText = remember { FocusRequester() }

   Column() {

      Text(text = stringResource(id = R.string.description), fontSize = 18.sp,
         modifier = Modifier.padding(start = 20.dp, end = 15.dp, top = 15.dp) )
      OutlinedTextField(
         value = if (commentEntered.value.toString() != "#empty#") commentEntered.value else "",
         onValueChange = { commentEntered.value = it },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Send),
         keyboardActions = KeyboardActions ( onSend = {
            if (IDday.value == 0) {
               addDayVM.SaveDay( WorkDay( 0, selectedDate.value.year, selectedDate.value.monthValue, selectedDate.value.dayOfMonth,
                  workedMinEntered.value, commentEntered.value.toString().trim(),colorEntered.value.toString() ) )
            } else {
               addDayVM.ReplaceDay( WorkDay( IDday.value, selectedDate.value.year, selectedDate.value.monthValue, selectedDate.value.dayOfMonth,
                  workedMinEntered.value,commentEntered.value.trim(), colorEntered.value ) )
            }
         } ),
         singleLine = false,
         //maxLines = 3,
         placeholder = { Text(text = stringResource(id = R.string.ifComment)) },
         textStyle = TextStyle(fontSize = 16.sp),
         modifier = Modifier
            .fillMaxWidth()
            //.height(120.dp)
            .focusable(enabled = true)
            .focusRequester(focusText)
            .padding(horizontal = 20.dp, vertical = 10.dp),
         colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.Black)
      )

      if (!appSettings.hintComment) {           //если включены подсказки, клавиатуру автоматически не показывать
         LaunchedEffect(Unit) {
            delay(500)
            focusText.requestFocus()
         }
      }
   }
}

@Composable
fun ChooseColor(colorEntered: MutableState<String>, dialogPicker: MaterialDialogState, commentEntered: MutableState<String>) {
//цвета
   val sizeColorBloks = 35.dp
   val sizeHeightColorBloks =  remember { mutableStateOf(45.dp) }
   Text(text = stringResource(id = R.string.SelectColor), fontSize = 18.sp, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 15.dp))
   Row(horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .fillMaxWidth()
         .height(75.dp)
         .horizontalScroll(rememberScrollState())
         .padding(vertical = 5.dp, horizontal = 20.dp)) {
      colorsCell(colorCell = "#FFFFFFFF", colorChosser = colorEntered, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, commentEntered)
      colorsCell(colorCell = "#FFA7FFFF", colorChosser = colorEntered, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, commentEntered)
      colorsCell(colorCell = "#FFFFA199", colorChosser = colorEntered, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, commentEntered)
      colorsCell(colorCell = "#FFD2FAA4", colorChosser = colorEntered, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, commentEntered)
      colorsCell(colorCell = "#FFB7C2FF", colorChosser = colorEntered, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, commentEntered)
      colorsCell(colorCell = "#FFF7FF9E", colorChosser = colorEntered, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, commentEntered)
      colorsCell(colorCell = "#FFF3ADFF", colorChosser = colorEntered, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, commentEntered)
      colorsCell(colorCell = "#FFFFCE94", colorChosser = colorEntered, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, commentEntered)

      Card(modifier = Modifier
         .width(sizeColorBloks)
         .height(sizeHeightColorBloks.value - 20.dp)
         .padding(3.dp), elevation = 2.dp,
         border = BorderStroke(1.dp,Color.Gray)) {
         Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .clickable {
               if (commentEntered.value == "#empty#") commentEntered.value = ""
               dialogPicker.show()
            },
            contentAlignment = Alignment.Center ) {
            Text(text = "?")
         }
      }
   }
   Text(text = stringResource(id = R.string.chosePallete_string), fontSize = 13.sp,
      modifier = Modifier
         .padding(vertical = 10.dp, horizontal = 20.dp)
         .fillMaxWidth(1f),
      textAlign = TextAlign.Center)
}

@Composable
fun SuccesSave(
   composableScope: CoroutineScope,
   IDday: MutableState<Int>,
   workedMinEntered: MutableState<Int>,
   commentEntered: MutableState<String>,
   colorEntered: MutableState<String>,
   addDayVM: AddDayVM,
   selectedDate: MutableState<LocalDate>
) {
   Column(modifier = Modifier.fillMaxWidth(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
      Text(text = "Успешно сохранено", fontSize = 18.sp, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp))
   }
   LoadDay(composableScope, IDday, workedMinEntered, commentEntered, colorEntered, addDayVM, selectedDate)    //загружаем день

}

@Composable
fun ErrorState(error: String) {
   Column(modifier = Modifier.fillMaxWidth(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
      Text(text = "Ошибка $error", fontSize = 18.sp, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 10.dp))
   }
}

   //демонстрационный день
@Composable
fun SimpleViewCellDay(
      mainMenuVM: MainMenuViewModel,
      selectedDate: MutableState<LocalDate>,
      workedMinEntered: MutableState<Int>,
      commentEntered: MutableState<String>,
      colorEntered: MutableState<String>,
      addDayVM: AddDayVM
   ) {
   val date = selectedDate.value
   val dayofweek = weekdayToWD(date.dayOfWeek, LocalContext.current)                            //день недели
   val colorDayOfWeek = if (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) Color(0xffE10707) else Color.Black               //сделать цвет от дня недели. Выходной красный
   val colorData = if (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) Color(0xff810707) else Color.Black               //цвет числа даты в зависимости от выходного дня
   val worked = "${minuteToHours(workedMinEntered.value).hours}:${(String.format("%02d", minuteToHours(workedMinEntered.value).minutes))}"                                        //отработано в часах и минуте
   val colorBackgrCell = if (commentEntered.value != "#empty#") colorEntered.value else "#FFFFFFFF"     //workday.color                  //цвет ячейки дня

   val correctFont = mainMenuVM.correctFont.observeAsState().value?.plus(0) ?: 0
   val correctPadding = mainMenuVM.correctPadding.observeAsState().value?.plus(0) ?: 0
   val interactionSource = remember { MutableInteractionSource() }

   //Вся карточка
   Card(
      modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 0.dp, bottom = 0.dp),border = BorderStroke(1.dp, Color.Gray), elevation = 0.dp,
      backgroundColor = try {
         Color(colorBackgrCell.toColorInt())
      }catch (e:Exception){
         Color.White
      }
   ) {
      Column(
         modifier = Modifier
            .fillMaxWidth(1f)
            .wrapContentHeight(Alignment.CenterVertically)
            .height(65.dp)
      ) {

         Row(
            modifier = Modifier
               .wrapContentHeight(Alignment.CenterVertically)
               .height(63.dp)

         ) {
            Card(       //карточка числа
               modifier = Modifier
                  .padding(top = 4.dp, bottom = 2.dp, start = 4.dp, end = 1.dp)
                  .width(55.dp),
               backgroundColor = Color.White,
               shape = RoundedCornerShape(6.dp),
               elevation = 2.dp
            ) {
               //дата, число и день недели
               Box(modifier = Modifier
                  .wrapContentHeight(Alignment.CenterVertically)
                  .clickable(interactionSource = interactionSource, indication = null) {
                     addDayVM.SelectState(StateEditing.ChooseDate)
                  },
                  contentAlignment = Alignment.Center) {
                  Text( //число
                     text = date.dayOfMonth.toString(),
                     fontSize = (30 + correctFont).sp,
                     modifier = Modifier.padding(1.dp),
                     textAlign = TextAlign.Center,
                     fontWeight = FontWeight.Bold,
                     style = TextStyle(color = colorData, shadow = Shadow(Color(0xFF929292), Offset(3f, 3f), 2f))
                  )
               }
               Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.BottomCenter) {
                  Text( //день недели
                     text = dayofweek, color = colorDayOfWeek,
                     fontSize = (15).sp, modifier = Modifier.padding(0.dp), textAlign = TextAlign.Center,
                  )
               }
            }

            Box(
               modifier = Modifier
                  .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
                  .fillMaxHeight(1f)
                  .width(65.dp)
                  .clickable(interactionSource = interactionSource, indication = null) {
                     addDayVM.SelectState(StateEditing.ChooseWorked)
                  },
               contentAlignment = Alignment.Center
            ) {
               Text(       // отработанное время
                  text = worked.toString(),
                  fontSize = (21 + correctFont).sp,
                  modifier = Modifier.padding(0.dp),
                  textAlign = TextAlign.Center,
                  style = TextStyle(shadow = Shadow(Color(0xFF929292), Offset(3f, 3f), 2f))
               )
            }

            Divider(
               modifier = Modifier
                  .width(1.dp)
                  .fillMaxHeight(1f)
                  .padding(vertical = 1.dp), color = Color(0xBFA5A5A5), thickness = 1.dp
            )
            Box(
               modifier = Modifier
                  .fillMaxHeight(1f)
                  .fillMaxWidth(1f)
                  .padding(start = 8.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                  .clickable(
                     interactionSource = interactionSource,
                     indication = null
                  ) { addDayVM.SelectState(StateEditing.ChooseComment) },
               contentAlignment = Alignment.Center
            ) {
               Text(       //комменатрий
                  text = if (commentEntered.value != "#empty#") commentEntered.value else "",
                  maxLines = 3, fontSize = (14).sp, modifier = Modifier.padding(end = 25.dp)
               )

            }
         }
      }
   }
}


@Composable
private fun SimpleViewDayContent(
   dayState: DayState<DynamicSelectionState>,
   selectedDate: MutableState<LocalDate>,
   choosing: MutableState<Boolean>,
   listWorkedDay: List<WorkDay>?,
   workedMinEntered: MutableState<Int>,
   IDday: MutableState<Int>,
   commentEntered: MutableState<String>,
   colorEntered: MutableState<String>,
   addDayVM: AddDayVM,
   mainMenuVM: MainMenuViewModel

) {
   val context = LocalContext.current
   //в связи с прошедшим глюком в ранних версиях программы по запросу по одной дате может выйти не один день,
   //поэтому берем первый из пришедшего списка
   val composableScope = rememberCoroutineScope()
   val isWorkDay = remember { mutableStateOf(false)   }
   val colorCyrcle = remember {mutableStateOf(Color.White)}
   val selectionColor: Color = Color(0xFF091C97)
   val chooseDayColor: Color = Color.Green          //сегодняшнее число
   val weekendDay = dayState.date.dayOfWeek.value == 7 || dayState.date.dayOfWeek.value == 6
  // val isSelected = selectionState.isDateSelected(date)
   val correctFont = mainMenuVM.correctFont.observeAsState().value?.plus(0) ?: 0


   //из всех дней выделяем только один для ячейки date
   if (listWorkedDay != null) {
      if (listWorkedDay.isNotEmpty()) {       //закраска кружком
         val _listWork = listWorkedDay.filter {
            (it.day == dayState.date.dayOfMonth) && (it.month == dayState.date.monthValue) && (it.year == dayState.date.year)
         }
         val workday = if (_listWork.isNotEmpty()) _listWork[0] else {
            WorkDay(0,0,0,0,0,"#empty#","#FFFFFF")
         }
         if ((workday.day == dayState.date.dayOfMonth) && (workday.month == dayState.date.monthValue) && (workday.year == dayState.date.year)) {
            isWorkDay.value = true
            colorCyrcle.value = try { Color(workday.color.toColorInt()) } catch (e:Exception) { Color.Yellow }

         }else {isWorkDay.value = false}
      }else {isWorkDay.value = false}
   } else {isWorkDay.value = false}

   Card(
      modifier = Modifier
         .clickable {
            selectedDate.value = dayState.date
            choosing.value = true
            LoadDay(
               composableScope,
               IDday,
               workedMinEntered,
               commentEntered,
               colorEntered,
               addDayVM,
               selectedDate
            )    //загружаем день
         }
         .aspectRatio(1f)
         .padding(2.dp),
      elevation = if (dayState.isFromCurrentMonth) 3.dp else 0.dp,
      //border = if (dayState.isCurrentDay) BorderStroke(1.dp, currentDayColor) else null,
      border = if (selectedDate.value == dayState.date) BorderStroke(2.dp, selectionColor) else null,
      contentColor = contentColorFor( backgroundColor = MaterialTheme.colors.surface ),
      backgroundColor = if (weekendDay) { Color(0xFFFFF2F2)          //если выходной иначе простой день
         } else  {   Color(0xFFFFFFFF)

      }
   ){
      Box(modifier = Modifier,
         contentAlignment = Alignment.Center,
      ) {
         if (isWorkDay.value) {
            Canvas(modifier = Modifier.fillMaxSize(), onDraw = {  //если такой день есть, рисуем круг цветом дня
               drawCircle(color = Color.Black,
                  radius = size.maxDimension/3,
                  center = center,
                  style = Stroke(width = if (correctFont > 0) 3f else 11f)
               )
               drawCircle(color = colorCyrcle.value,
                  radius = size.maxDimension/3,
                  center = center,
                  style = Stroke(width = if (correctFont > 0) 2f else 8f)
               )
            })
         }
         Text(text = dayState.date.dayOfMonth.toString(),
            color = if (dayState.isCurrentDay) Color(0xFF1CCC09) else { if (dayState.isFromCurrentMonth) Color.Black else Color.LightGray },
         style = TextStyle(fontWeight = if (dayState.isCurrentDay) FontWeight.Bold else FontWeight.Normal, fontSize = 16.sp)
         )
      }
   }
}

@Composable
private fun SimpleViewWeekHeader(daysOfWeek: List<DayOfWeek>) {
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
private fun SimpleViewMonthHeader(monthState: MonthState) {
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
private fun SimpleViewMonthContainer(content: @Composable (PaddingValues) -> Unit) {
   Box(
      content = { content(PaddingValues()) },
   )
}

private fun LoadDay(
   composableScope: CoroutineScope,
   IDday: MutableState<Int>,
   workedMinEntered: MutableState<Int>,
   commentEntered: MutableState<String>,
   colorEntered: MutableState<String>,
   addDayVM: AddDayVM,
   selectedDate: MutableState<LocalDate>
) {
   composableScope.launch {
      val ready1: Deferred<WorkDay> = async(start = CoroutineStart.LAZY) {addDayVM.getDayFromDate(selectedDate.value)}

      if (ready1.await().comment != "#empty#") {
         workedMinEntered.value = ready1.getCompleted().worked
         IDday.value = ready1.getCompleted()._id
         commentEntered.value = ready1.getCompleted().comment
         colorEntered.value = ready1.getCompleted().color
      } else{
         IDday.value = 0
         workedMinEntered.value = 0
         commentEntered.value = "#empty#"
         colorEntered.value = "#FFFFFFFF"
      }
   }
}


@Composable
private fun colorsCell(colorCell: String, colorChosser: MutableState<String>, sizeColorBloks: Dp, sizeHeightColorBloks: MutableState<Dp>, commentEntered: MutableState<String>){
   Card(modifier = Modifier
      .width(sizeColorBloks)
      .height(if (colorChosser.value != colorCell) sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
      .padding(3.dp), elevation = 2.dp,
      border = if (colorChosser.value != colorCell) BorderStroke(1.dp,Color.Gray) else {
         BorderStroke(1.dp,Color.Red)
      }) {
      Box(modifier = Modifier
         .fillMaxSize()
         .background(Color(colorCell.toColorInt()))
         .clickable {
            if (commentEntered.value == "#empty#") commentEntered.value = ""
            colorChosser.value = colorCell
         }, contentAlignment = Alignment.Center ) {
         if (colorChosser.value == colorCell) Text(text = "✓")
      }
   }
}

private fun weekdayToWD(str: DayOfWeek, context: Context):String = context.resources.getStringArray(R.array.weeks_array)[str.value - 1].toString()
//Пересчет из минут в часы
private fun minuteToHours(minute: Int): WorkedTimeInHours {
   var hours = 0
   var _minute = minute
   while (_minute >= 60) {
      hours++
      _minute -= 60
   }
   return WorkedTimeInHours(hours, _minute)
}

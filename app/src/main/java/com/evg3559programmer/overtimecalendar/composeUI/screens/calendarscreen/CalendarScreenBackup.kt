//package com.evg3559programmer.overtimecalendar.composeUI.screens.calendarscreen
//
//import android.annotation.SuppressLint
//import android.content.Context
//import androidx.compose.animation.*
//import androidx.compose.foundation.*
//import androidx.compose.foundation.interaction.MutableInteractionSource
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.relocation.BringIntoViewRequester
//import androidx.compose.foundation.relocation.bringIntoViewRequester
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.KeyboardArrowDown
//import androidx.compose.material.icons.filled.KeyboardArrowLeft
//import androidx.compose.material.icons.filled.KeyboardArrowRight
//import androidx.compose.material.icons.filled.KeyboardArrowUp
//import androidx.compose.runtime.*
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.focus.onFocusEvent
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.ColorFilter
//import androidx.compose.ui.graphics.Shadow
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.toArgb
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.layout.onGloballyPositioned
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.testTag
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringArrayResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.unit.toSize
//import androidx.core.graphics.toColorInt
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavController
//import com.evg3559programmer.overtimecalendar.BuildConfig
//import com.evg3559programmer.overtimecalendar.R
//import com.evg3559programmer.overtimecalendar.composeUI.screens.mainmenu.MainMenuViewModel
//import com.evg3559programmer.overtimecalendar.composeUI.screens.settings.AlertDlgSet
//import com.evg3559programmer.overtimecalendar.di.*
//import com.vanpra.composematerialdialogs.MaterialDialog
//import com.vanpra.composematerialdialogs.color.ARGBPickerState
//import com.vanpra.composematerialdialogs.color.colorChooser
//import com.vanpra.composematerialdialogs.rememberMaterialDialogState
//import io.github.boguszpawlowski.composecalendar.SelectableCalendar
//import io.github.boguszpawlowski.composecalendar.day.DayState
//import io.github.boguszpawlowski.composecalendar.header.MonthState
//import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
//import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
//import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import java.time.DayOfWeek
//import java.time.LocalDate
//import java.time.YearMonth
//
//@Composable
//fun CalendarScreen (navController: NavController, mainMenuVM: MainMenuViewModel) {
//   val vm: CalendarVM = hiltViewModel()
//   val stateDialog = vm.stateDialog.collectAsState().value              //статус диалогового окна
//   val selectedDate: MutableState<LocalDate> = remember { mutableStateOf(LocalDate.now())}
//   val composableScope = rememberCoroutineScope()
//   val id = remember { mutableStateOf(0)   }
//   val listWorkedDay = vm.daysWork.observeAsState().value
//   val workedMinEntered =  remember { mutableStateOf(0)}            //значение введеное пользователем
//   val commentEntered =  remember { mutableStateOf("")}
//   val colorEntered =  remember { mutableStateOf("#empty#")}
//   val showButtons = remember { mutableStateOf(false)}              //показать кнопки редактирвания
//   val selectionMode = remember { mutableStateOf(SelectionMode.Single) }
//
//   Column(modifier = Modifier
//      .fillMaxWidth(1f)
//      .verticalScroll(enabled = true, state = rememberScrollState()),
//      horizontalAlignment = Alignment.CenterHorizontally) {
//      Column(modifier = Modifier.fillMaxWidth(0.85f)){
//         SelectableCalendar(calendarState = rememberSelectableCalendarState(
//            initialMonth = YearMonth.now(),
//            initialSelection = listOf(LocalDate.now()),
//            initialSelectionMode = selectionMode.value
//         ),
//            modifier = Modifier.animateContentSize(),
//            showAdjacentMonths = true,
//            firstDayOfWeek = DayOfWeek.MONDAY,
//            today = LocalDate.now(),
//            monthContainer = { CalendarViewMonthContainer(it) },
//            dayContent = { CalendarViewDayContent(dayState = it, id, selectedDate, listWorkedDay, workedMinEntered, commentEntered, colorEntered, showButtons, vm, composableScope) },
//            weekHeader = { CalendarViewWeekHeader(daysOfWeek = it) },
//            monthHeader = { CalendarViewMonthHeader(monthState = it) },
//         )
//      }
//      Divider(modifier = Modifier
//         .fillMaxWidth(1f)
//         .padding(top = 6.dp, bottom = 6.dp, start = 0.dp, end = 0.dp), color = Color.DarkGray)
//
//      if (BuildConfig.DEBUG) { SelectionMenu(selectionMode) }
//
//      when (selectionMode.value){
//         SelectionMode.Single -> {
//            CalendarViewCellDay(mainMenuVM, id, selectedDate, workedMinEntered, commentEntered, colorEntered, showButtons, vm)
//         }
//         SelectionMode.Multiple, SelectionMode.Period -> {}
//         else -> {
//            CalendarViewCellDay(mainMenuVM, id, selectedDate, workedMinEntered, commentEntered, colorEntered, showButtons, vm)
//         }
//      }
//
//   }
//
//   when (stateDialog){
//      is StateDialog.None -> {}
//      is StateDialog.ShowDelete -> {
//         AlertDlgSet(onDismiss ={ vm.SelectStateDialog(StateDialog.None) },
//            onAccept = {   vm.deleteDayFromID(stateDialog.IDday); vm.SelectStateDialog(StateDialog.None) },
//            title = "Подтвердите",
//            text = stateDialog.massage)
//      }
//      else -> {}
//   }
//}
//
//@Composable
//fun SelectionMenu(selectionMode: MutableState<SelectionMode>) {
//   val list = listOf ("Одиночный", "Мультивыбор", "Период", "Нет")
//   var expanded by remember { mutableStateOf(false) }
//   var selectedItem by remember { mutableStateOf("") }
//   var textFieldSize by remember { mutableStateOf(Size.Zero) }
//   val icon = if (expanded) {
//      Icons.Filled.KeyboardArrowUp
//   } else {
//      Icons.Filled.KeyboardArrowDown
//   }
//
//   Column(modifier = Modifier.padding(20.dp)) {
//      OutlinedTextField(
//         value = selectedItem,
//         onValueChange = {selectedItem = it},
//         readOnly = true,
//         modifier = Modifier.fillMaxWidth()
//         .onGloballyPositioned { layoutCoordinates -> textFieldSize = layoutCoordinates.size.toSize() },
//         label = {Text(text = list[1])},
//         trailingIcon = {
//            Icon (icon, "", Modifier.clickable { expanded = !expanded })
//         }
//      )
//      DropdownMenu(expanded = expanded,
//         onDismissRequest = { expanded = false }) {
//         list.forEach { label ->
//            DropdownMenuItem(onClick = {
//               selectedItem = label
//               when (label) {
//                  "Одиночный" -> {selectionMode.value = SelectionMode.Single}
//                  "Мультивыбор" -> {selectionMode.value = SelectionMode.Multiple}
//                  "Период" -> {selectionMode.value = SelectionMode.Period}
//                  "Нет" -> {selectionMode.value = SelectionMode.None}
//               }
//               expanded = false
//            }) {
//               Text (text = label)
//            }
//         }
//      }
//   }
//
//
//}
//
//
//@Composable
//fun CalendarViewCellDay(
//   mainMenuVM: MainMenuViewModel,
//   id: MutableState<Int>,
//   selectedDate: MutableState<LocalDate>,
//   workedMinEntered: MutableState<Int>,
//   commentEntered: MutableState<String>,
//   colorEntered: MutableState<String>,
//   showButtons: MutableState<Boolean>,
//   vm: CalendarVM
//) {
//   //val date = selectedDate.value
//   val stateEdition = remember { mutableStateOf<StateEditing>(StateEditing.None) }              //состояние редактирования
//   val dayofweek = weekdayToWD(selectedDate.value.dayOfWeek, LocalContext.current)                            //день недели
//   val colorDayOfWeek = if (selectedDate.value.dayOfWeek.value == 6 || selectedDate.value.dayOfWeek.value == 7) Color(0xffE10707) else Color.Black               //сделать цвет от дня недели. Выходной красный
//   val colorData = if (selectedDate.value.dayOfWeek.value == 6 || selectedDate.value.dayOfWeek.value == 7) Color(0xff810707) else Color.Black               //цвет числа даты в зависимости от выходного дня
//
//   val worked = "${minuteToHours(workedMinEntered.value).hours}:${(String.format("%02d", minuteToHours(workedMinEntered.value).minutes))}"                                        //отработано в часах и минуте
//   val colorBackgrCell = if (colorEntered.value != "#empty#") colorEntered.value else "#FFFFFF"     //workday.color                  //цвет ячейки дня
//
//   val correctFont = mainMenuVM.correctFont.observeAsState().value?.plus(0) ?: 0
//   val correctPadding = mainMenuVM.correctPadding.observeAsState().value?.plus(0) ?: 0
//   val interactionSource = remember { MutableInteractionSource() }
//   val contextResource = LocalContext.current.resources
//
//   //Вся карточка
//   Column(modifier = Modifier.fillMaxWidth(1f)) {
//      Card(
//         modifier = Modifier
//            .padding(start = 2.dp, end = 2.dp, top = 0.dp, bottom = 0.dp)
//            .alpha(if (colorEntered.value == "#empty#") 0.3f else 1f),
//         border = BorderStroke(1.dp, Color.Gray),
//         elevation = 0.dp,
//         backgroundColor = Color(colorBackgrCell.toColorInt())
//      ) {
//         Column(
//            modifier = Modifier
//               .fillMaxWidth(1f)
//               .wrapContentHeight(Alignment.CenterVertically)
//         ) {
//
//            Row(
//               modifier = Modifier
//                  .wrapContentHeight(Alignment.CenterVertically)
//                  .height(63.dp)
//
//            ) {
//               Card(       //карточка числа
//                  modifier = Modifier
//                     .padding(top = 4.dp, bottom = 2.dp, start = 4.dp, end = 1.dp)
//                     .width(55.dp),
//                  backgroundColor = Color.White,
//                  shape = RoundedCornerShape(6.dp),
//                  elevation = 2.dp
//               ) {
//                  //дата, число и день недели
//                  Box(modifier = Modifier
//                     .wrapContentHeight(Alignment.CenterVertically)
//                     .clickable(interactionSource = interactionSource, indication = null) {
//                        vm.SelectState(StateEditing.ChooseDate)
//                     }, contentAlignment = Alignment.Center
//                  ) {
//                     Text( //число
//                        text = selectedDate.value.dayOfMonth.toString(),
//                        fontSize = (30 + correctFont).sp,
//                        modifier = Modifier.padding(1.dp),
//                        textAlign = TextAlign.Center,
//                        fontWeight = FontWeight.Bold,
//                        style = TextStyle(color = colorData, shadow = Shadow(Color(0xFF929292), Offset(3f, 5f), 2f))
//                     )
//                  }
//                  Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.BottomCenter) {
//                     Text(
//                        //день недели
//                        text = dayofweek, color = colorDayOfWeek,
//                        fontSize = (15 + correctFont).sp, modifier = Modifier.padding(0.dp), textAlign = TextAlign.Center,
//                     )
//                  }
//               }
//
//               Box(
//                  modifier = Modifier
//                     .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
//                     .fillMaxHeight(1f)
//                     .width(65.dp)
//                     .clickable(interactionSource = interactionSource, indication = null) {
//                        vm.SelectState(StateEditing.ChooseWorked)
//                     }, contentAlignment = Alignment.Center
//               ) {
//                  Text(       // отработанное время
//                     text = worked.toString(),
//                     fontSize = (21 + correctFont).sp,
//                     modifier = Modifier.padding(0.dp),
//                     textAlign = TextAlign.Center,
//                     style = TextStyle(shadow = Shadow(Color(0xFF929292), Offset(3f, 5f), 1f))
//                  )
//               }
//
//               Divider(
//                  modifier = Modifier
//                     .width(1.dp)
//                     .fillMaxHeight(1f)
//                     .padding(vertical = 1.dp), color = Color(0xBFA5A5A5), thickness = 1.dp
//               )
//               Box(
//                  modifier = Modifier
//                     .fillMaxHeight(1f)
//                     .fillMaxWidth(1f)
//                     .padding(start = 8.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
//                     .clickable(interactionSource = interactionSource, indication = null) { vm.SelectState(StateEditing.ChooseComment) },
//                  contentAlignment = Alignment.Center
//               ) {
//                  Text(       //комменатрий
//                     text = commentEntered.value, maxLines = 3, fontSize = (14).sp, modifier = Modifier.padding(end = 25.dp)
//                  )
//                  Box(
//                     contentAlignment = Alignment.TopEnd,
//                     modifier = Modifier
//                        .fillMaxSize(1f)
//                        .padding(top = 2.dp, end = 2.dp, bottom = 2.dp, start = 2.dp)
//                  ) {
//                     Image(      //редактировать
//                        painterResource(id = R.drawable.ic_pencil),
//                        contentDescription = "Редактировать",
//                        contentScale = ContentScale.FillBounds,
//                        modifier = Modifier
//                           .width(20.dp)
//                           .height(21.dp)
//                           .clickable(interactionSource = interactionSource, indication = null) {
//                              showButtons.value = !showButtons.value
//                              stateEdition.value = StateEditing.None
//                           })
//                  }
//               }
//            }
//
//         }
//      }
//      if (!showButtons.value) {stateEdition.value = StateEditing.None}
//      if (colorEntered.value != "#empty#") {
//         AnimatedVisibility(
//            visible = showButtons.value, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()
//         ) {
//            Divider(
//               modifier = Modifier
//                  .fillMaxWidth(1f)
//                  .padding(top = 2.dp, bottom = 2.dp, start = 0.dp, end = 0.dp), color = Color.Gray
//            )
//            Row(
//               modifier = Modifier
//                  .fillMaxWidth(1f)
//                  .background(Color.White)
//                  .padding(top = 10.dp, bottom = 5.dp, start = 0.dp, end = 0.dp),
//               verticalAlignment = Alignment.CenterVertically,
//               horizontalArrangement = Arrangement.SpaceAround
//            ) {
//               IconButton(onClick = { if (stateEdition.value != StateEditing.ChooseWorked ) stateEdition.value = StateEditing.ChooseWorked else stateEdition.value = StateEditing.None},
//                  modifier = Modifier.size(20.dp),
//                  interactionSource = remember { MutableInteractionSource() },
//                  content = {
//                     Icon(
//                        painterResource(id = R.drawable.ic_timeworking),
//                        contentDescription = "Время работы",
//                        tint = if (stateEdition.value == StateEditing.ChooseWorked) Color.Green else Color.Black
//                     )
//                  })
//               IconButton(onClick = { if (stateEdition.value != StateEditing.ChooseComment ) stateEdition.value = StateEditing.ChooseComment  else stateEdition.value = StateEditing.None },
//                  modifier = Modifier.size(20.dp),
//                  interactionSource = remember { MutableInteractionSource() },
//                  content = {
//                     Icon(
//                        painterResource(id = R.drawable.ic_comment),
//                        contentDescription = "Комментарий",
//                        tint = if (stateEdition.value == StateEditing.ChooseComment) Color.Green else Color.Black
//                     )
//                  })
//               IconButton(onClick = {if (stateEdition.value != StateEditing.ChooseColor) stateEdition.value = StateEditing.ChooseColor  else stateEdition.value = StateEditing.None },
//                  modifier = Modifier.size(20.dp),
//                  interactionSource = remember { MutableInteractionSource() },
//                  content = {
//                     Icon(
//                        painterResource(id = R.drawable.ic_paintbrush),
//                        contentDescription = "Цвет",
//                        tint = if (stateEdition.value == StateEditing.ChooseColor) Color.Green else Color.Black
//                     )
//                  })
//               IconButton(onClick = {stateEdition.value = StateEditing.None
//                  vm.SelectStateDialog(
//                     StateDialog.ShowDelete(
//                        contextResource.getString(R.string.reallyDelete, "${selectedDate.value.dayOfMonth}.${selectedDate.value.monthValue}.${selectedDate.value.year}"), id.value )
//                  )
//               }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource() }, content = {
//                  Icon(
//                     painterResource(id = R.drawable.ic_recycle_icon),
//                     contentDescription = "Удалить",
//                     tint = if (stateEdition.value == StateEditing.ChooseDate) Color.Green else Color.Black
//                  )
//               })
//            }
//         }
//         AnimatedVisibility(
//            visible = stateEdition.value == StateEditing.ChooseWorked,
//            enter = expandVertically() + fadeIn(),
//            exit = shrinkVertically() + fadeOut()
//         ) {
//            ChooseWorked(id.value, workedMinEntered, vm)
//         }
//         AnimatedVisibility(
//            visible = stateEdition.value == StateEditing.ChooseComment,
//            enter = expandVertically() + fadeIn(),
//            exit = shrinkVertically() + fadeOut()
//         ) {
//            ChooseComment(id.value, commentEntered, vm)
//         }
//         AnimatedVisibility(
//            visible = stateEdition.value == StateEditing.ChooseColor,
//            enter = expandVertically() + fadeIn(),
//            exit = shrinkVertically() + fadeOut()
//         ) {
//            ChooseColor(id.value, colorEntered, vm)
//         }
//      } else {
//         showButtons.value = false
//         stateEdition.value = StateEditing.None
//      }
//   }
//}
//
//
//@SuppressLint("CoroutineCreationDuringComposition")
//@Composable
//private fun CalendarViewDayContent(
//   dayState: DayState<DynamicSelectionState>,
//   id: MutableState<Int>,
//   selectedDate: MutableState<LocalDate>,
//   listWorkedDay: List<WorkDay>?,
//   workedMinEntered: MutableState<Int>,
//   commentEntered: MutableState<String>,
//   colorEntered: MutableState<String>,
//   showButtons: MutableState<Boolean>,
//   vm: CalendarVM,
//   composableScope: CoroutineScope
//) {
//   val context = LocalContext.current
//
//   //в связи с прошедшим глюком в ранних версиях программы по запросу по одной дате может выйти не один день,
//   //поэтому берем первый из пришедшего списка
//
//   val isWorkDay = remember { mutableStateOf(false)   }
//   val colorCyrcle = remember {mutableStateOf(Color.White)}
//   val selectionColor: Color = Color(0xFF091C97)
//   val chooseDayColor: Color = Color.Green          //сегодняшнее число
//   val weekendDay = dayState.date.dayOfWeek.value == 7 || dayState.date.dayOfWeek.value == 6
//
//
//   //из всех дней выделяем только один для ячейки date
//   if (listWorkedDay != null) {
//      if (listWorkedDay.isNotEmpty()) {       //закраска кружком
//         val _listWork = listWorkedDay.filter {
//            (it.day == dayState.date.dayOfMonth) && (it.month == dayState.date.monthValue) && (it.year == dayState.date.year)
//         }
//         val workday = if (_listWork.isNotEmpty()) _listWork[0] else {
//            WorkDay(0,0,0,0,0,"","FFFFFF")
//         }
//         //заполняем демонстрационную строку
//         if (selectedDate.value == dayState.date){
//            composableScope.launch {
//               val getday = vm.getDayFromDate(selectedDate.value)
//               id.value = getday._id
//               workedMinEntered.value = getday.worked
//               commentEntered.value = getday.comment
//               colorEntered.value = getday.color
//               showButtons.value = false
//            }
//         }
//         if ((workday.day == dayState.date.dayOfMonth) && (workday.month == dayState.date.monthValue) && (workday.year == dayState.date.year)) {
//            isWorkDay.value = true
//            colorCyrcle.value = try { Color(workday.color.toColorInt()) } catch (e:Exception) { Color.Yellow }
//
//         }else {isWorkDay.value = false}
//      }else {isWorkDay.value = false}
//   } else {isWorkDay.value = false}
//
//
//   Card(
//      modifier = Modifier
//         .clickable {
//            selectedDate.value = dayState.date
//         }
//         .aspectRatio(1f)
//         .padding(2.dp),
//      elevation = if (dayState.isFromCurrentMonth) 3.dp else 0.dp,
//      border = if (selectedDate.value == dayState.date) BorderStroke(2.dp, selectionColor) else null,
//      contentColor = contentColorFor( backgroundColor = MaterialTheme.colors.surface ),
//      backgroundColor = if (weekendDay) { Color(0xFFFFF2F2)          //если выходной иначе простой день
//      } else  {   Color(0xFFFFFFFF)  }
//   ){
//      Box(modifier = Modifier,
//         contentAlignment = Alignment.Center,
//      ) {
//
//         if (isWorkDay.value) {
//            Canvas(modifier = Modifier.fillMaxSize(), onDraw = {  //если такой день есть, рисуем круг цветом дня
//               drawCircle(color = Color.Black,
//                  radius = size.maxDimension/3,
//                  center = center,
//                  style = Stroke(width = 11f)
//               )
//               drawCircle(color = colorCyrcle.value,
//                  radius = size.maxDimension/3,
//                  center = center,
//                  style = Stroke(width = 8f)
//               )
//            })
//         }
//         Text(text = dayState.date.dayOfMonth.toString(),
//            color = if (dayState.isCurrentDay) Color(0xFF1CCC09) else { if (dayState.isFromCurrentMonth) Color.Black else Color.LightGray },
//            style = TextStyle(fontWeight = if (dayState.isCurrentDay) FontWeight.Bold else FontWeight.Normal, fontSize = 16.sp)
//         )
//      }
//   }
//}
//
//@Composable
//private fun CalendarViewWeekHeader(daysOfWeek: List<DayOfWeek>) {
//   Row() {
//      daysOfWeek.forEach { dayOfWeek ->
//         Text(
//            color = if (dayOfWeek.value == 6 || dayOfWeek.value == 7) Color(0xFFA50000) else (Color.Black),
//            textAlign = TextAlign.Center,
//            text = stringArrayResource(id = R.array.material_calendar_weeks_array)[dayOfWeek.value-1],  // dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ROOT),
//            modifier = Modifier
//               .weight(1f)
//               .wrapContentHeight()
//         )
//      }
//   }
//}
//
//@Composable
//private fun CalendarViewMonthHeader(monthState: MonthState) {
//   Row(
//      modifier = Modifier.fillMaxWidth(),
//      horizontalArrangement = Arrangement.Center,
//      verticalAlignment = Alignment.CenterVertically
//   ) {
//      IconButton(modifier = Modifier.testTag("Decrement"), onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }) {
//         Image(
//            imageVector = Icons.Default.KeyboardArrowLeft,
//            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
//            contentDescription = "Previous",
//         )
//      }
//      Text(
//         modifier = Modifier.testTag("MonthLabel"),
//         text = stringArrayResource(id = R.array.material_calendar_months_array)[monthState.currentMonth.monthValue-1],  //monthState.currentMonth.month.name.lowercase().replaceFirstChar { it.titlecase() },
//         style = MaterialTheme.typography.h5
//      )
//      Spacer(modifier = Modifier.width(8.dp))
//      Text(text = monthState.currentMonth.year.toString(), style = MaterialTheme.typography.h5)
//      IconButton(modifier = Modifier.testTag("Increment"), onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }) {
//         Image(
//            imageVector = Icons.Default.KeyboardArrowRight,
//            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
//            contentDescription = "Next",
//         )
//      }
//   }
//}
//
//@Composable
//private fun CalendarViewMonthContainer(content: @Composable (PaddingValues) -> Unit) {
//   Box(
//      content = { content(PaddingValues()) },
//   )
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//private fun ChooseWorked(id: Int, worked: MutableState<Int>, vm: CalendarVM) {
//   val hours = remember { mutableStateOf(minuteToHours(worked.value).hours)}
//   val minutes = remember { mutableStateOf(minuteToHours(worked.value).minutes)}
//   val composableScope = rememberCoroutineScope()
//   val bringIntoViewRequester = remember { BringIntoViewRequester() }       // скролл к редактируемому элементу
//
//   val focusRequester = FocusRequester()
//   Column(modifier = Modifier
//      .fillMaxWidth(1f)
//      .background(Color.White)) {
//      Text(text = stringResource(id = R.string.hoursPerDay_packAdd), fontSize = 18.sp,
//         modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 1.dp, bottom = 10.dp) )              //Количество часов в день:
//      Row(horizontalArrangement = Arrangement.Start,
//         verticalAlignment = Alignment.CenterVertically,
//         modifier = Modifier
//            .padding(start = 15.dp, top = 2.dp, end = 15.dp)
//            .fillMaxWidth()
//      ) {
//         OutlinedTextField(      //часы
//            value = if  (hours.value == 0) "" else hours.value.toString(),
//            onValueChange = { if (it != "") {
//               try {
//                  hours.value = it.toInt()
//                  if (hours.value > 48) hours.value = 48
//                  if (hours.value < -12) hours.value = -12}
//               catch (e:Exception){minutes.value}
//            } else { hours.value = 0 }
//               if (it.length >= 2) focusRequester.requestFocus()
//            },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier
//               .width(60.dp)
//               .bringIntoViewRequester(bringIntoViewRequester)
//               .onFocusEvent {
//                  if (it.isFocused) {
//                     composableScope.launch {
//                        delay(300)
//                        bringIntoViewRequester.bringIntoView()
//                     }
//                  }
//               } ,
//            singleLine = true,
//            placeholder = { Text(text = "0") },
//            textStyle = TextStyle(fontSize = 16.sp),
//            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
//         )
//         Text(text = stringResource(id = R.string.hours), modifier = Modifier)          // - часов,
//         OutlinedTextField(      //минуты
//            value = if  (minutes.value == 0) "" else minutes.value.toString(),
//            onValueChange = { if (it != "") {
//               try { minutes.value = it.toInt()
//                  if (minutes.value > 480) minutes.value = 480
//                  if (minutes.value < -60) minutes.value = -60 }
//               catch (e: Exception){ minutes.value  }
//            } else minutes.value = 0
//
//            },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier
//               .width(60.dp)
//               .focusRequester(focusRequester)
//               .bringIntoViewRequester(bringIntoViewRequester)
//               .onFocusEvent {
//                  if (it.isFocused) {
//                     composableScope.launch {
//                        delay(300)
//                        bringIntoViewRequester.bringIntoView()
//                     }
//                  }
//               },
//            singleLine = true,
//            placeholder = { Text(text = "0") },
//            textStyle = TextStyle(fontSize = 16.sp),
//            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
//         )
//         Text(text = stringResource(id = R.string.minute), modifier = Modifier )               // - минут
//      }
//
//      Button(modifier = Modifier
//         .align(alignment = Alignment.End)
//         .padding(top = 10.dp, end = 20.dp),
//         enabled = (worked.value != hours.value * 60 + minutes.value),
//         colors = ButtonDefaults.buttonColors(Color.LightGray),
//         onClick = {
//            vm.saveWorked(id, hours.value * 60 + minutes.value)
//         }) {
//         Text(text = stringResource(id = R.string.save), maxLines = 1)
//      }
//   }
//}
//
//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//private fun ChooseComment(id: Int, comment: MutableState<String>, vm: CalendarVM){
//   val commentEntered = remember { mutableStateOf(comment.value)}
//   val composableScope = rememberCoroutineScope()
//   val bringIntoViewRequester = remember { BringIntoViewRequester() }       // скролл к редактируемому элементу
//
//   Column(modifier = Modifier.background(Color.White)) {
//      OutlinedTextField(
//         value = commentEntered.value.toString(),
//         onValueChange = { commentEntered.value = it },
//         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
//         singleLine = false,
//         maxLines = 3,
//         placeholder = { Text(text = stringResource(id = R.string.ifComment)) },
//         textStyle = TextStyle(fontSize = 16.sp),
//         modifier = Modifier
//            .fillMaxWidth()
//            .height(120.dp)
//            .padding(horizontal = 10.dp, vertical = 3.dp)
//            .bringIntoViewRequester(bringIntoViewRequester)
//            .onFocusEvent {
//               if (it.isFocused) {
//                  composableScope.launch {
//                     delay(300)
//                     bringIntoViewRequester.bringIntoView()
//                  }
//               }
//            },
//         colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.Black)
//      )
//      Button(modifier = Modifier
//         .align(alignment = Alignment.End)
//         .padding(top = 0.dp, end = 20.dp, bottom = 10.dp),
//         enabled = (comment.value != commentEntered.value),
//         colors = ButtonDefaults.buttonColors(Color.LightGray),
//         onClick = {
//            vm.saveComment(id, commentEntered.value)
//         }) {
//         Text(text = stringResource(id = R.string.save), maxLines = 1)
//      }
//   }
//}
//
//@Composable
//private fun ChooseColor(id: Int, color: MutableState<String>, vm: CalendarVM){
////цвета
//   //val colorEntered = remember { mutableStateOf(color) }
//   val dialogPicker = rememberMaterialDialogState()
//   val colorAdditional = vm.colorAdditional.observeAsState(listOf<Color>()).value
//   val sizeColorBloks = 35.dp
//   val sizeHeightColorBloks =  remember { mutableStateOf(45.dp) }
//   Column(modifier = Modifier.background(Color.White)) {
//      Text(text = stringResource(id = R.string.SelectColor), fontSize = 18.sp, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp))
//      Row(horizontalArrangement = Arrangement.SpaceEvenly,
//         verticalAlignment = Alignment.CenterVertically,
//         modifier = Modifier
//            .fillMaxWidth()
//            .height(75.dp)
//            .background(Color.White)
//            .horizontalScroll(rememberScrollState())
//            .padding(vertical = 5.dp, horizontal = 20.dp)) {
//         colorsCell(colorCell = "#FFFFFF", colorChosser = color.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm = vm, id = id)
//         colorsCell(colorCell = "#A7FFFF", colorChosser = color.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm = vm, id = id)
//         colorsCell(colorCell = "#FFA199", colorChosser = color.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm = vm, id = id)
//         colorsCell(colorCell = "#D2FAA4", colorChosser = color.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm = vm, id = id)
//         colorsCell(colorCell = "#B7C2FF", colorChosser = color.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm = vm, id = id)
//         colorsCell(colorCell = "#F7FF9E", colorChosser = color.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm = vm, id = id)
//         colorsCell(colorCell = "#F3ADFF", colorChosser = color.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm = vm, id = id)
//         colorsCell(colorCell = "#FFCE94", colorChosser = color.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm = vm, id = id)
//         Card(modifier = Modifier
//            .width(sizeColorBloks)
//            .height(sizeHeightColorBloks.value - 20.dp)
//            .padding(3.dp), elevation = 2.dp,
//            border = BorderStroke(1.dp,Color.Gray)) {
//            Box(modifier = Modifier
//               .fillMaxSize()
//               .background(Color.LightGray)
//               .clickable {
//                  dialogPicker.show()
//               },
//               contentAlignment = Alignment.Center ) {
//               Text(text = "?")
//            }
//         }
//      }
//
//      Text(text = stringResource(id = R.string.chosePallete_string), fontSize = 13.sp,
//         modifier = Modifier
//            .padding(vertical = 10.dp, horizontal = 20.dp)
//            .fillMaxWidth(1f),
//         textAlign = TextAlign.Center)
//      vm.LastColors()
//
//      MaterialDialog(dialogState = dialogPicker) {
//         val selectedColors = remember { mutableStateOf("#FFFFFFFF") }
//         val selected = remember { mutableStateOf(false) }
//
//         colorChooser(colors = (MyColors.ColorsPicker + listOf(Color(color.value.toColorInt())) + colorAdditional).distinct(),            //удаление лишних цветов в списке
//            argbPickerState = ARGBPickerState.WithoutAlphaSelector,
//            waitForPositiveButton = false,
//            initialSelection = (MyColors.ColorsPicker + listOf(Color(color.value.toColorInt())) + colorAdditional).distinct().indexOfFirst { it == Color(color.value.toColorInt()) },          //удаление лишних цветов в спике и поиск и выбор элемента в списке
//            onColorSelected = {
//               selectedColors.value = "#${Integer.toHexString(it.toArgb())}"
//               selected.value = true
//            })
//         Text(text = "OK", fontSize = 20.sp,
//            modifier = Modifier
//               .padding(vertical = 10.dp, horizontal = 20.dp)
//               .fillMaxWidth(1f)
//               .clickable {
//                  if (selected.value) vm.saveColor(id, selectedColors.value)
//                  dialogPicker.hide()
//               },
//            textAlign = TextAlign.End)
//
//      }
//   }
//}
//
//@Composable
//private fun colorsCell(
//   colorCell: String,
//   colorChosser: String,
//   sizeColorBloks: Dp,
//   sizeHeightColorBloks: MutableState<Dp>,
//   vm: CalendarVM,
//   id: Int
//){
//   Card(modifier = Modifier
//      .width(sizeColorBloks)
//      .height(if (colorChosser != colorCell) sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
//      .padding(3.dp), elevation = 2.dp,
//      border = if (colorChosser != colorCell) BorderStroke(1.dp,Color.Gray) else {
//         BorderStroke(1.dp,Color.Red)
//      }) {
//      Box(modifier = Modifier
//         .fillMaxSize()
//         .background(Color(colorCell.toColorInt()))
//         .clickable {
//            vm.saveColor(id, colorCell)
//         }, contentAlignment = Alignment.Center ) {
//         if (colorChosser == colorCell) Text(text = "✓")
//      }
//   }
//}
//
//
//private fun weekdayToWD(str: DayOfWeek, context: Context):String = context.resources.getStringArray(R.array.weeks_array)[str.value - 1].toString()
////Пересчет из минут в часы
//private fun minuteToHours(minute: Int): WorkedTimeInHours {
//   var hours = 0
//   var _minute = minute
//   while (_minute >= 60) {
//      hours++
//      _minute -= 60
//   }
//   return WorkedTimeInHours(hours, _minute)
//}

package com.evg3559programmer.overtimecalendar.composeUI.screens.listdays

import android.os.Bundle
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.datastore.core.DataStore
import androidx.navigation.NavController
//import com.evg3559programmer.overtimecalendar.BuildConfig
import com.evg3559programmer.overtimecalendar.DataStore.AppSettings
import com.evg3559programmer.overtimecalendar.R
import com.evg3559programmer.overtimecalendar.composeUI.Screens
import com.evg3559programmer.overtimecalendar.composeUI.SnackbarHostMyScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.addEdit.ItemlistComments
import com.evg3559programmer.overtimecalendar.composeUI.screens.addEdit.ItemlistWorked
import com.evg3559programmer.overtimecalendar.composeUI.screens.fragments.dateToMonthStr
import com.evg3559programmer.overtimecalendar.composeUI.screens.fragments.declensionOfWords
import com.evg3559programmer.overtimecalendar.composeUI.screens.fragments.minuteToHours
import com.evg3559programmer.overtimecalendar.composeUI.screens.fragments.weekdayToWD
import com.evg3559programmer.overtimecalendar.composeUI.screens.mainmenu.MainMenuViewModel
import com.evg3559programmer.overtimecalendar.di.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.color.ARGBPickerState
import com.vanpra.composematerialdialogs.color.colorChooser
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ListDaysScreen(navController: NavController, daysVM: ListDaysViewModelCompose, mainMenuVM: MainMenuViewModel, dataStore: DataStore<AppSettings>) {
   val context = LocalContext.current
   val contextResource = LocalContext.current.resources
   val MAX_PAGES: Int = 240   //240 месяцев будет доступно всего от текущей даты
   val stateDialog = daysVM.stateDialog.collectAsState().value              //статус диалогового окна
   val composableScope = rememberCoroutineScope()
   val snackbarHostState = SnackbarHostState()        //снэкбар
   val snackbartext = remember { mutableStateOf("") }
   val lookDate = daysVM.lookDate.observeAsState(LocalDate.now()).value               //текущий просматриваемый месяц
   val minutesWorked = daysVM.workedTime.observeAsState(0).value               //отработано минут в текущем месяце
   val shiftsWorked = daysVM.workedShifts.observeAsState(0).value               //отработано смен в текущем месяце
   val pagerState = rememberPagerState(initialPage = MAX_PAGES / 2, )
   daysVM.changeLookDate(pagerState.currentPage)
   val deleteList = remember { mutableStateListOf<WorkDay>() }                //список дней для удаления

   val _strHours = minuteToHours(minutesWorked).hours.toString() + " ${context.getString(R.string.hoursAbbr)} "
   val _strMinHours = if (minuteToHours(minutesWorked).minutes > 0) {
      minuteToHours(minutesWorked).minutes.toString() + " ${context.getString(R.string.minuteAbbr)}"
   } else {
      ""
   }
   LaunchedEffect(Unit){
      val params = Bundle()
      params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MyArt");
      params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "List of days");
      Firebase.analytics.logEvent("MovementScreen", params)
   }

   Column(
      modifier = Modifier
         .padding(top = 0.dp, bottom = 0.dp, start = 0.dp, end = 0.dp)
         .fillMaxSize(1f)
   ) {
      Row(modifier = Modifier.fillMaxWidth(1f)) {
         //карточки статистики
         CardsMenu(header = stringResource(id = R.string.TotalClock), value = _strHours + _strMinHours, modifier = Modifier.weight(1f))
         CardsMenu(header = stringResource(id = R.string.Month), value = dateToMonthStr(lookDate, context), modifier = Modifier
            .weight(1f)
            .clickable {
               composableScope.launch { pagerState.animateScrollToPage(120) }
            })
         CardsMenu(header = stringResource(id = R.string.TotalShifts), value = shiftsWorked.toString(), modifier = Modifier.weight(1f))

      }
      if (deleteList.size > 0) {
         val stateChoser = remember { mutableStateOf<StateEditing>(StateEditing.None) }                    //состояние редактирования
         //предупредить пользователя о необходимости сохранения данных перед переключением stateChoser
         val stateSavedContent = remember { mutableStateOf(true) }
         Row(modifier = Modifier
            .fillMaxWidth(1f)
            .background(Color.White)
            .padding(top = 10.dp, bottom = 5.dp, start = 0.dp, end = 0.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
               IconButton(modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()}, content = { Icon(painterResource(id = R.drawable.ic_removet), contentDescription = "Очистить", tint = Color(0xFFBB0101)) },
                  onClick = {stateChoser.value = StateEditing.None; deleteList.clear() })
               Text(text = stringResource(id = R.string.cancel2), overflow = TextOverflow.Visible)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
               IconButton(modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource() },
                  content = {Icon( painterResource(id = R.drawable.ic_timeworking), contentDescription = "Время работы", tint = if (stateChoser.value == StateEditing.ChooseWorked) Color.Green else Color.Black)},
                  onClick = { stateChoser.value = StateEditing.ChooseWorked })
               Text(text = stringResource(id = R.string.time), overflow = TextOverflow.Visible)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()}, content = { Icon(painterResource(id = R.drawable.ic_comment), contentDescription = "Комментарий", tint = if (stateChoser.value == StateEditing.ChooseComment) Color.Green else Color.Black) },
               onClick = { stateChoser.value = StateEditing.ChooseComment  })
               Text(text = stringResource(id = R.string.comment), overflow = TextOverflow.Visible)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()}, content = { Icon(painterResource(id = R.drawable.ic_paintbrush), contentDescription = "Цвет", tint = if (stateChoser.value == StateEditing.ChooseColor) Color.Green else Color.Black) },
               onClick = { stateChoser.value = StateEditing.ChooseColor })
               Text(text = stringResource(id = R.string.color), overflow = TextOverflow.Visible)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IconButton(modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()}, content = { Icon(painterResource(id = R.drawable.ic_recycle_icon), contentDescription = "Удалить", tint = if (stateChoser.value == StateEditing.ChooseDate) Color.Green else Color.Black) },
               onClick = { stateChoser.value = StateEditing.None
                 // vm.SelectStateDialog(StateDialog.ShowDeleteList(contextResource.getString(R.string.reallyDeleteSelected), deleteList.toList()))
                  daysVM.SelectStateDialog(StateDialog.ShowDeleteList(contextResource.getString(R.string.reallyDeleteSelected), deleteList))
               })
               Text(text = stringResource(id = R.string.delete), overflow = TextOverflow.Ellipsis)
            }
         }

         when (stateChoser.value) {
            is StateEditing.ChooseWorked -> {ChooseWorkedList(deleteList.toList(), 0, daysVM)}
            is StateEditing.ChooseComment -> {ChooseCommentList(deleteList.toList(), "", daysVM, )}
            is StateEditing.ChooseColor -> {ChooseColorList(deleteList.toList(), "#FFFFFFFF", daysVM)}
            else -> {}
         }
      }

      Box(modifier = Modifier
         .padding(0.dp)
         .fillMaxSize()
         .background(Color(0xFFF1F0F0))) {
         PagerContent(pagerState = pagerState, MAX_PAGES, daysVM, mainMenuVM, snackbartext, deleteList, dataStore, navController)
      }
   }
   //показать вопрос об удалении дня
   when (stateDialog){
      is StateDialog.None -> {}
      is StateDialog.ShowDelete -> {
         AlertDlgSet(onDismiss ={ daysVM.SelectStateDialog(StateDialog.None) },
         onAccept = {   daysVM.deleteDayFromID(stateDialog.IDday); daysVM.SelectStateDialog(StateDialog.None) },
         title = "Подтвердите",
         text = stateDialog.massage)
      }
      is StateDialog.ShowDeleteList -> {
         AlertDlgSet(onDismiss ={ daysVM.SelectStateDialog(StateDialog.None) },
            onAccept = {
               Log.d("MyTag - AlertDlgSet", "stateDialog.IDsdays - ${deleteList.size}")
               daysVM.deleteListDayFromID(deleteList)
               deleteList.clear()
               daysVM.SelectStateDialog(StateDialog.None)
                },
            title = "Подтвердите",
            text = stateDialog.massage)
      }
   }

   LaunchedEffect(snackbartext.value){
      if (snackbartext.value != "") {
            snackbarHostState.showSnackbar(message = snackbartext.value, actionLabel = "▼", duration = SnackbarDuration.Short)
         snackbartext.value = ""
      }
   }
   SnackbarHostMyScreen(snackbarHostState)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PagerContent(
   pagerState: PagerState, count: Int, daysVM: ListDaysViewModelCompose, mainMenuVM: MainMenuViewModel, snackbartext: MutableState<String>,
   deleteList: SnapshotStateList<WorkDay>, dataStore: DataStore<AppSettings>, navController: NavController
) {
   //disable scroll   (error)
   //CompositionLocalProvider(LocalOverScrollConfiguration provides null)
   val MAX_PAGES: Int = 240   //240 месяцев будет доступно всего от текущей даты

   val liveList by daysVM.getDays.observeAsState(emptyList())
   val lookDate = daysVM.lookDate.observeAsState(LocalDate.now()).value

   val monthstart = LocalDate.now().monthValue
   val yearstart = LocalDate.now().year

   HorizontalPager(
      modifier = Modifier.fillMaxSize(1f),
      count = count,
      state = pagerState,
   ) { pager ->
      var yearposition = 0
      var monthposition = pager - (MAX_PAGES / 2)

      if ((monthstart + monthposition) > 12) {
         while ((monthstart + monthposition) > 12) {
            monthposition -= 12
            yearposition += 1
         }
      }
      if ((monthstart + monthposition) < 1) {
         while ((monthstart + monthposition) < 1) {
            monthposition += 12
            yearposition -= 1
      }
      }
      val month = monthstart + monthposition
      val year = yearstart + yearposition
      val filteredDaysList by remember {
         derivedStateOf {
            liveList.filter { (it.month == month) && (it.year == year) }
         }
      }
      ListingDays(filteredDaysList, daysVM, mainMenuVM, deleteList, snackbartext, dataStore, navController)

      if ((lookDate.monthValue == month) and (lookDate.year == year)){
         daysVM.updLookDate(filteredDaysList.sumOf { it.worked }, filteredDaysList.size)         //если смотрим такой-то месяц, то обновляем его статистику в карточках
      }
   }

}


@Composable
fun ListingDays(
   list: List<WorkDay>, daysVM: ListDaysViewModelCompose, mainMenuVM: MainMenuViewModel,
   deleteList: SnapshotStateList<WorkDay>, snackbartext: MutableState<String>, dataStore: DataStore<AppSettings>, navController: NavController
) {
   //val workedMin = remember { mutableStateOf(0) }

   val appSettings = dataStore.data.collectAsState(initial = AppSettings()).value
   val listState = rememberLazyListState()


   val rowColorShifts = remember { mutableListOf<RowColorShifts>() }

   rowColorShifts.clear()
   for (i in list.indices) {        ///////проскакивает empty#  !!!!!!!!!!
      val findIndex = findIndex(rowColorShifts, Color(list[i].color.toColorInt()))            //поиск индекса цвета
      if (findIndex == -1){
         rowColorShifts.add(RowColorShifts(Color(list[i].color.toColorInt()), 1, list[i].worked))
      } else {
         rowColorShifts[findIndex] = RowColorShifts(rowColorShifts[findIndex].color, rowColorShifts[findIndex].shifts+1,rowColorShifts[findIndex].time+list[i].worked)
      }
   }

   if (list.isNotEmpty()) {
      LazyColumn(state = listState, verticalArrangement = Arrangement.Top,
         modifier = Modifier
            //.fillMaxSize(1f)
            .animateContentSize()) {
         itemsIndexed(list, key = { _, workDay -> workDay._id }) { index, workDay ->
            Column(
               modifier = Modifier
            ) {
               //ячейка списка, список
               if (appSettings.compatLists) compactCellDay(workDay, mainMenuVM, daysVM, deleteList, snackbartext, listState, index, dataStore) else
                  CellDay(workDay, mainMenuVM, daysVM, deleteList, snackbartext, listState, index, dataStore)
            }
         }

         //Разбивка по цветам
         item() {
            Row(
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 20.dp, start = 10.dp, end = 10.dp), horizontalArrangement = Arrangement.Center
            ) {
               Text(text = stringResource(R.string.totalByColor), fontSize = 17.sp, color = Color(0xff7E494B))
            }
            Divider(modifier = Modifier.fillMaxWidth(), color = Color.Black, thickness = 1.dp)
         }
         items(rowColorShifts) { colorString ->
            ColorRow(colorString.color, colorString.shifts, colorString.time)
         }

         //кнопка экспорта
         item() {
            Column(modifier = Modifier.fillMaxWidth()) {
               Spacer(modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 10.dp))
               Card(modifier = Modifier
                  .align(Alignment.End)
                  .wrapContentSize(Alignment.TopCenter)
                  .padding(top = 0.dp, bottom = 5.dp, end = 15.dp, start = 15.dp)
                  // .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(5.dp))
                  .defaultMinSize(minWidth = 100.dp)
                  .clickable {
                     Screens.Export.list = list
                     navController.navigate(Screens.Export.route)
                  },
                  elevation = 4.dp,
                  backgroundColor = Color(0xFFFCFCFC),
                  border = BorderStroke(1.dp, Color(0xFF85BFF8))) {
                  Text(text = "Экспортровать",
                     maxLines = 2, style = TextStyle(color = Color.Black, fontSize = 15.sp, textAlign = TextAlign.Center),
                     overflow = TextOverflow.Ellipsis,
                     modifier = Modifier.padding(5.dp)
                  )
               }

            }

         }

         item(){
            Box(modifier = Modifier
               .padding(10.dp)
               .fillMaxWidth(1f)
               .height(250.dp)){}
         }
      }

   } else {
      NoItems()
   }

}


//поиск индекса цвета
fun findIndex(rowColorShifts: MutableList<RowColorShifts>, toColorInt: Color):Int{
   var index = -1
   var find = false
   for (i in rowColorShifts.indices){
      if(!find) if (rowColorShifts[i].color == toColorInt) {
         find = true
         index += 1
      } else {
         index += 1
      }
   }
   return if (find) index else -1
}



@Composable
fun ColorRow(color: Color, shifts: Int, time: Int){
   val resContx = LocalContext.current.resources
   val _strHours = minuteToHours(time).hours.toString() + " ${resContx.getString(R.string.hoursAbbr)} "
   val _strMinHours = if (minuteToHours(time).minutes > 0) {
      minuteToHours(time).minutes.toString() + " ${resContx.getString(R.string.minuteAbbr)}"
   } else { "" }

   Row(modifier = Modifier.padding(start = 10.dp, end = 10.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
      Box(modifier = Modifier
         .size(width = 50.dp, height = 20.dp)
         .background(color = color)
         .border(width = 1.dp, color = Color.Black))
      Text(text = " - ", style = TextStyle(fontSize = 18.sp, color = Color.Black))
      Text(text = declensionOfWords(shifts, resContx), style = TextStyle(fontSize = 18.sp, color = Color.Black))
      Text(text = "- $_strHours $_strMinHours", style = TextStyle(fontSize = 18.sp, color = Color.Black))
   }
}


sealed class StateList {
   object Success: StateList()
   data class Error(val exception: Exception) : StateList()
   object NoItems: StateList()
   object Loading: StateList()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CellDay(
   workDay: WorkDay,
   mainMenuVM: MainMenuViewModel,
   daysVM: ListDaysViewModelCompose,
   deleteList: SnapshotStateList<WorkDay>,
   snackbartext: MutableState<String>,
   listState: LazyListState,
   index: Int,
   dataStore: DataStore<AppSettings>,
) {
   Log.d("Generated", "CellDay - ${workDay.month}")

   val stateEdition = remember { mutableStateOf<StateEditing>(StateEditing.None) }              //состояние редактирования
   var showButtons by remember { mutableStateOf(false)}                                 //показать кнопки редактирвания
   //Если ошибка повторится и на сентябрь с 31ым числом, то переписать переменную date на try catch, и отправить лог с данными ячейки в firebase
   //Fatal Exception: java.time.DateTimeException
   //Invalid value for DayOfMonth (valid values 1 - 28/31): 32
  // val date by remember { mutableStateOf(LocalDate.of(workDay.year,workDay.month, if (workDay.day in 1..31)  workDay.day else 31))}
   val errordate = remember { mutableStateOf(false) }
   val date = try {LocalDate.of(workDay.year,workDay.month, workDay.day)}
                                             catch (e:Exception){
                                                errordate.value = true
                                                LocalDate.of(workDay.year,workDay.month, 1)
                                             }

   val dayofweek = if (!errordate.value) weekdayToWD(date.dayOfWeek, LocalContext.current) else workDay.day.toString()                          //день недели    //день недели

   val colorDayOfWeek = if (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) Color(0xffE10707) else Color.Black               //сделать цвет от дня недели. Выходной красный
   val colorData = if (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) Color(0xff810707) else Color.Black               //цвет числа даты в зависимости от выходного дня
   val worked:String = "${minuteToHours(workDay.worked).hours}:${(String.format("%02d", minuteToHours(workDay.worked).minutes))}"                                         //отработано в часах и минуте
   val ColorBackgrCell = if (workDay.comment != "#empty#") Color(workDay.color.toColorInt())   else Color("#FFFFFF".toColorInt())                  //цвет ячейки дня
   val correctFont = mainMenuVM.correctFont.observeAsState().value?.plus(0) ?: 0
   val correctPadding = mainMenuVM.correctPadding.observeAsState().value?.plus(0) ?: 0
   val interactionSource = remember { MutableInteractionSource() }
   val stateButtons = remember { mutableStateOf(false)}
   val contextResource = LocalContext.current.resources
   val scope = rememberCoroutineScope()
   val bringIntoCellRequester = remember { BringIntoViewRequester() }       // скролл к элементу

   //Log.d("CellDayTag", "liststate - ${listState.scrollToItem(5)}")
      //Вся карточка
   Column(modifier = Modifier
      .fillMaxWidth(1f)
      .bringIntoViewRequester(bringIntoCellRequester))
   {               //проматывание именно к этому элементу
      Card( modifier = Modifier.padding(start = 4.dp, end = 5.dp, top = 5.dp, bottom = 5.dp),
         elevation = 4.dp,
         backgroundColor = ColorBackgrCell,
         border = if (deleteList.contains(workDay)) BorderStroke(width = 2.dp, color = Color.Green) else BorderStroke(0.dp, Color.LightGray))  {
         Column(
            modifier = Modifier
               .fillMaxWidth(1f)
               .wrapContentHeight(Alignment.CenterVertically)
               //.height(62.dp)
         ) {

            Row(
               modifier = Modifier
                  .wrapContentHeight(Alignment.CenterVertically)
                  .height(65.dp)
            ) {
               Card(       //карточка числа
                  modifier = Modifier
                     .padding(top = 3.dp, bottom = 4.dp, start = 3.dp, end = 3.dp)
                     .width(55.dp),
                  backgroundColor = Color.White,
                  shape = RoundedCornerShape(6.dp),
                  elevation = 4.dp
               ) {
                  //дата, число и день недели
                  Box(modifier = Modifier
                     .wrapContentHeight(Alignment.CenterVertically)
                     .clickable() {
                        if (deleteList.contains(workDay)) deleteList.removeIf { it == workDay } else deleteList.add(workDay)
                     },
                     contentAlignment = Alignment.Center,
                  ) {
                     Text( //число
                        text = workDay.day.toString(),
                        fontSize = (30 + correctFont).sp,
                        modifier = Modifier.padding(0.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(color = colorData, shadow = Shadow(Color(0xFF929292), Offset(3f, 5f), 9f))
                     )
                  }

                  Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.BottomCenter) {
                     Text(
                        //день недели
                        text = dayofweek.toString(), color = if (!errordate.value) colorDayOfWeek else Color.Cyan,
                        fontSize = (15).sp, modifier = Modifier.padding(0.dp), textAlign = TextAlign.Center,
                     )
                     if (deleteList.contains(workDay)) Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                        contentDescription = "",
                        modifier = Modifier.size(25.dp,25.dp)
                     )
                  }

               }
               Box(
                  modifier = Modifier
                     .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
                     .fillMaxHeight(1f)
                     .width(65.dp)
                     .clickable(interactionSource = interactionSource, indication = null) {}, contentAlignment = Alignment.Center
               ) {
                  Text(       // отработанное время
                     text = worked.toString(),
                     fontSize = (21 + correctFont).sp,
                     modifier = Modifier.padding(0.dp),
                     textAlign = TextAlign.Center,
                     style = TextStyle(shadow = Shadow(Color(0xFF929292), Offset(3f, 5f), 9f))
                  )
               }

               Divider(
                  modifier = Modifier
                     .width(1.dp)
                     .fillMaxHeight(1f)
                     .padding(vertical = 5.dp), color = Color(0xBFA5A5A5), thickness = 1.dp
               )

               Box(
                  modifier = Modifier
                     .fillMaxHeight(1f)
                     .fillMaxWidth(1f)
                     .padding(start = 8.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                     .clickable(interactionSource = interactionSource, indication = null) {},
                  contentAlignment = Alignment.Center
               ) {
                  Text(       //комменатрий
                     text = workDay.comment, maxLines = 3, fontSize = (14).sp, modifier = Modifier.padding(end = 25.dp)
                  )
                  //кнопки удалить и редактировать
                  Box(contentAlignment = Alignment.TopEnd,
                     modifier = Modifier
                        .fillMaxSize(1f)
                        .padding(top = 2.dp, end = 2.dp, bottom = 2.dp, start = 2.dp)
                        ) {
                     IconButton(onClick = {
                        scope.launch {
                           Log.d("MyTag", "listState.layoutInfo.visibleItemsInfo - ${listState.layoutInfo.visibleItemsInfo}")
                           delay(200)
                           bringIntoCellRequester.bringIntoView()                //проматывание списка к элементу
                        }
                        showButtons = !showButtons
                        stateEdition.value = StateEditing.None },
                        modifier = Modifier
                           .width(20.dp)
                           .height(21.dp),
                           interactionSource = remember { MutableInteractionSource()},
                           content = { Icon(painterResource(id =  R.drawable.ic_pencil),
                           contentDescription = "Edit",
                           tint = if (showButtons) Color.Green else Color.Black) })
                  }
               }
            }

         }

      }
      //кнопки
      AnimatedVisibility(
         visible = showButtons,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()) {
         Divider(modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 2.dp, bottom = 2.dp, start = 0.dp, end = 0.dp), color = Color.Gray)
         Row(modifier = Modifier
            .fillMaxWidth(1f)
            .background(Color.White)
            .padding(top = 10.dp, bottom = 5.dp, start = 0.dp, end = 0.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
            IconButton(onClick = {
               if (stateEdition.value != StateEditing.ChooseWorked) stateEdition.value = StateEditing.ChooseWorked else stateEdition.value = StateEditing.None
               scope.launch {
                  delay(200)
                  bringIntoCellRequester.bringIntoView()       //проматывание списка к элементу
               }
                                 }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
               content = { Icon(painterResource(id = R.drawable.ic_timeworking), contentDescription = "Время работы", tint = if (stateEdition.value == StateEditing.ChooseWorked) Color.Green else Color.Black) })
            IconButton(onClick = {
               if (stateEdition.value != StateEditing.ChooseComment) stateEdition.value = StateEditing.ChooseComment else stateEdition.value = StateEditing.None
               scope.launch {
                  delay(200)
                  bringIntoCellRequester.bringIntoView()       //проматывание списка к элементу
               }
                                 }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
               content = { Icon(painterResource(id = R.drawable.ic_comment), contentDescription = "Комментарий", tint = if (stateEdition.value == StateEditing.ChooseComment) Color.Green else Color.Black) })
            IconButton(onClick = {
               if (stateEdition.value != StateEditing.ChooseColor) stateEdition.value = StateEditing.ChooseColor else stateEdition.value = StateEditing.None
               scope.launch {
                  delay(200)
                  bringIntoCellRequester.bringIntoView()       //проматывание списка к элементу
               }
                                 }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
               content = { Icon(painterResource(id = R.drawable.ic_paintbrush), contentDescription = "Цвет", tint = if (stateEdition.value == StateEditing.ChooseColor) Color.Green else Color.Black) })
            IconButton(onClick = {stateEdition.value = StateEditing.None
               daysVM.SelectStateDialog(StateDialog.ShowDelete(
                  contextResource.getString(R.string.reallyDelete, "${workDay.day}.${workDay.month}.${workDay.year}"), workDay._id)
               )
            }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
               content = { Icon(painterResource(id = R.drawable.ic_recycle_icon), contentDescription = "Удалить", tint = if (stateEdition.value == StateEditing.ChooseDate) Color.Green else Color.Black) })

         }
      }
      AnimatedVisibility(
         visible = stateEdition.value == StateEditing.ChooseWorked,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()) {
         ChooseWorked(workDay._id, workDay.worked, stateEdition, daysVM, index, listState, dataStore)
      }
      AnimatedVisibility(
         visible = stateEdition.value == StateEditing.ChooseComment,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()) {
         ChooseComment(workDay._id, workDay.comment, daysVM, stateEdition, snackbartext, index, listState, dataStore)
      }
      AnimatedVisibility(
         visible = stateEdition.value == StateEditing.ChooseColor,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()) {
         ChooseColor(workDay._id, workDay.color, daysVM)
      }
   }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun compactCellDay(
   workDay: WorkDay,
   mainMenuVM: MainMenuViewModel,
   daysVM: ListDaysViewModelCompose,
   deleteList: SnapshotStateList<WorkDay>,
   snackbartext: MutableState<String>,
   listState: LazyListState,
   index: Int,
   dataStore: DataStore<AppSettings>,
) {
   Log.d("Generated", "CompactCellDay - ${workDay.month}")

   val stateEdition = remember { mutableStateOf<StateEditing>(StateEditing.None) }              //состояние редактирования
   var showButtons by remember { mutableStateOf(false)}                                 //показать кнопки редактирвания
   val errordate = remember { mutableStateOf(false) }
   val date = try {LocalDate.of(workDay.year,workDay.month, workDay.day)}
   catch (e:Exception){
      errordate.value = true
      LocalDate.of(workDay.year,workDay.month, 1)
   }

   val dayofweek = if (!errordate.value) weekdayToWD(date.dayOfWeek, LocalContext.current) else workDay.day.toString()                          //день недели    //день недели

   val colorDayOfWeek = if (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) Color(0xffE10707) else Color.Black               //сделать цвет от дня недели. Выходной красный
   val colorData = if (date.dayOfWeek.value == 6 || date.dayOfWeek.value == 7) Color(0xff810707) else Color.Black               //цвет числа даты в зависимости от выходного дня
   val worked:String = "${minuteToHours(workDay.worked).hours}:${(String.format("%02d", minuteToHours(workDay.worked).minutes))}"                                         //отработано в часах и минуте
   val ColorBackgrCell = if (workDay.comment != "#empty#") Color(workDay.color.toColorInt())   else Color("#FFFFFF".toColorInt())                  //цвет ячейки дня
   val correctFont = mainMenuVM.correctFont.observeAsState().value?.plus(0) ?: 0
   val correctPadding = mainMenuVM.correctPadding.observeAsState().value?.plus(0) ?: 0
   val interactionSource = remember { MutableInteractionSource() }
   val stateButtons = remember { mutableStateOf(false)}
   val contextResource = LocalContext.current.resources
   val scope = rememberCoroutineScope()
   val bringIntoCellRequester = remember { BringIntoViewRequester() }       // скролл к элементу

   //Log.d("CellDayTag", "liststate - ${listState.scrollToItem(5)}")
   //Вся карточка
   Column(modifier = Modifier
      .fillMaxWidth(1f)
      .bringIntoViewRequester(bringIntoCellRequester))               //проматывание именно к этому элементу
   {
      Card( modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
         elevation = 2.dp,
         backgroundColor = ColorBackgrCell,
         border = if (deleteList.contains(workDay)) BorderStroke(width = 2.dp, color = Color.Green) else BorderStroke(0.dp, Color.LightGray))  {
         Column(
            modifier = Modifier
               .fillMaxWidth(1f)
               .wrapContentHeight(Alignment.CenterVertically)
            //.height(62.dp)
         ) {

            Row(
               modifier = Modifier
                  .wrapContentHeight(Alignment.CenterVertically)
                  .height(39.dp)
            ) {
               Card(       //карточка числа
                  modifier = Modifier
                     .padding(top = 2.dp, bottom = 2.dp, start = 2.dp, end = 2.dp)
                     .width(35.dp),
                  backgroundColor = Color.White,
                  shape = RoundedCornerShape(3.dp),
                  elevation = 2.dp
               ) {
                  //дата, число и день недели
                  Box(modifier = Modifier
                     .wrapContentHeight(Alignment.CenterVertically)
                     .clickable() {
                        if (deleteList.contains(workDay)) deleteList.removeIf { it == workDay } else deleteList.add(workDay)
                     },
                     contentAlignment = Alignment.Center,
                  ) {
                     Text( //число
                        text = workDay.day.toString(),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(0.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(color = colorData, shadow = Shadow(Color(0xFF929292), Offset(3f, 5f), 7f))
                     )
                  }

                  Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.BottomCenter) {
                     Text(
                        //день недели
                        text = dayofweek.toString(), color = if (!errordate.value) colorDayOfWeek else Color.Cyan,
                        fontSize = (13).sp, modifier = Modifier.padding(0.dp), textAlign = TextAlign.Center,
                     )
                     if (deleteList.contains(workDay)) Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_check),
                        contentDescription = "",
                        modifier = Modifier.size(15.dp,15.dp)
                     )
                  }

               }
               Box(
                  modifier = Modifier
                     .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
                     .fillMaxHeight(1f)
                     .width(55.dp)
                     .clickable(interactionSource = interactionSource, indication = null) {}, contentAlignment = Alignment.Center
               ) {
                  Text(       // отработанное время
                     text = worked.toString(),
                     fontSize = (16).sp,
                     modifier = Modifier.padding(0.dp),
                     textAlign = TextAlign.Center,
                     style = TextStyle(shadow = Shadow(Color(0xFF929292), Offset(3f, 5f), 7f))
                  )
               }

               Divider(
                  modifier = Modifier
                     .width(1.dp)
                     .fillMaxHeight(1f)
                     .padding(vertical = 5.dp), color = Color(0xBFA5A5A5), thickness = 1.dp
               )

               Box(
                  modifier = Modifier
                     .fillMaxHeight(1f)
                     .fillMaxWidth(1f)
                     .padding(start = 3.dp, end = 3.dp, top = 3.dp, bottom = 3.dp)
                     .clickable(interactionSource = interactionSource, indication = null) {},
                  contentAlignment = Alignment.Center
               ) {
                  Text(       //комменатрий
                     text = workDay.comment, maxLines = 2, fontSize = (13).sp, modifier = Modifier.padding(end = 25.dp),
                     overflow = TextOverflow.Ellipsis
                  )
                  //кнопки удалить и редактировать   убрать
                  Box(contentAlignment = Alignment.TopEnd,
                     modifier = Modifier
                        .fillMaxSize(1f)
                        .padding(top = 1.dp, end = 1.dp, bottom = 1.dp, start = 1.dp)
                  ) {
                     IconButton(onClick = {
                        scope.launch {
                           //Log.d("MyTag", "listState.layoutInfo.visibleItemsInfo - ${listState.layoutInfo.visibleItemsInfo}")
                           delay(200)
                           bringIntoCellRequester.bringIntoView()                //проматывание списка к элементу
                        }
                        showButtons = !showButtons
                        stateEdition.value = StateEditing.None },
                        modifier = Modifier
                           .width(18.dp)
                           .height(19.dp),
                        interactionSource = remember { MutableInteractionSource()},
                        content = { Icon(painterResource(id =  R.drawable.ic_pencil),
                           contentDescription = "Edit",
                           tint = if (showButtons) Color.Green else Color.Black) })
                  }
               }
            }

         }

      }
      //кнопки
      AnimatedVisibility(
         visible = showButtons,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()) {
         Divider(modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 2.dp, bottom = 2.dp, start = 0.dp, end = 0.dp), color = Color.Gray)
         Row(modifier = Modifier
            .fillMaxWidth(1f)
            .background(Color.White)
            .padding(top = 10.dp, bottom = 5.dp, start = 0.dp, end = 0.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
            IconButton(onClick = {
               if (stateEdition.value != StateEditing.ChooseWorked) stateEdition.value = StateEditing.ChooseWorked else stateEdition.value = StateEditing.None
               scope.launch {
                  delay(200)
                  bringIntoCellRequester.bringIntoView()       //проматывание списка к элементу
               }
            }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
               content = { Icon(painterResource(id = R.drawable.ic_timeworking), contentDescription = "Время работы", tint = if (stateEdition.value == StateEditing.ChooseWorked) Color.Green else Color.Black) })
            IconButton(onClick = {
               if (stateEdition.value != StateEditing.ChooseComment) stateEdition.value = StateEditing.ChooseComment else stateEdition.value = StateEditing.None
               scope.launch {
                  delay(200)
                  bringIntoCellRequester.bringIntoView()       //проматывание списка к элементу
               }
            }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
               content = { Icon(painterResource(id = R.drawable.ic_comment), contentDescription = "Комментарий", tint = if (stateEdition.value == StateEditing.ChooseComment) Color.Green else Color.Black) })
            IconButton(onClick = {
               if (stateEdition.value != StateEditing.ChooseColor) stateEdition.value = StateEditing.ChooseColor else stateEdition.value = StateEditing.None
               scope.launch {
                  delay(200)
                  bringIntoCellRequester.bringIntoView()       //проматывание списка к элементу
               }
            }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
               content = { Icon(painterResource(id = R.drawable.ic_paintbrush), contentDescription = "Цвет", tint = if (stateEdition.value == StateEditing.ChooseColor) Color.Green else Color.Black) })
            IconButton(onClick = {stateEdition.value = StateEditing.None
               daysVM.SelectStateDialog(StateDialog.ShowDelete(
                  contextResource.getString(R.string.reallyDelete, "${workDay.day}.${workDay.month}.${workDay.year}"), workDay._id)
               )
            }, modifier = Modifier.size(20.dp), interactionSource = remember { MutableInteractionSource()},
               content = { Icon(painterResource(id = R.drawable.ic_recycle_icon), contentDescription = "Удалить", tint = if (stateEdition.value == StateEditing.ChooseDate) Color.Green else Color.Black) })

         }
      }
      AnimatedVisibility(
         visible = stateEdition.value == StateEditing.ChooseWorked,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()) {
         ChooseWorked(workDay._id, workDay.worked, stateEdition, daysVM, index, listState, dataStore)
      }
      AnimatedVisibility(
         visible = stateEdition.value == StateEditing.ChooseComment,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()) {
         ChooseComment(workDay._id, workDay.comment, daysVM, stateEdition, snackbartext, index, listState, dataStore)
      }
      AnimatedVisibility(
         visible = stateEdition.value == StateEditing.ChooseColor,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()) {
         ChooseColor(workDay._id, workDay.color, daysVM)
      }
   }
}


@Composable
private fun ChooseWorkedList(listid: List<WorkDay>, worked: Int, daysVM:ListDaysViewModelCompose) {
   val hours = remember { mutableStateOf(minuteToHours(worked).hours)}
   val minutes = remember { mutableStateOf(minuteToHours(worked).minutes)}
   val focusRequester = remember { FocusRequester() }
   Column(modifier = Modifier
      .fillMaxWidth(1f)
      .background(Color.White)) {
      Text(text = stringResource(id = R.string.hoursPerDay_packAdd), fontSize = 18.sp,
         modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 1.dp, bottom = 10.dp) )              //Количество часов в день:
      Row(horizontalArrangement = Arrangement.Start,
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .padding(start = 15.dp, top = 2.dp, end = 15.dp)
            .fillMaxWidth()
      ) {
         OutlinedTextField(      //часы
            value = if  (hours.value == 0) "" else hours.value.toString(),
            onValueChange = { if (it != "") {
               try {
                  hours.value = it.toInt()
                  if (hours.value > 48) hours.value = 48
                  if (hours.value < -12) hours.value = -12}
               catch (e:Exception){minutes.value}
            } else { hours.value = 0 }
               if (it.length >= 2) focusRequester.requestFocus()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions( onSend = {focusRequester.requestFocus()}),
            modifier = Modifier.width(60.dp) ,
            singleLine = true,
            placeholder = { Text(text = "0") },
            textStyle = TextStyle(fontSize = 16.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
         )
         Text(text = stringResource(id = R.string.hours), modifier = Modifier)          // - часов,
         OutlinedTextField(      //минуты
            value = if  (minutes.value == 0) "" else minutes.value.toString(),
            onValueChange = { if (it != "") {
               try { minutes.value = it.toInt()
                  if (minutes.value > 480) minutes.value = 480
                  if (minutes.value < -60) minutes.value = -60 }
               catch (e: Exception){ minutes.value  }
            } else minutes.value = 0

            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
               .width(60.dp)
               .focusRequester(focusRequester),
            singleLine = true,
            placeholder = { Text(text = "0") },
            textStyle = TextStyle(fontSize = 16.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
         )
         Text(text = stringResource(id = R.string.minute), modifier = Modifier )               // - минут
      }

      Button(modifier = Modifier
         .align(alignment = Alignment.End)
         .padding(top = 10.dp, end = 20.dp),
         enabled = (worked != hours.value * 60 + minutes.value),
         colors = ButtonDefaults.buttonColors(Color.LightGray),
         onClick = {
            daysVM.saveWorkedListing(listid, hours.value * 60 + minutes.value)
         }) {
         Text(text = stringResource(id = R.string.save), maxLines = 1)
      }
   }
}

@Composable
private fun ChooseCommentList(listid: List<WorkDay>, comment: String, vm:ListDaysViewModelCompose){
   val commentEntered = remember { mutableStateOf(comment)}
   Column(modifier = Modifier.background(Color.White)) {
      Text(text = stringResource(id = R.string.description), fontSize = 18.sp,
         modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 1.dp, bottom = 10.dp) )
      OutlinedTextField(
         value = commentEntered.value.toString(),
         onValueChange = { commentEntered.value = it },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences),
         singleLine = false,
         maxLines = 3,
         placeholder = { Text(text = stringResource(id = R.string.ifComment)) },
         textStyle = TextStyle(fontSize = 16.sp),
         modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 10.dp, vertical = 3.dp),
         colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.Black)
      )
      Button(modifier = Modifier
         .align(alignment = Alignment.End)
         .padding(top = 0.dp, end = 20.dp, bottom = 10.dp),
         enabled = (comment != commentEntered.value),
         colors = ButtonDefaults.buttonColors(Color.LightGray),
         onClick = {
            vm.saveCommentListing(listid, commentEntered.value.trim())
         }) {
         Text(text = stringResource(id = R.string.save), maxLines = 1)
      }
   }
}

@Composable
private fun ChooseColorList(listid: List<WorkDay>, color:String, vm:ListDaysViewModelCompose){
//цвета
   //val colorEntered = remember { mutableStateOf(color) }
   val dialogPicker = rememberMaterialDialogState()
   val colorAdditional = vm.colorAdditional.observeAsState(listOf<Color>()).value
   val sizeColorBloks = 35.dp
   val sizeHeightColorBloks =  remember { mutableStateOf(45.dp) }
   val chooseColor = remember { mutableStateOf("#FFFFFF")   }
   Column(modifier = Modifier.background(Color.White)) {
      Text(text = stringResource(id = R.string.SelectColor), fontSize = 18.sp, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp))

      Row(horizontalArrangement = Arrangement.SpaceEvenly,
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(Color.White)
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 5.dp, horizontal = 20.dp)) {
         colorsCell(colorCell = "#FFFFFFFF", colorChosser = chooseColor.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, chooseColor)
         colorsCell(colorCell = "#FFA7FFFF", colorChosser = chooseColor.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, chooseColor)
         colorsCell(colorCell = "#FFFFA199", colorChosser = chooseColor.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, chooseColor)
         colorsCell(colorCell = "#FFD2FAA4", colorChosser = chooseColor.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, chooseColor)
         colorsCell(colorCell = "#FFB7C2FF", colorChosser = chooseColor.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, chooseColor)
         colorsCell(colorCell = "#FFF7FF9E", colorChosser = chooseColor.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, chooseColor)
         colorsCell(colorCell = "#FFF3ADFF", colorChosser = chooseColor.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, chooseColor)
         colorsCell(colorCell = "#FFFFCE94", colorChosser = chooseColor.value, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, chooseColor)
         Card(modifier = Modifier
            .width(sizeColorBloks)
            .height(sizeHeightColorBloks.value - 20.dp)
            .padding(3.dp), elevation = 2.dp,
            border = BorderStroke(1.dp,Color.Gray)) {
            Box(modifier = Modifier
               .fillMaxSize()
               .background(Color(chooseColor.value.toColorInt()))
               .clickable {
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
      vm.LastColors()

      MaterialDialog(dialogState = dialogPicker) {
         val selectedColors = remember { mutableStateOf("#FFFFFFFF") }
         val selected = remember { mutableStateOf(false) }
         colorChooser(colors = (MyColors.ColorsPicker + colorAdditional).distinct(),            //удаление лишних цветов в списке
            argbPickerState = ARGBPickerState.WithoutAlphaSelector,
            waitForPositiveButton = false,
            initialSelection = 1,
            onColorSelected = {
               //color = "#${Integer.toHexString(it.toArgb())}"
               selectedColors.value = "#${Integer.toHexString(it.toArgb())}"
               selected.value = true
            })
         Text(text = "OK", fontSize = 20.sp,
            modifier = Modifier
               .padding(vertical = 10.dp, horizontal = 20.dp)
               .fillMaxWidth(1f)
               .clickable {
                  if (selected.value) chooseColor.value = selectedColors.value
                  dialogPicker.hide()
               },
            textAlign = TextAlign.End)

      }

      Button(modifier = Modifier
         .align(alignment = Alignment.End)
         .padding(top = 0.dp, end = 20.dp, bottom = 10.dp),
         enabled = true,
         colors = ButtonDefaults.buttonColors(Color.LightGray),
         onClick = {
            vm.saveColorListing(listid, chooseColor.value)
         }) {
         Text(text = stringResource(id = R.string.save), maxLines = 1)
      }
   }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChooseWorked(
   id: Int, worked: Int,
   stateEdition: MutableState<StateEditing>,
   daysVM:ListDaysViewModelCompose,
   index: Int,
   listState: LazyListState,
   dataStore: DataStore<AppSettings>,
) {
   val hours = remember { mutableStateOf(minuteToHours(worked).hours)}
   val minutes = remember { mutableStateOf(minuteToHours(worked).minutes)}
   val focusRequester = remember { FocusRequester() }
   val composableScope = rememberCoroutineScope()
   val bringIntoViewRequester = remember { BringIntoViewRequester() }       // скролл к редактируемому элементу
   val scope = rememberCoroutineScope()

   Column(modifier = Modifier
      .fillMaxWidth(1f)
      .background(Color.White)) {
   Text(text = stringResource(id = R.string.hoursPerDay_packAdd), fontSize = 18.sp,
      modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 1.dp, bottom = 10.dp) )              //Количество часов в день:
   Row(horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .padding(start = 15.dp, top = 2.dp, end = 15.dp)
         .fillMaxWidth()
   ) {
      val hourstext = remember { mutableStateOf("") }
      val minutestext = remember { mutableStateOf("") }

      OutlinedTextField(      //часы
         value = if  (hourstext.value.isEmpty() and (hours.value == 0)) "" else hours.value.toString(),
         onValueChange = { hourstext.value = it
            if (it != "") {
               try {
                  hours.value = it.toInt()
                  if (hours.value > 48) hours.value = 48
                  if (hours.value < -12) hours.value = -12}
               catch (e:Exception){minutes.value}
            } else { hours.value = 0 }
               if (it.length >= 2) focusRequester.requestFocus()
            },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, capitalization = KeyboardCapitalization.Sentences),
         keyboardActions = KeyboardActions(onDone = {focusRequester.requestFocus()}),
         modifier = Modifier
            .width(60.dp)
            .onFocusEvent {
               if (it.isFocused) {
                  composableScope.launch {
                     listState.scrollToItem(index)

                     // bringIntoViewRequester.bringIntoView()
                  }
               }
            },
         singleLine = true,
         placeholder = { Text(text = "0") },
         textStyle = TextStyle(fontSize = 16.sp),
         colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
      )
      Text(text = stringResource(id = R.string.hours), modifier = Modifier)          // - часов,
      OutlinedTextField(      //минуты
         value = if  ((minuteToHours(minutes.value).minutes == 0) and (minutestext.value.isEmpty())) ""
                 else  if ((minuteToHours(minutes.value).minutes == 0) and (minutestext.value.isNotEmpty())) { minutestext.value }
                 else  minuteToHours(minutes.value).minutes.toString(),
         onValueChange = {
            if (it == "000") minutestext.value = "00" else minutestext.value = it
            if (it != "") {
               try {
                  if (it.toInt() in -59..59) {
                     minutes.value = minuteToHours(minutes.value).hours*60 + it.toInt()
                  } else {
                     if (it.toInt() > 59) minutes.value = minuteToHours(minutes.value).hours*60 + 59
                     if (it.toInt() < -59) minutes.value = minuteToHours(minutes.value).hours*60 - 59
                  }
               }
               catch (e: Exception){ minutes.value = 0 + minuteToHours(minutes.value).hours*60  }
            } else minutes.value = 0 + minuteToHours(minutes.value).hours*60
         },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
         keyboardActions = KeyboardActions(onDone = {
            daysVM.saveWorked(id, hours.value * 60 + minutes.value)
            stateEdition.value = StateEditing.None}),
         modifier = Modifier
            .width(60.dp)
            .focusRequester(focusRequester)
            .onFocusEvent {
               if (it.isFocused) {
                  composableScope.launch {
                     //listState.scrollToItem(index)
                     listState.scrollToItem(index)
                     // bringIntoViewRequester.bringIntoView()
                  }
               }
            },
         singleLine = true,
         placeholder = { Text(text = "00") },
         textStyle = TextStyle(fontSize = 16.sp),
         colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Red, unfocusedBorderColor = Color.LightGray)
      )
      Text(text = stringResource(id = R.string.minute), modifier = Modifier )               // - минут
   }
      val appSettings = dataStore.data.collectAsState(initial = AppSettings()).value

      AnimatedVisibility(
         visible = (stateEdition.value == StateEditing.ChooseWorked),
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()
      ) {
         Row(modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
            Row() {
               Checkbox(checked = appSettings.hintHours, modifier = Modifier.padding(0.dp),
                  onCheckedChange = {
                     scope.launch {
                        dataStore.updateData {
                           it.copy(hintHours = !appSettings.hintHours)
                        }
                     }
                  })
               IconButton(modifier = Modifier.testTag("HintComment"),
                  onClick = {
                     scope.launch {
                        dataStore.updateData {
                           it.copy(hintHours = !appSettings.hintHours)
                        }
                     }
                  }) {
                  Image(modifier = Modifier.size(14.dp),
                     imageVector = Icons.Outlined.Info,
                     colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                     contentDescription = "Hint",
                  )
               }
            }

            Button(elevation = ButtonDefaults.elevation(4.dp), border = BorderStroke(1.dp, Color(0xFF85BFF8)),
               colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFEBEBEB)),
               modifier = Modifier
                  .padding(top = 10.dp, end = 20.dp)
                  .bringIntoViewRequester(bringIntoViewRequester),               //проматывание именно к этому элементу
               enabled = (worked != hours.value * 60 + minutes.value),
               onClick = {
                  daysVM.saveWorked(id, hours.value * 60 + minutes.value)
                  stateEdition.value = StateEditing.None
               }) {
               Text(text = stringResource(id = R.string.save), maxLines = 1)
            }
            }
         }


      AnimatedVisibility(
         visible = (stateEdition.value == StateEditing.ChooseWorked) and appSettings.hintHours,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()
      ) {
         val configuration = LocalConfiguration.current
         val screenWidth = configuration.screenWidthDp.dp
         val listWorked = remember { mutableListOf<Int>() }
         //список


         Column(modifier = Modifier
            .wrapContentSize()
            .padding(top = 0.dp, bottom = 4.dp, end = 15.dp, start = 15.dp)) {
            listWorked.forEach{n ->
               ItemlistWorked(worked = n) {
                  hours.value = minuteToHours(it).hours
                  minutes.value = minuteToHours(it).minutes
               }
            }

         }

         LaunchedEffect(Unit){
            listWorked.addAll(daysVM.LastWorkeds())
         }
      }
   }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChooseComment(
   id: Int,
   comment: String,
   daysVM: ListDaysViewModelCompose,
   stateEdition: MutableState<StateEditing>,
   snackbartext: MutableState<String>,
   index: Int,
   listState: LazyListState,
   dataStore: DataStore<AppSettings>
   ){
   val commentEntered = remember { mutableStateOf(comment)}
   val composableScope = rememberCoroutineScope()
   val bringIntoViewRequester = remember { BringIntoViewRequester() }       // скролл к редактируемому элементу
   val contextResource = LocalContext.current.resources
   val scope = rememberCoroutineScope()


   Column(modifier = Modifier.background(Color.White)) {
      Text(text = stringResource(id = R.string.description), fontSize = 18.sp,
         modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 1.dp, bottom = 10.dp) )
      OutlinedTextField(
         value = commentEntered.value.toString(),
         onValueChange = { commentEntered.value = it },
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Sentences),
         singleLine = false,
         maxLines = 5,
         placeholder = { Text(text = stringResource(id = R.string.ifComment)) },
         textStyle = TextStyle(fontSize = 16.sp),
         modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .onFocusEvent {
               if (it.isFocused) {
                  composableScope.launch {
                     listState.scrollToItem(index)
                     //delay(500)
                     //bringIntoViewRequester.bringIntoView()       //проматывание списка к элементу
                  }
               }
            },
         colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.LightGray, unfocusedBorderColor = Color.Black)
      )

       val appSettings = dataStore.data.collectAsState(initial = AppSettings()).value

       AnimatedVisibility(
           visible = (stateEdition.value == StateEditing.ChooseComment),
           enter = expandVertically() + fadeIn(),
           exit = shrinkVertically() + fadeOut()
       ) {
           Row(modifier = Modifier
              .padding(0.dp)
              .fillMaxWidth(),
               verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween) {
               Row() {
                   Checkbox(checked = appSettings.hintComment, modifier = Modifier.padding(0.dp),
                       onCheckedChange = {
                           scope.launch {
                               dataStore.updateData {
                                   it.copy(hintComment = !appSettings.hintComment)
                               }
                           }
                       })
                   IconButton(modifier = Modifier.testTag("HintComment"),
                       onClick = {
                           scope.launch {
                               dataStore.updateData {
                                   it.copy(hintComment = !appSettings.hintComment)
                               }
                           }
                       }) {
                       Image(modifier = Modifier.size(14.dp),
                           imageVector = Icons.Outlined.Info,
                           colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
                           contentDescription = "Hint",
                       )
                   }
               }

              Button(modifier = Modifier
                 .padding(top = 0.dp, end = 20.dp, bottom = 10.dp)
                 .bringIntoViewRequester(bringIntoViewRequester),               //проматывание именно к этому элементу
                 enabled = (comment != commentEntered.value),
                 colors = ButtonDefaults.buttonColors(Color.LightGray),
                 onClick = {
                    daysVM.saveComment(id, commentEntered.value.trim())
                    snackbartext.value = contextResource.getString(R.string.saved)
                    stateEdition.value = StateEditing.None
                 }) {
                 Text(text = stringResource(id = R.string.save), maxLines = 1)
              }
           }
       }
      AnimatedVisibility(
         visible = (stateEdition.value == StateEditing.ChooseComment) and appSettings.hintComment,
         enter = expandVertically() + fadeIn(),
         exit = shrinkVertically() + fadeOut()
      ) {
         val listComments = remember { mutableListOf<String>() }
         //список
         Column(modifier = Modifier
            .wrapContentSize()
            .padding(top = 0.dp, bottom = 4.dp, end = 15.dp, start = 15.dp)) {
            listComments.forEach { str ->
               ItemlistComments(str = str){
                  commentEntered.value = it.toString()
               }
            }
         }

         LaunchedEffect(Unit){
            listComments.addAll(daysVM.LastComments())
         }
      }
   }
}


@Composable
private fun ChooseColor(id: Int, color:String, vm:ListDaysViewModelCompose){
   //цвета
   //val colorEntered = remember { mutableStateOf(color) }
   val dialogPicker = rememberMaterialDialogState()
   val colorAdditional = vm.colorAdditional.observeAsState(listOf<Color>()).value
   val sizeColorBloks = 35.dp
   val sizeHeightColorBloks =  remember { mutableStateOf(45.dp) }
   Column(modifier = Modifier.background(Color.White)) {

   Text(text = stringResource(id = R.string.SelectColor), fontSize = 18.sp, modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp))
   Row(horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
         .fillMaxWidth()
         .height(75.dp)
         .background(Color.White)
         .horizontalScroll(rememberScrollState())
         .padding(vertical = 5.dp, horizontal = 20.dp)) {
      colorsCell(colorCell = "#FFFFFFFF", colorChosser = color, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm, id)
      colorsCell(colorCell = "#FFA7FFFF", colorChosser = color, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm, id)
      colorsCell(colorCell = "#FFFFA199", colorChosser = color, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm, id)
      colorsCell(colorCell = "#FFD2FAA4", colorChosser = color, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm, id)
      colorsCell(colorCell = "#FFB7C2FF", colorChosser = color, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm, id)
      colorsCell(colorCell = "#FFF7FF9E", colorChosser = color, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm, id)
      colorsCell(colorCell = "#FFF3ADFF", colorChosser = color, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm, id)
      colorsCell(colorCell = "#FFFFCE94", colorChosser = color, sizeColorBloks = sizeColorBloks, sizeHeightColorBloks = sizeHeightColorBloks, vm, id)
      Card(modifier = Modifier
         .width(sizeColorBloks)
         .height(sizeHeightColorBloks.value - 20.dp)
         .padding(3.dp), elevation = 2.dp,
         border = BorderStroke(1.dp,Color.Gray)) {
         Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .clickable {
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
      vm.LastColors()

      MaterialDialog(dialogState = dialogPicker) {
         val selectedColors = remember { mutableStateOf("#FFFFFFFF") }
         val selected = remember { mutableStateOf(false) }

//            Text(text = "Выбирайте бледные цвета, что бы не нарушалась гармоничность цветов приложения", fontSize = 13.sp,
//               modifier = Modifier
//                  .padding(vertical = 10.dp, horizontal = 20.dp)
//                  .fillMaxWidth(1f)
//                  .clickable {
//                     if (selected.value) vm.saveColor(id, selectedColors.value)
//                     dialogPicker.hide()
//                  },
//               textAlign = TextAlign.Center)
            colorChooser(colors = (MyColors.ColorsPicker + listOf(Color(color.toColorInt())) + colorAdditional).distinct(),            //удаление лишних цветов в списке
               argbPickerState = ARGBPickerState.WithoutAlphaSelector,
               waitForPositiveButton = false,
               initialSelection = (MyColors.ColorsPicker + listOf(Color(color.toColorInt())) + colorAdditional).distinct().indexOfFirst { it == Color(color.toColorInt()) },          //удаление лишних цветов в спике и поиск и выбор элемента в списке
               onColorSelected = {
                  //color = "#${Integer.toHexString(it.toArgb())}"
                  selectedColors.value = "#${Integer.toHexString(it.toArgb())}"
                  selected.value = true
               })
                  Text(text = "OK", fontSize = 20.sp,
                     modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                        .fillMaxWidth(1f)
                        .clickable {
                           if (selected.value) vm.saveColor(id, selectedColors.value)
                           dialogPicker.hide()
                        },
                     textAlign = TextAlign.End)

      }
   }
}

@Composable
private fun colorsCell(
   colorCell: String,
   colorChosser: String,
   sizeColorBloks: Dp,
   sizeHeightColorBloks: MutableState<Dp>,
   vm: ListDaysViewModelCompose,
   id: Int
){
   Card(modifier = Modifier
      .width(sizeColorBloks)
      .height(if (colorChosser != colorCell) sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
      .padding(3.dp), elevation = 2.dp,
      border = if (colorChosser != colorCell) BorderStroke(1.dp,Color.Gray) else {
         BorderStroke(1.dp,Color.Red)
      }) {
      Box(modifier = Modifier
         .fillMaxSize()
         .background(Color(colorCell.toColorInt()))
         .clickable {
            vm.saveColor(id, colorCell)
         }, contentAlignment = Alignment.Center ) {
         if (colorChosser == colorCell) Text(text = "✓")
      }
   }
}


@Composable
private fun colorsCell(
   colorCell: String,
   colorChosser: String,
   sizeColorBloks: Dp,
   sizeHeightColorBloks: MutableState<Dp>,
   chooseColor: MutableState<String>
){
   Card(modifier = Modifier
      .width(sizeColorBloks)
      .height(if (colorChosser != colorCell) sizeHeightColorBloks.value - 20.dp else sizeHeightColorBloks.value)
      .padding(3.dp), elevation = 2.dp,
      border = if (colorChosser != colorCell) BorderStroke(1.dp,Color.Gray) else {
         BorderStroke(1.dp,Color.Red)
      }) {
      Box(modifier = Modifier
         .fillMaxSize()
         .background(Color(colorCell.toColorInt()))
         .clickable {
            chooseColor.value = colorCell
         }, contentAlignment = Alignment.Center ) {
         if (colorChosser == colorCell) Text(text = "✓")
      }
   }
}


@Composable
fun CardsMenu(header: String, value: String, modifier: Modifier) {
   //val interactionSource = remember { MutableInteractionSource() }
   Box(modifier = modifier) {
      Card(
         modifier = Modifier.padding(top = 2.dp, start = 2.dp, end = 2.dp, bottom = 2.dp),
         //.fillMaxWidth(width),
         backgroundColor = Color(0xFFF3F3F3), shape = RoundedCornerShape(5.dp), elevation = 6.dp, border = BorderStroke(0.dp, Color(0x5520232B))
      ) {
         Column(
            modifier = Modifier
               .fillMaxWidth()
               .padding(top = (2).dp, bottom = (4).dp), horizontalAlignment = Alignment.CenterHorizontally
         ) {
            Text(text = header, fontSize = (16).sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = value, fontSize = (14).sp, textAlign = TextAlign.Start, maxLines = 1, overflow = TextOverflow.Visible)
         }
      }
   }
}

@Composable
fun NoItems() {
   Column(modifier = Modifier.fillMaxSize(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

         Image(painterResource(id = R.drawable.nosearch), contentDescription = "not found")
         Text(text = stringResource(R.string.no_days))

   }
}

@Composable
fun Error(text:String) {
   Column(modifier = Modifier.fillMaxSize(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
      Text(text = "Ошибка загрузки - $text")
   }
}

@Composable
private fun AlertDlgSet(onDismiss: () -> Unit, onAccept: () -> Unit, title: String, text: String) {
   AlertDialog(onDismissRequest = { onDismiss() },
      title = { Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
      text = { Text(text = text, fontSize = 16.sp, modifier = Modifier.fillMaxWidth(0.9f), textAlign = TextAlign.Center) },
      buttons = {
         Row(
            modifier = Modifier
               .padding(all = 8.dp)
               .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
         ) {
            Button(modifier = Modifier
               .wrapContentSize()
               .padding(10.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE4E4E4)),
               onClick = { onDismiss() }) {
               Text(text = stringResource(id = R.string.Cancel))
            }
            //TextFieldDefaults.textFieldColors(textColor = Color.Black, backgroundColor = Color.White)
            Button(modifier = Modifier
               .wrapContentSize()
               .padding(10.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE4E4E4)),
               onClick = { onAccept() }) {
               Text("Ok")
            }
         }
      })
}
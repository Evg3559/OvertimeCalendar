package com.evg3559programmer.overtimecalendar.composeUI.screens.mainmenu


import android.os.Bundle
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evg3559programmer.overtimecalendar.R
import androidx.navigation.NavController
import com.evg3559programmer.overtimecalendar.composeUI.Screens
import com.evg3559programmer.overtimecalendar.di.WorkedTimeInHours
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

@Composable
fun MainMenuScreen(navController: NavController, mainMenuVM: MainMenuViewModel) {
   val userDeleted = mainMenuVM.deletedUser.observeAsState(false).value
   val context = LocalContext.current.resources
   val displayMetrics = context.displayMetrics
   mainMenuVM.setScrHeight(displayMetrics.heightPixels / displayMetrics.density)
   val scrWidth = displayMetrics.widthPixels / displayMetrics.density
   val scrHeight = mainMenuVM.scrHeight.observeAsState(700f).value    //меньше 600 уменьшать интерфейс
   val correctFont = mainMenuVM.correctFont.observeAsState(0).value
   val correctPadding = mainMenuVM.correctPadding.observeAsState(0).value
   //сделать отметку в настройках "Уменьшить интерфейс" для маленьких экранов
   //сохрнаить значения в базе данных настройках приложения

   if (scrHeight < 620){
      mainMenuVM.setCorrectFont(-5)
      mainMenuVM.setCorrectPadding(-6)
   }

   val todayDate = mainMenuVM.todayDate.observeAsState("${LocalDate.now()}").value               //текущее число
   val userName = mainMenuVM.userName.observeAsState("Anonim").value               //текущее имя
   val minutesWorked = mainMenuVM.workedTime.observeAsState(0).value               //отработано минут в текущем месяце
   val shiftsWorked = mainMenuVM.workedShifts.observeAsState(0).value               //отработано смен в текущем месяце
   val minutesWorkedAlltime = mainMenuVM.workedTimeAllTime.observeAsState(0).value               //отработано минут за все время
   val shiftsWorkedAlltime = mainMenuVM.workedShiftsAllTime.observeAsState(0).value               //отработано смен за все время
   mainMenuVM.changeLookDate(LocalDate.now())
   val _strMonHours = minuteToHours(minutesWorked)
   val _strHoursAlltime = minuteToHours(minutesWorkedAlltime)

   val stringShiftsAllTime = shiftsWorkedAlltime.toString()
   val stringHoursAllTime = _strHoursAlltime.hours.toString() + " ${context.getString(R.string.hoursAbbr)} " + _strHoursAlltime.minutes.toString() + " ${context.getString(R.string.minuteAbbr)}"
   val stringMonthHours = _strMonHours.hours.toString() + " ${context.getString(R.string.hoursAbbr)} " + _strMonHours.minutes.toString() + " ${context.getString(R.string.minuteAbbr)}"
   val stringMonthShifts = shiftsWorked.toString()

   val turncard3 = mainMenuVM.turnCard3.observeAsState().value ?: false            //для разворачивания карточки 3. Команда повернуть
   val turncard4 = mainMenuVM.turnCard4.observeAsState().value ?: false            //для разворачивания карточки 4. Команда повернуть
   val turnedCard3 = mainMenuVM.turnedCard3.observeAsState().value ?: false             //Для анимации, карточка повернулась
   val turnedCard4 = mainMenuVM.turnedCard4.observeAsState().value ?: false             //Для анимации, карточка повернулась


   LaunchedEffect(Unit){
      val params = Bundle()
      params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MyArt");
      params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Main menu");
      Firebase.analytics.logEvent("MovementScreen", params)
   }


   Box(
      modifier = Modifier
         .fillMaxWidth()
         .padding(start = 0.dp, end = 0.dp, top = 0.dp, bottom = 0.dp),
   ) {
      Image(
         painterResource(id = R.drawable.background_menu),
         alpha = 0.3f,
         contentDescription = "",
         colorFilter = ColorFilter.tint(Color(0x7AE6E7E7), BlendMode.Screen),
         contentScale = ContentScale.FillBounds, // or some other scale
         modifier = Modifier.fillMaxSize()
      )
      CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
         Column(
            modifier = Modifier
               .fillMaxWidth()
               .padding(start = 4.dp, end = 4.dp, top = 1.dp, bottom = 3.dp)
         ) {
            Row(modifier = Modifier.fillMaxWidth()) {
               CardsMenu(header = stringResource(id = R.string.menu_header_name), value = if (userDeleted) "Deleted" else userName, 0.5f, mainMenuVM = mainMenuVM){}
               CardsMenu(header = stringResource(id = R.string.today_headermenu), value = todayDate, mainMenuVM = mainMenuVM){}
            }
            Row(modifier = Modifier.fillMaxWidth()) {
               //3 карточка
               if (!turnedCard3) {           //проверяем состояние, если не повернуто..      //смен за месяц
                  val animateFloat = remember { Animatable(1f) }
                  CardsMenu(header = stringResource(id = R.string.shifts_per_month_menuheader), value = if (userDeleted) "deleted" else stringMonthShifts, 0.5f, alpha = animateFloat.value, mainMenuVM = mainMenuVM){
                     mainMenuVM.setTurned3(true)
                  }
                  if (turncard3) {           //если поступил сигнал повернуть, поворачиваем
                     LaunchedEffect(animateFloat) {
                        animateFloat.animateTo(
                           targetValue = 0f, animationSpec = tween(durationMillis = 200, easing = LinearEasing)
                        ).let {
                           mainMenuVM.turned3(true)
                        }
                     }
                  }
               } else {      //иначе, если повернуто //смен за все время
                  val animateFloat = remember { Animatable(1f) }
                  CardsMenu(header = stringResource(id = R.string.shifts_all_time_menuheader), value = if (userDeleted) "deleted" else stringShiftsAllTime, 0.5f, alpha = animateFloat.value, mainMenuVM = mainMenuVM){
                     mainMenuVM.setTurned3(false)           //повернуть сейчас
                  }
                  if (!turncard3) {       //поступил сигнал развернуть, анимировать и в начало
                     LaunchedEffect(animateFloat) {
                        animateFloat.animateTo(
                           targetValue = 0f, animationSpec = tween(durationMillis = 200, easing = LinearEasing)
                        ).let {
                                  //установить на неповернутое
                           mainMenuVM.turned3(false)
                        }
                     }
                  }
               }
               //4 карточка
               if (!turnedCard4) {         //"Часов за месяц"
                  val animateFloat = remember { Animatable(1f) }
                  CardsMenu(header = stringResource(id = R.string.hours_month_headermenu), value = if (userDeleted) "deleted" else stringMonthHours, 1f, alpha = animateFloat.value, mainMenuVM = mainMenuVM){
                     mainMenuVM.setTurned4(true)
                  }
                  if (turncard4) {
                     LaunchedEffect(animateFloat) {
                        animateFloat.animateTo(
                           targetValue = 0f, animationSpec = tween(durationMillis = 200, easing = LinearEasing)
                        ).let {
                           mainMenuVM.turned4(true)
                        }
                     }
                  }
               } else {       //"Часы за все время"
                  val animateFloat = remember { Animatable(1f) }
                  CardsMenu(header = stringResource(id = R.string.hours_all_headermenu), value = if (userDeleted) "deleted" else stringHoursAllTime, 1f, alpha = animateFloat.value, mainMenuVM = mainMenuVM){
                     mainMenuVM.setTurned4(false)           //повернуть сейчас
                  }
                  if (!turncard4) {       //если повернута то анимировать и в начало
                     LaunchedEffect(animateFloat) {
                        animateFloat.animateTo(
                           targetValue = 0f, animationSpec = tween(durationMillis = 200, easing = LinearEasing)
                        ).let {
                                  //установить на неповернутое
                           mainMenuVM.turned4(false)
                        }
                     }
                  }
               }
            }

            //Само меню
            Menu(navController, mainMenuVM = mainMenuVM, userDeleted)
         }
      }
   }
}

@Composable
fun CardsMenu(header: String, value: String, width: Float = 1f, alpha: Float = 1f, mainMenuVM: MainMenuViewModel, Onclick: () -> Unit) {
   val correctFont = mainMenuVM.correctFont.observeAsState().value?.plus(3) ?: 0
   val correctPadding = mainMenuVM.correctPadding.observeAsState().value?.plus(3) ?: 0
   val userDeleted = mainMenuVM.deletedUser.observeAsState(false).value

   val interactionSource = remember { MutableInteractionSource() }
   Box(modifier = Modifier
      .fillMaxWidth(width)
      .alpha(alpha)
      .clickable(interactionSource = interactionSource, indication = null) { Onclick() }) {
      Card(
         modifier = Modifier.padding(top = 2.dp, start = 2.dp, end = 2.dp, bottom = 2.dp),
         //.fillMaxWidth(width),
         backgroundColor = Color(0xFFF3F3F3), shape = RoundedCornerShape(5.dp), elevation = 6.dp, border = BorderStroke(0.dp, Color(0x5520232B))
      ) {
         Column(
            modifier = Modifier
               .fillMaxWidth()
               .padding(top = (4 + correctPadding).dp, bottom = (4 + correctPadding).dp), horizontalAlignment = Alignment.CenterHorizontally
         ) {
            Text(text = header, fontSize = (16+correctFont).sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold,  maxLines = 1,
               style = TextStyle(textGeometricTransform = TextGeometricTransform(0.94f)))
            Text(text = value, fontSize = (15+correctFont).sp, textAlign = TextAlign.Start, fontWeight = FontWeight.Normal, maxLines = 1, overflow = TextOverflow.Ellipsis,
               color = if (!userDeleted) Color.Black else Color.Red)
         }
      }
   }
}

@Composable
fun Menu(navController: NavController, mainMenuVM: MainMenuViewModel, userDeleted:Boolean) {
   LazyColumn(
      modifier = Modifier
         .fillMaxSize()
         .fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
   ) {
      item { ItemMenu(itemName = stringResource(id = R.string.ListDays_menu), mainMenuVM = mainMenuVM, userDeleted) { navController.navigate(Screens.ListDays.route) } }
      item { ItemMenu(itemName = stringResource(id = R.string.AddDay_menu), mainMenuVM = mainMenuVM, userDeleted) { navController.navigate(Screens.AddDays.route) } }
      item { ItemMenu(itemName = stringResource(id = R.string.calendar_menu), mainMenuVM = mainMenuVM, userDeleted) { navController.navigate(Screens.Calendar.route) } }
      item { ItemMenu(itemName = stringResource(id = R.string.Settings_menu), mainMenuVM = mainMenuVM, userDeleted) { navController.navigate(Screens.Settings.route) } }
      item { ItemMenu(itemName = stringResource(id = R.string.Hints_menu), mainMenuVM = mainMenuVM, userDeleted) { navController.navigate(Screens.Hints.route) } }
      item { ItemMenu(itemName = stringResource(id = R.string.About_menu), mainMenuVM = mainMenuVM, userDeleted) { navController.navigate(Screens.About.route) } }
   }
}

@Composable
fun ItemMenu(itemName: String, mainMenuVM: MainMenuViewModel, userDeleted:Boolean, onClicked: () -> Unit) {
   val correctFont = mainMenuVM.correctFont.observeAsState().value ?: 0
   val correctPadding = mainMenuVM.correctPadding.observeAsState().value ?: 0

   val interactionSource = remember { MutableInteractionSource() }
   Card(
      modifier = Modifier
         .padding(top = 0.dp, start = 2.dp, end = 2.dp, bottom = (14 + correctPadding).dp)
         .clickable(interactionSource = interactionSource, indication = null) { if (!userDeleted) onClicked() },
      backgroundColor = if (userDeleted) Color(0xFFFFE8E8) else Color(0xFFF3F3F3),
      shape = RoundedCornerShape(15.dp),
      elevation = 8.dp,
      border = BorderStroke(1.dp, Color(0x5520232B))
   ) {
      Column(
         modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center
      ) {

         Text(
            text = itemName, fontSize = (22 + correctFont).sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 10.dp),
            color = if (userDeleted) Color.LightGray else Color(0xFF535353)
         )
      }
   }
}

private fun minuteToHours(minute: Int): WorkedTimeInHours {
   var hours = 0
   var _minute = minute
   while (_minute >= 60) {
      hours++
      _minute -= 60
   }
   return WorkedTimeInHours(hours, _minute)
}
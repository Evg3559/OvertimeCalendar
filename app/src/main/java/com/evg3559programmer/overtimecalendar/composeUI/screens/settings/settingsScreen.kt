package com.evg3559programmer.overtimecalendar.composeUI.screens.settings

import android.content.Context
import android.os.Bundle
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.navigation.NavController
import com.evg3559programmer.overtimecalendar.DataStore.AppSettings
import com.evg3559programmer.overtimecalendar.R
import com.evg3559programmer.overtimecalendar.composeUI.Screens
import com.evg3559programmer.overtimecalendar.composeUI.SnackbarHostMyScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.mainmenu.MainMenuViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.rewarded.RewardedAd
import kotlinx.coroutines.*
import java.io.File


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SettingsScreen(
   navController: NavController,
   settingVM: SettingsVM,
   mainMenuVM: MainMenuViewModel,
   rewardedAd: RewardedAd,
   dataStore: DataStore<AppSettings>
) {
   val contextResource = LocalContext.current.resources
   val context = LocalContext.current
   settingVM.UserName()
   val appSettings = dataStore.data.collectAsState(initial = AppSettings()).value
   val userDeleted = mainMenuVM.deletedUser.observeAsState(false).value
   val nameTextOld = settingVM.userName.observeAsState("Anonim").value
   val nameTextNew = remember { mutableStateOf("") }
   val stateButtonOK = remember { mutableStateOf(false) }
   val stateNameChanged = remember { mutableStateOf(false) }
   val scope = rememberCoroutineScope()
   val snackbarHostState = SnackbarHostState()        //снэкбар
   val alertDialogShow = remember { mutableStateOf(false) }
   val dialogTitle = remember { mutableStateOf("header")}
   val dialogText = remember { mutableStateOf("text")}
   val keyboardController = LocalSoftwareKeyboardController.current
   val focusManager = LocalFocusManager.current
   val dialogRun = remember { mutableStateOf("")}
   val errorConnecting = settingVM.errorConnecting.observeAsState().value
   val visibleAdvButton = settingVM.visibleAdvButton.observeAsState(false)
   val mFirebaseAnalytics = Firebase.analytics
   val prPoint = if (!userDeleted) settingVM.prPoint.observeAsState(0).value else 0
   if (!userDeleted) settingVM.getPrPoint()
   val MAX_PRPOINT = 15
   val textStyle13 = if (!userDeleted) TextStyle(fontSize = 13.sp, color = Color(0xFF4D4545)) else TextStyle(fontSize = 13.sp, color = Color(0xFFFD9397))
   val textStyle16 = if (!userDeleted) TextStyle(fontSize = 16.sp, color = Color(0xFF4D4545)) else TextStyle(fontSize = 16.sp, color = Color(0xFFFD9397))
   LaunchedEffect(Unit){
      val params = Bundle()
      params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MyArt");
      params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Settings Screen");
      Firebase.analytics.logEvent("MovementScreen", params)
   }
   LaunchedEffect(nameTextNew.value){
      if (!userDeleted) {
         if (nameTextNew.value.length > 3) {
            stateButtonOK.value = nameTextOld != nameTextNew.value
         } else {
            stateButtonOK.value = false
         }
      } else {
         stateButtonOK.value = false
      }
   }


   Column (modifier = Modifier.verticalScroll(rememberScrollState())) {
      Row(
         modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth(1f), verticalAlignment = Alignment.CenterVertically
      ) {
         Text(text = stringResource(id = R.string.nameOfUser), modifier = Modifier.padding(start = 1.dp, end = 10.dp), fontSize = 17.sp, maxLines = 1)
         TextField(
            value = if (!userDeleted) {
               if (stateNameChanged.value) nameTextNew.value else nameTextOld
            } else "Deleted",
            onValueChange = { stateNameChanged.value = true; nameTextNew.value = it },
            textStyle = TextStyle(fontSize = 17.sp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(textColor = if (!userDeleted) Color.Black else Color.Red, backgroundColor = Color.White),
            modifier = Modifier
               .padding(start = 5.dp, end = 5.dp)
               .weight(9f)
         )
         Card(backgroundColor = Color(0xFFF3F3F3),
            shape = RoundedCornerShape(1.dp),
            border = BorderStroke(0.dp, Color(if (stateButtonOK.value) 0xFF3DA012 else 0x55BBBCBE)),
            modifier = Modifier
               .size(width = 55.dp, height = 45.dp)
               .padding(top = 10.dp, end = 10.dp)
               .weight(2f)
               .clickable {
                  if (!userDeleted) {
                     if (stateButtonOK.value) {
                        scope.launch {
                           stateButtonOK.value = false
                           settingVM.setUserName(nameTextOld, nameTextNew.value)
                        }
                     }
                  }

                  focusManager.clearFocus()
               }) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
               Text(
                  text = "OK",
                  modifier = Modifier.padding(start = 5.dp, end = 5.dp),
                  color = if (stateButtonOK.value) Color.Black else Color.LightGray
               )
            }
         }
      }

      //премиум баллы
      Row(
         modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp), horizontalArrangement = Arrangement.Center
      ) {
         Text(text = stringResource(id = R.string.prPointsSector_setting), fontSize = 17.sp, color = Color(0xff7E494B))
      }
      Divider(modifier = Modifier.fillMaxWidth(), color = Color.Black, thickness = 1.dp)
      Row(
         modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
      ) {
         Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
            Text(text = stringResource(id = R.string.premuim_text_setting, prPoint), fontSize = 16.sp)
         }
         Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Card(backgroundColor = Color(0xFFF3F3F3),
               shape = RoundedCornerShape(1.dp),
               border = BorderStroke(0.dp, Color(0x55BBBCBE)),
               modifier = Modifier
                  .padding(top = 8.dp, end = 5.dp)
                  .clickable {
                     if (!userDeleted) {       //реклама
                        if (MAX_PRPOINT > prPoint) {
                           rewardedAd.loadAd(
                              AdRequest
                                 .Builder()
                                 .build()
                           )
                        } else {
                           scope.launch {
                              snackbarHostState.showSnackbar(
                                 message = contextResource.getString(R.string.enoughPremium_setting),
                                 actionLabel = "▼",
                                 duration = SnackbarDuration.Short
                              )
                           }
                        }
                     }
                  }) {
               Column() {
                  Text(
                     text = stringResource(id = R.string.adv_button_setting),
                     modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                     style = textStyle13,
                     maxLines = 1
                  )
               }
            }
         }
      }
      Text(
         text = stringResource(id = R.string.addPoints_text2_setting),
         fontSize = 12.sp,
         maxLines = 3,
         modifier = Modifier.padding(top = 5.dp, start = 12.dp)
      )

      val showInterfaceSettings = remember { mutableStateOf(false) }

      //Интерфейс
      Row(
         modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp), horizontalArrangement = Arrangement.Center
      ) {
         Text(
            text = "Интерфейс",
            fontSize = 17.sp,
            color = Color(0xff7E494B),
            modifier = Modifier.clickable { showInterfaceSettings.value = !showInterfaceSettings.value })
         Icon(if (showInterfaceSettings.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = "Показать настройки интерфейса",
            Modifier.clickable { showInterfaceSettings.value = !showInterfaceSettings.value })
      }
      Divider(modifier = Modifier.fillMaxWidth(), color = Color.Black, thickness = 1.dp)
      AnimatedVisibility(
         visible = showInterfaceSettings.value, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()
      ) {
         Column() {
            Row(     //плюсик в списках
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 0.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
               Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                  Text(text = stringResource(id = R.string.setPlusList), style = textStyle16)
               }
               Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                  Checkbox(checked = appSettings.fabShowInList, onCheckedChange = {
                     scope.launch {
                        if (!userDeleted) dataStore.updateData {
                           it.copy(fabShowInList = !appSettings.fabShowInList)
                        }
                     }
                  })
               }
            }
            Row(       //плюсик в календаре
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 0.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
               Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                  Text(text = stringResource(id = R.string.setPlusCalendar), style = textStyle16)
               }
               Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                  Checkbox(checked = appSettings.fabShowInCalendar, onCheckedChange = {
                     scope.launch {
                        if (!userDeleted) dataStore.updateData {
                           it.copy(fabShowInCalendar = !appSettings.fabShowInCalendar)
                        }
                     }
                  })
               }
            }

            Row(       //компактные списки
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 0.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
               Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                  Text(text = stringResource(id = R.string.setCompactList), style = textStyle16)
               }
               Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                  Checkbox(checked = appSettings.compatLists, onCheckedChange = {
                     scope.launch {
                        if (!userDeleted) dataStore.updateData {
                           it.copy(compatLists = !appSettings.compatLists)
                        }
                     }
                  })
               }
            }

            Row(     //экспериментальные функции
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 0.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
               Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                  Text(text = "Экспериментальные функции", style = textStyle16)
               }
               Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                  Checkbox(checked = appSettings.experimentalFun, onCheckedChange = {
                     scope.launch {
                        if (!userDeleted) dataStore.updateData {
                           it.copy(experimentalFun = !appSettings.experimentalFun)
                        }
                     }
                  })
               }
            }
//             Row(           // считать оплату
//               modifier = Modifier.fillMaxWidth().padding(top = 0.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
//            ) {
//               Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
//                  Text(text = "Считать оплату", style = textStyle16)
//               }
//               Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
//                  Checkbox(checked = appSettings.payment, onCheckedChange = {
//                     scope.launch {
//                        if (!userDeleted) dataStore.updateData {
//                           it.copy(payment = !appSettings.payment)
//                        }
//                     }
//                  })
//               }
//            }
//             Row(        //валюта счёта
//               modifier = Modifier.fillMaxWidth().padding(top = 0.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
//            ) {
//               Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
//                  Text(text = "Валюта", style = textStyle16)
//               }
//               Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
//                  TextField(
//                     value = appSettings.currency,
//                     onValueChange = { currency ->
//                        scope.launch {
//                           if (!userDeleted) if (appSettings.currency.length < 3) dataStore.updateData {
//                              it.copy(currency = currency)
//                           } else if (currency.length < appSettings.currency.length) dataStore.updateData {
//                              it.copy(currency = currency)
//                           }
//                        }
//                     },
//                     textStyle = TextStyle(fontSize = 18.sp, color = Color(0xFF4E4E4E)),
//                     isError = appSettings.currency.isEmpty(),
//                     singleLine = true,
//                     modifier = Modifier.padding(start = 5.dp, end = 5.dp).fillMaxWidth(0.7f)
//                  )
//               }
//            }
         }
      }


      //Сообщения об ошибках
      var presenceOfErrors = remember { Firebase.crashlytics.checkForUnsentReports().result }
      val showInterfaceErrorMessages = remember { mutableStateOf(false) }
      val sended = remember { mutableStateOf(false) }
      val deleted = remember { mutableStateOf(false) }

      Row(
         modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp), horizontalArrangement = Arrangement.Center
      ) {
         Text(text = stringResource(id = R.string.errorMessages_set), fontSize = 17.sp, color = Color(0xff7E494B), modifier = Modifier.clickable { showInterfaceErrorMessages.value = !showInterfaceErrorMessages.value })
         Icon(if (showInterfaceErrorMessages.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = "Показать настройки интерфейса",
         Modifier.clickable { showInterfaceErrorMessages.value = !showInterfaceErrorMessages.value })
      }
      Divider(modifier = Modifier.fillMaxWidth(), color = Color.Black, thickness = 1.dp)
      AnimatedVisibility(
         visible = showInterfaceErrorMessages.value, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()
      ) {
         Column() {
            Row(
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 10.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
               Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                  Text(text = stringResource(id = R.string.senderrorMessages_set), style = textStyle16)
               }
               Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                  Checkbox(checked = appSettings.sendErrorFirebase, onCheckedChange = {
                     scope.launch {
                        if (!userDeleted)  dataStore.updateData {
                                             it.copy(sendErrorFirebase = !appSettings.sendErrorFirebase)
                                                   }
                                           presenceOfErrors = Firebase.crashlytics.checkForUnsentReports().result

                     }
                  })
               }
            }

            if (!appSettings.sendErrorFirebase && !userDeleted) {
               if (presenceOfErrors) {
                  Row(
                     modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
                  ) {
                     Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                        Text(text = stringResource(id = R.string.accumulatederrors_set), fontSize = 16.sp)
                        Text(text = stringResource(id = R.string.sendReports_question), fontSize = 16.sp)
                     }
                     Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                        Card(backgroundColor = Color(0xFFF3F3F3), shape = RoundedCornerShape(1.dp), border = BorderStroke(0.dp, Color(0x55BBBCBE)), modifier = Modifier
                           .padding(top = 8.dp, end = 5.dp)
                           .clickable {
                              //send
                              if (!sended.value) Firebase.crashlytics.sendUnsentReports()
                              sended.value = true
                           }) {
                           Column() {
                              Text(
                                 text = if (!sended.value) stringResource(id = R.string.send_button) else stringResource(id = R.string.sending_button),
                                 modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                                 fontSize = 13.sp,
                                 maxLines = 1,
                                 color = Color.Black
                              )
                           }
                        }
                     }
                  }

                  Row(
                     modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
                  ) {
                     Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                        Text(text = stringResource(id = R.string.deleteRepors_question), fontSize = 16.sp)
                     }
                     Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                        Card(backgroundColor = Color(0xFFF3F3F3), shape = RoundedCornerShape(1.dp), border = BorderStroke(0.dp, Color(0x55BBBCBE)), modifier = Modifier
                           .padding(top = 8.dp, end = 5.dp)
                           .clickable {
                              //удалить
                              Firebase.crashlytics.deleteUnsentReports()
                              presenceOfErrors = Firebase.crashlytics.checkForUnsentReports().result
                              deleted.value = true
                           }) {
                           Column() {
                              Text(
                                 text = stringResource(id = R.string.Delete), modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp), fontSize = 13.sp, maxLines = 1, color = if (deleted.value) Color.Gray else Color.Black
                              )
                           }
                        }
                     }
                  }
               }
            }
         }
      }


      val showInterfaceService = remember { mutableStateOf(false) }

      //служебное
      Row(modifier = Modifier
         .fillMaxWidth()
         .padding(top = 20.dp), horizontalArrangement = Arrangement.Center
      ) {
         Text(text = stringResource(id = R.string.serviceSector_text_setting), fontSize = 17.sp, color = Color(0xff7E494B), modifier = Modifier.clickable { showInterfaceService.value = !showInterfaceService.value })
         Icon(if (showInterfaceService.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = "Показать настройки интерфейса",
            Modifier.clickable { showInterfaceService.value = !showInterfaceService.value })
      }
      Divider(modifier = Modifier.fillMaxWidth(), color = Color.Black, thickness = 1.dp)
      AnimatedVisibility(
         visible = showInterfaceService.value, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()
      ) {
         Column() {
            Row(modifier = Modifier
               .fillMaxWidth()
               .padding(top = 15.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
            ) {
               Column(modifier = Modifier.weight(2f), horizontalAlignment = Alignment.Start) {
                  Text(text = stringResource(id = R.string.deleteAllShifts_settings, 11), style = textStyle16)
               }
               Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                  val textTitle = stringResource(id = R.string.titledelete)
                  val textdial = stringResource(id = R.string.questionDeleteShifts)
                  Card(
                     backgroundColor = Color(0xFFF3F3F3), shape = RoundedCornerShape(1.dp), border = BorderStroke(0.dp, Color(0x55BBBCBE)), modifier = Modifier
                        .padding(top = 8.dp, end = 5.dp)
                        .combinedClickable(
                           onClick = {
                              if (!userDeleted) {
                                 scope.launch {
                                    snackbarHostState.showSnackbar(
                                       message = contextResource.getString(R.string.plsLongPress_settings),
                                       duration = SnackbarDuration.Short,
                                       actionLabel = "▼"
                                    )
                                 }
                              }
                           },
                           onLongClick = {
                              if (!userDeleted) {
                                 dialogRun.value = "Shifts"
                                 dialogText.value = textdial
                                 dialogTitle.value = textTitle
                                 alertDialogShow.value = true
                              }
                           },
                        )
                  ) {
                     Column() {
                        Text(
                           text = stringResource(id = R.string.Delete), modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp), style = textStyle13, maxLines = 1
                        )
                     }
                  }
               }
            }

            Row(modifier = Modifier
               .fillMaxWidth()
               .padding(top = 5.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically) {
               Column(modifier = Modifier.weight(2f), horizontalAlignment = Alignment.Start) {
                  Text(text = stringResource(id = R.string.cleanAllData_settings), style = textStyle16, maxLines = 3)
               }
               Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                  val message = stringResource(R.string.plsLongPress_settings)
                  val textTitle = stringResource(id = R.string.titledelete)
                  val textdial = stringResource(id = R.string.questionDeleteAllData)
                  Card( backgroundColor = Color(0xFFF3F3F3), shape = RoundedCornerShape(1.dp), border = BorderStroke(0.dp, Color(0x55BBBCBE)),
                     modifier = Modifier
                        .padding(top = 8.dp, end = 5.dp)
                        .wrapContentSize()
                        .combinedClickable(
                           onClick = {
                              if (!userDeleted) {
                                 scope.launch {
                                    snackbarHostState.showSnackbar(message = message, actionLabel = "▼", duration = SnackbarDuration.Short)
                                 }
                              }
                           },
                           onLongClick = {
                              if (!userDeleted) {
                                 dialogRun.value = "deleteUser"
                                 dialogText.value = textdial
                                 dialogTitle.value = textTitle
                                 alertDialogShow.value = true
                                 val params = Bundle()
                                 params.putString("UserDeleted", "Deleted")
                                 mFirebaseAnalytics.logEvent("eventDeleteUser", params)
                              }
                           },
                        )
                  ) {
                     Column(Modifier.wrapContentSize()) {
                        Text(
                           text = stringResource(id = R.string.Delete), modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                           style = textStyle13, overflow = TextOverflow.Visible, maxLines = 1
                        )
                     }
                  }
               }
            }
            val fileIS = remember {  mutableStateOf(false)  }
            LaunchedEffect(Unit){
               val file: File = context.getDatabasePath("mydatabase.db")
               if (file.exists()){
                  fileIS.value = true
               } else {
                  fileIS.value = false
               }
            }
            if (fileIS.value) {
               Row(     //восстановление бд базы данных
                  modifier = Modifier
                     .fillMaxWidth()
                     .padding(top = 10.dp, start = 10.dp, end = 10.dp), verticalAlignment = Alignment.CenterVertically
               ) {
                  Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
                     Text(text = "Вы обновились с очень старой", style = TextStyle(fontSize = 16.sp, color = Color(0xFFD80505)))
                     Text(text = "версии и всё пропало?", style = TextStyle(fontSize = 16.sp, color = Color(0xFFD80505)))
                  }
                  Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                     Card(backgroundColor = Color(0xFFF3F3F3), shape = RoundedCornerShape(1.dp), border = BorderStroke(0.dp, Color(0x55BBBCBE)), modifier = Modifier
                        .padding(top = 8.dp, end = 5.dp)
                        .clickable {
                           //войти в другую страницу
                           navController.navigate(Screens.Recovery.route)
                        }) {
                        Column() {
                           Text(
                              text = "Решить", modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp), fontSize = 13.sp, maxLines = 1, color = if (deleted.value) Color.Gray else Color.Black
                           )
                        }
                     }
                  }
               }
            }

         }
      }
      Text(text = stringResource(id = R.string.about_language), fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(start = 15.dp, top = 15.dp) )
   }

   if (alertDialogShow.value) {
      AlertDlgSet(onDismiss = {
         scope.launch {
            snackbarHostState.showSnackbar(
               message = contextResource.getString(if (dialogRun.value == "deleteUser") R.string.likes_settings else R.string.deletecancel), actionLabel = "▼", duration = SnackbarDuration.Short)}
         alertDialogShow.value = false },
         onAccept = {
            when (dialogRun.value){
               "deleteUser" -> {
                  scope.launch {
                     //File( getFilesDir(), "app-setting.json").delete()
                     dataStore.updateData {
                           AppSettings().defaultSetting()
                     }
                     settingVM.DeleteUser()
                     mainMenuVM.userDeleted(true)
                  }
               }
                   "Shifts" -> {
                  settingVM.DeleteShifts()}

               else -> {}
               }
            alertDialogShow.value = false },
            title = dialogTitle.value,
            text = dialogText.value
      )
   }
   SnackbarHostMyScreen(snackbarHostState)
   //if (showAdv.value) Reklama(settingVM)


}

@Composable
fun AlertDlgSet(onDismiss: () -> Unit, onAccept: () -> Unit, title: String, text: String) {
   AlertDialog(onDismissRequest = { onDismiss() },
      title = { Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
      text = { Text(text = text, fontSize = 15.sp) },
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

package com.evg3559programmer.overtimecalendar.composeUI.screens.ss

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.graphics.toColorInt
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evg3559programmer.overtimecalendar.BuildConfig
//import com.evg3559programmer.overtimecalendar.BuildConfig
import com.evg3559programmer.overtimecalendar.DataStore.AppSettingSerializer
import com.evg3559programmer.overtimecalendar.DataStore.AppSettings
import com.evg3559programmer.overtimecalendar.R
import com.evg3559programmer.overtimecalendar.composeUI.MyArt
import com.evg3559programmer.overtimecalendar.composeUI.dataStore
import com.evg3559programmer.overtimecalendar.di.WorkDay
import com.evg3559programmer.overtimecalendar.di.modules.ConfigureDB.ConfigurationEntity
import com.evg3559programmer.overtimecalendar.di.modules.UserDB.UserEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import kotlin.concurrent.schedule

val Context.dataStore by dataStore("app-setting.json", AppSettingSerializer)

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreen() : ComponentActivity() {

   @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
      val contextActivity = this
      FirebaseApp.initializeApp(this)

      setContent {
         val textLog = remember { mutableStateOf("") }
//         val registered = remember { mutableStateOf(false) }
         val splScrVM: SplScrVM = hiltViewModel()
         val context = LocalContext.current
//         val contextResource = context.resources
         val navController = rememberNavController()
         val routing = remember { mutableStateOf(SplashScreens.Load.route)  }
         Scaffold() {
            NavHost(navController = navController, startDestination = routing.value) {
               composable(SplashScreens.Load.route) { Load(textLog, dataStore, splScrVM, navController, contextActivity, routing) }
               composable(SplashScreens.FirstStart.route) { FirstStart(dataStore, splScrVM, navController, contextActivity, routing) }
               composable(SplashScreens.Loaded.route) { LoadNextActivity() }
            }


         }
      }
   }
}


sealed class SplashScreens(val route: String) {
   object Load : SplashScreens("splashscreen")
   object FirstStart : SplashScreens("firststart")
   object Loaded : SplashScreens("loaded")
}

@Composable
fun Load(textLog: MutableState<String>, dataStore: DataStore<AppSettings>, splScrVM: SplScrVM, navController: NavHostController, contextActivity: SplashScreen, routing: MutableState<String>) {
   val context = LocalContext.current
   val contextResource = context.resources
   val scope = rememberCoroutineScope()

   Column( modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally) {
      Image(painterResource(id = R.drawable.splashlogo), contentDescription = "Logo",
      modifier = Modifier.padding(top = 40.dp, bottom = 40.dp, start = 80.dp, end = 80.dp))
      Text(text = stringResource(id = R.string.Starting), style = TextStyle(color = Color.Black, fontSize = 18.sp))
      Text(text = textLog.value, style = TextStyle(color = Color.Black, fontSize = 12.sp))
   }

   LaunchedEffect(Unit) {
      textLog.value = "Стартуем"
      val appSettings = dataStore.data.cancellable().first()
      if (BuildConfig.DEBUG) {
         Firebase.crashlytics.setCrashlyticsCollectionEnabled(false)
      } else {
         Firebase.crashlytics.setCrashlyticsCollectionEnabled(appSettings.sendErrorFirebase)
      }

      if (splScrVM.getCountStarting() == 0) {
         textLog.value = contextResource.getString(R.string.fisrtStart)
         Handler(Looper.getMainLooper()).postDelayed(          //задержка выполнения
            {
               //navController.navigate(SplashScreens.FirstStart.route)
               routing.value = SplashScreens.FirstStart.route

            }, 3000
         )
      } else {
         textLog.value = "Загрузка..."
         Handler(Looper.getMainLooper()).postDelayed(          //задержка выполнения
            {
               textLog.value = "Пуск..."
               scope.launch {
                  splScrVM.countStartPlus()                    //увеличиваем счетчик

               }
               val intent = Intent(contextActivity, MyArt::class.java)
               Timer().schedule(0) {
                  startActivity(contextActivity, intent, null)
                  contextActivity.finish()
               }
            }, 1000)
      }


   }
}


@Composable
fun FirstStart(
   dataStore: DataStore<AppSettings>, splScrVM: SplScrVM, navController: NavHostController, contextActivity: SplashScreen, routing: MutableState<String>
) {
   val context = LocalContext.current
   val contextResource = context.resources
   val clickedReady = remember { mutableStateOf(false) }
   val nameUser = remember { mutableStateOf(contextResource.getString(R.string.anonimous)) }
   val scope = rememberCoroutineScope()
   val dateStart = LocalDate.now()
   val defaultConfig = ConfigurationEntity(user_default = nameUser.value, number_of_laucnhers = 1, language = "SYS", reserved = "0" )
   val exampleDay1 = WorkDay(_id = 0, day = dateStart.dayOfMonth, month = dateStart.monthValue, year = dateStart.year,
      worked = 600, comment = contextResource.getString(R.string.myFirstWorkedDay), color = "#FFFFFF")
   val exampleDay2 = WorkDay(_id = 0, day = dateStart.dayOfMonth.minus(1), month = dateStart.monthValue, year = dateStart.year,
      worked = 700, comment = contextResource.getString(R.string.mySecondWorkedDay), color = "#FFFFFF")


   Column(modifier = Modifier
      .fillMaxWidth(1f)
      .background(color = Color("#FDFDF1".toColorInt()))) {
      Text(text = stringResource(id = R.string.firstPreferences),
         modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 35.dp, start = 10.dp, end = 10.dp),
         style = TextStyle(color = Color(0xFF4E4E4E), fontSize = 30.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center))
      Text(text = stringResource(id = R.string.Edit_Your_name),
         modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 15.dp, start = 10.dp, end = 10.dp),
         style = TextStyle(color = Color(0xFF4E4E4E), fontSize = 20.sp, fontWeight = FontWeight.Normal, textAlign = TextAlign.Start))
      TextField(
         value =  nameUser.value,
         onValueChange = { if (nameUser.value.length < 20) nameUser.value = it },
         textStyle = TextStyle(fontSize = 18.sp, color = if (nameUser.value.length > 2) Color(0xFF4E4E4E) else Color(0xFFD31D1D)),
         singleLine = true,
         modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .fillMaxWidth(0.7f),
         keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences))

      Text(text = stringResource(id = R.string.hintUsername),
         modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 1.dp, start = 10.dp, end = 10.dp),
         style = TextStyle(color = Color(0xFF4E4E4E), fontSize = 14.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.Start))
      Text(text = stringResource(id = R.string.hint_aboutName),
         modifier = Modifier
            .fillMaxWidth(1f)
            .padding(top = 1.dp, start = 10.dp, end = 10.dp),
         style = TextStyle(color = Color(0xFF4E4E4E), fontSize = 14.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.Start))

      Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom ) {

         Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){

            Text(text = stringResource(id = R.string.okPrivacyPolicy),
               modifier = Modifier
                  .weight(2f)
                  .clickable {
                     val uris = Uri.parse("https://github.com/Evg3559/PrivacyPolicy/blob/main/README.md")
                     val intents = Intent(Intent.ACTION_VIEW, uris)
                     startActivity(context, intents, null)
                  },
               style = TextStyle(color = Color(0xFF4E4E4E), fontSize = 14.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.Start))

            Card(backgroundColor = Color(0xFFF3F3F3),
               shape = RoundedCornerShape(1.dp),
               border = BorderStroke(0.dp, Color(0xFFBBBCBE)),
               modifier = Modifier
                  .width(50.dp)
                  .weight(0.5f)
                  .clickable {
                     if (nameUser.value.length > 2) {
                        if (!clickedReady.value) {
                           scope.launch {
                              val appSettings = dataStore.data.cancellable().first()

                              clickedReady.value = true
                              splScrVM.insertConfiguration(defaultConfig)
                              splScrVM.insertUser(UserEntity(name_user = nameUser.value, points = 0, cost_of_hours = 1, prime = 3, migrated = true))
                              splScrVM.insertDay(exampleDay1)
                              splScrVM.insertDay(exampleDay2)
                              dataStore.updateData {
                                 it.copy()
                              }
                              Handler(Looper.getMainLooper()).postDelayed(          //задержка выполнения
                                 {
                                   // navController.navigate(SplashScreens.Load.route)
                                    routing.value = SplashScreens.Load.route

                                 }, 500
                              )
                           }
                        }
                     }
                  }) {
               Column() {
                  Text(
                     text = stringResource(id = R.string.ready),
                     modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
                     maxLines = 1,
                     style = TextStyle(color = if (nameUser.value.length > 2) Color(0xFF4E4E4E) else Color(0xFFCAC6C6), fontSize = 14.sp, fontWeight = FontWeight.Normal),
                  )

               }

            }
         }
      }





   }


}

@Composable
fun LoadNextActivity(){

}

package com.evg3559programmer.overtimecalendar.composeUI

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.datastore.dataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.evg3559programmer.overtimecalendar.BuildConfig
import com.evg3559programmer.overtimecalendar.DataStore.AppSettingSerializer
import com.evg3559programmer.overtimecalendar.DataStore.AppSettings
import com.evg3559programmer.overtimecalendar.R
import com.evg3559programmer.overtimecalendar.composeUI.screens.about.AboutScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.about.AboutVM
import com.evg3559programmer.overtimecalendar.composeUI.screens.addEdit.AddEditScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.calendarscreen.CalendarScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.export.ExportScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.hints.HintsScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.listdays.ListDaysScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.listdays.ListDaysViewModelCompose
import com.evg3559programmer.overtimecalendar.composeUI.screens.mainmenu.MainMenuScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.mainmenu.MainMenuViewModel
import com.evg3559programmer.overtimecalendar.composeUI.screens.recoverydb.RecoveryDB
import com.evg3559programmer.overtimecalendar.composeUI.screens.settings.SettingsScreen
import com.evg3559programmer.overtimecalendar.composeUI.screens.settings.SettingsVM
import com.evg3559programmer.overtimecalendar.di.WorkDay
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.*
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import dagger.hilt.android.AndroidEntryPoint

val Context.dataStore by dataStore("app-setting.json", AppSettingSerializer)

@AndroidEntryPoint
class MyArt : ComponentActivity() {
   private var rewardedAd: RewardedAd? = null         ///яндех

   @SuppressLint("UnusedMaterialScaffoldPaddingParameter")        //https://stackoverflow.com/questions/72084865/content-padding-parameter-it-is-not-used

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
      MobileAds.initialize(this, InitializationListener() {
         //if (BuildConfig.DEBUG) Log.d("MyTag", "yandex SDK initialized")
      })

      setContent {
         val appSettings = dataStore.data.collectAsState(initial = AppSettings()).value
         val daysVM: ListDaysViewModelCompose = hiltViewModel()
         val mainMenuVM: MainMenuViewModel = hiltViewModel()
         val settingVM: SettingsVM = hiltViewModel()
         val aboutVM: AboutVM = hiltViewModel()

         // val addDayPackedVM: AddDayVM = hiltViewModel()
         val rewardedAdEventListener = RewardedYandexEventList(settingVM)
         rewardedAd = RewardedAd(this)
         val AD_UNIT_ID_YA = if (BuildConfig.DEBUG) "R-M-DEMO-rewarded-client-side-rtb" else "R-M-1586362-3"    //R-M-DEMO-rewarded-client-side-rtb  R-M-1586362-3
         rewardedAd?.setAdUnitId(AD_UNIT_ID_YA)
         rewardedAd?.setRewardedAdEventListener(rewardedAdEventListener)
         val navController = rememberNavController()
         val items=listOf(Screens.MainMenu, Screens.ListDays, Screens.Calendar, Screens.Settings, Screens.About, Screens.Hints, Screens.AddDays, Screens.Export)
         Scaffold(
               floatingActionButton = {
                  val navBackStackEntry by navController.currentBackStackEntryAsState()
                  val currentDestination = navBackStackEntry?.destination
                  items.forEach { value ->
                     if (currentDestination?.hierarchy?.any { it.route == value.route } == true) {
                        when (value.route){
                           Screens.ListDays.route -> { if (appSettings.fabShowInList ){ FabShow(navController)} }
                           Screens.Calendar.route -> { if (appSettings.fabShowInCalendar ){ FabShow(navController)} }
                        }

                     }
                  }
               },
               isFloatingActionButtonDocked = false,
               topBar = { MyTopBar(navController) }, bottomBar = { MyAdsBar() },
               //floatingActionButton = { FloatingActionButt(mainMenuVM) }
            ) {


            NavHost(navController = navController, startDestination = Screens.MainMenu.route) {
                  composable(Screens.MainMenu.route) { MainMenuScreen(navController = navController, mainMenuVM) }
                  composable(Screens.Calendar.route) { CalendarScreen(navController = navController, mainMenuVM) }
                  composable(Screens.About.route) { AboutScreen(navController = navController, aboutVM) }
                  composable(Screens.ListDays.route) { ListDaysScreen(navController = navController, daysVM, mainMenuVM, dataStore) }
                  composable(Screens.Hints.route) { HintsScreen(navController = navController) }
                  composable(Screens.Settings.route) { SettingsScreen(navController = navController, settingVM, mainMenuVM, rewardedAd!!, dataStore) }
                  composable(Screens.AddDays.route) { AddEditScreen(navController = navController, mainMenuVM, dataStore) }
                  composable(Screens.Recovery.route) { RecoveryDB(navController = navController,dataStore) }
                  composable(Screens.Export.route) { ExportScreen(Screens.Export.list) }
               }
            }
      }
   }
   //можно попробовать всесь rewarded запихнуть во фрагмент, фрагмент в переменную и уже функции вытаскивать из переменной в компоузе
   private inner class RewardedYandexEventList(val settingVM: SettingsVM) : RewardedAdEventListener {

      override fun onAdLoaded() {
        // Logger.debug("onAdLoaded")
         rewardedAd?.show()
         settingVM.changeVisibleAdvButton(true)
         //rewardedAd?.show()
         //binding.showReklSettings.isEnabled = true                //кнопка
      }

      override fun onRewarded(reward: Reward) {              //засчитано
         //val message = "onRewarded, amount = ${reward.amount}, type = ${reward.type}"
         settingVM.changeVisibleAdvButton(true)                              //кнопка
         //Logger.debug("Просмотрено и получено")
         settingVM.writePrPoint(reward.amount)
         //   prPoint += reward.amount                                    //прибавляем
         //   launch { dbUserViewModel.setPrPoints(prPoint) }             //записываем в базу
         //   settingsViewModel.changePrPoint(prPoint)                    //изменяем в интерфейсе
         //showSnackBar(getString(R.string.addedPrPointString_setting, reward.amount))     //"Просмотрено и получено ${reward.amount} баллов"

      }

      override fun onAdFailedToLoad(adRequestError: AdRequestError) {
        // val message = "onAdFailedToLoad, error = ${adRequestError.description}"
         //Logger.debug("Не удалось загрузить рекламу $message")
         settingVM.changeVisibleAdvButton(false)                        //кнопка
         settingVM.errorConnecting(false)
         //binding.showReklSettings.isEnabled = false
         //showSnackBar(getString(R.string.errorLoadAds_setting, adRequestError.description))   //"Не удалось загрузить рекламу: ${adRequestError.description}"

      }

      override fun onImpression(impressionData: ImpressionData?) {
         //Logger.debug("onImpression")
         //showSnackBar("impressionData - $impressionData")   засчитан рекламный показ для баннеров
      }

      override fun onAdShown() {
         // Logger.debug("onAdShown")
         //Log.d("MyTag","onAdShown")
      }

      override fun onAdDismissed() {
         // Logger.debug("onAdDismissed")
         //Log.d("MyTag","onAdDismissed")
      }

      override fun onAdClicked() {
         val params = Bundle()
         params.putString("ad_click", "clicked")
         Firebase.analytics.logEvent("ad_click", params)
         //Logger.debug( "onAdClicked")    //клик по рекламному баннеру
         //Log.d("MyTag","onAdClicked")
      }

      override fun onLeftApplication() {
         // Logger.debug("onLeftApplication")
         //Log.d("MyTag","onLeftApplication")
      }

      override fun onReturnedToApplication() {
         // Logger.debug("onReturnedToApplication")
         //Log.d("MyTag","onReturnedToApplication")
      }
   }
}

//Список всех главных экранов
sealed class Screens(val route: String, val StringId: Int, var list: List<WorkDay> = emptyList()) {
   object MainMenu : Screens("mainmenu", R.string.MainMenu_menu)
   object ListDays : Screens("listdays", R.string.ListDays_menu)
   object Calendar : Screens("calendar", R.string.calendar_menu)
   object Settings : Screens("settings", R.string.Settings_menu)
   object About : Screens("about", R.string.About_menu)
   object Hints : Screens("hints", R.string.Hints_menu)
   object AddDays : Screens("adddays", R.string.Editing_menu)
   object Recovery : Screens("recoverydb", R.string.RecoveryDB)
   object Export : Screens("export", R.string.Export_menu)
}

@Composable
fun MyTopBar(navController: NavController, adsViewModel: AdsViewModel = viewModel()) {
   val navBackStackEntry by navController.currentBackStackEntryAsState()
   val items = listOf(Screens.MainMenu, Screens.ListDays, Screens.Calendar, Screens.Settings, Screens.About, Screens.Hints, Screens.AddDays, Screens.Recovery, Screens.Export)
   val currentDestination = navBackStackEntry?.destination
   var textTopBar by remember { mutableStateOf("")}

   //сравниваем текущий стек с существующими экранами и даем название страницы в топбаре
   items.forEach { value ->
      if (currentDestination?.hierarchy?.any { it.route == value.route } == true) {
         textTopBar = stringResource(id = value.StringId)
         //если открыт экран главного меню показать крестик закрытия рекламы
         if (value.StringId != Screens.MainMenu.StringId) {
            adsViewModel.setVisibleCross(true)
         } else {
            adsViewModel.setVisibleCross(false)
            adsViewModel.setVisibleBar(true)
            adsViewModel.setVisibleAds(View.VISIBLE)
         }
      }
   }

   Column(
      modifier = Modifier
         .fillMaxWidth()
         .height(45.dp)
         .background(Color(0xFF03A9F4)),
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.Center
   ) {
      Text(
         text = textTopBar.toString(),          //из вьюмодели или навКонтроллера
         modifier = Modifier.padding(start = 15.dp), color = Color.Black, fontSize = 20.sp, textAlign = TextAlign.Start,
         fontWeight = FontWeight.Bold, maxLines = 1
      )
 // Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
//         if (textTopBar == stringResource(id = R.string.ListDays_menu)) IconButton(modifier = Modifier.size(30.dp), interactionSource = remember { MutableInteractionSource() },
//            content = {Icon( painterResource(id = R.drawable.ic_fileexport), contentDescription = "Export")},
//            onClick = {  })
//      }

   }
}

//переключение на другие экраны закрыть рекламу через две секунды и по возвращении показать рекламу вновь.
@Composable
fun MyAdsBar(adsViewModel: AdsViewModel = viewModel()) {
   val AD_UNIT_ID = if (BuildConfig.DEBUG) "R-M-DEMO-320x50" else "R-M-1586362-1"   //R-M-1586362-1   R-M-DEMO-320x50
   val visibleAds = adsViewModel.visibleAds.observeAsState(View.VISIBLE ).value        //показывать скрывать только BannerAdView на всякий случай для корректного засчитывания рекламы
   val visibleBar = adsViewModel.visibleBar.observeAsState().value ?: true                //показывать скрывать весь бар полностью
   val visibleCross = adsViewModel.visibleCross.observeAsState().value ?: false           //показывать скрывать крестик закрытия
   val loadedAds = adsViewModel.loadedAds.observeAsState().value ?: false           //подтверждение загрузки рекламы
   val bannerAdEventListener = BannerAdYandexAdsEventListener(adsViewModel)
   val radius = 40f

   if (visibleBar) {
      Box(
         modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter
      ) {
         Column() {
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
               if (visibleCross) {
                  // if (true) {
                  Box(
                     modifier = Modifier
                        .size(25.dp)
                        .padding(end = 0.dp, top = 0.dp)
                  ) {
                     val animateFloat = remember { Animatable(1f) }

                     if (loadedAds) {
                        LaunchedEffect(animateFloat) {
                           animateFloat.animateTo(
                              targetValue = 0f, animationSpec = tween(durationMillis = 12000, easing = LinearEasing)
                           ).let {
                              adsViewModel.setVisibleAds(View.INVISIBLE)
                              adsViewModel.setVisibleBar(!visibleBar)
                              adsViewModel.setLoadedAds(false)
                           }
                        }
                     }
                     //переместить и сделать по нормальному, зона клика находится не на крестике
                     Canvas(modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                           if (adsViewModel.loadedAds.value!! or adsViewModel.errorAds.value!!) {
                              adsViewModel.setVisibleAds(View.INVISIBLE)
                              adsViewModel.setVisibleBar(!visibleBar)
                           }
                        }, onDraw = {
                        //корректировка Х и Y
                        val xx = 0f
                        val yy = 10f
                        drawArc(
                           color = Color.Red,
                           startAngle = 270f,
                           sweepAngle = 360f * animateFloat.value,
                           useCenter = false,
                           topLeft = Offset(0f + xx, 0f + yy),
                           size = Size(radius * 1, radius * 1),
                           style = Stroke(4f)
                        )
                        drawLine(
                           color = Color.Black,
                           start = Offset(10f + xx, 10f + yy),
                           end = Offset(30f + xx, 30f + yy),
                           strokeWidth = 2f
                        )
                        drawLine(
                           color = Color.Black,
                           start = Offset(30f + xx, 10f + yy),
                           end = Offset(10f + xx, 30f + yy),
                           strokeWidth = 2f
                        )
                     })
                  }
               }
            }
            Row(horizontalArrangement = Arrangement.Center,modifier = Modifier.fillMaxWidth()) {
               AndroidView(factory = { context ->
                  BannerAdView(context).apply {
                     setAdUnitId(AD_UNIT_ID)
                     setAdSize(AdSize.flexibleSize(320, 50))
                     setBannerAdEventListener(bannerAdEventListener)
                     loadAd(AdRequest.Builder().build())
                     visibility = visibleAds
                  }
               }, update = { view ->
                  view.visibility = visibleAds
               },
                modifier = Modifier.height(55.dp))
            }
         }
      }
   }

}

@Composable
fun FabShow(navController: NavHostController) {
      FloatingActionButton(onClick = {
         val params = Bundle()
         params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MyArt")
         params.putString("Action", "Add days screen from PLUS")
         Firebase.analytics.logEvent("Action", params)
         navController.navigate(Screens.AddDays.route)
      }, elevation = FloatingActionButtonDefaults.elevation(4.dp),
      modifier = Modifier.size(40.dp)) {
         Icon(Icons.Filled.Add, contentDescription = "Добавить",
         modifier = Modifier.size(25.dp))
      }
}

private class BannerAdYandexAdsEventListener(val adsViewModel: AdsViewModel) : BannerAdEventListener {

   override fun onAdLoaded() {
      adsViewModel.setVisibleAds(View.VISIBLE)
      adsViewModel.setVisibleBar(true)
      adsViewModel.setLoadedAds(false)
     // Log.d("MyTag", "Реклама загрузилась")
   }

   override fun onAdFailedToLoad(adRequestError: AdRequestError) {
      adsViewModel.setVisibleAds(View.INVISIBLE)
      adsViewModel.setVisibleBar(false)
      adsViewModel.setLoadedAds(true)     //на случай, что б бар закрылся, если не сработает код выше, что равно вероятности 0.000001%
      // Logger.error(adRequestError.description)
      Log.d("MyTag", "Реклама не загрузилась $adRequestError")
   }

   override fun onImpression(impressionData: ImpressionData?) {
      // Logger.debug("onImpression")
      adsViewModel.setLoadedAds(true)
      //Log.d("MyTag", "Реклама загружается")
   }

   override fun onAdClicked() {
      //Logger.debug( "onAdClicked")
   }

   override fun onLeftApplication() {
      // Logger.debug( "onLeftApplication")
   }

   override fun onReturnedToApplication() {
      // Logger.debug( "onReturnedToApplication")
   }
}

package com.evg3559programmer.overtimecalendar.composeUI.screens.about

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import com.evg3559programmer.overtimecalendar.BuildConfig
import com.evg3559programmer.overtimecalendar.R
import com.evg3559programmer.overtimecalendar.composeUI.Screens
import com.evg3559programmer.overtimecalendar.di.WorkDay
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase


@Composable
fun AboutScreen (navController: NavController, aboutVM:AboutVM) {
   val context = LocalContext.current
   val countStarting = aboutVM.countStarting.observeAsState(0).value
   aboutVM.getCountStarting()
   LaunchedEffect(Unit){
      val params = Bundle()
      params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MyArt");
      params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "About Screen");
      Firebase.analytics.logEvent("MovementScreen", params)
   }
               val list = listOf<WorkDay>(
               WorkDay(1,2023,1,12, 290, "крепость", "#FFFFFF"),
               WorkDay(2,2023,1,15, 360, "Ладно", "#FFFFFF"),
               WorkDay(3,2023,1,12, 290, "крепость", "#FFFFFF"),
               WorkDay(4,2023,1,15, 360, "Крапивница", "#FFFFFF"),
               WorkDay(5,2023,1,12, 290, "крепость не согласная с оставшимися", "#FFFFFF"),
               WorkDay(6,2023,1,15, 360, "Ладно", "#FFFFFF"),
               WorkDay(7,2023,1,12, 290, "крепость", "#FFFFFF"),
               WorkDay(8,2023,1,12, 290, "крепость", "#FFFFFF"),
               WorkDay(9,2023,1,15, 360, "Ладно", "#FFFFFF"),
               WorkDay(10,2023,1,15, 360, "Ладно", "#FFFFFF"),
               WorkDay(11,2023,1,12, 290, "крепость", "#FFFFFF"),
               WorkDay(12,2023,1,15, 360, "Ладно", "#FFFFFF"),
               WorkDay(13,2023,1,12, 290, "крепость", "#FFFFFF"),
               WorkDay(14,2023,1,15, 360, "Ладно", "#FFFFFF"),
               WorkDay(15,2023,1,12, 290, "крепость", "#FFFFFF"),
               WorkDay(16,2023,1,15, 360, "Ладно", "#FFFFFF"),
               WorkDay(17,2023,1,117, 140, "пуля", "#FFFFFF") )

   LazyColumn(modifier = Modifier.padding(top = 0.dp, bottom = 0.dp, start = 18.dp, end = 18.dp), horizontalAlignment = Alignment.CenterHorizontally){
      item {  Column(modifier = Modifier
         .fillMaxWidth(1f)
         .padding(25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
         Image(painterResource(id = R.drawable.logo2021), contentDescription = "Logo About Page", modifier = Modifier.fillMaxWidth(0.5f)
            .clickable {},
            contentScale = ContentScale.FillWidth )
      } }
      item { Text(text = stringResource(id = R.string.abouttext1, BuildConfig.VERSION_NAME), fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 0.dp, end = 0.dp)) }
      item { Text(text = stringResource(id = R.string.number_of_launches, countStarting), fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 0.dp, end = 0.dp)) }
      item { Text(text = stringResource(id = R.string.abouttext2), fontSize = 16.sp, textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 0.dp, end = 0.dp).clickable {
         val uris = Uri.parse("https://vk.com/kolobok_programmer")
         val intents = Intent(Intent.ACTION_VIEW, uris)
         startActivity(context, intents, null)
      }) }
      item { Text(text = stringResource(id = R.string.abouttext4), fontSize = 16.sp, textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 0.dp, end = 0.dp).clickable {
         val uris = Uri.parse("https://kolobok-prog.my1.ru/")
         val intents = Intent(Intent.ACTION_VIEW, uris)
         startActivity(context, intents, null)
      }) }
      item { Text(text = stringResource(id = R.string.abouttext5), fontSize = 16.sp, textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 0.dp, end = 0.dp).clickable {
         val uris = Uri.parse("https://play.google.com/store/apps/details?id=com.evg3559programmer.overtimecalendar")
         val intents = Intent(Intent.ACTION_VIEW, uris)
         startActivity(context, intents, null)     }) }
      item { Text(text = stringResource(id = R.string.abouttext3), fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 0.dp, end = 0.dp)) }
      item { Text(text = stringResource(id = R.string.ProlicyPrivacyText), fontSize = 17.sp, textDecoration = TextDecoration.Underline, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 0.dp, end = 0.dp).clickable {
         val uris = Uri.parse("https://github.com/Evg3559/PrivacyPolicy/blob/main/README.md")
         val intents = Intent(Intent.ACTION_VIEW, uris)
         startActivity(context, intents, null)
      }) }
   }
}
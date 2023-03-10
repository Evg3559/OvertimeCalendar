package com.evg3559programmer.overtimecalendar.composeUI.screens.hints

import android.os.Bundle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.evg3559programmer.overtimecalendar.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun HintsScreen(navController: NavController) {

   LaunchedEffect(Unit){
      val params = Bundle()
      params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "MyArt");
      params.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Hints");
      Firebase.analytics.logEvent("MovementScreen", params)
   }
   LazyColumn() {
      item { cart(stringResource(id = R.string.hint_1), R.drawable.screenshot_1) }
      item { cart(stringResource(id = R.string.hint_3), R.drawable.screenshot_2) }
      item { cart(stringResource(id = R.string.hint_2))}
      item { cart(stringResource(id = R.string.hint_5), R.drawable.screenshot_5)}
      //item { cart(stringResource(id = R.string.hint_5), R.drawable.hint_5)}
      //item { cart(stringResource(id = R.string.hint_3))}
      //item { cart(stringResource(id = R.string.hint_4), R.drawable.hint4)}
   }
}


@Composable
fun cart(text: String, imgID: Int = -1) {
   Card(modifier = Modifier.padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 5.dp),
   backgroundColor = Color(0xFFFDFFEB),
      shape = RoundedCornerShape(10.dp),
      elevation = 10.dp,
      border = BorderStroke(1.dp, Color(0x5520232B))
   ) {
      Column(modifier = Modifier
         .fillMaxWidth()
         .padding(top = 10.dp, bottom = 10.dp, start = 5.dp, end = 5.dp),
      horizontalAlignment = Alignment.CenterHorizontally) {
         Text(text = text,
         textAlign = TextAlign.Center,
         fontSize = 20.sp)

         if (imgID != -1) {
            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
               Image(
               painterResource(id = imgID), contentDescription = "Image", modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
               contentScale = ContentScale.FillWidth
            )
         }
         }
      }
   }
}
package com.evg3559programmer.overtimecalendar.composeUI

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SnackbarHostMyScreen(snackbarHostState : SnackbarHostState){
   //val snackbarhostref = FocusRequester.createRefs()
   Column(modifier = Modifier.fillMaxSize(1f), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
      SnackbarHost(
         modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 0.dp, bottom = 15.dp),
         hostState = snackbarHostState,
         snackbar = {
            Snackbar (
               backgroundColor = Color(0xFFDCDDDD),
               action = {
                  Text(
                     text = snackbarHostState.currentSnackbarData?.actionLabel?:"",
                     modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                           snackbarHostState.currentSnackbarData?.dismiss()
                        },
                     style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE00000)
                     )
                  )
               }
            ){
               Text(text = snackbarHostState.currentSnackbarData?.message?:"",
                  color = Color.Black,
                  style = TextStyle(fontWeight =  FontWeight.Bold),
                  textAlign = TextAlign.Start)
            }
         },

         )
   }
}
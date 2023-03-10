package com.evg3559programmer.overtimecalendar.composeUI.screens.recoverydb

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.evg3559programmer.overtimecalendar.composeUI.MainRepository
import com.evg3559programmer.overtimecalendar.di.WorkDay
import dagger.hilt.android.lifecycle.HiltViewModel
import models.Daywork
import models.MyApp
import javax.inject.Inject

@HiltViewModel
class recoverydbVM @Inject constructor(
      private val mainRepository: MainRepository
) :ViewModel() {

   suspend fun addDay(d: WorkDay){
      mainRepository.SaveDay(d)
   }



}
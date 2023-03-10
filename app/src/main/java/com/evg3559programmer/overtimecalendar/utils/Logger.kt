package com.evg3559programmer.overtimecalendar.utils

import android.app.Application
import android.util.Log
import android.widget.Toast

object Logger {
   private const val TAG = "MyReklTag"
   private lateinit var application: Application

   fun initialize(application: Application) {
      this.application = application
   }

   fun debug(message: String) {
      Log.d(TAG, message)
   }

   fun error(message: String) {
      Log.e(TAG, message)
      //Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
   }
}
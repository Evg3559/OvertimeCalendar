package com.evg3559programmer.overtimecalendar.DataStore

import androidx.datastore.core.Serializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception

object AppSettingSerializer: Serializer<AppSettings> {
   override val defaultValue: AppSettings
      get() = AppSettings()

   override suspend fun readFrom(input: InputStream): AppSettings {
      return try {
         Json.decodeFromString(
            deserializer = AppSettings.serializer(),
            string = input.readBytes().decodeToString()
         )
      } catch (e: Exception){
         e.printStackTrace()
         defaultValue
      }
   }

   override suspend fun writeTo(t: AppSettings, output: OutputStream) {
      output.write(
         Json.encodeToString(
            serializer = AppSettings.serializer(),
            value = t
         ).encodeToByteArray()
      )
   }
}
package com.evg3559programmer.overtimecalendar.DataStore

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
   val language: Language = Language.DEFAULT,
   val fabShowInList: Boolean = false,          //показывать фаб в списках
   val fabShowInCalendar: Boolean = false,            //показывать фаб в календаре
   val payment: Boolean = false,                               //Считать Оплату?
   val costDay: Int = 0,                               //стоимость работы часа
   val cashBonus1: Int = 0,                               //стоимость работы в месяц (оклад, премия)
   val cashBonus2: Int = 0,                               //стоимость работы в месяц (оклад, премия)
   val currency: String = "₽",                                      //валюта оплаты
   val experimentalFun: Boolean = false,                                      //валюта оплаты
   val sendErrorFirebase: Boolean = true,                      //отправлять сбои программы в firebase
   val darkTheme: ThemeApp = ThemeApp.DEFAULT,                     //тема приложения
   val hintComment: Boolean = true,                                   //подсказка комментариев
   val hintHours: Boolean = true,                               //подсказка отработанного времени
   val compatLists: Boolean = false                               //компактные списки
){
   fun defaultSetting (): AppSettings{
      return AppSettings()
   }
}

enum class Language {
   DEFAULT, ENGLISH, RUSSIAN, UKRAINE
}
enum class ThemeApp {
   DEFAULT, LIGHT, DARK
}


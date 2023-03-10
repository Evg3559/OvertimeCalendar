package com.evg3559programmer.overtimecalendar.composeUI.screens.fragments

import android.content.Context
import android.content.res.Resources
import com.evg3559programmer.overtimecalendar.R
import com.evg3559programmer.overtimecalendar.di.WorkedTimeInHours
import java.time.DayOfWeek
import java.time.LocalDate

fun minuteToHours(minute: Int): WorkedTimeInHours {
    var hours = 0
    var _minute = minute
    while (_minute >= 60) {
        hours++
        _minute -= 60
    }
    return WorkedTimeInHours(hours, _minute)
}

// Понедельник, вторник -> ПН ВТ
fun weekdayToWD(str: DayOfWeek, context: Context):String {
    return context.resources.getStringArray(R.array.weeks_array)[str.value - 1].toString()
}

//03.04.2021 -> апрель
//03.05.2021 -> май
fun dateToMonthStr(date: LocalDate, context: Context): String {
    return "${context.resources.getStringArray(R.array.material_calendar_months_array)[date.monthValue - 1].toString()}'${shotyear(date.year.toString())}"
}

//2021 -> 21
fun shotyear(y: String): String {
    return if (y.length >2) "${y[2]}${y[3]}" else "00"
}

///склонение смен смены смена
fun declensionOfWords(shifts: Int, resContx: Resources): String{
    var str = ""

    when (shifts) {
        1, 21, 31, -> str = resContx.getString(R.string.header_shifts1)
        2, 3, 4, 22, 23, 24, 32, 33, 34 -> str = resContx.getString(R.string.header_shifts2)
        else -> str = resContx.getString(R.string.header_shifts)
    }
    return "$shifts $str "
}

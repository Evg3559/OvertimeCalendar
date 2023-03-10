package models


data class Daywork(
    val year: Int, val month: Int, val day: Int,
    val worked:Int, val comments: String, val IDsql:Int, val color: String){
    val type: Int = 1

    //fun getID(): Int

//сделать worked double с одним знаком после запятой

    //типы 1 - обычный
    //типы 2 - рекламный
    //типы 3 - пустой

    }


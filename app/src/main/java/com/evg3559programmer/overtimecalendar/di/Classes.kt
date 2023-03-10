package com.evg3559programmer.overtimecalendar.di

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class WorkDay (
   var _id: Int,
   var year: Int,
   var month: Int,
   var day: Int,
   var worked:Int,  //в мин
   var comment: String,
   var color: String
        ){}


data class RowColorShifts(
   var color:Color,
   var shifts:Int,
   var time: Int
)

data class UserApp(
   var _id:Int,
   var name_user:String,
   var points:Int,
   var cost_of_hours:Int,
   var prime:Int,
   var migrated:Boolean
)


data class AppConfig(
   var _id:Long = 0,
   var user_default:String,
   var number_of_laucnher:Int,
   var language: String,
   var reserved:String
)

//для отображения перерасчета из минут в часы
data class WorkedTimeInHours(
   val hours:Int,
   val minutes:Int
)

//для статистики в карточках в списках дней
data class statistickaMonth(
   val minutes: Int,
   val numberShifts: Int
)

//States для listDaysScreen  и  экрана редактирования
sealed class StateEditing {
   object None: StateEditing()                    //для старта - "выберите что вводить"
   object ChooseDate: StateEditing()             //если пользователь выбирает дату
   object ChooseWorked: StateEditing()               //если пользователь вводит отработанное время
   object ChooseComment: StateEditing()             //когда пользователь решил набрать комментарий
   object ChooseColor: StateEditing()               //когда пользователь решил выбрать цвет ячейки
   object SuccessSave: StateEditing()                                      //успешно сохранено
   data class Error(val error: String): StateEditing()                     //неуспешнаая операция
}


//states для диалогового окна
sealed class StateDialog{
   object None:StateDialog()
   data class ShowDelete(val  massage: String, val IDday: Int):StateDialog()    //вопрос об удалении дня
   data class ShowDeleteList(val massage: String, val IDsdays: List<WorkDay>):StateDialog()    //вопрос об удалении дня

}

//дата класс итоговых смен
data class Shifts(
   val date: LocalDate,
   val worked: Boolean
)

data class workDays(
   val day: Int,
   val month: Int,
   val Year: Int
)


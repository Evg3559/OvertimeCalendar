package models

import androidx.lifecycle.LiveData

class MyApp  {
    companion object{
        var quantity: Int = 4  ///количество записей для показа рекламы
        var emptyCell = "_#empty21735_"
        var reklCell = "_#reklama3314_"
        var date_starting:DateModel= (DateModel(0,0,0))
        var table_name_user = ""
        var nameUser = "Anonimous"
        var lookMonth = 1
        var lookYear = 2021
        var MyLang = "ru"
        var IAMDELETE = false
    }

}
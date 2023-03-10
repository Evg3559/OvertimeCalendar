package com.evg3559programmer.overtimecalendar.di

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.vanpra.composematerialdialogs.color.ColorPalette


class MyColors {

   companion object{
       val ColorsPicker: List<Color> = listOf(Color.White,
                        Color("#FFA7FFFF".toColorInt()),
                        Color("#FFFFA199".toColorInt()),
                        Color("#FFD2FAA4".toColorInt()),
                        Color("#FFB7C2FF".toColorInt()),
                        Color("#FFF7FF9E".toColorInt()),
                        Color("#FFF3ADFF".toColorInt()),
                        Color("#FFFFCE94".toColorInt()))
         }
}

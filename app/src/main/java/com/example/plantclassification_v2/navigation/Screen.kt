package com.example.plantclassification_v2.navigation

sealed class Screen(
    val route:String,
){
    object MainScreen:Screen("main_screen")
    object RecognitionScreen:Screen("recogniton_screen")
    object WebScreen:Screen("web_screen")
    object HistoryScreen:Screen("history_screen")
    object InfoScreen:Screen("info_screen")

    fun withArgs(vararg args:String):String{
        return buildString {
            append(route)
            if (args.isNotEmpty()) {
                args.forEach { arg ->
                    append("/$arg")
                }
            }
        }
    }
}
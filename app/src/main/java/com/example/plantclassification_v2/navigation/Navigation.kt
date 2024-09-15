package com.example.plantclassification_v2.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.plantclassification_v2.Page.HistoryPage
import com.example.plantclassification_v2.Page.HomePage
import com.example.plantclassification_v2.Page.InfoPage
import com.example.plantclassification_v2.Page.Recognition
import com.example.plantclassification_v2.Page.Web
import com.example.plantclassification_v2.viewModel.HistoryViewModel
import com.example.plantclassification_v2.viewModel.PlantRecognition

@Composable
fun Navigation(
    plantRecognition: PlantRecognition,
    navController: NavHostController,
    historyViewModel: HistoryViewModel,
    finishActivity:()->Unit = {},
    ctx:Context
){
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(
            route = Screen.MainScreen.route
        ){
            HomePage(navController = navController,
                initPageFromHome = {plantRecognition.initPageFromHome()},
                getTag = {plantRecognition.getTag(it)},
                finishActivity = finishActivity
            )
        }
        composable(
            route = Screen.RecognitionScreen.route+"/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.LongType
                    defaultValue = -1
                },
            )
        ){entry->
            Recognition(
                navController = navController,
                recognition = plantRecognition,
                insertHistoryRecord = {historyViewModel.insert(it)},
                hId = entry.arguments?.getLong("id")!!,
                ctx = ctx
            )
        }
        composable(
            route = Screen.WebScreen.route + "/{param}",
            arguments = listOf(
                navArgument("param"){
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ){entry->
            Web(param = entry.arguments?.getString("param"),navController)
        }
        composable(
            route = Screen.HistoryScreen.route
        ){
            HistoryPage(
                historyVM = historyViewModel,
                getTag = {plantRecognition.getTag(it)},
                navController = navController
            )
        }
        composable(
            route = Screen.InfoScreen.route
        ){
            InfoPage(navController = navController)
        }
    }
}
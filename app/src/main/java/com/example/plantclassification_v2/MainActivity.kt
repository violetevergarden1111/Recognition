package com.example.plantclassification_v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.plantclassification_v2.navigation.Navigation
import com.example.plantclassification_v2.ui.theme.PlantClassification_v2Theme
import com.example.plantclassification_v2.viewModel.HistoryViewModel
import com.example.plantclassification_v2.viewModel.PlantRecognition
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlantClassification_v2Theme {
                val recognition: PlantRecognition by viewModels()
                val historyViewModel:HistoryViewModel by viewModels()
                val navController = rememberNavController()
                val context = applicationContext
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        navController = navController,
                        plantRecognition = recognition,
                        historyViewModel = historyViewModel,
                        finishActivity = {finishAffinity()}
                        , ctx = context
                    )
                }
            }
        }
    }
}

package com.example.plantclassification_v2.Page

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.plantclassification_v2.R
import com.example.plantclassification_v2.navigation.BackBottom
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun Web(param:String?,navController: NavHostController){
    val context = LocalContext.current
    val netWorkState = remember {
        mutableStateOf(false)
    }
    Scaffold(
        topBar ={
            TopAppBar(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                BackBottom(navController = navController)
            }

        }
    ) {
        Box(modifier = Modifier.padding(it)){
            val state = rememberWebViewState(url = "https://baike.baidu.com/item/"+param)
            if (!netWorkState.value){
                NetworkErrorPage()
            }
            else{
                WebView(
                    state = state,
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
                )
            }
        }
    }
    val callback = object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            netWorkState.value = true
        }
        override fun onLost(network: Network) {
            super.onLost(network)
            netWorkState.value = false
        }
    }
    val connMgr = context.getSystemService(ConnectivityManager::class.java)
    val request = NetworkRequest.Builder().build()
    connMgr?.registerNetworkCallback(request, callback)
}
@Composable
fun NetworkErrorPage(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        )
        {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.no_network),
                contentDescription =null,
                tint = Color.Gray,
                modifier = Modifier.size(40.dp,40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "网络开小差了",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Light,
                color = Color.Gray
            )
        }
    }
}
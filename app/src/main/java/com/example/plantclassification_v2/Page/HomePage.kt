package com.example.plantclassification_v2.Page

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plantclassification_v2.R
import com.example.plantclassification_v2.navigation.BottomMenu
import com.example.plantclassification_v2.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun HomePage(navController: NavController,
             initPageFromHome: ()->Unit={},
             getTag:(String)->Unit = {},
             finishActivity:()->Unit= {}
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val showHint = remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
             TopAppBar(
                 backgroundColor = MaterialTheme.colorScheme.surfaceVariant
             ) {
                 Spacer(modifier = Modifier.width(4.dp))
                 Text(
                     text = "首页",
                     style = MaterialTheme.typography.titleMedium,
                     color = MaterialTheme.colorScheme.onSurfaceVariant,
                     modifier = Modifier.padding(8.dp)
                 )
             }
        },
        bottomBar = {
            BottomMenu(navController = navController)
        },
        snackbarHost = {
            BackHandler(true) {
                if (showHint.value){
                    finishActivity()
                    return@BackHandler
                }
                else{
                    showHint.value = true
                    scope.launch{
                        showHint.value = snackBarHostState.showSnackbar(message = "再按一次以退出", duration = SnackbarDuration.Short) != SnackbarResult.Dismissed
                    }
                }
            }
            SnackbarHost(hostState = snackBarHostState, snackbar = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.outline
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 3.dp
                    )
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = it.visuals.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            })
        }
        ) {
        Box(
            modifier = Modifier
                .padding(it)
        )
        {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, top = 50.dp)
            ) {
                item{
                    RecognitionCard(
                        title = "植物识别",
                        tag = "Plant",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        navController = navController,
                        initPageFromHomePage = { initPageFromHome() },
                        getPageTag = {getTag(it)},
                        description = "支持 4066 个植物分类单元, Top1 准确率 0.848, Top5 准确率 0.959, 植物名称参考自 iPlant, 包括学名和中文正式名",
                        coverResource = R.drawable.plant_cover
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item{
                    RecognitionCard(
                        title = "昆虫识别",
                        tag = "Insect",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        navController = navController,
                        initPageFromHomePage = {initPageFromHome() },
                        getPageTag = {getTag(it)},
                        description = "支持 2037 类 (可能是目, 科, 属或种等) 昆虫或其他节肢动物, top1/top5 准确率为 0.922/0.981",
                        coverResource = R.drawable.insect_cover
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognitionCard(
    title: String,
    tag:String,
    description:String,
    modifier: Modifier = Modifier,
    navController: NavController,
    initPageFromHomePage: ()->Unit={},
    getPageTag:(String)->Unit = {},
    coverResource:Int
){
    Card(
        modifier = modifier.padding(bottom = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        shape = MaterialTheme.shapes.large,
        onClick = {
            initPageFromHomePage()
            navController.navigate(Screen.RecognitionScreen.withArgs("-1"))
            getPageTag(tag)
        },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 8.dp,
            )

    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ){
            Image(painter = painterResource(id = coverResource),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(507f / 710f)
            )
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .width(160.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Start,
                        letterSpacing = 4.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier.width(140.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.width(180.dp),
                        textAlign = TextAlign.Start,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

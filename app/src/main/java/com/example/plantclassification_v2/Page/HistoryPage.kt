package com.example.plantclassification_v2.Page

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.plantclassification_v2.R
import com.example.plantclassification_v2.navigation.BackBottom
import com.example.plantclassification_v2.navigation.BottomMenu
import com.example.plantclassification_v2.navigation.Screen
import com.example.plantclassification_v2.viewModel.HistoryViewModel

@Composable
fun HistoryPage(
    historyVM: HistoryViewModel,
    getTag: (String) -> Unit ={},
    navController: NavController
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    historyVM.initPage(currentRoute != Screen.HistoryScreen.route)
    val showDeleteDialog = remember {
        mutableStateOf(false)
    }
    val listState = rememberLazyListState()
    Scaffold(bottomBar = {
        if (historyVM.isDelete()){
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.height(58.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row (
                        modifier = Modifier
                            .clickable {
                                if (!historyVM.isSelectedAll.value)
                                    historyVM.selectAll()
                                else
                                    historyVM.overSelectAll()
                            }
                            .padding(start = 18.dp)
                    ){
                        Checkbox(checked = historyVM.isSelectedAll.value, onCheckedChange = null)
                        Text(text = "全选", modifier = Modifier
                            .padding(start = 8.dp))
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    ) {
                        IconButton(
                            enabled = historyVM.okToDelete.value,
                            onClick = {
                                showDeleteDialog.value = true
                            }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "",
                                tint = if(historyVM.okToDelete.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
        else BottomMenu(navController = navController)
    },
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        BackBottom(navController = navController, initHistoryPage = {historyVM.overDelete()})
                        Text(
                            text = "历史记录",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (historyVM.isDelete()){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                IconButton(onClick = { historyVM.overDelete() }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "")
                                }
                            }
                        }
                        else if (historyVM.historyList.isNotEmpty()){
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 16.dp)
                            ){
                                Text(
                                    text = "管理",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .clickable {
                                            historyVM.launchDelete()
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }) {
        if (showDeleteDialog.value){
            AlertDialog(
                onDismissRequest = { showDeleteDialog.value = false},
                confirmButton = {
                    TextButton(onClick = {
                        historyVM.buildDeleteList()
                        historyVM.deleteItem(
                            buildList{
                                historyVM.deleteList.forEach {id->
                                    if (id!=null){
                                        add(id)
                                    }
                                }
                            })
                        showDeleteDialog.value = false
                    }) {
                        Text(
                            text = "删除",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog.value = false
                        }
                    ) {
                        Text("取消")
                    }
                },
                title = {
                    Text(text = "确认删除？")
                }
            )
        }
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        )
        {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.padding(5.dp)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    items(
                        historyVM.historyList.size
                    ){index->
                        if (historyVM.historyList[index] != null){
                            Row {
                                RecordItem(index = index,
                                    navController = navController,
                                    historyVM = historyVM,
                                    getTag = {getTag(it)}
                                )
                            }
                        }
                    }
                    item {
                        Row (
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ){
                            val text:String = if (historyVM.isFull()) "没有更多了" else "加载中"
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
                if (listState.isScrolledToTheEnd()){
                    if(!historyVM.isFull())
                        historyVM.loadMore()
                }
            }
        }
    }
}

@Composable
fun RecordItem(index:Int,
               navController: NavController,
               historyVM: HistoryViewModel,
               getTag:(String)->Unit={},
               ) {
    val paint = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(historyVM.historyList[index]!!.imageUri.toUri())
            .crossfade(true)
            .build(),
        placeholder = painterResource(id = R.drawable.image_loading),
        error = painterResource(id = R.drawable.image_load_failed)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (historyVM.isDelete()) {
                            if (historyVM.selectedList[index] == false) {
                                historyVM.selectedList[index] = true
                                historyVM.okToDelete.value = true
                                historyVM.isSelectedAll()
                            } else {
                                historyVM.selectedList[index] = false
                                historyVM.isSelectedAll.value = false
                                historyVM.isOkToDelete()
                            }
                        } else {
                            getTag(historyVM.historyList[index]!!.tag)
                            navController.navigate(
                                Screen.RecognitionScreen.withArgs(
                                    historyVM.historyList[index]!!.id.toString(),
                                )
                            )
                        }
                    },
                    onLongPress = {
                        if (!historyVM.isDelete()) {
                            historyVM.launchDelete()
                            historyVM.selectedList[index] = true
                            historyVM.okToDelete.value =true
                        }
                    }
                )
            },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (historyVM.isDelete()){
                Checkbox(
                    checked = historyVM.selectedList[index]!!,
                    onCheckedChange = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Box(modifier = Modifier
                .size(100.dp, 100.dp),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = paint,
                    contentDescription = "",
                    modifier = Modifier.size(100.dp, 100.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .width(100.dp)
            ) {
                Text(
                    text = historyVM.historyList[index]!!.chineseNameList[0],
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = historyVM.historyList[index]!!.latinNameList[0],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    ResultProbCircle(prob = historyVM.historyList[index]!!.probList[0], radius = 40.dp, ringWidth = 4.dp)
                    Text(
                        text = "${"%.0f".format(historyVM.historyList[index]!!.probList[0] * 100.0)}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "可信度 ${"%.2f".format(historyVM.historyList[index]!!.probList[0] * 100.0)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

fun LazyListState.isScrolledToTheEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
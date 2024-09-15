package com.example.plantclassification_v2.Page

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.plantclassification_v2.HistoryDataBase.RecognitionRecord
import com.example.plantclassification_v2.R
import com.example.plantclassification_v2.navigation.BackBottom
import com.example.plantclassification_v2.navigation.Screen
import com.example.plantclassification_v2.viewModel.PlantRecognition
import kotlinx.coroutines.launch
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Recognition(
    navController: NavController,
    recognition: PlantRecognition,
    insertHistoryRecord:(RecognitionRecord)->Unit = {},
    hId:Long,
    ctx:Context
){
    val imgUri = remember {
        recognition.imageUri
    }
    val isDetected = remember {
        recognition.isDetected
    }
    val loadImg = remember {
        recognition.loadImg
    }
    val resultListState = rememberLazyListState()
    if (hId > 0){
        recognition.queryRecord(hId)
    }
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) {
        it?.let { uri ->
            ctx.contentResolver.takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION)
            loadImg.value = true
            recognition.getPicture(it)
        }
    }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            if (it && imgUri.value!=null){
                loadImg.value = true
            }
            else{
                imgUri.value = null
            }
        })
    val openBottomSheet = remember {
        mutableStateOf(false)
    }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val title = when(recognition.tag){
        "Plant" -> "植物识别"
        "Insect" -> "昆虫识别"
        else -> "识别"
    }
    val modelDescription = when(recognition.tag){
        "Plant" -> "支持 4066 个植物分类单元 (可能是科, 属, 种或亚种等)"
        "Insect" -> "支持 2037 类 (可能是目, 科, 属或种等) 昆虫或其他节肢动物"
        else -> "无模型"
    }
    Scaffold (
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
            ){
                BackBottom(navController = navController)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        }
    ){
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ModelDescription(about = modelDescription)
                Box(modifier = Modifier
                    .height(300.dp)
                    .width(300.dp),
                    contentAlignment = Alignment.Center
                ){
                    if (loadImg.value){
                        if (imgUri.value!=null){
                            val paint = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(imgUri.value as Uri)
                                    .crossfade(false)
                                    .build(),
                                placeholder = painterResource(id = R.drawable.image_loading),
                                error = painterResource(id = R.drawable.image_load_failed)
                            )
                            Image(
                                painter = paint,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .aspectRatio(1f / 1f)
                                    .pointerInput(Unit) {
                                        detectTapGestures(onTap = {
                                            openBottomSheet.value = !openBottomSheet.value
                                        })
                                    }
                            )
                        }
                    }
                    else{
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.uploadimg),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp, 200.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(onTap = {
                                        openBottomSheet.value = !openBottomSheet.value
                                    })
                                },
                            tint = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
                if (!isDetected.value)
                {
                    Button(
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 5.dp
                        ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.inversePrimary
                        ),
                        shape = MaterialTheme.shapes.medium,
                        enabled = imgUri.value!=null,
                        onClick = {
                        if (imgUri.value != null) {
                            if (recognition.tag == "Plant")
                                recognition.plantImageDetect()
                            else if (recognition.tag == "Insect")
                                recognition.insectImageDetect()
                            scope.launch {
                                val recognitionRecord = RecognitionRecord(
                                    id = LocalTime.now().nano.toLong(),
                                    tag = recognition.tag,
                                    imageUri = imgUri.value.toString(),
                                    chineseNameList = recognition.getChineseNameList(),
                                    latinNameList = recognition.getLatinNameList(),
                                    probList = recognition.getProbList()
                                )
                                insertHistoryRecord(recognitionRecord)
                            }
                        }
                    }) {
                        if (imgUri.value!=null)
                            Text(
                                text = "识别",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 4.sp,
                            )
                        else Text(
                            text = "请先上传图片",
                            style = MaterialTheme.typography.bodyLarge,
                            letterSpacing = 1.sp
                        )
                    }
                }
                LazyColumn(state = resultListState) {
                    items(recognition.resultList) {
                        if (it!=null)
                            ResultItem(latinName = it.latinName, chineseName = it.chineseName,prob = it.prob, navController = navController)
                    }
                }
            }
        }
        if (openBottomSheet.value){
            ModalBottomSheet(
                onDismissRequest = { openBottomSheet.value = false },
                sheetState = bottomSheetState,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ){
                Row (
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    IconButton(
                        onClick = {
                            launcher.launch(arrayOf("image/*"))
                            recognition.resultList.clear()
                            isDetected.value = false
                            scope.launch {
                                bottomSheetState.hide()
                            }.invokeOnCompletion {
                                if (!bottomSheetState.isVisible){
                                    openBottomSheet.value = false
                                }
                            }
                        }
                    ) {
                        SheetButton(description = "打开相册", iconResource = R.drawable.album)
                    }
                    IconButton(
                        onClick = {
                            val uri = recognition.provideUri()
                            loadImg.value = false
                            recognition.resultList.clear()
                            cameraLauncher.launch(uri)
                            isDetected.value = false
                            imgUri.value = uri
                            scope.launch {
                                bottomSheetState.hide()
                            }.invokeOnCompletion {
                                if (!bottomSheetState.isVisible){
                                    openBottomSheet.value = false
                                }
                            }
                    }) {
                        SheetButton(description = "打开相机", iconResource = R.drawable.camera)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultItem(
    latinName:String,
    chineseName:String,
    prob:Float,
    navController: NavController
){
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        onClick = {
            navController.navigate(Screen.WebScreen.withArgs(chineseName))
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .width(180.dp)
            ) {
                Text(
                    text = chineseName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = latinName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "在百度上查看详细信息",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Light,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp,12.dp),
                        tint = Color.Gray
                    )
                }
            }
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Box (
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    ResultProbCircle(prob = prob)
                    Text(
                        text = "${"%.2f".format((prob*100.0f))}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "可信度 ${"%.2f".format((prob*100.0f))}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
fun SheetButton(
    description:String,
    iconResource:Int
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconResource),
            contentDescription = null,
            modifier = Modifier.size(40.dp,40.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
@Composable
fun ResultProbCircle(
    prob: Float,
    radius:Dp = 60.dp,
    ringWidth:Dp = 6.dp
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val primaryColor = MaterialTheme.colorScheme.primary
        val inversePrimaryColor = MaterialTheme.colorScheme.inversePrimary
        Canvas(modifier = Modifier.size(radius)) {
            drawArc(
                color = inversePrimaryColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                alpha = 0.8f,
                style = Stroke(width = ringWidth.toPx()),
                size = Size(radius.toPx(),radius.toPx())
            )
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360f*prob,
                useCenter = false,
                alpha = 0.8f,
                style = Stroke(width = ringWidth.toPx()),
                size = Size(radius.toPx(),radius.toPx())
            )
        }
    }
}
@Composable
fun ModelDescription(
    about:String
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(top = 24.dp)
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Info, 
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(20.dp, 20.dp)
                    .padding(2.dp)
            )
            Text(
                text = about,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.upload),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(20.dp, 20.dp)
                    .padding(start = 3.dp, top = 2.dp, bottom = 2.dp, end = 4.dp)
            )
            Text(
                text = "支持两种图像上传方式: 本地图像上传; 拍照上传.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row {
            Icon(
                imageVector = Icons.Default.Menu, 
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .size(20.dp, 20.dp)
                    .padding(2.dp)
            )
            Text(
                text = "返回前5个可能性最高的物种/属/科, 结果包括其中文名, 学名和可信度.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
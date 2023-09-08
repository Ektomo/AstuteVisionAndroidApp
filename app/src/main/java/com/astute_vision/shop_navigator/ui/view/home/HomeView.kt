package com.astute_vision.shop_navigator.ui.view.home

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.astute_vision.shop_navigator.MainActivity
import com.astute_vision.shop_navigator.navigation.AVNavigationGraphDestination
import com.astute_vision.shop_navigator.ui.theme.robotoFontFamily
import com.astute_vision.shop_navigator.ui.view.util.LoadingView
import com.astute_vision.shop_navigator.ui.view.util.ViewStateClass


@Composable
fun HomeView(vm: HomeViewModel, navController: NavController) {

    val viewState by vm.state.collectAsState()




    val activity = LocalContext.current as MainActivity

    LaunchedEffect(key1 = Unit) {
        activity.onNfcRead = {
            vm.onReadNfc(it)
        }
    }



    Crossfade(targetState = viewState, label = "HomeView") { s ->
        when (s) {
            is ViewStateClass.Data -> {
                if (s.data == HomeStateView.GoForward) {
                    navController.navigate(AVNavigationGraphDestination.SearchList.name) {
                        launchSingleTop = true
                    }
                    vm.resetState()
                } else {
                    HomeCard(vm = vm, navController)
                }
            }

            is ViewStateClass.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = s.e.message ?: "Неизвестная ошибка")
                }
            }

            ViewStateClass.Loading -> {
                LoadingView()
            }
        }
    }



    BackHandler {
        if (viewState != ViewStateClass.Data(HomeStateView.StayHome)) {
            vm.resetState()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - vm.mLastPress > vm.TOAST_DURATION) {
                vm.onBackPressedToast =
                    Toast.makeText(activity, "Для выхода нажмите еще раз", Toast.LENGTH_SHORT)
                vm.onBackPressedToast!!.show()
                vm.mLastPress = currentTime
            } else {
                if (vm.onBackPressedToast != null) {
                    vm.onBackPressedToast!!.cancel()
                    vm.onBackPressedToast = null
                }
                activity.finish()
            }
        }
    }
}

@Composable
fun HomeCard(vm: HomeViewModel, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header()
            MsgBlock()
            Button(onClick = { vm.mockGoForward() }, modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                Text("Считать метку")
            }
        }
        IconButton(
            onClick = {
                navController.navigate(AVNavigationGraphDestination.Settings.name) {
                    launchSingleTop = true
                }
            },
            modifier = Modifier.align(BiasAlignment(0.95f, -0.98f))
        ) {
            Icon(Icons.Filled.Settings, "", modifier = Modifier.size(30.dp), tint = Color.White)
        }
    }
}

@Composable
private fun Header() {
    val gradient =
        Brush.linearGradient(
            listOf(
//                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.onBackground,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.onBackground,
            )
        )
    Box(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f)
            .background(gradient),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                "Добро пожаловать в систему ",
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = robotoFontFamily,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.padding(vertical = 16.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                FioCircle(text = "AV")
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                Text(
                    text = "Astute Vision",
                    fontFamily = robotoFontFamily,
                    color = Color.White,
                    fontSize = 20.sp
                )
            }

        }
    }
}


@Composable
fun FioCircle(text: String) {
    Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF009DDE))
                .border(width = 1.dp, color = Color.White, shape = CircleShape)
        )
        Text(
            text = text,
            fontFamily = robotoFontFamily,
            color = Color.White,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MsgBlock() {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(8.dp, shape = RoundedCornerShape(25.dp))
            .background(
                color = Color.White, shape = RoundedCornerShape(24.dp)
            )
            .padding(top = 4.dp, end = 4.dp, bottom = 4.dp, start = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Необходимо прочитать метку\n для синхронизации",
            fontSize = 20.sp,
            color = Color(0xFF686868),
            fontFamily = robotoFontFamily,
            textAlign = TextAlign.Start
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogStringField(
    onConfirm: (String) -> Unit,
    onDismissRequest: () -> Unit
) {

    var text by remember {
        mutableStateOf("")
    }
    val errorMsg = remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismissRequest, properties = DialogProperties()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(size = 16.dp)
                )
        ) {
            Column(
                Modifier
                    .defaultMinSize(minHeight = 48.dp)
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Введите текст для записи метки",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(16.dp))
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    enabled = true,
                    isError = errorMsg.value.isNotEmpty()
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text(
                        text = "Отмена",
                    )
                }
                TextButton(
                    onClick = {
                        if (text.isNotEmpty()) {
                            errorMsg.value = ""
                            onConfirm(text)
                        } else {
                            errorMsg.value = "Это поле не может быть пустым"
                        }
                    }
                ) {
                    Text(
                        text = "Ок",
                    )
                }
            }
        }
    }
}
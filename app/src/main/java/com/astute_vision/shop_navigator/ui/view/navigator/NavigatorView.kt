package com.astute_vision.shop_navigator.ui.view.navigator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.astute_vision.shop_navigator.MainActivity
import com.astute_vision.shop_navigator.ui.theme.robotoFontFamily
import com.astute_vision.shop_navigator.ui.view.util.LoadingView
import com.astute_vision.shop_navigator.ui.view.util.ViewStateClass

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigatorView(vm: NavigatorViewModel, navController: NavController) {


    val viewState by vm.state.collectAsState()


    val activity = LocalContext.current as MainActivity
    val nearReal by vm.nearReal.collectAsState()

    val recommendedGoods = remember {
        vm.recommendedGoods
    }

    val needSize by remember {
        vm.visible
    }

    val animatedPadding by animateDpAsState(
        if (!needSize) {
            0.dp
        } else {
            90.dp * recommendedGoods.size
        },
        label = "padding"
    )


    DisposableEffect(key1 = Unit) {
        activity.onNfcRead = {}
        onDispose {
            vm.disconnect()
        }
    }











    Crossfade(targetState = viewState, label = "HomeView") { s ->
        when (s) {
            is ViewStateClass.Data -> {
                val angle by vm.rotation.collectAsState()
                val alpha: Float by animateFloatAsState(angle.toFloat(), label = "compas")

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

//                    Box(modifier = Modifier.)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
//                        .animateContentSize(animationSpec =
//                        tween(700, easing = LinearEasing))
                            .align(Alignment.TopCenter)
                    )
                    {


                        recommendedGoods.forEach { g ->
                            AnimatedVisibility(visible = needSize,
                                enter = slideInHorizontally(
                                    animationSpec =
                                    tween(700, easing = LinearEasing)
                                ) { -it },
                                exit = slideOutHorizontally(
                                    tween(
                                        700,
                                        easing = LinearEasing
                                    )
                                ) { it }) {
                                NotificationGoodView(
                                    good = g
                                ) {
                                    g.needDelete = true
//                                        checkNeedDelete = true
                                }
                            }

                        }
                    }


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
//                            .padding(top = animatedPadding)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


//                        AnimatedVisibility(visible = recommendedGoods.isNotEmpty()) {

//                        }


                        Image(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "",
                            modifier = Modifier
                                .size(200.dp)
                                .rotate(alpha)
                        )
                        Text(
                            "Направляйтесь по стрелке",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            fontFamily = robotoFontFamily,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        AnimatedVisibility(visible = nearReal.isNotEmpty()) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                text = nearReal,
                                fontFamily = robotoFontFamily,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                        Divider()

                        Column(
                            modifier = Modifier
                                .padding(top = 12.dp, bottom = 12.dp, start = 12.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .shadow(8.dp, shape = RoundedCornerShape(25.dp))
                                .background(
                                    color = Color.White, shape = RoundedCornerShape(24.dp)
                                )
                                .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
                        ) {
                            Text(
                                "Место для рекламы",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                        }

//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(animatedSize)
//                        ) {


//                        }


//                        Row(modifier = Modifier.fillMaxWidth()) {
//
//                            Spacer(Modifier.weight(0.05f))
//                            Column(
//                                modifier = Modifier
//                                    .padding(top = 12.dp, bottom = 12.dp, end = 12.dp)
//                                    .weight(1f)
////                                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
//                                    .fillMaxWidth()
//                                    .wrapContentHeight()
//                                    .shadow(8.dp, shape = RoundedCornerShape(25.dp))
//                                    .background(
//                                        color = Color.White, shape = RoundedCornerShape(24.dp)
//                                    )
//                                    .padding(top = 12.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
//                            ) {
//                                Text(
//                                    "Справа от вас",
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(6.dp),
//                                    style = MaterialTheme.typography.titleMedium,
//                                    textAlign = TextAlign.Center
//                                )
//                                Text(
//                                    "Мартини бьянка",
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 2.dp),
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    textAlign = TextAlign.Start
//                                )
//                                Text(
//                                    "Пармезан",
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .padding(vertical = 2.dp),
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    textAlign = TextAlign.Start
//                                )
//                            }
//                        }
                    }
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
}

@Composable
fun NotificationGoodView(good: RecommendGood, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        IconButton(
            onClick = onClick, modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "")
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 6.dp)
                .shadow(8.dp, shape = RoundedCornerShape(25.dp))
                .background(
                    color = Color.White, shape = RoundedCornerShape(24.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                Icons.Default.Image, "", modifier = Modifier
                    .size(100.dp)
                    .padding(6.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = good.sCaption,
                fontFamily = robotoFontFamily,
                fontSize = 18.sp,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

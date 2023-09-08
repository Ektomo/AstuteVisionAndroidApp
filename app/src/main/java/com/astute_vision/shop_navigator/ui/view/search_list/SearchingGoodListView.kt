package com.astute_vision.shop_navigator.ui.view.search_list

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.astute_vision.shop_navigator.MainActivity
import com.astute_vision.shop_navigator.navigation.AVNavigationGraphDestination
import com.astute_vision.shop_navigator.ui.theme.robotoFontFamily
import com.astute_vision.shop_navigator.ui.view.util.LoadingView
import com.astute_vision.shop_navigator.ui.view.util.SearchingListView
import com.astute_vision.shop_navigator.ui.view.util.ViewStateClass
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SearchingGoodListView(vm: SearchingGoodListViewModel, navController: NavController) {

    val viewState by vm.state.collectAsState()
    val goodList = remember { vm.goodList }


    val activity = LocalContext.current as MainActivity

    var showGood by remember {
        mutableStateOf(false)
    }
    var curGood: Good? by remember {
        mutableStateOf(null)
    }

    var showGoodList by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        activity.onNfcRead = {}
    }

    if (showGood && curGood != null) {
        GoodCard(good = curGood!!, onDismissRequest = {
            showGood = false
            curGood = null
        }, onAdd = {
            if (!vm.goodList.contains(curGood!!)) {
                vm.goodList.add(curGood!!.copy())
            }
            showGood = false
            curGood = null

        }) {
            navController.navigate(AVNavigationGraphDestination.Navigator.name) {

            }
            showGood = false
            curGood = null
        }
    }

    if (showGoodList && goodList.isNotEmpty()){
        RouteGoodListCard(goods = goodList, onDismissRequest = { showGoodList = false }) {
            navController.navigate(AVNavigationGraphDestination.Navigator.name) {

            }
            showGoodList = false
        }
    }

    Crossfade(targetState = viewState, label = "HomeView") { s ->
        when (s) {
            is ViewStateClass.Data -> {

                val isRefreshing by remember {
                    vm.isRefreshing
                }

                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
                    onRefresh = {

                    }) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {

                        Button(onClick = {
                            showGoodList = true
                        }, modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth().padding(top = 6.dp)) {
                            Text(text = "Список товаров для маршрута")
                        }

                        SearchingListView(
                            list = s.data,
                            listState = rememberLazyListState(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(vertical = 6.dp)
                                    .shadow(8.dp, shape = RoundedCornerShape(25.dp))
                                    .background(
                                        color = Color.White, shape = RoundedCornerShape(24.dp)
                                    )
                                    .clickable {
                                        showGood = true
                                        curGood = it
                                    },
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
                                    text = it.sCaption,
                                    fontFamily = robotoFontFamily,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
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
fun GoodCard(good: Good, onDismissRequest: () -> Unit, onAdd: () -> Unit, onOk: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 8.dp)
        ) {
            val measureSize = this
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    good.sCaption, fontFamily = robotoFontFamily, modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp), style = MaterialTheme.typography.titleMedium
                )
                Divider(modifier = Modifier.padding(bottom = 4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        Icons.Default.Image,
                        "",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(horizontal = 4.dp, vertical = 8.dp)
                    )

                    Column() {
                        Text(text = "Стоимость: 75.3р", fontFamily = robotoFontFamily)
                        Text(text = "Состав: Все натуральное", fontFamily = robotoFontFamily)
                        Text(text = "Описание: То, что нужно", fontFamily = robotoFontFamily)
                    }
                }
                Column(
                    modifier = Modifier
//                    .padding(8.dp)
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Button(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
                        Text("Добавить в корзину")
                    }
                    Spacer(modifier = Modifier.padding(2.dp))
                    Button(onClick = onOk, modifier = Modifier.fillMaxWidth()) {
                        Text("Найти")
                    }
                    Spacer(modifier = Modifier.padding(2.dp))
                    Button(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
                        Text("Закрыть")
                    }
                }
            }
        }
    }
}

@Composable
fun RouteGoodListCard(goods: List<Good>, onDismissRequest: () -> Unit, onOk: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 8.dp)
        ) {
            val measureSize = this
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                goods.forEach { good ->
                    Text(
                        good.sCaption, fontFamily = robotoFontFamily, modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp), style = MaterialTheme.typography.titleMedium
                    )

                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Row(
                    modifier = Modifier
//                    .padding(8.dp)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onDismissRequest, modifier = Modifier.weight(1f)) {
                        Text("Закрыть")
                    }
                    Spacer(modifier = Modifier.weight(0.2f))
                    Button(onClick = onOk, modifier = Modifier.weight(1f)) {
                        Text("Маршрут")
                    }
                }
            }
        }
    }
}
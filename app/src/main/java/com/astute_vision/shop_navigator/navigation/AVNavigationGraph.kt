package com.astute_vision.shop_navigator.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.astute_vision.shop_navigator.ui.view.SettingsView
import com.astute_vision.shop_navigator.ui.view.home.HomeView
import com.astute_vision.shop_navigator.ui.view.home.HomeViewModel
import com.astute_vision.shop_navigator.ui.view.navigator.NavigatorView
import com.astute_vision.shop_navigator.ui.view.navigator.NavigatorViewModel
import com.astute_vision.shop_navigator.ui.view.search_list.SearchingGoodListView
import com.astute_vision.shop_navigator.ui.view.search_list.SearchingGoodListViewModel

enum class AVNavigationGraphDestination {
    Home, SearchList, Navigator, Settings
}

@Composable
fun AVNavigationGraph(navController: NavHostController) {
    NavHost(navController, startDestination = AVNavigationGraphDestination.Home.name) {

        composable(AVNavigationGraphDestination.Home.name) {
            val vm = hiltViewModel<HomeViewModel>()
            HomeView(vm = vm, navController)
        }

        composable(AVNavigationGraphDestination.SearchList.name) {
            val vm = hiltViewModel<SearchingGoodListViewModel>()
            SearchingGoodListView(vm = vm, navController)
        }

        composable(AVNavigationGraphDestination.Navigator.name) {
            val vm = hiltViewModel<NavigatorViewModel>()
            NavigatorView(vm = vm, navController)
        }

        composable(AVNavigationGraphDestination.Settings.name) {
            SettingsView()
        }

    }
}
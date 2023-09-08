package com.astute_vision.shop_navigator.ui.view.util

sealed class ViewStateClass<out T>{

    object Loading : ViewStateClass<Nothing>()
    data class Error(val e: Exception) : ViewStateClass<Nothing>()
    data class Data<T>(val data: T) : ViewStateClass<T>()

}
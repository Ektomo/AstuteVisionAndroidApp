package com.astute_vision.shop_navigator.ui.view.search_list

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astute_vision.shop_navigator.gate.AVGate
import com.astute_vision.shop_navigator.ui.view.util.ViewStateClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Good(
    val serverId: String,
    val sCaption: String
)

@HiltViewModel
class SearchingGoodListViewModel @Inject constructor(
    private val gate: AVGate
) : ViewModel() {

    private val _state: MutableStateFlow<ViewStateClass<List<Good>>> =
        MutableStateFlow(ViewStateClass.Loading)
    val state = _state.asStateFlow()
    val isRefreshing = mutableStateOf(false)
    val goodList = mutableStateListOf<Good>()


    private fun mockList(): List<Good> {
        return listOf(
            Good("1", "Молоко 'Коровка'"),
            Good("2", "Мороженное 'СССР'"),
            Good("3", "Пиво 'Amstel'")
        )
    }

    init {
        loadGoodList()
    }

    private fun loadGoodList(){
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewStateClass.Loading
            try {
//                gate.makePostRequest()
//                delay(1500)
                _state.value = ViewStateClass.Data(mockList())
            } catch (e: Exception) {
                _state.value = ViewStateClass.Error(e)
            }
        }
    }

}
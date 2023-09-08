package com.astute_vision.shop_navigator.ui.view.home

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astute_vision.shop_navigator.gate.AVGate
import com.astute_vision.shop_navigator.gate.WebSocketGate
import com.astute_vision.shop_navigator.ui.view.util.ViewStateClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HomeStateView{
    GoForward, StayHome
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gate: AVGate,
    private val webSocketGate: WebSocketGate
): ViewModel() {

    private val _state: MutableStateFlow<ViewStateClass<HomeStateView>> =
        MutableStateFlow(ViewStateClass.Data(HomeStateView.StayHome))
    val state = _state.asStateFlow()
    var mLastPress: Long = 0
    val TOAST_DURATION: Int = 2000
    var onBackPressedToast: Toast? = null



    fun onReadNfc(text: String){
        //Обращаемся на сервер для синка
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewStateClass.Loading
            try {
//                gate.makePostRequest()
//                val answer = gate.register()
//                webSocketGate.setClientId(answer.id)
                _state.value = ViewStateClass.Data(HomeStateView.GoForward)
            } catch (e: Exception) {
                _state.value = ViewStateClass.Error(e)
            }
        }
    }

    fun mockGoForward(){
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewStateClass.Loading
            try {
                val answer = gate.register()
                webSocketGate.setClientId(answer.id)
                _state.value = ViewStateClass.Data(HomeStateView.GoForward)
            } catch (e: Exception) {
                _state.value = ViewStateClass.Error(e)
            }
        }
    }

    fun resetState(){
        _state.value = ViewStateClass.Data(HomeStateView.StayHome)
    }


}
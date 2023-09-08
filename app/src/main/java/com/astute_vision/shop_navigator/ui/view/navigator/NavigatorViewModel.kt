package com.astute_vision.shop_navigator.ui.view.navigator

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astute_vision.shop_navigator.gate.AVGate
import com.astute_vision.shop_navigator.gate.SocketListener
import com.astute_vision.shop_navigator.gate.WebSocketGate
import com.astute_vision.shop_navigator.ui.view.search_list.Good
import com.astute_vision.shop_navigator.ui.view.util.ViewStateClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

@Serializable
data class RecommendGood(
    val id: String,
    val sCaption: String,
    var needDelete: Boolean = false
)


@Serializable
enum class WebSocketMessageType{
    NEAR_REAL, NEAR_RECOMMENDED, DIRECTION
}

@Serializable
data class WebSocketMessageData(
    val type: WebSocketMessageType,
    val content: String
)

@HiltViewModel
class NavigatorViewModel @Inject constructor(
    private val gate: AVGate,
    private val webSocketGate: WebSocketGate,
    val application: Application
) : ViewModel() {

    private val _state: MutableStateFlow<ViewStateClass<Int>> =
        MutableStateFlow(ViewStateClass.Loading)
    val state = _state.asStateFlow()
    private val _rotation: MutableStateFlow<Int> =
        MutableStateFlow(1)
    val rotation = _rotation.asStateFlow()
    val recommendedGoods = mutableStateListOf<RecommendGood>()
    var visible = mutableStateOf(false)
    val nearReal = MutableStateFlow<String>("")


    fun disconnect(){
        webSocketGate.disconnect()
    }

    init {
//        generateDirection()


        webSocketGate.setSocketListener(object : SocketListener {
            override fun onMessage(message: String) {
                onSocketMessage(message)
            }
        })
        webSocketGate.connect()
    }

    private val goods = listOf(
        RecommendGood("4", "Мука"),
        RecommendGood("5", "Торт"),
        RecommendGood("6", "Виски")

    )


    private fun generateDirection() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = ViewStateClass.Loading
            try {
                _state.value = ViewStateClass.Data(0)
                var count = 0
//                gate.makePostRequest()
//                _state.value = ViewStateClass.Data(Random.nextInt(1, 360))
                while (true) {
                    for (i in 1..360) {
                        _rotation.value = i
                        if (count == 3){
                            recommendedGoods.add(goods[Random.nextInt(0, 2)].copy())

                        }
                        if (count == 5){
                            visible.value = true
                        }
                        if (count == 10){
                            visible.value = false
                        }
                        if (count == 12){
                            if(recommendedGoods.isNotEmpty()) {
                                recommendedGoods.removeFirst()
                            }
                            count = 0
                        }

                        count++
//                        if (i % 5 == 0){
//                            visible.value = false
//                            if(recommendedGoods.isNotEmpty()) {
//                                recommendedGoods.removeFirst()
//                            }
//                        }
//                        if (i % 10 == 0) {
//
//                            recommendedGoods.add(goods[Random.nextInt(0, 2)].copy())
//                            visible.value = true
//                        }
                        delay(1000)
                    }

//                    delay(1000)
                }
            } catch (e: Exception) {
                _state.value = ViewStateClass.Error(e)
            }
        }

    }

    private fun onSocketMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                _state.value = ViewStateClass.Data(0)
                val answer = gate.format.decodeFromString<WebSocketMessageData>(text)
                when(answer.type){
                    WebSocketMessageType.NEAR_REAL -> {
                        nearReal(answer.content)
                    }
                    WebSocketMessageType.NEAR_RECOMMENDED -> {

                    }
                    WebSocketMessageType.DIRECTION -> {
                        _rotation.value += answer.content.toInt()
                    }
                }

                println("TAG $text")
//                _rotation.value = Random.nextInt(1, 360)
            } catch (e: Exception) {
                _state.value = ViewStateClass.Error(e)
            }
        }
    }

    fun nearReal(text: String){
        viewModelScope.launch(Dispatchers.Default) {
            nearReal.value = text
            delay(5000)
            nearReal.value = ""
        }

    }

}
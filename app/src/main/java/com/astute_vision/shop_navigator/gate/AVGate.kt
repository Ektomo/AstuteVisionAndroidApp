package com.astute_vision.shop_navigator.gate

import com.astute_vision.shop_navigator.gate.data.RegisterResponse
import kotlinx.serialization.decodeFromString

class AVGate: Gate() {

    fun register(): RegisterResponse{
        val answer = makePostRequest("register", "")
        return format.decodeFromString(answer)
    }

}
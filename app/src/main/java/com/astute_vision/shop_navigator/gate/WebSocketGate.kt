package com.astute_vision.shop_navigator.gate

import kotlinx.serialization.Serializable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


interface SocketListener{
    fun onMessage(message: String)
}

class WebSocketGate() {
    private var clientId = ""
    private var socketUrl: String = "ws://192.168.138.8:8000/ws"
    private var socketListener: SocketListener? = null
    private var shouldReconnect: Boolean = true
    private var client: OkHttpClient = OkHttpClient()
    private lateinit var webSocket: WebSocket

    fun setClientId(idClient: String){
        this.clientId = idClient
    }

    fun initWebSocket(){
        client = OkHttpClient()

        val request = Request.Builder().addHeader("client-id", clientId).url(url = socketUrl).build()
        webSocket = client.newWebSocket(request, webSocketListener)
        //this must me done else memory leak will be caused
        client.dispatcher.executorService.shutdown()
    }

    fun connect(){
        shouldReconnect = true
        initWebSocket()
    }

    fun setSocketUrl(url: String){
        this.socketUrl = url
    }

    fun setSocketListener(socketListener: SocketListener){
        this.socketListener = socketListener
    }

    fun reconnect(){
        initWebSocket()
    }

    fun disconnect() {
        if (::webSocket.isInitialized) webSocket.close(1000, "Do not need connection anymore.")
        shouldReconnect = false
    }

    fun sendMessage(message: String){
        if (::webSocket.isInitialized) webSocket.send(message)
    }


    private val webSocketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            socketListener?.onMessage(text)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {

        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            if (shouldReconnect){
                reconnect()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            if (shouldReconnect) reconnect()
        }
    }








}
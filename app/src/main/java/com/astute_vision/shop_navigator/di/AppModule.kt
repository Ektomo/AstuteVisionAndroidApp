package com.astute_vision.shop_navigator.di

import com.astute_vision.shop_navigator.gate.AVGate
import com.astute_vision.shop_navigator.gate.WebSocketGate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideGate(): AVGate = AVGate()

    @Singleton
    @Provides
    fun provideWebSocket(): WebSocketGate = WebSocketGate()

}
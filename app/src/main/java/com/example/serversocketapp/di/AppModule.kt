package com.example.serversocketapp.di

import com.example.serversocketapp.presentation.ServerScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory { SocketServer() }
    viewModel { ServerScreenViewModel(get()) }
}
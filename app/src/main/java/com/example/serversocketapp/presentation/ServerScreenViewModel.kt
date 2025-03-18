package com.example.serversocketapp.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.serversocketapp.data.SocketServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServerScreenViewModel(
    private val socketServer: SocketServer
) : ViewModel() {
    private val _isServerRunning = MutableLiveData(false)
    val isServerRunning: LiveData<Boolean> get() = _isServerRunning

    private val _serverIPAddress = MutableLiveData<String>()
    val serverIPAddress: LiveData<String> get() = _serverIPAddress

    private val _messages = MutableLiveData<List<String>>(emptyList())
    val messages: LiveData<List<String>> get() = _messages

    fun startServer(port: Int, backlog: Int) {
        _isServerRunning.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            socketServer.startServer(port, backlog)
            addMessage("Server started on port $port with backlog $backlog.")
            getServerIPAddress()
            startListenClient()
        }
    }

    fun stopServer() {
        _isServerRunning.postValue(false)
        socketServer.stopServer()
        addMessage("Server stopped")
    }

    private fun getServerIPAddress() {
        val ipAddress = socketServer.getLocalIpAddress()
        _serverIPAddress.postValue(ipAddress ?: "Unknown IP Address")
    }

    private fun startListenClient() {
        socketServer.startListenClient(
            onMessageReceived = { message -> addMessage(message) }
        )
    }

    private fun addMessage(message: String) {
        val newMessages = _messages.value?.toMutableList() ?: mutableListOf()
        newMessages.add(message)
        _messages.postValue(newMessages)

    }
}
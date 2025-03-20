package com.example.serversocketapp.presentation

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serversocketapp.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServerScreen : ComponentActivity() {
    private val viewModel: ServerScreenViewModel by viewModel()

    private lateinit var serverIpAddress: TextView
    private lateinit var port: EditText
    private lateinit var backlog: EditText
    private lateinit var startStopButton: Button
    private lateinit var rvMessages: RecyclerView
    private lateinit var messagesAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)

        setupUI()

        observeViewModel()
        setupClickListeners()
    }

    private fun setupUI() {
        messagesAdapter = MessageAdapter()
        serverIpAddress = findViewById(R.id.tvServerIp)
        port = findViewById(R.id.etPort)
        backlog = findViewById(R.id.etBacklog)
        startStopButton = findViewById(R.id.btnStartStopServer)
        rvMessages = findViewById<RecyclerView?>(R.id.rvMessages).apply {
            layoutManager = LinearLayoutManager(this@ServerScreen)
            adapter = messagesAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.serverIPAddress.observe(this) { ipAddress ->
            serverIpAddress.text = getString(R.string.server_ip, ipAddress)
        }

        viewModel.isServerRunning.observe(this) { isRunning ->
            startStopButton.text = if (isRunning) "Stop Server" else "Start Server"
        }

        viewModel.messages.observe(this) { messages ->
            messagesAdapter.submitList(messages)
            rvMessages.scrollToPosition(messages.size - 1)
        }
    }

    private fun setupClickListeners() {
        startStopButton.setOnClickListener {
            val port = port.text.toString().toIntOrNull() ?: 6868
            val backlog = backlog.text.toString().toIntOrNull() ?: 10

            if (viewModel.isServerRunning.value == true) {
                viewModel.stopServer()
            } else {
                viewModel.startServer(port, backlog)
            }
        }
    }
}


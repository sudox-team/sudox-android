package ru.sudox.api.connections.impl

import ru.sudox.api.connections.Connection
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * Соединение на базе WebSocket'ов
 */
class WebSocketConnection : Connection() {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val socketListener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            listener?.onStart()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            listener?.onEnd()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            listener?.onEnd()
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            listener?.onReceive(bytes.toByteArray())
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            listener?.onReceive(text)
        }
    }

    override fun start(address: String, port: Int) {
        val request = Request.Builder()
                .url("ws://$address:$port")
                .build()

        webSocket = client.newWebSocket(request, socketListener)
    }

    override fun send(text: String) {
        webSocket?.send(text)
    }

    override fun end() {
        webSocket?.close(0, null)
    }
}
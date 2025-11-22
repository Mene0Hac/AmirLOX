package com.example.android01

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import okio.ByteString

class ToPy(private val serverUrl: String = "ws://192.168.0.100:8765") {

    private val TAG = "WebSocket"
    private val client = OkHttpClient()
    var lastResponse: String? = null
        private set

    suspend fun sendAndWait(event: String, text: String, attribute: String): String? =
        withContext(Dispatchers.IO) {
            val msg = "$event||$text||$attribute"
            val deferredResponse = CompletableDeferred<String?>()

            try {
                val request = Request.Builder().url(serverUrl).build()

                val listener = object : WebSocketListener() {
                    override fun onOpen(ws: WebSocket, response: Response) {
                        Log.d(TAG, "✅ Connected to server")
                        ws.send(msg)
                        Log.d(TAG, "📤 Sent: $msg")
                    }

                    override fun onMessage(ws: WebSocket, text: String) {
                        Log.d(TAG, "📨 Received: $text")
                        lastResponse = text
                        if (!deferredResponse.isCompleted)
                            deferredResponse.complete(text)
                        ws.close(1000, "Done")
                    }

                    override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                        Log.e(TAG, "💥 WebSocket failure: ${t.message}")
                        if (!deferredResponse.isCompleted)
                            deferredResponse.complete(null)
                    }
                }

                client.newWebSocket(request, listener)

                // ждём максимум 5 секунд, если ответа нет — вернёт null
                return@withContext withTimeoutOrNull(5000) {
                    deferredResponse.await()
                }

            } catch (e: Exception) {
                Log.e(TAG, "⚠️ Exception: ${e.message}")
                return@withContext null
            }
        }
}

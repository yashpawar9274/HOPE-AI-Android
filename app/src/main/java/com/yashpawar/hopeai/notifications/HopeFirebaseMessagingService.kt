package com.yashpawar.hopeai.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yashpawar.hopeai.data.HopeBackendClient
import com.yashpawar.hopeai.data.HopeStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HopeFirebaseMessagingService : FirebaseMessagingService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        val accessToken = HopeStore(this).session?.accessToken
        scope.launch { HopeBackendClient().registerFcmToken(token, accessToken) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "HOPE AI"
        val body = message.notification?.body ?: message.data["body"] ?: return
        NotificationCenter.show(this, title, body, message.messageId?.hashCode() ?: body.hashCode())
    }
}


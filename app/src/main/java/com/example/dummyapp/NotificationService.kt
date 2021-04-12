package com.example.dummyapp

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationService: FirebaseMessagingService() {

    private val TAG = NotificationService::class.java.simpleName

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.i(TAG, "Message ID" + p0.messageId.toString())
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i(TAG, "New Token $p0")
    }
}
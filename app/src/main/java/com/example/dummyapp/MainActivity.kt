package com.example.dummyapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "demo_channel"
    lateinit var firebaseAnalytics: FirebaseAnalytics
    private val TAG = MainActivity::class.java.simpleName
    // One way of creating trace
    val myTrace= FirebasePerformance.getInstance().newTrace("testTrace")

    @AddTrace(name = "onCreateTrace")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialise Firebase analytics
        firebaseAnalytics = Firebase.analytics

        createNotificationChannel()

        crash_BTN.setOnClickListener {
            makeACrash()
        }

        logEvent_BTN.setOnClickListener {
            setUpUserProperty()
            setUpUserID()
            setDefaultProperty()
            logAnEvent()
        }

        //getFirebaseToken()
        // Get custom data from Firebase cloud messaging
        //getCustomDataFromFCM()

        createNotification_BTN.setOnClickListener {
            createBasicNotification()
        }

        myFunction()
    }

    // Another way of creating trace
    @AddTrace(name = "onStart")
    override fun onStart() {
        super.onStart()
    }

    private fun myFunction() {
        myTrace.start()
        myTrace.incrementMetric("hit", 1)
        myTrace.stop()
    }

    private fun createBasicNotification() {
        // Setting up tap action
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // Building notification
        // Pass one of the NotificationChannelID you have created to
        // show notification under that specific channel
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
            .setContentTitle("My notification")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line..." +
                        "Much longer text that cannot fit one line..."))
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // will remove notification once clicked

        // Show the notification
        with(NotificationManagerCompat.from(this)) {
            notify(100, builder.build()) // Keep the notification Id as it will be needed if you want to update/remove notification
        }
    }

    // Notification channel should be created as soon as app is opened
    // Calling again and again would not cause any problem
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getCustomDataFromFCM() {
        Log.i(TAG, "${ intent.extras?.getString("custom_data") }")
    }


    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.i(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            Log.i(TAG, token.toString())
            //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setUpUserID() {
        firebaseAnalytics.setUserId("00001")
    }

    private fun logAnEvent() {
        // custom event
        val eventName = "share_image"
        val bundle = Bundle().apply {
            putString("image_name", "some_name")
            putString("image_description", "some_text")
        }
        firebaseAnalytics.logEvent(eventName, bundle)
    }

    private fun makeACrash() {
        throw NullPointerException("Test crash")
    }

    private fun setUpUserProperty() {
        firebaseAnalytics.setUserProperty("location", "INDIA")
        firebaseAnalytics.setUserProperty("currency", "INR")
        firebaseAnalytics.setUserProperty("is_subscribe", "false")
    }

    private fun setDefaultProperty(){
        val bundle = Bundle().apply {
            firebaseAnalytics.setUserProperty(FirebaseAnalytics.Param.LOCATION, "INDIA")
            firebaseAnalytics.setUserProperty(FirebaseAnalytics.Param.CURRENCY, "INR")
        }
        firebaseAnalytics.setDefaultEventParameters(bundle)
        // To set default property as blank
        //firebaseAnalytics.setDefaultEventParameters(null)
    }
}
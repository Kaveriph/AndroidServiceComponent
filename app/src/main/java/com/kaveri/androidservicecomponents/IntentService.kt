package com.kaveri.androidservicecomponents

import android.app.*
import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import kotlinx.coroutines.*

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class IntentService : IntentService("IntentService") {

    private var timer = 5
    //var coroutine: Job? = null
    private val TAG: String? = "IntentService"

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "Executing Intent")
        val seconds = intent?.extras?.getInt("SEC")
        val tag = intent?.extras?.getString("SERVICE_INSTANCE")
        executeCoroutine(seconds, tag)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand $startId")
        //startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForeground() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            }
        val CHANNEL_DEFAULT_ID = "DEFAULT"
        val notificationChannel = NotificationChannel(
            CHANNEL_DEFAULT_ID,
            "service_channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            notificationChannel
        )
        val notification = Notification.Builder(this, CHANNEL_DEFAULT_ID)
            .setContentText("Testing FG................")
            .setContentTitle("Android service component")
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
    }

    private fun executeCoroutine(seconds: Int?, tag: String?) {
        var i: Int = seconds as Int
            while (i != 0) {
                Log.d(TAG, "$tag : delaying : $i")
                broacast(i)
                Thread.sleep(1000)
                i--
            }
    }

    private fun broacast(i: Int) {
        val intent = Intent().apply {
            action = "SERVICE_INFO"
            putExtra("Value", i)
        }
        sendBroadcast(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "IntentService destroyed")
    }

}
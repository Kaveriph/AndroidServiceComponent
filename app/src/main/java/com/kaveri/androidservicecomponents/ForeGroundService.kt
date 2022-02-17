package com.kaveri.androidservicecomponents

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*

class ForeGroundService : Service() {

    private var timer = 5
    var coroutine: Job? = null
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Return the communication channel to the service.")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        if (intent != null) {
            executeTheIntent(intent)
        }
      startForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForeground() {
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }
        val CHANNEL_DEFAULT_ID = "DEFAULT"
        val notificationChannel = NotificationChannel(CHANNEL_DEFAULT_ID, "service_channel", NotificationManager.IMPORTANCE_DEFAULT)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(notificationChannel)
        var notification = Notification.Builder(this, CHANNEL_DEFAULT_ID)
            .setContentText("Testing FG................")
            .setContentTitle("Android service component")
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
    }

    fun enableOrDisableTimer(enable:Boolean) {
        if(enable) {
            timer = 5
            coroutine = CoroutineScope(Dispatchers.Default).launch {
                while(timer > 0) {
                    delay(1000)
                    println("waiting to kill service")
                    timer--
                }
                println("stopping self...")
                stopSelf()
            }
        } else {
            timer = 5
            coroutine?.cancel("cancelling as new intent received")
        }
    }

    private fun executeTheIntent(intent: Intent) {
        Log.d(TAG, "Executing Intent")
        val seconds = intent.extras?.getInt("SEC")
        val tag = intent.extras?.getString("SERVICE_INSTANCE")

        var i: Int = seconds as Int
        CoroutineScope(Dispatchers.Default).launch {
            while (i != 0) {
                enableOrDisableTimer(false)
                Log.d(TAG, "$tag : delaying : $i")
                delay(1000)
                i--
            }
            enableOrDisableTimer(true)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    companion object {
        private val TAG = BackGroundService::class.simpleName
    }
}
package com.kaveri.androidservicecomponents

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*

class BackGroundService : Service() {

    private var timer = 5
    var coroutine:Job? = null
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        if (intent != null) {
            executeTheIntent(intent)
        }
        return super.onStartCommand(intent, flags, startId)
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
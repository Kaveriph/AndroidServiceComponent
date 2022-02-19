package com.kaveri.androidservicecomponents

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import androidx.core.app.JobIntentService

class SimpleJobIntentService : JobIntentService() {
    private val TAG = "SimpleJobIntentService"

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "Executing onHandleWork")
        val seconds = intent?.extras?.getInt("SEC")
        val tag = intent?.extras?.getString("SERVICE_INSTANCE")
        executeCoroutine(seconds, tag)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        fun enqueueWork(@NonNull context:Context, intent: Intent) {
            enqueueWork(context, SimpleJobIntentService::class.java, 1000, intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }
    private fun executeCoroutine(seconds: Int?, tag: String?) {
        var i: Int = seconds as Int
        //CoroutineScope(Dispatchers.Default).launch {
        while (i != 0) {
            Log.d(TAG, "$tag : delaying : $i")
            Thread.sleep(1000)
            i--
        }
        // enableOrDisableTimer(true)
        //}
    }
}


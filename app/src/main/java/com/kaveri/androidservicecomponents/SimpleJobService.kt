package com.kaveri.androidservicecomponents

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.*

class SimpleJobService : JobService() {

    private lateinit var coroutine: Job
    private val TAG = SimpleJobService::class.simpleName
    private var param: JobParameters? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.d(TAG, "onStart")
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob")
        param = p0
        val tag = param?.extras?.getString("SERVICE_INFO") ?: "FIRST"
        val delayInSec = param?.extras?.getInt("SEC") ?: 10
        executeTheJob(tag, delayInSec)
        return true
    }

    private fun executeTheJob(tag: String, delayInSec: Int) {
        var delaySec = delayInSec
        coroutine = CoroutineScope(Dispatchers.Default).launch {
            while (delaySec > 0) {
                delay(1000)
                delaySec--
                Log.d(TAG, "waiting for $tag job : $delaySec sec")
            }
            withContext(Dispatchers.Main) {
                //Job Finished. No need to reschedule
                jobFinished(param, false)
            }
        }
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        // Reschedule the job is stopped in between
        Log.d(TAG, "Rescheduling the job to resume")
        coroutine.cancel()
        return true
    }
}
package com.kaveri.androidservicecomponents

import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*

class SimpleJobServiceWithMutlipleWorks : JobService() {

    private val TAG: String = SimpleJobServiceWithMutlipleWorks::class.simpleName ?: ""
    private lateinit var coroutine: Job
    private var param: JobParameters? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d(TAG, "onStartJob()")
        param = p0
        executeJob()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun executeJob() {
        coroutine = CoroutineScope(Dispatchers.Default).launch {
            var workItem = param?.dequeueWork()
            Log.d(TAG, "Executing work $workItem")
            while (workItem != null) {
                var name = workItem?.intent.getStringExtra("SERVICE_INSTANCE")
                var delay = workItem?.intent?.getIntExtra("SEC", 5)
                Log.d(TAG, "Executing $name, $delay")
                while (delay!! > 0) {
                    delay(1000)
                    Log.d(TAG, "working on $name : $delay")
                    delay--
                }
                param?.completeWork(workItem)
                workItem = param?.dequeueWork()
            }
        }
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        coroutine.cancel()
        return true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "onStartCommand")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }
}
package com.kaveri.androidservicecomponents

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.kaveri.androidservicecomponents.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val JOB_ID: Int = 1
    private val TAG = MainActivity::class.simpleName
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        startTestService()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startTestService() {
        startFirstIntent("FIRST", 30)
       /* CoroutineScope(Dispatchers.Default).launch {
            var cnt = 4
            while(cnt > 0) {
                Log.d(TAG, "waiting for a second delay to call next intent")
                delay(5000)
                startFirstIntent("SECOND", 14)
                cnt--
            }
        }*/
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startFirstIntent(name: String, delayInSec: Int) {
        val intent = Intent(this, IntentService::class.java)
        val bundle = Bundle()
        bundle.putString("SERVICE_INSTANCE", name)
        bundle.putInt("SEC", delayInSec)
        intent.putExtras(bundle)
       // startService(intent)
        //startForegroundService(intent)
        //startJobIntentService(intent)
        startJobScheduler(intent)
    }

    private fun startJobScheduler(intent: Intent) {
        val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val bundle = PersistableBundle()
        bundle.putString("SERVICE_INFO", intent.extras?.getString("SERVICE_INFO"))
        bundle.putInt("SEC", intent.extras?.getInt("SEC") ?: 15)
        val jobInfo = JobInfo.Builder(JOB_ID, ComponentName(this, SimpleJobService::class.java))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setBackoffCriteria(5000, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
            .setExtras(bundle)
            .build()
        jobScheduler.schedule(jobInfo)
    }

    private fun startJobIntentService(intent: Intent) {
        SimpleJobIntentService.enqueueWork(this, intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


    override fun onResume() {
        super.onResume()
        IntentFilter("SERVICE_INFO").also {
            registerReceiver(receiver,it)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.d(TAG, "Broadcast received ${p1?.extras?.getInt("Value")}")
        }

    }
}

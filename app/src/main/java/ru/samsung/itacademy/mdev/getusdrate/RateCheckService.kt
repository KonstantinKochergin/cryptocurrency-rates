package ru.samsung.itacademy.mdev.getusdrate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RateCheckService : Service() {
    val handler = Handler(Looper.getMainLooper())
    var rateCheckAttempt = 0
    val rateCheckInteractor = RateCheckInteractor()
    private val context = this

    val rateCheckRunnable: Runnable = Runnable {
        requestAndCheckRate()
    }

    private fun requestAndCheckRate() {
        // Write your code here
        GlobalScope.launch(Dispatchers.Main) {
            val rate = rateCheckInteractor.requestRate()
            val intent = Intent()
            intent.action = BROADCAST_ACTION
            intent.putExtra("rate", rate)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            rateCheckAttempt += 1
            handler.postDelayed(rateCheckRunnable, RATE_CHECK_INTERVAL * rateCheckAttempt)
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.postDelayed(rateCheckRunnable, RATE_CHECK_INTERVAL * rateCheckAttempt)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(rateCheckRunnable)
    }


    companion object {
        const val TAG = "RateCheckService"
        const val RATE_CHECK_INTERVAL = 500L
        const val BROADCAST_ACTION = "RateUpdates"

        fun startService(ctx: Context) {
            ctx.startService(Intent(ctx, RateCheckService::class.java))
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, RateCheckService::class.java))
        }
    }



}
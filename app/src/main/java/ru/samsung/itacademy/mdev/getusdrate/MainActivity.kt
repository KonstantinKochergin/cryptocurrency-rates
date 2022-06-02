package ru.samsung.itacademy.mdev.getusdrate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    lateinit var viewModel: MainViewModel
    lateinit var textRate: TextView
    lateinit var rootView: View
    lateinit var updateUIReceiver: BroadcastReceiver
    lateinit var deltaInput: EditText
    lateinit var applyDelta: Button
    var delta = 0
    var prevRate: Double? = null
    val CHANNEL_ID = "CHANNEL_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initView()

        updateUIReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                val rate = intent?.getStringExtra("rate")
                if (prevRate != null && delta > 0) {
                    val current = if (rate != null) rate.toString().toDouble() else 1000.0
                    if (abs(prevRate!! - current) >= delta) {
                        val contentText = if (prevRate!! > current) "Цены выросла ↑" else "Цена упала ↓"
                        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("Котировки ETH")
                            .setContentText(contentText)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)


                        if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(LongArray(0))
                        with(NotificationManagerCompat.from(applicationContext)) {
                            notify(7, builder.build())
                        }
                    }
                }
                onRateUpdate(rate!!)
                prevRate = rate.toDouble()
            }
        }

        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_DEFAULT)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        channel.description = "channelDescription"
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)

    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(updateUIReceiver, IntentFilter(RateCheckService.BROADCAST_ACTION))
        RateCheckService.startService(this)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(updateUIReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        RateCheckService.stopService(this)
    }

    fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.ethRate.observe(this, {
            textRate.text = "$it RUB"
        })
        viewModel.onCreate()
    }

    fun initView() {
        textRate = findViewById(R.id.textUsdRubRate)
        rootView = findViewById(R.id.rootView)
        deltaInput = findViewById(R.id.delta_input)
        applyDelta = findViewById(R.id.apply_delta)

        applyDelta.setOnClickListener {
            val deltaInputValue = deltaInput.text.toString().toIntOrNull()
            if (deltaInputValue != null) {
                delta = deltaInputValue
            }
        }
    }

    fun onRateUpdate(rate: String) {
        textRate.text = "$rate RUB"
    }
}
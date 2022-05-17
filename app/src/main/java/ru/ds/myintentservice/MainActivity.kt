package ru.ds.myintentservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import ru.ds.myintentservice.databinding.ActivityMainBinding

//IntentService спользуется, если надо выполнять какие-то тяжёлые задачи с намерениями,
// которые могут выполняться асинхронно

class MainActivity : AppCompatActivity() {

    private var mProgressBar: ProgressBar? = null
    private var mMyBroadcastReceiver: MyBroadcastReceiver? = null
    private var mUpdateBroadcastReceiver: UpdateBroadcastReceiver? = null
    private var mMyServiceIntent: Intent? = null
    private var mNumberOfIntentService = 0

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Запускаем встроенный Сервис по умолчанию
        val intService =
            DefaultService.launchDefaultService(this, message = "Test message from MainActivity")
        binding.buttonDefaultServiceStart.setOnClickListener {
            startService(intService)
        }
        //останавливаем сервис
        binding.buttonDefaultServiceStop.setOnClickListener {
            stopService(intService)
        }


        mProgressBar = binding.progressbar

        binding.buttonStart.setOnClickListener(View.OnClickListener {
            mNumberOfIntentService++

            // Запускаем свой IntentService
            mMyServiceIntent = Intent(this@MainActivity, MyIntentService::class.java)
            startService(
                mMyServiceIntent!!.putExtra("time", 2)
                    .putExtra("task", "Задание 1")
            )
            startService(
                mMyServiceIntent!!.putExtra("time", 1)
                    .putExtra("task", "Задание 2")
            )

        })
        binding.buttonStop.setOnClickListener(View.OnClickListener {
            if (mMyServiceIntent != null) {
                stopService(mMyServiceIntent)
                mMyServiceIntent = null
            }
        })
        mNumberOfIntentService = 0
        mMyBroadcastReceiver = MyBroadcastReceiver()
        mUpdateBroadcastReceiver = UpdateBroadcastReceiver()

        // регистрируем BroadcastReceiver
        val intentFilter = IntentFilter(
            MyIntentService.ACTION_MYINTENTSERVICE
        )
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(mMyBroadcastReceiver, intentFilter)

        // Регистрируем второй приёмник
        val updateIntentFilter = IntentFilter(
            MyIntentService.ACTION_UPDATE
        )
        updateIntentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(mUpdateBroadcastReceiver, updateIntentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mMyBroadcastReceiver)
        unregisterReceiver(mUpdateBroadcastReceiver)
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val result = intent
                .getStringExtra(MyIntentService.EXTRA_KEY_OUT)
            binding.textView.text = result
        }
    }

    inner class UpdateBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val update = intent
                .getIntExtra(MyIntentService.EXTRA_KEY_UPDATE, 0)
            mProgressBar!!.progress = update
        }
    }
}
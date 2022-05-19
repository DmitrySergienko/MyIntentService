package ru.ds.myintentservice

import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.ds.myintentservice.databinding.ActivityMainBinding

//IntentService спользуется, если надо выполнять какие-то тяжёлые задачи с намерениями,
//которые могут выполняться асинхронно

class MainActivity : AppCompatActivity() {

//--------------------relate to Custom IntentService
    private var mProgressBar: ProgressBar? = null
    private var mMyBroadcastReceiver: MyBroadcastReceiver? = null
    private var mUpdateBroadcastReceiver: UpdateBroadcastReceiver? = null
    private var mMyServiceIntent: Intent? = null
    private var mNumberOfIntentService = 0


//---------------------relate to Default Service
//connection - callback который говорит что подсоединились/отсоединились отсервиса
//получаем ссылку на сервис и без интентов закидываем на него данные "привязываясь"

    private val connection = object : ServiceConnection {
        //callback вызывается при подлючении к сервису
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {

            val service = (binder as DefaultService.ToastServiceBinder).service
            service.setCallback { success: Boolean ->
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
                }
            }
            Log.d("Test", "onServiceConnected() called with: p0 = $p0")
        }

        //callback вызывается при отключении
        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d("Test", "onServiceDisconnected() called with: p0 = $p0")
        }

    }

    //------------------------------------------
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myCustomIntentService() //My custom IntentService
        defaultService() //Default Service

    }

    //-------------------------------------------
//Запускаем встроенный Сервис по умолчанию
    private fun defaultService() {

        val intService =
            DefaultService.launchDefaultService(this, message = "Test message from MainActivity")
        binding.buttonDefaultServiceStart.setOnClickListener {
            startService(intService)
        }
        //останавливаем сервис
        binding.buttonDefaultServiceStop.setOnClickListener {
            stopService(intService)
        }
        //привязваемся к сервису
        binding.buttonDefaultServiceBind.setOnClickListener {
            bindService(intService, connection, BIND_AUTO_CREATE)
            //intService - intent
            //connection - callback который говорит что подсоединились/отсоединились отсервиса
            //BIND_AUTO_CREATE  - созадется автоматически когда мы привязваемся к сервису
        }
        binding.buttonDefaultServiceUnbind.setOnClickListener {
            unbindService(connection)
        }
        binding.buttonForegroundService.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //applicationContext.startForegroundService(intService)
            }
        }
    }

    //-------------------------------------------
//Запускаем кастомный IntentService
    private fun myCustomIntentService() {

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
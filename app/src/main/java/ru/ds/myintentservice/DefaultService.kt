package ru.ds.myintentservice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class DefaultService : Service() {

    companion object {
        const val TAG = "Test"
        const val GET_EXTRA = "GET_EXTRA"

        //этот метод позволяет запускать сервис в данном случае из MainActivity запускать Intent
        //разружает mainActivity сохраняя в себе логику запука Intent
        fun launchDefaultService(context: Context, message: String): Intent {
            val intService = Intent(context, DefaultService::class.java)
            intService.putExtra(GET_EXTRA, message)
            return intService

        }
    }


    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val message = intent?.extras?.getString(GET_EXTRA) ?: "EMPTY" //иначе
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.d(
            TAG,
            "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId"
        )
        Thread {
            Thread.sleep(2000)
            stopSelf(startId) //останавливается по завершении операции

        }.start()
        return super.onStartCommand(intent, flags, startId)


    }
    //привязываемся к сервису
    //позволяет привязаться к сервису и сервис не умирает после выполнения задания

    private var callback: (Boolean) -> Unit = {}
    private val binder = ToastServiceBinder()

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind() called with: intent = $intent")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind() called with: intent = $intent")
        return super.onUnbind(intent)
    }

    fun setCallback(function: (Boolean) -> Unit) {
        callback = function
    }

    inner class ToastServiceBinder : Binder() {
        val service = this@DefaultService
        //тут можно прописать любые методы для сервиса
    }
}
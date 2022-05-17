package ru.ds.myintentservice

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast

class DefaultService : Service() {

    companion object {
        const val TAG = "Test"
        const val GET_EXTRA = "GET_EXTRA"

        //этот метод позволяет запускать сервис в данном случае из MainActivity запускать Intent
        //немного разружает mainActivity сохраняя в себе логику запука Intent
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

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val message = intent?.extras?.getString(GET_EXTRA) ?: "EMPTY" //иначе
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Log.d(
            TAG,
            "onStartCommand() called with: intent = $intent, flags = $flags, startId = $startId")
        Thread{
            Thread.sleep(2000)
            stopSelf(startId) //останавливается по завершении операции

        }.start()
        return super.onStartCommand(intent, flags, startId)


    }
}
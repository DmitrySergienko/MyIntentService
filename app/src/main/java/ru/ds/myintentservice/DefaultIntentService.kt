package ru.ds.myintentservice

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.util.logging.LogManager


private const val TAG = "Test"
class DefaultIntentService: IntentService("DefaultIntentService") {

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() called")
    }

    companion object {
        private const val TEXT_EXTRA = "TEXT_EXTRA"

        fun startDefaultIntentService(context: Context, message: String) {
            val serviceIntent = Intent(context, DefaultIntentService::class.java)
            serviceIntent.putExtra(TEXT_EXTRA, message)
            context.startService(serviceIntent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent() called with: intent = $intent")
        val message = intent?.getStringExtra(TEXT_EXTRA) ?: "EMPTY"
        val toastText = "$message ${Thread.currentThread().name}"
        Thread.sleep(2_000)
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()
        }
    }


}
package ru.ds.myintentservice

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.widget.Toast


class MyIntentService : IntentService("MyService") {
    var extraOut = "Задание выполненно"
    private var mNotificationManager: NotificationManager? = null
    private var mIsSuccess = false
    private var mIsStopped = false
    override fun onCreate() {
        super.onCreate()
        mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onDestroy() {
        val notice: String
        mIsStopped = true
        notice = if (mIsSuccess) {
            "onDestroy with success"
        } else {
            "onDestroy WITHOUT success!"
        }
        Toast.makeText(applicationContext, notice, Toast.LENGTH_LONG)
            .show()
        super.onDestroy()
    }

    override fun onHandleIntent(intent: Intent?) {
        for (i in 0..10) {
            try {
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            if (mIsStopped) {
                break
            }

            // посылаем промежуточные данные
            val updateIntent = Intent()
            updateIntent.action = ACTION_UPDATE
            updateIntent.addCategory(Intent.CATEGORY_DEFAULT)
            updateIntent.putExtra(EXTRA_KEY_UPDATE, i)
            sendBroadcast(updateIntent)
            mIsSuccess = true

            // формируем уведомление
            val notificationText = (100 * i / 10).toString() + " %"
            val notification: Notification = Notification.Builder(
                applicationContext
            )
                .setContentTitle("Progress")
                .setContentText(notificationText)
                .setTicker("Notification!")
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher)
                .build()
            mNotificationManager!!.notify(NOTIFICATION_ID, notification)
        }

        // возвращаем результат
        val responseIntent = Intent()
        responseIntent.action = ACTION_MYINTENTSERVICE
        responseIntent.addCategory(Intent.CATEGORY_DEFAULT)
        responseIntent.putExtra(EXTRA_KEY_OUT, extraOut)
        sendBroadcast(responseIntent)
    }

    companion object {
        const val ACTION_MYINTENTSERVICE = ""
        const val EXTRA_KEY_OUT = "EXTRA_OUT"
        const val ACTION_UPDATE = ""
        const val EXTRA_KEY_UPDATE = "EXTRA_UPDATE"
        private const val NOTIFICATION_ID = 1
    }
}
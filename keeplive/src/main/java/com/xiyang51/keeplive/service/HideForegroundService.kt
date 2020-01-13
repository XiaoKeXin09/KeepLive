package com.xiyang51.keeplive.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.xiyang51.keeplive.KeepLive
import com.xiyang51.keeplive.config.NotificationUtils
import com.xiyang51.keeplive.receiver.NotificationClickReceiver

/** 隐藏前台服务通知*/
class HideForegroundService : Service() {

    private var handler: Handler = Handler()

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground()
        handler.postDelayed({
            stopForeground(true)
            stopSelf()
        }, 2000)
        return START_NOT_STICKY
    }


    /** 显示通知，开启前台服务*/
    private fun startForeground() {
        val intent = Intent(applicationContext, NotificationClickReceiver::class.java)
        intent.action = NotificationClickReceiver.CLICK_NOTIFICATION
        val notification = NotificationUtils.createNotification(
                this,
                KeepLive.foregroundNotification.title,
                KeepLive.foregroundNotification.description,
                KeepLive.foregroundNotification.iconRes,
                intent)
        startForeground(13691, notification)
    }

    override fun onBind(intent: Intent): IBinder? = null

}
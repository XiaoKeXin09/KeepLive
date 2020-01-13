package com.xiyang51.keeplive.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import android.os.RemoteException
import com.xiyang51.keeplive.GuardAidl
import com.xiyang51.keeplive.config.NotificationUtils
import com.xiyang51.keeplive.receiver.NotificationClickReceiver

/** 远程服务*/
class RemoteService : Service() {

    private lateinit var mBinder: MyBinder

    override fun onCreate() {
        super.onCreate()
        mBinder = MyBinder()
    }

    override fun onBind(intent: Intent): IBinder? = mBinder

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            this.bindService(Intent(this@RemoteService, LocalService::class.java),
                    connection, Context.BIND_ABOVE_CLIENT)
        } catch (e: Exception) {
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }

    private inner class MyBinder : GuardAidl.Stub() {
        @Throws(RemoteException::class)
        override fun wakeUp(title: String, discription: String, iconRes: Int) {
            if (Build.VERSION.SDK_INT < 25) {
                val intent = Intent(applicationContext, NotificationClickReceiver::class.java)
                intent.action = NotificationClickReceiver.CLICK_NOTIFICATION
                val notification = NotificationUtils.createNotification(this@RemoteService, title, discription, iconRes, intent)
                this@RemoteService.startForeground(13691, notification)
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            val remoteService = Intent(this@RemoteService, LocalService::class.java)
            this@RemoteService.startService(remoteService)
            this@RemoteService.bindService(remoteService, this, Context.BIND_ABOVE_CLIENT)
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {}
    }

}

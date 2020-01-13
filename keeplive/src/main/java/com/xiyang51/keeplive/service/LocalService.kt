package com.xiyang51.keeplive.service

import android.app.Service
import android.content.*
import android.media.MediaPlayer
import android.os.*
import com.xiyang51.keeplive.GuardAidl
import com.xiyang51.keeplive.KeepLive
import com.xiyang51.keeplive.R
import com.xiyang51.keeplive.config.NotificationUtils
import com.xiyang51.keeplive.receiver.NotificationClickReceiver

/** 本地服务*/
class LocalService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mBinder: MyBinder

    override fun onCreate() {
        super.onCreate()
        mBinder = MyBinder()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {
            //播放无声音乐
            playMusic()
            //启用前台服务，提升优先级
            startForegroundService()
            //绑定守护进程
            bindGuardService()
            //隐藏服务通知
            hideServiceNotification()

            KeepLive.keepLiveService.onWorking()
        } catch (e: Exception) {
        }
        return START_STICKY
    }

    /** 隐藏服务通知*/
    private fun hideServiceNotification() {
        if (Build.VERSION.SDK_INT < 25)
            startService(Intent(this, HideForegroundService::class.java))
    }

    /** 绑定守护进程*/
    private fun bindGuardService() {
        val intent = Intent(this, RemoteService::class.java)
        bindService(intent, connection, Context.BIND_ABOVE_CLIENT)
    }

    /** 启动前台服务*/
    private fun startForegroundService() {
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

    /** 播放音乐*/
    private fun playMusic() {
        mediaPlayer = MediaPlayer.create(this, R.raw.novioce)
        //声音设置为0
        mediaPlayer.setVolume(0f, 0f)
        mediaPlayer.isLooping = true//循环播放
        if (!mediaPlayer.isPlaying) mediaPlayer.start()
    }

    private inner class MyBinder : GuardAidl.Stub() {
        @Throws(RemoteException::class)
        override fun wakeUp(title: String, discription: String, iconRes: Int) {
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceDisconnected(name: ComponentName) {
            val remoteService = Intent(this@LocalService, RemoteService::class.java)
            this@LocalService.startService(remoteService)
            this@LocalService.bindService(remoteService, this, Context.BIND_ABOVE_CLIENT)
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            try {
                val guardAidl = GuardAidl.Stub.asInterface(service)
                guardAidl.wakeUp(
                        KeepLive.foregroundNotification.title,
                        KeepLive.foregroundNotification.description,
                        KeepLive.foregroundNotification.iconRes)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //解绑服务
        unbindService(connection)
        KeepLive.keepLiveService.onStop()
    }

}
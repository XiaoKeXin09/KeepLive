package com.gfd.keeplive

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.xiyang51.keeplive.KeepLive
import com.xiyang51.keeplive.config.ForegroundNotification
import com.xiyang51.keeplive.config.ForegroundNotificationClickListener
import com.xiyang51.keeplive.config.KeepLiveService

class AppApplication : Application(),KeepLiveService {

    override fun onCreate() {
        super.onCreate()
        //启动保活服务
        KeepLive.startWork(this, KeepLive.RunMode.ROGUE, getNotification(),this)
    }

    //一直存活，可能调用多次
    override fun onWorking() {
        Log.e("KeepLive","onWorking()")
    }

    //可能调用多次，跟onWorking匹配调用
    override fun onStop() {
        Log.e("KeepLive","onStop()")
    }

    /**
     * 创建通知
     * @return ForegroundNotification
     */
    private fun getNotification(): ForegroundNotification {
        return ForegroundNotification("Title", "message", R.mipmap.ic_launcher,
                object : ForegroundNotificationClickListener {
                    override fun foregroundNotificationClick(context: Context, intent: Intent) {
                        //点击通知回调
                    }
                })
    }

}
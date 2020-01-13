package com.xiyang51.keeplive.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xiyang51.keeplive.KeepLive

/**
 * 通知栏点击广播接受者
 */
class NotificationClickReceiver : BroadcastReceiver() {

    companion object {
        const val CLICK_NOTIFICATION = "CLICK_NOTIFICATION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == CLICK_NOTIFICATION) {
            //回调方法
            KeepLive.foregroundNotification?.clickListener?.foregroundNotificationClick(context, intent)
        }
    }

}
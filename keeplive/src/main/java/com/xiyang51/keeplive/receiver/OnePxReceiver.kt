package com.xiyang51.keeplive.receiver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Looper
import com.xiyang51.keeplive.activity.OnePixelActivity

/** 1像素广播接收者*/
class OnePxReceiver : BroadcastReceiver() {

    /** 应用是否处于前台*/
    private var appIsForeground = false

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                //屏幕关闭的时候接受到广播
                screenOff(context)
            }
            Intent.ACTION_SCREEN_ON -> {
                //屏幕打开的时候发送广播  结束一像素
                screenOn(context)
            }
        }
    }

    /**
     * 屏幕打开处理
     * @param context Context
     */
    private fun screenOn(context: Context) {
        context.sendBroadcast(Intent("finish activity"))
        if (!appIsForeground) {
            appIsForeground = false
            try {
                val home = Intent(Intent.ACTION_MAIN)
                home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                home.addCategory(Intent.CATEGORY_HOME)
                context.applicationContext.startActivity(home)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 屏幕关闭处理
     * @param context Context
     */
    private fun screenOff(context: Context) {
        appIsForeground = isForeground(context)
        try {
            val it = Intent(context, OnePixelActivity::class.java)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            context.startActivity(it)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 应用是否处于前台
     * @param context Context
     * @return Boolean true:处于前台
     */
    private fun isForeground(context: Context): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasks = am.getRunningTasks(1)
        if (!tasks.isNullOrEmpty()) {
            val topActivity = tasks[0].topActivity
            if (topActivity.packageName == context.packageName) return true
        }
        return false
    }

}
package com.xiyang51.keeplive.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.support.v7.app.AppCompatActivity
import android.view.Gravity

/** 1像素Activity */
class OnePixelActivity : AppCompatActivity() {

    private lateinit var br: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //设定一像素的activity
        with(window) {
            setGravity(Gravity.START or Gravity.TOP)
            attributes = attributes.apply {
                this.x = 0
                this.y = 0
                this.height = 1
                this.width = 1
            }
        }
        //注册广播
        registerDestroyReceiver()
    }

    /** 注册1像素页面销毁广播*/
    private fun registerDestroyReceiver() {
        //在一像素activity里注册广播接受者    接受到广播结束掉一像素
        br = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                finish()
            }
        }
        registerReceiver(br, IntentFilter("finish activity"))
        checkScreenOn()
    }

    /**  检查屏幕是否点亮 */
    private fun checkScreenOn() {
        val pm = application.getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH)
            pm.isInteractive else pm.isScreenOn
        if (isScreenOn) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        checkScreenOn()
    }
    override fun onDestroy() {
        try {
            //销毁的时候解锁广播
            unregisterReceiver(br)
        } catch (e: IllegalArgumentException) {
        }
        super.onDestroy()
    }


}
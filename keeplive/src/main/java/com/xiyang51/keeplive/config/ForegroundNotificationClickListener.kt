package com.xiyang51.keeplive.config

import android.content.Context
import android.content.Intent

/**
 * 通知栏点击回调接口
 */
interface ForegroundNotificationClickListener {
    /**
     * 通知点击回调方法
     * @param context Context
     * @param intent Intent
     */
    fun foregroundNotificationClick(context: Context, intent: Intent)
}
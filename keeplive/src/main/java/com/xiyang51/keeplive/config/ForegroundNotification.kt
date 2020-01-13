package com.xiyang51.keeplive.config

import java.io.Serializable

/**
 * 默认前台通知样式
 */
class ForegroundNotification(
        /** 通知标题*/
        var title: String,
        /** 通知描述*/
        var description: String,
        /** 通知图标资源id*/
        var iconRes: Int,
        /** 通知图标点击监听*/
        var clickListener: ForegroundNotificationClickListener
) : Serializable

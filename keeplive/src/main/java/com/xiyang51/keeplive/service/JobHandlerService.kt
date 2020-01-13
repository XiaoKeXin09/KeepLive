package com.xiyang51.keeplive.service

import android.app.ActivityManager
import android.app.Service
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import com.xiyang51.keeplive.KeepLive
import com.xiyang51.keeplive.config.NotificationUtils
import com.xiyang51.keeplive.receiver.NotificationClickReceiver

/** JobHandlerService */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class JobHandlerService : JobService() {

    private var mJobScheduler: JobScheduler? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startService(this)
        startJobScheduler(startId)
        return Service.START_STICKY
    }

    /** 启动 JobScheduler*/
    private fun startJobScheduler(id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mJobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val builder = JobInfo.Builder(id, ComponentName(packageName, JobHandlerService::class.java.name))
            if (Build.VERSION.SDK_INT >= 24) {
                builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS) //执行的最小延迟时间
                builder.setOverrideDeadline(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)  //执行的最长延时时间
                builder.setMinimumLatency(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
                builder.setBackoffCriteria(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS, JobInfo.BACKOFF_POLICY_LINEAR)//线性重试方案
            } else {
                builder.setPeriodic(JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS)
            }
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            builder.setRequiresCharging(true) // 当插入充电器，执行该任务
            mJobScheduler?.schedule(builder.build())
        }
    }

    /**
     * 启动服务
     * @param context Context
     */
    private fun startService(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(applicationContext, NotificationClickReceiver::class.java)
            intent.action = NotificationClickReceiver.CLICK_NOTIFICATION
            val notification = NotificationUtils.createNotification(
                    this, KeepLive.foregroundNotification.title,
                    KeepLive.foregroundNotification.description,
                    KeepLive.foregroundNotification.iconRes,
                    intent)
            startForeground(13691, notification)
        }
        //启动本地服务
        val localIntent = Intent(context, LocalService::class.java)
        //启动守护进程
        val guardIntent = Intent(context, RemoteService::class.java)
        startService(localIntent)
        startService(guardIntent)
    }

    /**
     * 启动job
     * @param jobParameters JobParameters
     * @return Boolean
     */
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        if (!isServiceRunning(applicationContext, "com.xiyang51.keeplive.service.LocalService") || !isServiceRunning(applicationContext, "$packageName:remote")) {
            startService(this)
        }
        return false
    }

    /**
     * 停止job
     * @param jobParameters JobParameters
     * @return Boolean
     */
    override fun onStopJob(jobParameters: JobParameters): Boolean {
        if (!isServiceRunning(applicationContext, "com.xiyang51.keeplive.service.LocalService") || !isServiceRunning(applicationContext, "$packageName:remote")) {
            startService(this)
        }
        return false
    }

    /**
     * 服务是否正在运行
     * @param ctx Context
     * @param className String
     * @return Boolean
     */
    private fun isServiceRunning(ctx: Context, className: String): Boolean {
        var isRunning = false
        val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val servicesList = activityManager.getRunningServices(Integer.MAX_VALUE)
        val l = servicesList.iterator()
        while (l.hasNext()) {
            val si = l.next()
            if (className == si.service.className) isRunning = true
        }
        return isRunning
    }

}
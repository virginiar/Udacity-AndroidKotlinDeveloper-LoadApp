package com.udacity

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat


private const val NOTIFICATION_ID = 0

/**
 * Builds and delivers a notification.
 *
 * @param action, action containing intent pending.
 * @param applicationContext, activity context.
 */
fun NotificationManager.sendNotification(action: NotificationCompat.Action, applicationContext: Context) {

    val builder = NotificationCompat.Builder(
        applicationContext,
        "channelId"
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(applicationContext
            .getString(R.string.notification_description))
        .addAction(action)

    notify(NOTIFICATION_ID, builder.build())
}
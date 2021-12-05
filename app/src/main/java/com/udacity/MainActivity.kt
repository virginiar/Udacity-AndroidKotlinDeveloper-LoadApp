package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var filename = ""

    private lateinit var notificationManager: NotificationManager
    private lateinit var downloadManager: DownloadManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        notificationManager = getSystemService(NotificationManager::class.java)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(CHANNEL_ID, CHANNEL_NAME)

        customButton.setOnClickListener {
            when (downloadRadioGroup.checkedRadioButtonId) {
                R.id.glideDownload -> {
                    download(GLIDE_URL)
                    filename = getString(R.string.select_download_glide)
                }
                R.id.udacityDownload -> {
                    download(UDACITY_URL)
                    filename = getString(R.string.select_download_udacity)
                }
                R.id.retrofitDownload -> {
                    download(RETROFIT_URL)
                    filename = getString(R.string.select_download_retrofit)
                }
                else -> {
                    Toast.makeText(this,
                        getString(R.string.select_download_none),
                        Toast.LENGTH_SHORT ).show()
                    customButton.setStateCompleted()
                }
            }

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            customButton.setStateCompleted()
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            val query = DownloadManager.Query()
            query.setFilterById(id!!)

            val cursor =
                downloadManager.query(query)

            var status = ""

            if (cursor.moveToFirst()) {
                val result = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                status = if (result == DownloadManager.STATUS_SUCCESSFUL) {
                    getString(R.string.status_success)
                } else {
                    getString(R.string.status_fail)
                }
            }

            val contentIntent = Intent(applicationContext, DetailActivity::class.java)

            contentIntent.putExtra(getString(R.string.intent_status_extra), status)
            contentIntent.putExtra(getString(R.string.intent_filename_extra), filename)

            pendingIntent = PendingIntent.getActivity(
                applicationContext,
                NOTIFICATION_ID,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            action = NotificationCompat.Action(
                R.drawable.ic_assistant_black_24dp,
                getString(R.string.notification_button),
                pendingIntent)


            notificationManager.sendNotification(action, context!!)

        }

    }

    private fun download(url: String) {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)


        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.

    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/refs/heads/master.zip"
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "LoadApp"
        private const val NOTIFICATION_ID = 0
    }

}

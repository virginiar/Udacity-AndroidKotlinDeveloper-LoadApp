package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()


        filename.text = intent.getStringExtra(getString(R.string.intent_filename_extra))
        status.text = intent.getStringExtra(getString(R.string.intent_status_extra))

        if (status.text == getString(R.string.status_fail)) {
            status.setTextColor(getColor(R.color.red))
        }

        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }

    }

}

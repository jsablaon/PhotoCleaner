package com.jsab.photocleaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.jsab.photocleaner.ui.login.LoginActivity
import com.parse.Parse

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // make connection to db
        try {
            Parse.initialize(
                Parse.Configuration.Builder(this)
                    .applicationId(getString(R.string.back4app_app_id))
                    .clientKey(getString(R.string.back4app_client_key))
                    .server(getString(R.string.back4app_server_url))
                    .build()
            );
            println("++++++++++++++++++++++++++++++++++connected to DB+++++++++++++++++++++++++++++++++++++++++++++")
        } catch (err: Exception) {
            println("++++++++++++++++++++++++++++++++++connectDb error: ${err.message}+++++++++++++++++++++++++++++++++++++++++++++")
        }

        setContentView(R.layout.activity_splash)

        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // we used the postDelayed(Runnable, time) method
        // to send a message with a delayed time.
        Handler().postDelayed({
            var intent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000) // 5000 is the delayed time in milliseconds.
    }
}
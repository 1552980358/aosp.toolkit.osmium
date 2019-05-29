package aosp.toolkit.perseus

/*
 * OsToolkit - Kotlin
 *
 * Date : 31/12/2018
 *
 * By   : 1552980358
 *
 */

/*
 * Modify
 *
 * 9/1/2019
 *
 */

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val permission = arrayOf(android.Manifest.permission.INTERNET)
        ActivityCompat.requestPermissions(this, permission, 1)

        info.text =
            "${BuildConfig.APPLICATION_ID}\n${BuildConfig.BUILD_TYPE}\nv${BuildConfig.VERSION_NAME}\n${BuildConfig.VERSION_CODE}"

        if (!BuildConfig.DEBUG) {
            Thread {
                try {
                    Thread.sleep(2000)
                } catch (e: Exception) {
                    //
                }
                startActivity(
                    Intent(
                        this,
                        if (getSharedPreferences("launch", Context.MODE_PRIVATE).getBoolean(
                                "welcome",
                                false
                            )
                        ) {
                            MainActivity::class.java
                        } else {
                            WelcomeActivity::class.java
                        }
                    )
                )
            }.start()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()
    }
}
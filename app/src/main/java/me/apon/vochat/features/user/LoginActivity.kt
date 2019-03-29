package me.apon.vochat.features.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import me.apon.vochat.R
import me.apon.vochat.app.BaseActivity

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/13.
 */
class LoginActivity : BaseActivity() {
    companion object {
        fun start(context: Activity) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        title = "Login"
        supportActionBar.apply {
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
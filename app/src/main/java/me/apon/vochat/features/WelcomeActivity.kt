package me.apon.vochat.features

import android.os.Bundle
import android.os.Handler
import com.orhanobut.hawk.Hawk
import me.apon.vochat.R
import me.apon.vochat.app.BaseActivity
import me.apon.vochat.features.user.LoginActivity
import me.apon.vochat.model.LoginUser

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/17.
 */
class WelcomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        supportActionBar?.hide()

    }

    override fun onResume() {
        super.onResume()
        val loginUser = Hawk.get<LoginUser>("loginUser")
        Handler().postDelayed({
            if (loginUser == null) {
                LoginActivity.start(this)

            } else {
                VOChatActivity.start(this)
            }
            finish()
        }, 2000)
    }
}
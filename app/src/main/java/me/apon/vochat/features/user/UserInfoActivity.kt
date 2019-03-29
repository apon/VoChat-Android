package me.apon.vochat.features.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import me.apon.vochat.R
import me.apon.vochat.app.BaseActivity
import me.apon.vochat.model.User

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/24.
 */
class UserInfoActivity : BaseActivity() {
    companion object {
        fun start(context: Activity, user: User, from: String) {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra("user", user)
            intent.putExtra("from", from)
            context.startActivity(intent)
        }

        fun start(context: Activity, userId: String) {
            val intent = Intent(context, UserInfoActivity::class.java)
            intent.putExtra("userId", userId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_activity)
        supportActionBar.apply {
            this?.setDisplayHomeAsUpEnabled(true)
        }
        val user: User? = intent.getParcelableExtra<User>("user")
        val userId: String? = intent.getStringExtra("userId")
        val from: String? = intent.getStringExtra("from")

        title = user?.name ?: "User Info"

        val arguments = Bundle().apply {
            putParcelable("user", user)
            putString("userId", userId)
            putString("from", from)
        }
        val fragment = UserInfoFragment()
        fragment.arguments = arguments
        supportFragmentManager.beginTransaction().replace(R.id.user_info_activity_fragment, fragment).commit()

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
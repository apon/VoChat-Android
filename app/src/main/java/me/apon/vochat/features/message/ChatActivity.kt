package me.apon.vochat.features.message

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import me.apon.vochat.R
import me.apon.vochat.app.BaseActivity
import me.apon.vochat.features.user.UserInfoActivity

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/18.
 */
class ChatActivity : BaseActivity() {
    lateinit var toId: String

    companion object {
        fun start(context: Activity, toId: String, chatName: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("toId", toId)
            intent.putExtra("chatName", chatName)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_activity)

        initFragment()
        supportActionBar.apply {
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun initFragment() {
        toId = intent.getStringExtra("toId") ?: "0"
        val chatName = intent.getStringExtra("chatName") ?: "VoChat"
        title = chatName


        val arguments = Bundle().apply {
            putString("chatName", chatName)
            putString("toId", toId)
        }
        val fragment = ChatFragment()
        fragment.arguments = arguments
        supportFragmentManager.beginTransaction().replace(R.id.chat_activity_fragment, fragment).commit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
//        val toId = intent?.getStringExtra("toId")
//        val chatName = intent?.getStringExtra("chatName")
//        title = chatName
        initFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.action_user_info -> {
                UserInfoActivity.start(this, toId)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
package me.apon.vochat.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.vochat_activity.*
import me.apon.vochat.R
import me.apon.vochat.app.BaseActivity
import me.apon.vochat.features.user.AddContactsActivity

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/15.
 */
class VOChatActivity : BaseActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, VOChatActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var sessionFragment: Fragment? = null
    private var contactFragment: Fragment? = null
    private var meFragment: Fragment? = null
    private var active: Fragment? = null


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_session -> {
                supportFragmentManager.beginTransaction().hide(active!!).show(sessionFragment!!).commit()
                active = sessionFragment
                title = "VoChat"
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_contact -> {
                if (contactFragment == null) {
                    contactFragment = ChatContactFragment()
                    supportFragmentManager.beginTransaction().hide(active!!).add(
                        R.id.main_container,
                        contactFragment as ChatContactFragment
                    ).commit()

                } else {
                    supportFragmentManager.beginTransaction().hide(active!!).show(contactFragment!!).commit()
                }
                active = contactFragment as Fragment
                title = "Contact"
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_me -> {
                if (meFragment == null) {
                    meFragment = ChatMeFragment()
                    supportFragmentManager.beginTransaction().hide(active!!).add(
                        R.id.main_container,
                        meFragment as ChatMeFragment
                    ).commit()

                } else {
                    supportFragmentManager.beginTransaction().hide(active!!).show(meFragment!!).commit()
                }
                active = meFragment as Fragment
                title = "Me"
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vochat_activity)
        sessionFragment = ChatSessionFragment()
        active = sessionFragment
        supportFragmentManager.beginTransaction().add(R.id.main_container, sessionFragment as ChatSessionFragment)
            .commit()

        title = "VoChat"
        bottom_navigation_view.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_vochat, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_add_contact -> {
            AddContactsActivity.start(applicationContext)
            true
        }
        R.id.action_about -> {

            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
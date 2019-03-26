package me.apon.vochat.features.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import me.apon.vochat.R
import me.apon.vochat.app.BaseActivity

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/23.
 */
class AddContactsActivity : BaseActivity() {
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AddContactsActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_contact_activity)
        title = "Add Contact"
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
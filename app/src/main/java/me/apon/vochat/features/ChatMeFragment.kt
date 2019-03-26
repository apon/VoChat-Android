package me.apon.vochat.features

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.airbnb.mvrx.*
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.chat_me_fragment.*
import me.apon.vochat.R
import me.apon.vochat.features.user.LoginActivity
import me.apon.vochat.features.user.UserViewModel

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/19.
 */
class ChatMeFragment : BaseMvRxFragment() {
    private val viewModel by fragmentViewModel(UserViewModel::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_me_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUser()
        logout_bt.setOnClickListener {
            viewModel.unBind()
            Hawk.delete("loginUser")
            activity?.finish()
            LoginActivity.start(activity!!.applicationContext)
        }
        name_tv.setOnClickListener {
            updateName()
        }
    }

    private fun updateName() {
        val builder = AlertDialog.Builder(context!!)
        val dialogView = layoutInflater.inflate(R.layout.update_name_dialog, null)

        val newNameET = dialogView?.findViewById<EditText>(R.id.newNameET)

        builder.setTitle("Update Name")
        builder.setView(dialogView)
        builder.setPositiveButton("OK") { _, _ ->
            val name = newNameET?.text.toString()
            viewModel.updateName(name)
        }
        builder.setNegativeButton(android.R.string.no) { _, _ ->

        }
        builder.show()


    }

    override fun invalidate() {
        withState(viewModel) {
            val loginUser = it.loginUser
            name_tv.text = loginUser?.name
            phone_tv.text = loginUser?.phone

            when (it.updateName) {
                is Loading -> {

                }

                is Success -> {
                    val msg = it.updateName.invoke()
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }

                is Fail -> {
                    val msg = it.updateName.error.message
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
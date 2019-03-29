package me.apon.vochat.features.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.*
import kotlinx.android.synthetic.main.login_fragment.*
import me.apon.vochat.R
import me.apon.vochat.features.VOChatActivity

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/16.
 */
class LoginFragment : BaseMvRxFragment() {

    private val viewModel by activityViewModel(UserViewModel::class)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginButton.setOnClickListener {
            val phone = phoneEditText.text.toString()
            val pass = passwordEditText.text.toString()
            viewModel.login(phone, pass)
        }

        to_register_tv.setOnClickListener {
            RegisterActivity.start(activity!!)
        }
    }

    override fun invalidate() {
        withState(viewModel) {
            when (it.login) {
                is Loading -> {

                }

                is Success -> {
                    VOChatActivity.start(activity!!)
                    activity!!.finish()
                }

                is Fail -> {
                    val msg = it.login.error.message
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }

                is Incomplete -> {

                }
            }
        }
    }
}
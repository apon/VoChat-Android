package me.apon.vochat.features.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.airbnb.mvrx.*
import kotlinx.android.synthetic.main.register_fragment.*
import me.apon.vochat.R
import me.apon.vochat.features.VOChatActivity

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/16.
 */
class RegisterFragment : BaseMvRxFragment() {

    private val viewModel by activityViewModel(UserViewModel::class)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerButton.setOnClickListener {
            val phone = phoneEditText.text.toString()
            val pass = passwordEditText.text.toString()
            viewModel.register(phone, pass)
        }
    }

    override fun invalidate() {
        withState(viewModel) {


            when (it.register) {
                is Loading -> {

                }

                is Success -> {
                    VOChatActivity.start(activity!!)
                    activity!!.finish()
                }

                is Fail -> {
                    val msg = it.register.error.message
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
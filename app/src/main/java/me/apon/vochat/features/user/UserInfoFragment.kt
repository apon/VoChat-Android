package me.apon.vochat.features.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.*
import kotlinx.android.synthetic.main.user_info_fragment.*
import me.apon.vochat.R
import me.apon.vochat.features.message.ChatActivity
import me.apon.vochat.model.User

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/24.
 */
class UserInfoFragment : BaseMvRxFragment() {

    private val viewModel by activityViewModel(UserViewModel::class)
    private var user: User? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_info_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        user = arguments?.getParcelable<User>("user")
        if (user != null) {
            topNameTV.text = user!!.name
            nameTV.text = user!!.name
            phoneTV.text = user!!.phone
            signedTV.text = "Hello VoChat!"
            okBT.visibility = View.VISIBLE
        }
        val userId = arguments?.getString("userId")
        if (userId != null) {
            viewModel.getUserById(userId)
        }

        val from = arguments?.getString("from")

        okBT.text = when (from) {
            "contact" -> {
                "Send Message"
            }
            "search" -> {
                "Add Contact"
            }
            else -> {
                ""
            }
        }

        okBT.setOnClickListener {
            if (user != null) {
                when (from) {
                    "contact" -> {
                        ChatActivity.start(context!!, user!!.id, user!!.name)
                        activity!!.finish()
                    }
                    "search" -> {
                        viewModel.addContact(user!!.id)
                    }
                }

            }

        }
    }

    override fun invalidate() {
        withState(viewModel) {
            when (it.addContact) {
                is Loading -> {

                }

                is Success -> {
                    val msg = it.addContact.invoke()
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    ChatActivity.start(context!!, user!!.id, user!!.name)
                    activity!!.finish()
                }

                is Fail -> {
                    val msg = it.addContact.error.message
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }

            when (it.userInfo) {
                is Success -> {
                    val user = it.userInfo()
                    if (user != null) {
                        topNameTV.text = user.name
                        nameTV.text = user.name
                        phoneTV.text = user.phone
                        signedTV.text = "Hello VoChat!"
                        okBT.visibility = View.GONE
                    }
                }
            }
        }
    }
}
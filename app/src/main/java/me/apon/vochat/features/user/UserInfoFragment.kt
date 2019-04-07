package me.apon.vochat.features.user

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.mvrx.*
import kotlinx.android.synthetic.main.user_info_fragment.*
import me.apon.vochat.R
import me.apon.vochat.features.message.ChatActivity
import me.apon.vochat.features.message.VoiceActivity
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
                voiceBT.visibility = View.VISIBLE
                "Send Message"
            }
            "search" -> {
                voiceBT.visibility = View.GONE
                "Add Contact"
            }
            else -> {
                voiceBT.visibility = View.GONE
                ""
            }
        }

        okBT.setOnClickListener {
            if (user != null) {
                when (from) {
                    "contact" -> {
                        ChatActivity.start(activity!!, user!!.id, user!!.name)
                        activity!!.finish()
                    }
                    "search" -> {
                        viewModel.addContact(user!!.id)
                    }
                }

            }

        }

        voiceBT.setOnClickListener {
            toVoiceActivity()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    voiceStart()
                    toVoiceActivity()
                }
            }
        }
    }

    private fun toVoiceActivity(){
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                100
            )
            return
        }
        VoiceActivity.start(activity!!, user!!.id, user!!.name)
        activity!!.finish()
    }

    override fun invalidate() {
        withState(viewModel) {
            when (it.addContact) {
                is Loading -> {

                }

                is Success -> {
                    val msg = it.addContact.invoke()
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    ChatActivity.start(activity!!, user!!.id, user!!.name)
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
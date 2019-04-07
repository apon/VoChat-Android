package me.apon.vochat.features.message

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.*
import com.orhanobut.hawk.Hawk
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesListAdapter
import kotlinx.android.synthetic.main.chat_fragment.*
import me.apon.vochat.R
import me.apon.vochat.model.ChatMessage
import me.apon.vochat.model.ChatUser
import me.apon.vochat.model.LoginUser
import me.apon.vochat.service.NewMessageNotification
import java.lang.Exception
import java.util.*


/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/18.
 */
class ChatFragment : BaseMvRxFragment() {
    private val viewModel by activityViewModel(ChatViewModel::class)
    private lateinit var adapter: MessagesListAdapter<ChatMessage>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toId = arguments?.getString("toId", "0") ?: "0"
        val chatName = arguments?.getString("chatName", "VoChat") ?: "VoChat"
        val loginUser = Hawk.get<LoginUser>("loginUser")

        adapter = MessagesListAdapter(loginUser.id, ImageLoader { imageView, _, _ ->
            imageView.setImageResource(R.mipmap.ic_launcher)
        })
        messagesList.setAdapter(adapter)

        inputView.setInputListener {
            //validate and send message
            viewModel.sendMsg(toId, chatName, it.toString())

            true
        }


        viewModel.getMsg(loginUser.id, toId)
        viewModel.clearUnReadCount(toId)
        NewMessageNotification.cancel(context!!, toId)

    }

    override fun onResume() {
        super.onResume()
        val toId = arguments?.getString("toId", "0") ?: "0"
        val loginUser = Hawk.get<LoginUser>("loginUser")
        Handler().postDelayed({
            viewModel.receiveOn(loginUser.id, toId)
        }, 1000)
    }

    override fun onPause() {
        super.onPause()
        viewModel.receiveOn("", "")
    }


    override fun invalidate() {
        withState(viewModel) {
            when (it.messages) {

                is Loading -> {

                }

                is Success -> {
                    val res = it.messages.invoke()
                    adapter.clear()
                    adapter.addToEnd(res, true)
                }
                is Fail -> {
                    val msg = it.messages.error.message
                    Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
                }

                is Incomplete -> {

                }
                else -> {

                }
            }
        }
    }
}
package me.apon.vochat.features

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.activityViewModel
import com.orhanobut.hawk.Hawk
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.dialogs.DialogsListAdapter
import kotlinx.android.synthetic.main.chat_session_fragment.*
import me.apon.vochat.R
import me.apon.vochat.db.AppRoomDatabase
import me.apon.vochat.features.message.ChatActivity
import me.apon.vochat.features.message.ChatViewModel
import me.apon.vochat.model.*
import java.util.*

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/16.
 */
class ChatSessionFragment : BaseMvRxFragment() {

    lateinit var adapter: DialogsListAdapter<ChatSession>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_session_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = DialogsListAdapter<ChatSession>(ImageLoader { imageView, _, _ ->
            imageView.setImageResource(R.mipmap.ic_launcher)
        })
        dialogsList.setAdapter(adapter)

        adapter.setOnDialogClickListener {

            ChatActivity.start(context!!, it.sessionId, it.sessionName)

        }
    }

    override fun invalidate() {

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            loadSession()
        }
    }

    override fun onResume() {
        super.onResume()
        loadSession()
    }

    private fun loadSession() {
        val user = Hawk.get<LoginUser>("loginUser")
        val sessionDao = AppRoomDatabase.getDatabase(context!!).localSessionDao()
        val sessions = sessionDao.loadSession(user.id)
        adapter.clear()
        adapter.addItems(sessions)
    }
}
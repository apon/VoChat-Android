package me.apon.vochat.features

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.*
import kotlinx.android.synthetic.main.chat_contact_fragment.*
import me.apon.vochat.R
import me.apon.vochat.adapter.ContactsAdapter
import me.apon.vochat.features.message.ChatActivity
import me.apon.vochat.features.user.AddContactsActivity
import me.apon.vochat.features.user.UserInfoActivity
import me.apon.vochat.features.user.UserViewModel
import me.apon.vochat.model.User

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/16.
 */
class ChatContactFragment : BaseMvRxFragment() {


    private val viewModel by activityViewModel(UserViewModel::class)
    private val adapter = ContactsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.chat_contact_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener {
            UserInfoActivity.start(context!!, it, "contact")
        }
        adapter.setOnChildItemClickListener {
            when (it.id) {
                R.id.avatarIV -> {
                    Toast.makeText(activity, "点击头像", Toast.LENGTH_LONG).show()
                }
            }
        }


        addContacts.setOnClickListener {
            AddContactsActivity.start(context!!)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.getContacts()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getContacts()
    }

    override fun invalidate() {
        withState(viewModel) {

            when (it.contacts) {
                is Loading -> {
                }

                is Success -> {
                    val res: List<User> = it.contacts.invoke()
                    if (res.isNotEmpty()) {
                        addContacts.visibility = View.GONE
                    } else {
                        addContacts.visibility = View.VISIBLE
                    }
//                    adapter.currentList.clear()
                    adapter.submitList(res)
                }

                is Fail -> {
                    val msg = it.contacts.error.message
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

                }


                is Incomplete -> {

                }

                else -> {
                }
            }
        }
    }
}
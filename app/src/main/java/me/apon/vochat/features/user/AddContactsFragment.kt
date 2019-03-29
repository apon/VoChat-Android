package me.apon.vochat.features.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.airbnb.mvrx.*
import kotlinx.android.synthetic.main.add_contact_fragment.*
import me.apon.vochat.R

/**
 * Created by yaopeng(aponone@gmail.com) on 2019/3/23.
 */
class AddContactsFragment : BaseMvRxFragment() {
    private val viewModel by activityViewModel(UserViewModel::class)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_contact_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchBT.setOnClickListener {
            val phone = phoneET.text.toString()
            viewModel.searchUser(phone)
        }
    }

    override fun invalidate() {

        withState(viewModel) {
            when (it.searchUser) {
                is Loading -> {

                }

                is Success -> {
                    val res = it.searchUser.invoke()
                    if (res.isNotEmpty()) {
                        val user = res[0]
                        UserInfoActivity.start(activity!!, user, "search")
                        activity!!.finish()
                    } else {
                        Toast.makeText(context, "Not found!", Toast.LENGTH_LONG).show()
                    }
                }

                is Fail -> {
                    val msg = it.searchUser.error.message
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}
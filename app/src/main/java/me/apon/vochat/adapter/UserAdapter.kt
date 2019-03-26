package me.apon.vochat.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.apon.vochat.R
import me.apon.vochat.model.LoginUser

class UserAdapter : ListAdapter<LoginUser, UserAdapter.ItemViewholder>(LoginUserDiffCallback()) {

    lateinit var callback: (LoginUser) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserAdapter.ItemViewholder, position: Int) {
        val item: LoginUser = getItem(position)

        with(holder.itemView) {

            setOnClickListener {
                if (::callback.isInitialized) {
                    callback(item)
                }
            }

            // TODO: 绑定数据
            //nameTv.text = item.name
        }
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun setOnItemClickListener(callback: (LoginUser) -> Unit) {
        this.callback = callback
    }
}

class LoginUserDiffCallback : DiffUtil.ItemCallback<LoginUser>() {
    override fun areItemsTheSame(oldItem: LoginUser, newItem: LoginUser): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LoginUser, newItem: LoginUser): Boolean {
        return oldItem == newItem
    }
}
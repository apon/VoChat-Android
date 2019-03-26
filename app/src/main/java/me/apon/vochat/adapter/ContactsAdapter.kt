package me.apon.vochat.adapter

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.user_list_item.view.*
import me.apon.vochat.R
import me.apon.vochat.model.User

class ContactsAdapter : ListAdapter<User, ContactsAdapter.ItemViewholder>(DiffCallback()) {

    lateinit var  callback:(User)->Unit

    lateinit var  childeCallback:(View)->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {
        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContactsAdapter.ItemViewholder, position: Int) {

        val item:User = getItem(position)

        with(holder.itemView){

            setOnClickListener {
                if(::callback.isInitialized){
                    callback(item)
                }
            }

            // TODO: 绑定数据
            nameTv.text = item.name
            phoneTV.text = item.phone
        }
    }

    class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun bindEvent(view:View){
        view.setOnClickListener {
            if(::childeCallback.isInitialized){
                childeCallback(view)
            }
        }
    }

    fun setOnChildItemClickListener(callback: (View)->Unit){
        this.childeCallback = callback
    }

    fun setOnItemClickListener(callback: (User)->Unit){
        this.callback = callback
    }

}

class DiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
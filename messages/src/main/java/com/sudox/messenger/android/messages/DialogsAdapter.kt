package com.sudox.messenger.android.messages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.sudox.messenger.android.messages.views.DialogItemView
import com.sudox.messenger.android.messages.vos.DialogItemViewVO
import kotlinx.android.synthetic.main.dialogs_count.view.*
import java.util.*

class DialogsAdapter(val context: Context) : RecyclerView.Adapter<DialogsAdapter.ViewHolder>() {

    val dialogs = SortedList<DialogItemViewVO>(DialogItemViewVO::class.java, DialogsCallback(this))
    var deleteDialogCallback: ((DialogItemViewVO) -> (Unit))? = null
    var addDialogCallback: (() -> (Unit))? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            1 -> {
                val view = LayoutInflater.from(context).inflate(R.layout.dialogs_count, parent, false)

                view.setOnClickListener {
                    addDialogCallback!!()
                    notifyItemChanged(itemCount-1)
                }

                ViewHolder(view)
            }
            else -> {
                val view = DialogItemView(context)
                val holder = ViewHolder(view)

                view.setOnClickListener {
                    if(holder.adapterPosition != -1) {
                        dialogs[holder.adapterPosition].isMuted = !dialogs[holder.adapterPosition].isMuted
                        notifyItemChanged(holder.adapterPosition)
                    }
                }

                view.setOnLongClickListener {
                    if(holder.adapterPosition != -1) {
                        //dialogs.remove(dialogs[holder.adapterPosition])
                        dialogs.removeItemAt(holder.adapterPosition)
                        notifyItemChanged(holder.adapterPosition)
                        notifyItemChanged(itemCount-1)
                    }
                    true
                }

                holder
            }
        }
    }

    override fun getItemCount(): Int {
        return dialogs.size() + 1
    }


    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) 1 else 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.let {
            when (getItemViewType(position)) {
                0 -> {
                    val dialog = dialogs[position]
                    it as DialogItemView
                    it.vo = dialog
                }
                1 -> {
                    it.dialogCountTextView.text = "${dialogs.size()} chats"
                }
            }
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}
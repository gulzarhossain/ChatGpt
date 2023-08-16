package com.project.chatgpt.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.chatgpt.Chat
import com.project.chatgpt.Model.MsgData
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.Utils.AppUtility
import com.project.chatgpt.databinding.ItemMsgBinding

class MsgAdapter(val context: Context,val list:ArrayList<MsgData>):RecyclerView.Adapter<MsgAdapter.Item>() {
    var lastdate=""

    class Item (val binding: ItemMsgBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        val binding=ItemMsgBinding.inflate(LayoutInflater.from(context),parent,false)
        return Item(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        with(holder) {
            with(list[position]) {
                if (position == 0) {
                    binding.date.text = AppUtility.setDate(list.get(position).time)
                    binding.date.visibility = View.VISIBLE
                } else {
                    if (AppUtility.convertDate(list.get(position).time)
                            .equals(AppUtility.convertDate(list.get(position - 1).time))
                    ) {
                        binding.date.visibility = View.GONE
                    } else {
                        binding.date.text = AppUtility.setDate(list.get(position).time)
                        binding.date.visibility = View.VISIBLE
                    }
                }
                if (list[position].name.equals(AppPreferences.getUserName(context))) {
                    binding.ly1.visibility = View.GONE
                    binding.lyown.visibility = View.VISIBLE
                    binding.msgown.text = list[position].msg
                    binding.timeown.text = AppUtility.convertTime(list[position].time)
                } else {
                    binding.ly1.visibility = View.VISIBLE
                    binding.lyown.visibility = View.GONE
                    binding.msg1.text = list[position].msg
                    binding.time1.text = AppUtility.convertTime(list[position].time)
                }

                binding.msg1.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        (context as Chat).popup(holder.adapterPosition,binding.msg1,false)
                        return false
                    }
                })

                binding.msgown.setOnLongClickListener(object : View.OnLongClickListener {
                    override fun onLongClick(p0: View?): Boolean {
                        (context as Chat).popup(holder.adapterPosition,binding.msgown,true)
                        return false
                    }
                })
            }
        }
    }
}
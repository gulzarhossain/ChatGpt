package com.project.chatgpt.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.chatgpt.Chat
import com.project.chatgpt.Model.MsgData
import com.project.chatgpt.R
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.Utils.AppUtility
import com.project.chatgpt.databinding.ItemMsgBinding

class MsgAdapter(val context: Context,val list:ArrayList<MsgData>):RecyclerView.Adapter<MsgAdapter.Item>() {
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
                    binding.date.text = AppUtility.setDate(this.time)
                    binding.date.visibility = View.VISIBLE
                } else {
                    if (AppUtility.convertDate(this.time)
                            .equals(AppUtility.convertDate(list[position - 1].time))
                    ) {
                        binding.date.visibility = View.GONE
                    } else {
                        binding.date.text = AppUtility.setDate(this.time)
                        binding.date.visibility = View.VISIBLE
                    }
                }
                if (this.name.equals(AppPreferences.getUserName(context))) {
                    if (this.typ.equals("text") || this.typ.equals("null")) {
                        binding.lyimg1.visibility = View.GONE
                        binding.lyimgown.visibility = View.GONE
                        binding.ly1.visibility = View.GONE
                        binding.lyown.visibility = View.VISIBLE
                        binding.msgown.visibility = View.VISIBLE
                        binding.msgown.text = this.msg
                        binding.timeown.text = AppUtility.convertTime(this.time)
                    }else if (this.typ.equals("gif")) {
                        binding.ly1.visibility = View.GONE
                        binding.lyown.visibility = View.VISIBLE
                        binding.msgown.visibility = View.GONE
                        binding.lyimg1.visibility = View.GONE
                        binding.lyimgown.visibility = View.VISIBLE
                        binding.timeown.text = AppUtility.convertTime(this.time)
                        Glide.with(context).asGif().load(this.msg).placeholder(R.drawable.imageload).into(binding.imgown)
                    }else{
                        binding.ly1.visibility = View.GONE
                        binding.lyown.visibility = View.VISIBLE
                        binding.msgown.visibility = View.GONE
                        binding.lyimg1.visibility = View.GONE
                        binding.lyimgown.visibility = View.VISIBLE
                        binding.timeown.text = AppUtility.convertTime(this.time)
                        Glide.with(context).load(this.msg).placeholder(R.drawable.imageload).into(binding.imgown)
                    }
                    } else {
                    if (this.typ.equals("text") || this.typ.equals("null")) {
                        binding.lyimg1.visibility = View.GONE
                        binding.lyimgown.visibility = View.GONE
                        binding.ly1.visibility = View.VISIBLE
                        binding.lyown.visibility = View.GONE
                        binding.msg1.visibility = View.VISIBLE
                        binding.msg1.text = this.msg
                        binding.time1.text = AppUtility.convertTime(this.time)
                    }else if (this.typ.equals("gif")) {
                        binding.ly1.visibility = View.VISIBLE
                        binding.lyown.visibility = View.GONE
                        binding.msg1.visibility = View.GONE
                        binding.lyimg1.visibility = View.VISIBLE
                        binding.lyimgown.visibility = View.GONE
                        binding.time1.text = AppUtility.convertTime(this.time)
                        Glide.with(context).asGif().load(this.msg).into(binding.img1)
                    }else{
                        binding.ly1.visibility = View.VISIBLE
                        binding.lyown.visibility = View.GONE
                        binding.msg1.visibility = View.GONE
                        binding.lyimg1.visibility = View.VISIBLE
                        binding.lyimgown.visibility = View.GONE
                        binding.time1.text = AppUtility.convertTime(this.time)
                        Glide.with(context).load(this.msg).into(binding.img1)
                    }
                }

                binding.msg1.setOnLongClickListener {
                    (context as Chat).popup(holder.adapterPosition, binding.msg1, false)
                    false
                }

                binding.msgown.setOnLongClickListener {
                    (context as Chat).popup(holder.adapterPosition, binding.msgown, true)
                    false
                }

                binding.lyimgown.setOnLongClickListener {
                    (context as Chat).popup(holder.adapterPosition, binding.msgown, true)
                    false
                }
            }
        }
    }
}
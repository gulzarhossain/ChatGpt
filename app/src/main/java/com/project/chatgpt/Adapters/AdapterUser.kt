package com.project.chatgpt.Adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.project.chatgpt.Fragments.ChatFragment
import com.project.chatgpt.Model.UserData
import com.project.chatgpt.R
import com.project.chatgpt.Utils.AppUtility
import com.project.chatgpt.databinding.UserItemBinding

class AdapterUser(val fragment: Fragment,private val context: Context, private val list:ArrayList<UserData>):RecyclerView.Adapter<AdapterUser.Item>() {
    class Item(val binding: UserItemBinding):RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        val binding=UserItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return Item(binding)
    }
    override fun onBindViewHolder(holder: Item, position: Int) {
        with(holder){
            with(list[position]){
                binding.name.text= list[position].name.replace("@gmailcom","")

                if (type.equals("text") || type.equals("null")){
                    binding.lastmsg.setCompoundDrawablesRelative(null,null,null,null)
                    if (this.msg==""){
                        binding.lastmsg.text="-------"
                    }else binding.lastmsg.text=this.msg
                    if (this.time==""){
                        binding.lasttime.text="-------"
                    }else  binding.lasttime.text= AppUtility.setDate(this.time)
                }else if (type.equals("gif")){
                    binding.lastmsg.text="GIF"
                    binding.lastmsg.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.baseline_gif_box_24),null,null,null)
                    binding.lasttime.text= AppUtility.setDate(this.time)
                }else{
                    binding.lastmsg.text="Image"
                    binding.lastmsg.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.baseline_image_24),null,null,null)
                    binding.lasttime.text= AppUtility.setDate(this.time)
                }

                Glide.with(context).load(list[position].pic).placeholder(R.drawable.round_person_2_24).listener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(e: GlideException?, model: Any?,
                                              target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                }).into(binding.img)
                holder.itemView.setOnClickListener {
                    (fragment as ChatFragment).Click(list[position])
                }
                binding.img.setOnClickListener {
                    (fragment as ChatFragment).Click2(it.rootView)
                }
            }
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
}
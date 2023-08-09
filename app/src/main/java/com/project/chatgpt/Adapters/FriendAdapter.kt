package com.project.chatgpt.Adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.project.chatgpt.Fragments.FindFriends
import com.project.chatgpt.Fragments.FriendReq
import com.project.chatgpt.Model.FriendData
import com.project.chatgpt.R
import com.project.chatgpt.databinding.UserFindItemBinding

class FriendAdapter(val frag:Fragment,val g:Int,val context: Context,val list: ArrayList<FriendData>):RecyclerView.Adapter<FriendAdapter.Item>() {
    class Item (val binding:UserFindItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        return Item(UserFindItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        with(holder){
            with(list[position]){
                if (g==1){
                    binding.btrem.visibility=View.GONE
                }else binding.btrem.visibility=View.VISIBLE

                    if (list[position].isCheck){
                        if (g==1){
                            binding.bt.text="Requested"
                        }else binding.bt.text="Accept"
                }else if (g==1){
                        binding.bt.text="Add"
                    }else binding.bt.text="Accept"

                binding.name.text= list[position].name.replace("@gmailcom","")
                Glide.with(context).load(list[position].pic).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).placeholder(
                    R.drawable.round_person_2_24).listener(object : RequestListener<Drawable> {
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

                binding.bt.setOnClickListener {
                    if (g==1){
                        (frag as FindFriends).AddRequest(holder.adapterPosition)
                    }else (frag as FriendReq).AcceptReq(holder.adapterPosition)

                }
                binding.btrem.setOnClickListener {
                    (frag as FriendReq).RemoveReq(holder.adapterPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
}
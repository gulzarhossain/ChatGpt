package com.project.chatgpt.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.chatgpt.Model.ImageData
import com.project.chatgpt.databinding.ShowcaseItemBinding

class ProfileAdapter(val context: Context,val list:ArrayList<ImageData>):RecyclerView.Adapter<ProfileAdapter.Item>() {
    class Item(val binding:ShowcaseItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        return Item(ShowcaseItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        with(holder){
            with(list[position]){
                if (this.drawb==0){
                    Glide.with(context).load(this.url).skipMemoryCache(true).into(binding.img)
                }else{
                    binding.img.setImageDrawable(context.resources.getDrawable(this.drawb,null))
                }
            }
        }
    }
}
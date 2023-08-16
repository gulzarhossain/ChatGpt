package com.project.chatgpt.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.project.chatgpt.Model.ShowCaseData
import com.project.chatgpt.R
import com.project.chatgpt.Showcase
import com.project.chatgpt.databinding.ShowItemBinding

class ShowCaseAdapter(val context: Context,val list:ArrayList<ShowCaseData>):RecyclerView.Adapter<ShowCaseAdapter.Item>() {
    class Item (val binding: ShowItemBinding):RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Item {
        return Item(ShowItemBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Item, position: Int) {
        with(holder){
            with(list[position]){
                binding.slno.text="Showcase "+(holder.adapterPosition+1)
                if (this.uri!=null){
                    binding.img.setImageURI(this.uri)
                    binding.img.scaleType=ImageView.ScaleType.FIT_XY
                    if (this.isCheck){
                        binding.progres.setProgress(this.progrs,true)
                        if (binding.progres.visibility!=View.VISIBLE) {
                            binding.progres.visibility = View.VISIBLE
                            binding.shim.visibility = View.VISIBLE
                            binding.shim.startShimmer()
                        }
                    }else{
                        binding.shim.stopShimmer()
                        binding.shim.visibility=View.GONE
                        binding.progres.visibility=View.GONE
                    }
                }else{
                    binding.img.setImageDrawable(context.getDrawable(R.drawable.baseline_add_photo_alternate_24))
                    binding.img.scaleType=ImageView.ScaleType.FIT_CENTER
                    binding.shim.stopShimmer()
                    binding.shim.visibility=View.GONE
                    binding.progres.visibility=View.GONE
                }

            }
            binding.bt.setOnClickListener {
                (context as Showcase).PickerPhoto(holder.adapterPosition)
            }
            binding.btclose.setOnClickListener {
                (context as Showcase).DeletePhoto(holder.adapterPosition)
            }
        }
    }
}
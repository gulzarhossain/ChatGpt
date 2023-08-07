package com.project.chatgpt.Adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentAdapter(activity:AppCompatActivity,val flist:ArrayList<Fragment>):FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return flist.size
    }

    override fun createFragment(position: Int): Fragment {
        return flist[position]
    }
}
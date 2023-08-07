package com.project.chatgpt

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.chatgpt.Adapters.FragmentAdapter
import com.project.chatgpt.Fragments.ChatFragment
import com.project.chatgpt.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {
    lateinit var binding:ActivityHomeBinding
    lateinit var adapter:FragmentAdapter
    var flist=ArrayList<Fragment>()
    var TabList: List<String> = listOf<String>("Chat","Find Friends")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil. setContentView(this,R.layout.activity_home)
        val window = this.window
        window.statusBarColor = Color.parseColor("#070B17")
        flist.add(ChatFragment(this@Home))
        flist.add(ChatFragment(this@Home))

        adapter= FragmentAdapter(this@Home,flist)
        binding.pager.adapter=adapter

        TabLayoutMediator(binding.lytab,binding.pager,true,true,object :TabLayoutMediator.TabConfigurationStrategy{
            override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                tab.text=TabList[position]
            }
        }).attach()
    }
}
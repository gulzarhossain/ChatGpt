package com.project.chatgpt

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.project.chatgpt.Adapters.FragmentAdapter
import com.project.chatgpt.Fragments.ChatFragment
import com.project.chatgpt.Fragments.FindFriends
import com.project.chatgpt.Fragments.FriendReq
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.Utils.AppUtility
import com.project.chatgpt.databinding.ActivityHomeBinding

class Home : AppCompatActivity() {
    lateinit var binding:ActivityHomeBinding
    lateinit var adapter:FragmentAdapter
    var flist=ArrayList<Fragment>()
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var firebaseAuth: FirebaseAuth

    val list= listOf("Chat","Find Friends","Requests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil. setContentView(this,R.layout.activity_home)
        val window = this.window
        window.statusBarColor = Color.parseColor("#000000")

        flist.add(ChatFragment(this@Home))
        flist.add(FindFriends(this@Home))
        flist.add(FriendReq(this@Home))

        adapter=FragmentAdapter(this,flist)
        binding.pager.adapter=adapter

        TabLayoutMediator(binding.lytab,binding.pager){ tab, pos ->
            tab.text=list[pos]
        }.attach()
        binding.lytab.setBackgroundColor(Color.parseColor("#000000"))
        binding.srchbar.setBackgroundColor(Color.parseColor("#000000"))

        binding.lytab.addOnTabSelectedListener(object :OnTabSelectedListener{
            @RequiresApi(Build.VERSION_CODES.R)
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position==0){
                    binding.text.setTextColor(Color.WHITE)
                    binding.btmenu.iconTint= ColorStateList.valueOf(Color.WHITE)
                    binding.srchbar.setBackgroundColor(Color.parseColor("#000000"))
                    binding.lytab.setBackgroundColor(Color.parseColor("#000000"))
                    binding.lytab.setSelectedTabIndicatorColor(Color.WHITE)
                    binding.lytab.setTabTextColors(resources.getColor(R.color.white2,null),resources.getColor(R.color.white,null))
                    window.statusBarColor = Color.parseColor("#000000")
                    window.decorView.systemUiVisibility= View.STATUS_BAR_VISIBLE
                }else{
                    binding.lytab.setSelectedTabIndicatorColor(resources.getColor(R.color.purple_700,null))
                    binding.text.setTextColor(resources.getColor(R.color.purple_700,null))
                    binding.btmenu.iconTint= ColorStateList.valueOf(resources.getColor(R.color.purple_700,null))
                    binding.lytab.setBackgroundColor(Color.WHITE)
                    binding.srchbar.setBackgroundColor(Color.WHITE)
                    binding.lytab.setTabTextColors(resources.getColor(R.color.purple_700,null),resources.getColor(R.color.black,null))
                    window.statusBarColor =Color.WHITE
                    window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientID))
            .requestProfile().requestEmail()
            .build()
        firebaseAuth= FirebaseAuth.getInstance()

        googleSignInClient = GoogleSignIn.getClient(this@Home, gso)

        binding.btmenu.setOnClickListener {
//          var wrapper: Context =  ContextThemeWrapper(this, R.style.CustomPopUpStyle)
            val pop= PopupMenu(this,binding.btmenu)
            pop.menuInflater.inflate(R.menu.popup2,pop.menu)
            pop.setOnMenuItemClickListener(object :PopupMenu.OnMenuItemClickListener{
                override fun onMenuItemClick(p0: MenuItem?): Boolean {
                    when(p0!!.itemId){
                        R.id.log->{
                            if (AppPreferences.getUserLoginType(this@Home)=="FB"){
                                LoginManager.getInstance().logOut()
                                AppPreferences.setUserLoginStatus(this@Home,false)
                                startActivity(Intent(this@Home,Login::class.java))
                                finish()
                            }else if (AppPreferences.getUserLoginType(this@Home)=="GGL"){
                                googleSignInClient.signOut().addOnCompleteListener({
                                    if (it.isSuccessful){
                                        AppPreferences.setUserLoginStatus(this@Home,false)
                                        startActivity(Intent(this@Home,Login::class.java))
                                        finish()
                                    }
                                })
                            }else {
                                firebaseAuth.signOut()
                                AppPreferences.setUserLoginStatus(this@Home,false)
                                startActivity(Intent(this@Home,Login::class.java))
                                finish()
                            }
                        }
                        R.id.pro->{
                            startActivity(Intent(this@Home,Profile::class.java))
                        }
                    }
                    return false
                }
            })
            pop.show()
        }
    }
    fun ChangeTab(){
        binding.lytab.selectTab(binding.lytab.getTabAt(0))
        AppUtility.SnacView("Friend Added",binding.lytab)
    }

}
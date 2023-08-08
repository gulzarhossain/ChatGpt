package com.project.chatgpt

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.chatgpt.Adapters.AdapterUser
import com.project.chatgpt.Fragments.ChatFragment
import com.project.chatgpt.Model.UserData
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

    val database = Firebase.database
    val myRef = database.getReferenceFromUrl("https://chatgpt-5941d-default-rtdb.firebaseio.com/")
    var keym:String=""
    lateinit var adapterUser: AdapterUser
    val list=ArrayList<UserData>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)

       val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = Color.parseColor("#070B17")

        adapterUser=AdapterUser(ChatFragment(this@MainActivity),this,list)
        binding.rvuser.adapter=adapterUser

        firebaseAuth= FirebaseAuth.getInstance()
        googleSignInClient=GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)

        binding.bt.setOnClickListener {
            firebaseAuth.signOut()
            googleSignInClient.signOut().addOnCompleteListener {
                startActivity(Intent(this@MainActivity,Login::class.java))
                finish()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        myRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (snap in snapshot.child("users").children){
                    if (!snap.key.equals(AppPreferences.getUserName(this@MainActivity))) {
                        myRef.child("chat").addListenerForSingleValueEvent(object :ValueEventListener{
                            override fun onDataChange(snapshot2: DataSnapshot) {
                                var b=false
                                var i=0
                                if (snapshot2.childrenCount>0){
                                    for (snap2 in snapshot2.children){
                                        val user1=snap2.child("user_1").value.toString()
                                        val user2=snap2.child("user_2").value.toString()
                                        if (user1.equals(AppPreferences.getUserName(this@MainActivity)) &&
                                            user2.equals(snap.key.toString()) || user1.equals(snap.key.toString()) &&
                                            user2.equals(AppPreferences.getUserName(this@MainActivity))){
                                            keym=snap2.key.toString()
                                            i++
                                            b=true
                                        }else{
                                            i++
                                           if (i==snapshot2.childrenCount.toInt()){
                                               if (!b)keym=""
                                           }
                                        }
                                    }
                                }else{
                                    keym=""
                                }
                                list.add(UserData(snap.key!!,snap.child("pic").value.toString(),keym))
                                adapterUser.notifyDataSetChanged()
                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}




package com.project.chatgpt

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.chatgpt.Adapters.MsgAdapter
import com.project.chatgpt.Model.MsgData
import com.project.chatgpt.Model.UserData
import com.project.chatgpt.Service.ServiceChat
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.databinding.ActivityChatBinding

class Chat : AppCompatActivity() {
    lateinit var binding: ActivityChatBinding

    private val database = Firebase.database
    private val myRef = database.getReferenceFromUrl("https://chatgpt-5941d-default-rtdb.firebaseio.com/")
    var  msglist=ArrayList<MsgData>()
    var genchatkey=""
    lateinit var msgAdapter: MsgAdapter
    lateinit var popupMenu:PopupMenu
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_chat)
        val window = this.window
        window.statusBarColor = Color.parseColor("#80070B17")

        val userdata = intent.getParcelableExtra<UserData>("Bundle")
        msgAdapter= MsgAdapter(this, msglist)
        binding.rvmsg.adapter=msgAdapter

        binding.name.text=userdata!!.name
        Glide.with(this@Chat).load(userdata.pic).placeholder(R.drawable.round_person_2_24).into(binding.img)

        binding.btback.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }

        startService(Intent(this@Chat,ServiceChat::class.java))

        genchatkey=userdata.chat_key

        myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    msglist.clear()
                    if (genchatkey.isEmpty()){
                        genchatkey="1"
                        if (snapshot.hasChild("chat")){
                            genchatkey= (snapshot.child("chat").childrenCount+1).toString()
                        }
                    }else{
                        genchatkey=genchatkey
                    }

                    if (snapshot.hasChild("chat")){
                        if (snapshot.child("chat").child(genchatkey.toString()).hasChild("messages")){
                            for (snp in snapshot.child("chat").child(genchatkey.toString()).child("messages").children){
                                if (snp.hasChild("msg") && snp.hasChild("name")){
                                    msglist.add(MsgData(snp.child("msg").value.toString(),snp.key,snp.child("name").value.toString()))
                                    msgAdapter.notifyDataSetChanged()
                                    binding.rvmsg.scrollToPosition(msglist.size-1)
                                }
                            }
                        }else{
                            msgAdapter.notifyDataSetChanged()
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }

            })

        binding.btsend.setOnClickListener {
            if (binding.etmsg.text.toString().length>0){
                val time=System.currentTimeMillis()
                myRef.child("chat").child(genchatkey.toString()).child("user_1").setValue(
                    AppPreferences.getUserName(this@Chat))
                myRef.child("chat").child(genchatkey.toString()).child("user_2").setValue(userdata.name)
                myRef.child("chat").child(genchatkey.toString()).child("messages").child(time.toString()).child("msg").setValue(binding.etmsg.text.toString())
                myRef.child("chat").child(genchatkey.toString()).child("messages").child(time.toString()).child("name").setValue(AppPreferences.getUserName(this@Chat))
                binding.etmsg.text!!.clear()
            }
        }

        binding.btrecording.setOnTouchListener(object :OnTouchListener{
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                TODO("Not yet implemented")
            }
        })
    }
    fun popup(pos:Int, view: View){
       popupMenu = PopupMenu(this@Chat,view)
        val menuInflater:MenuInflater=popupMenu.menuInflater
        menuInflater.inflate(R.menu.popup,popupMenu.menu)
        val menuHelper: Any
        val argTypes:Class<*>
        try {
            val fMenuHelper: java.lang.reflect.Field = PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.setAccessible(true)
            menuHelper = fMenuHelper.get(popupMenu)
            argTypes = Boolean::class.javaPrimitiveType!!
            menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", argTypes)
                .invoke(menuHelper, true)
        } catch (e: Exception) {
        }
        popupMenu.setOnMenuItemClickListener { item ->
            val id = item!!.itemId
            when (id) {
                R.id.copy -> {
                    Toast.makeText(this@Chat, "Copied", Toast.LENGTH_LONG).show()
                }
                R.id.del -> {
                    myRef.child("chat").child(genchatkey).child("messages")
                        .child(msglist.get(pos).time).removeValue()
                    Toast.makeText(this@Chat, "Delete", Toast.LENGTH_LONG).show()
                }
            }
            false
        }
        popupMenu.show()
    }
}

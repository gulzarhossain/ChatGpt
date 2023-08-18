package com.project.chatgpt.Fragments
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.chatgpt.Adapters.AdapterUser
import com.project.chatgpt.Chat
import com.project.chatgpt.Model.UserData
import com.project.chatgpt.R
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.databinding.FragmentChatBinding
class ChatFragment(val mcontext: Context) : Fragment() {
    lateinit var binding: FragmentChatBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    val database = Firebase.database
    val myRef = database.getReferenceFromUrl("https://chatgpt-5941d-default-rtdb.firebaseio.com/")
    var keym: String = ""
    var time: String = ""
    var msg: String = ""
    var TYPE: String = ""


    lateinit var adapterUser: AdapterUser
    val list = ArrayList<UserData>()
    lateinit var ncontext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ncontext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        adapterUser = AdapterUser(ChatFragment(mcontext), ncontext, list)
        binding.rvuser.adapter = adapterUser

        firebaseAuth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            requireContext().applicationContext,
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (snap in snapshot.child("users").children) {
                    if (!snap.key.equals(
                            AppPreferences.getUserName(requireContext().applicationContext)
                                .replace(".", "")
                        )
                    ) {
                        if (snap.child("friendlist").value.toString().contains(
                                AppPreferences.getUserName(requireContext().applicationContext)
                                    .replace(".", "")
                            )
                        ) {
                            myRef.child("chat").addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot2: DataSnapshot) {
                                    var b = false
                                    var i = 0
                                    if (snapshot2.childrenCount > 0) {
                                        for (snap2 in snapshot2.children) {
                                            val user1 = snap2.child("user_1").value.toString()
                                            val user2 = snap2.child("user_2").value.toString()
                                            if (user1.equals(
                                                    AppPreferences.getUserName(requireContext().applicationContext).replace(".","")) &&
                                                user2.equals(snap.key.toString()) || user1.equals(
                                                    snap.key.toString()
                                                ) &&
                                                user2.equals(
                                                    AppPreferences.getUserName(
                                                        requireContext().applicationContext
                                                    ).replace(".","")
                                                )
                                            ) {
                                                keym = snap2.key.toString()
                                                if (snap2.hasChild("messages")){
                                                    for (snap3 in snap2.child("messages").children) {
                                                        time=snap3.key.toString()
                                                            msg=snap3.child("msg").value.toString()
                                                        TYPE=snap3.child("typ").value.toString()
                                                    }
                                            }
                                                i++
                                                b = true
                                            } else {
                                                i++
                                                if (i == snapshot2.childrenCount.toInt()) {
                                                    if (!b){
                                                        time=""
                                                        msg=""
                                                        keym = ""
                                                        TYPE = ""
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        time=""
                                        msg=""
                                        keym = ""
                                        TYPE = ""
                                    }
                                    list.add(UserData(snap.key!!, snap.child("pic").value.toString(),keym,msg,time,TYPE)
                                    )
                                    adapterUser.notifyDataSetChanged()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    fun Click(user: UserData) {
        val intent = Intent(mcontext, Chat::class.java)
        intent.putExtra("Bundle", user)
        mcontext.startActivity(intent)
    }
    fun Click2(v:View) {
        val v2: View = LayoutInflater.from(mcontext).inflate(R.layout.user_find_item, null)
        val po=PopupWindow(v2,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        po.showAtLocation(v,Gravity.TOP,0,0)
        v2.findViewById<MaterialButton>(R.id.bt).setOnClickListener {
            po.dismiss()
        }
    }
}
package com.project.chatgpt.Fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.chatgpt.Adapters.FriendAdapter
import com.project.chatgpt.Home
import com.project.chatgpt.Model.FriendData
import com.project.chatgpt.R
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.Utils.AppUtility
import com.project.chatgpt.databinding.FragmentFriendReqBinding

class FriendReq(val mcontext: Context) : Fragment() {
    lateinit var binding:FragmentFriendReqBinding
    lateinit var adapter: FriendAdapter
    val list=ArrayList<FriendData>()
    val database = Firebase.database
    val myRef = database.getReferenceFromUrl("https://chatgpt-5941d-default-rtdb.firebaseio.com/")
    var friendlist=""
    var request=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       binding=DataBindingUtil.inflate(inflater,R.layout.fragment_friend_req, container,false)
        adapter= FriendAdapter(this@FriendReq,2,mcontext,list)
        binding.rvuser.adapter=adapter

        return binding.root
    }
    override fun onResume() {
        super.onResume()
        GetFrnds()
    }
    fun GetFrnds(){
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (snap in snapshot.child("users").children){
                    if (snap.key.equals(AppPreferences.getUserName(mcontext).replace(".",""))) {
                        if (snap.hasChild("friendlist")){
                            friendlist=snap.child("friendlist").value.toString()
                        }
                        if (snap.hasChild("requests")){
                            if (snap.child("requests").value.toString().length>0){
                                myRef.addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onDataChange(snapshot2: DataSnapshot) {
                                        for (snap2 in snapshot2.child("users").children){
                                            if (snap.child("requests").value.toString().contains(snap2.key.toString())){
                                                if (snap2.hasChild("friendlist")){
                                                    list.add(FriendData(snap2.key!!,snap2.child("pic").value.toString(),snap2.child("friendlist").value.toString(),snap.child("requests").value.toString(),false))
                                                }else list.add(FriendData(snap2.key!!,snap2.child("pic").value.toString(),"",snap.child("requests").value.toString(),false))
                                                adapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                    }

                                })
                            }else {
                                adapter.notifyDataSetChanged()
                                AppUtility.SnacView(
                                    "You don't have any request yet.",
                                    binding.rvuser
                                )
                            }
                        }else {
                            adapter.notifyDataSetChanged()
                            AppUtility.SnacView("You don't have any request yet.", binding.rvuser)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    fun AcceptReq(pos:Int){
            myRef.child("users")
                .child(list[pos].name)
                .child("friendlist").setValue(list[pos].frndkey+AppPreferences.getUserName(mcontext).replace(".",""))

        myRef.child("users")
            .child(AppPreferences.getUserName(mcontext).replace(".",""))
            .child("friendlist").setValue(friendlist+list[pos].name)

        myRef.child("users")
            .child(AppPreferences.getUserName(mcontext).replace(".",""))
            .child("requests").setValue(list[pos].reqkey.replace(list[pos].name,"")).addOnCompleteListener {
                if (it.isSuccessful){
                    (activity as Home).ChangeTab()
                }else{
                    AppUtility.SnacView("Something went wrong!!",binding.etsrch)
                }
            }
    }
    fun RemoveReq(pos:Int){
        myRef.child("users")
            .child(AppPreferences.getUserName(mcontext).replace(".",""))
            .child("requests").setValue(list[pos].reqkey.replace(list[pos].name,""))
        GetFrnds()
    }
}
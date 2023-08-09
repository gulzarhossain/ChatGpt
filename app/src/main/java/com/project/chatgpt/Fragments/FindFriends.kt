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
import com.project.chatgpt.Model.FriendData
import com.project.chatgpt.R
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.databinding.FragmentFindFriendsBinding

class FindFriends(val mcontext: Context) : Fragment() {
    lateinit var binding:FragmentFindFriendsBinding
    lateinit var adapter: FriendAdapter
    val list=ArrayList<FriendData>()

    val database = Firebase.database
    val myRef = database.getReferenceFromUrl("https://chatgpt-5941d-default-rtdb.firebaseio.com/")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_find_friends, container, false)

        adapter= FriendAdapter(this,1,mcontext,list)
        binding.rvuser.adapter=adapter


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getFriends()
    }
    private fun getFriends() {
        binding.rvuser.visibility=View.GONE
        binding.shim.startShimmer()
        binding.shim.visibility=View.VISIBLE
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (snap in snapshot.child("users").children){
                    if (!snap.key.equals(AppPreferences.getUserName(mcontext).replace(".",""))) {
                        if (snap.hasChild("friendlist")) {
                            if (!snap.child("friendlist").value.toString()
                                    .contains(AppPreferences.getUserName(mcontext).replace(".", ""))
                            ) {
                                if (snap.hasChild("requests")) {
                                    if (snap.child("requests").value.toString().contains(
                                            AppPreferences.getUserName(mcontext).replace(".", "")
                                        )
                                    ) {
                                        list.add(
                                            FriendData(
                                                snap.key.toString(),
                                                snap.child("pic").value.toString(),
                                                snap.child("requests").value.toString(),
                                                "",
                                                true
                                            )
                                        )
                                    } else list.add(
                                        FriendData(
                                            snap.key.toString(),
                                            snap.child("pic").value.toString(),
                                            "",
                                            "",
                                            false
                                        )
                                    )

                                } else {
                                    list.add(
                                        FriendData(
                                            snap.key!!,
                                            snap.child("pic").value.toString(),
                                            "",
                                            "",
                                            false
                                        )
                                    )
                                }
                                adapter.notifyDataSetChanged()
                            }
                        }else{
                            if (snap.hasChild("requests")) {
                                if (snap.child("requests").value.toString().contains(
                                        AppPreferences.getUserName(mcontext).replace(".", "")
                                    )
                                ) {
                                    list.add(
                                        FriendData(
                                            snap.key.toString(),
                                            snap.child("pic").value.toString(),
                                            snap.child("requests").value.toString(),
                                            "",
                                            true
                                        )
                                    )
                                } else list.add(
                                    FriendData(
                                        snap.key.toString(),
                                        snap.child("pic").value.toString(),
                                        "",
                                        "",
                                        false
                                    )
                                )

                            } else {
                                list.add(
                                    FriendData(
                                        snap.key!!,
                                        snap.child("pic").value.toString(),
                                        "",
                                        "",
                                        false
                                    )
                                )
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                binding.shim.stopShimmer()
                binding.shim.visibility=View.GONE
                binding.rvuser.visibility=View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                binding.shim.stopShimmer()
                binding.shim.visibility=View.GONE
                binding.rvuser.visibility=View.VISIBLE
            }
        })
    }

    fun AddRequest(pos:Int){
        if (list[pos].isCheck){
            myRef.child("users")
                .child(list[pos].name)
                .child("requests").setValue(list[pos].frndkey.toString().replace(AppPreferences.getUserName(mcontext).replace(".",""),""))
            getFriends()
        }else{
            myRef.child("users")
                .child(list[pos].name)
                .child("requests").setValue(AppPreferences.getUserName(mcontext).replace(".",""))
            getFriends()
        }
    }
}
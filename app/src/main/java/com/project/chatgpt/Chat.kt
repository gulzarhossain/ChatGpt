package com.project.chatgpt

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.project.chatgpt.Adapters.MsgAdapter
import com.project.chatgpt.Model.MsgData
import com.project.chatgpt.Model.UserData
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
    var type=""
    lateinit var uriimg:Uri
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_chat)
        val window = this.window
        window.statusBarColor = Color.parseColor("#4A4A4A")

        val userdata = intent.getParcelableExtra<UserData>("Bundle")
        msgAdapter= MsgAdapter(this, msglist)
        binding.rvmsg.adapter=msgAdapter

        binding.name.text=userdata!!.name.replace("@gmailcom","")
        Glide.with(this@Chat).load(userdata.pic).placeholder(R.drawable.round_person_2_24).into(binding.img)

        binding.btback.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
        genchatkey=userdata.chat_key

        myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    msglist.clear()
                    if (genchatkey.isEmpty()){
                        genchatkey="1"
                        if (snapshot.hasChild("chat")){
                            for (snan in snapshot.child("chat").children){
                                if (snan.child("user_1").value.toString().contentEquals(AppPreferences.getUserName(this@Chat).replace(".","")) &&
                                    snan.child("user_2").value.toString().contentEquals(userdata.name) ||
                                    snan.child("user_1").value.toString().contentEquals(userdata.name) &&
                                    snan.child("user_2").value.toString().contentEquals(AppPreferences.getUserName(this@Chat).replace(".",""))){
                                    genchatkey=snan.key.toString()
                                }else genchatkey= (snapshot.child("chat").childrenCount+1).toString()
                            }
                        }
                    }else{
                        genchatkey=genchatkey
                    }

                    if (snapshot.hasChild("chat")){
                        if (snapshot.child("chat").child(genchatkey.toString()).hasChild("messages")){
                            for (snp in snapshot.child("chat").child(genchatkey.toString()).child("messages").children){
                                if (snp.hasChild("msg") && snp.hasChild("name")){
                                    msglist.add(MsgData(snp.child("msg").value.toString(),snp.child("typ").value.toString(),snp.key,snp.child("name").value.toString()))
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

        binding.etmsg.requestFocus()

      val et=object : TextInputEditText(this@Chat) {
          var mimeTypes: Array<String> = arrayOf("image/png",
              "image/gif",
              "image/jpeg",
              "image/webp")
          override fun onCreateInputConnection(editorInfo: EditorInfo): InputConnection {
              val ic: InputConnection? = super.onCreateInputConnection(editorInfo)
              EditorInfoCompat.setContentMimeTypes(editorInfo, mimeTypes)
              val callback = InputConnectionCompat.OnCommitContentListener { inputContentInfo, flags, opts ->
                  val lacksPermission = (flags and
                          InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0
                  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && lacksPermission) {
                      try {
                          inputContentInfo.requestPermission()
                      } catch (e: Exception) {
                          return@OnCommitContentListener false // return false if failed
                      }
                  }
                  var supported = false
                      for (mimeType in mimeTypes) {
                          if (inputContentInfo.description.hasMimeType(mimeType)) {
                              supported = true
                              type=inputContentInfo.description.getMimeType(0).replace("image/","")
                              binding.lyimg.animation=AnimationUtils.loadAnimation(this@Chat, com.facebook.R.anim.abc_fade_in)
                              binding.lyimg.visibility=View.VISIBLE
                              binding.imagesend.setImageURI(inputContentInfo.contentUri)
                              uriimg=inputContentInfo.contentUri
                              break
                          }
                      }
                      if (!supported) {
                          return@OnCommitContentListener false
                      }
                      true
                  }
              return InputConnectionCompat.createWrapper(ic!!, editorInfo, callback)
          }
      }

        et.layoutParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        et.background=getDrawable(R.drawable.etbg)
        et.setPadding(34,40,45,40)
        et.hint="Type a message"
        et.typeface=resources.getFont(R.font.calibri_normal)
        et.setHintTextColor(ColorStateList.valueOf(Color.WHITE))
        et.setTextColor(ColorStateList.valueOf(Color.WHITE))
        binding.lyedit.addView(et)
        et.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                binding.rvmsg.scrollToPosition(msglist.size-1)
                if (p0.toString().isNotEmpty()){
                    binding.btcam.animation=AnimationUtils.loadAnimation(this@Chat, com.facebook.R.anim.abc_fade_out)
                    binding.btcam.visibility=View.GONE
                    binding.btadd.animation=AnimationUtils.loadAnimation(this@Chat,com.facebook.R.anim.abc_fade_out)
                    binding.btadd.visibility=View.GONE
                }else{
                    binding.btadd.animation=AnimationUtils.loadAnimation(this@Chat, com.facebook.R.anim.abc_fade_in)
                    binding.btadd.visibility=View.VISIBLE
                    binding.btcam.animation=AnimationUtils.loadAnimation(this@Chat, com.facebook.R.anim.abc_fade_in)
                    binding.btcam.visibility=View.VISIBLE
                }
            }
        })

        binding.btsend.setOnClickListener {
            if (et.text.toString().isNotEmpty()){
                val time=System.currentTimeMillis()
                myRef.child("chat").child(genchatkey.toString()).child("user_1").setValue(
                    AppPreferences.getUserName(this@Chat).replace(".",""))
                myRef.child("chat").child(genchatkey.toString()).child("user_2").setValue(userdata.name)
                myRef.child("chat").child(genchatkey.toString()).child("messages").child(time.toString()).child("msg").setValue(et.text.toString())
                myRef.child("chat").child(genchatkey.toString()).child("messages").child(time.toString()).child("typ").setValue("text")
                myRef.child("chat").child(genchatkey.toString()).child("messages").child(time.toString()).child("name").setValue(AppPreferences.getUserName(this@Chat))
                et.text!!.clear()
            }
        }

        binding.btcam.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(this@Chat, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    val fileName = "temp.jpg"
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.TITLE, fileName)
                    uriimg = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    )!!
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimg)
                    caminten.launch(takePictureIntent)
                }else ActivityCompat.requestPermissions(this@Chat, arrayOf(Manifest.permission.CAMERA),500)
            } else if (checkAndRequestPermissions(this)) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val fileName = "temp.jpg"
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, fileName)
                uriimg = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimg)
                caminten.launch(takePictureIntent)
            }
        }
        binding.btadd.setOnClickListener {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
                pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }else{
                if (checkAndRequestPermissions(this)) {
                    pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }
        binding.addimg.setOnClickListener {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q) {
                pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }else{
                if (checkAndRequestPermissions(this)) {
                    pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }
        binding.btcloseimagely.setOnClickListener {
            binding.lyimg.animation=AnimationUtils.loadAnimation(this@Chat, com.facebook.R.anim.abc_fade_out)
            binding.lyimg.visibility=View.GONE
        }
        binding.btsendimg.setOnClickListener {
            binding.imgprogres.visibility=View.VISIBLE
            val time=System.currentTimeMillis()
            FirebaseStorage.getInstance().reference.child(AppPreferences.getUserName(this@Chat).replace(".","")).child("gif"+(time).toString()).putFile(uriimg).addOnSuccessListener  {
                it.storage.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful){
                        myRef.child("chat").child(genchatkey.toString()).child("user_1").setValue(
                            AppPreferences.getUserName(this@Chat).replace(".",""))
                        myRef.child("chat").child(genchatkey.toString()).child("user_2").setValue(userdata.name)
                        myRef.child("chat").child(genchatkey.toString()).child("messages").child(time.toString()).child("msg").setValue(it.result.toString())
                        myRef.child("chat").child(genchatkey.toString()).child("messages").child(time.toString()).child("typ").setValue(type)
                        myRef.child("chat").child(genchatkey.toString()).child("messages").child(time.toString()).child("name").setValue(AppPreferences.getUserName(this@Chat))
                        binding.imgprogres.visibility=View.GONE
                        binding.lyimg.animation=AnimationUtils.loadAnimation(this@Chat, com.facebook.R.anim.abc_fade_out)
                        binding.lyimg.visibility=View.GONE
                    }
                }
            }
        }
    }
    fun checkAndRequestPermissions(context: Activity?): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.CAMERA
        )
        val storagePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val storage2Permission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded: MutableList<String> = java.util.ArrayList()
        listPermissionsNeeded.clear()
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (storage2Permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                context, listPermissionsNeeded
                    .toTypedArray(),
                111
            )
            return false
        }
        return true
    }

    val pickImg=registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if (uri!=null){
            binding.lyimg.animation=AnimationUtils.loadAnimation(this@Chat, com.facebook.R.anim.abc_fade_in)
            binding.lyimg.visibility=View.VISIBLE
            binding.imagesend.setImageURI(uri)
            uriimg=uri
            type="png"
        }
    }
    val caminten=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK) {
            Log.e("URI",uriimg.toString())
            if (uriimg!=null){
                binding.lyimg.animation=AnimationUtils.loadAnimation(this@Chat, com.facebook.R.anim.abc_fade_in)
                binding.lyimg.visibility=View.VISIBLE
                binding.imagesend.setImageURI(uriimg)
                type="png"
            }
//            val projection = arrayOf(MediaStore.Images.Media.DATA)
//            val cursor = contentResolver.query(imageUri, projection, null, null, null)
//            val index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            cursor.moveToFirst()
//            val file = File(cursor.getString(index))
//            ADDRESSF = BitmapFactory.decodeFile(file.path)
//            adrssF = com.project.sdbloan.Activity.MemberDocs.encodeToBase64(
//                ADDRESSF,
//                Bitmap.CompressFormat.JPEG,
//                100
//            )
//            imglist.get(requestCode - 1).setImg(adrssF)
//            imageAdapter.notifyItemChanged(requestCode - 1)
        }
    }

    fun popup(pos:Int, view: View,boolean: Boolean){
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
        popupMenu.menu.findItem(R.id.del).isEnabled = boolean
        popupMenu.menu.findItem(R.id.copy).isEnabled = msglist[pos].typ.equals("text")

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item!!.itemId
            when (id) {
                R.id.copy -> {
                    Toast.makeText(this@Chat, "Copied", Toast.LENGTH_LONG).show()
                }
                R.id.del -> {
                    if (msglist[pos].typ.equals("text")) {
                        myRef.child("chat").child(genchatkey).child("messages")
                            .child(msglist.get(pos).time).removeValue()
                    }else{
                        myRef.child("chat").child(genchatkey).child("messages")
                            .child(msglist.get(pos).time).removeValue()
                        FirebaseStorage.getInstance().reference.child(AppPreferences.getUserName(this@Chat).replace(".","")).child("gif"+(msglist[pos].time).toString()).delete()
                        }
                    Toast.makeText(this@Chat, "Delete", Toast.LENGTH_LONG).show()
                }
            }
            false
        }
        popupMenu.show()
    }
}

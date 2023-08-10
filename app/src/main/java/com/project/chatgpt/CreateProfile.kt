package com.project.chatgpt

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.Utils.AppUtility
import com.project.chatgpt.databinding.ActivityCreateProfileBinding
import java.io.ByteArrayOutputStream
import java.io.FileDescriptor

class CreateProfile : AppCompatActivity(), TextWatcher {
    lateinit var binding: ActivityCreateProfileBinding
    private val myRef = Firebase.database.getReferenceFromUrl("https://chatgpt-5941d-default-rtdb.firebaseio.com/")

    var b:Boolean=false
    var i:Boolean=false
    var rec:Int=0
    lateinit var dialog:AlertDialog
    var img:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_create_profile)
        binding.ly.layoutParams.height=(AppUtility.getScreenResolusion(this@CreateProfile,"w")/2)-50

        dialog=AlertDialog.Builder(this@CreateProfile).create()
        dialog.setIcon(R.drawable.round_mic_24)
        dialog.setTitle("Recordind..")
        dialog.setCancelable(false)

        binding.btaddpic.setOnClickListener {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.cnfpass.addTextChangedListener(this)
        binding.etpass.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
               binding.cnfpass.text!!.clear()
                binding.cnfpassly.error=""
            }
        })

        val provider = OAuthProvider.newBuilder("twitter.com")

     binding.btlog.setOnClickListener {
         val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
         imm.hideSoftInputFromWindow(binding.btlog.windowToken, 0)
         if (b){
             if (i) {
                 if (binding.email.text.toString().isNotEmpty() && binding.name.text.toString()
                         .isNotEmpty()
                 ) {
                     myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                         override fun onDataChange(snapshot: DataSnapshot) {
                             if (snapshot.child("users")
                                     .hasChild(binding.name.text.toString())
                             ) {
                                 AppPreferences.setUserName(
                                     this@CreateProfile,
                                     binding.name.text.toString()
                                 )
                                 val intent: Intent =
                                     Intent(this@CreateProfile, Home::class.java)
                                 startActivity(intent)
                             } else {
                                 AppPreferences.setUserName(this@CreateProfile, binding.name.text.toString())
                                 AppPreferences.setUserLoginType(this@CreateProfile, "T")
                                 myRef.child("users")
                                     .child(binding.name.text.toString())
                                     .child("pic").setValue(img)
                                 myRef.child("users")
                                     .child(binding.name.text.toString())
                                     .child("email").setValue(binding.email.text.toString())
                                 myRef.child("users")
                                     .child(binding.name.text.toString())
                                     .child("pass").setValue(binding.cnfpass.text.toString())
                                 val intent: Intent =
                                     Intent(this@CreateProfile, Home::class.java)
                                 startActivity(intent)
                             }
                         }

                         override fun onCancelled(error: DatabaseError) {

                         }
                     })
                 } else {
                     AppUtility.SnacView("Enter Name & Email",binding.btlog)
                 }
             }else{
                 AppUtility.SnacView("Add Photo",binding.btlog)
             }
         }else{
             binding.etpass.requestFocus()
             AppUtility.SnacView("Create password",binding.btlog)
         }
     }
        binding.bttwit.setOnClickListener {
            FirebaseAuth.getInstance().startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener {
                    binding.name.setText(it.user!!.displayName.toString())
                    binding.email.setText(it.user!!.email.toString())
                    Glide.with(this@CreateProfile).load(it.user!!.photoUrl.toString()).addListener(object :RequestListener<Drawable>{
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
                        ): Boolean {
                            i=false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            i=true
                            img=it.user!!.photoUrl.toString()
                            return false
                        }

                    }).into(binding.imgpr)
                }
                .addOnFailureListener {

                }
        }
          binding.btrecording.setOnTouchListener(object :OnTouchListener{
              override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                  if (p1!!.action==2){
                      if (rec==0){
                          if (checkperm()) {
                              rec = 2
                              dialog.show()
                          }
                      }
                  }else{
                      rec=0
                      dialog.dismiss()
                  }
                  return false
              }
          })
    }
    fun checkperm():Boolean{
        var hc=false
        if (ContextCompat.checkSelfPermission(this@CreateProfile,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){
            hc=true
        }else{
            hc=false
            ActivityCompat.requestPermissions(this@CreateProfile, arrayOf(Manifest.permission.RECORD_AUDIO),1122)
        }
        return hc
    }
    val permissionlauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
           dialog.show()
        }
    }
    private val launcher= registerForActivityResult(ActivityResultContracts.PickVisualMedia()){
        if (it!=null){
            binding.imgpr.setImageURI(it)
            img=BitMapToString(it)
            i=true
        }
    }
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }
    override fun afterTextChanged(p0: Editable?) {
        binding.cnfpassly.error=""
        if (p0.toString().isNotEmpty()){
            if (p0.toString().equals(binding.etpass.text.toString())){
                b=true
            }else{
                b=false
                binding.cnfpassly.setErrorTextColor(ColorStateList.valueOf(android.graphics.Color.RED))
                binding.cnfpassly.error="Password didn't matched"
            }
        }
    }
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        return image
    }
    fun BitMapToString(uri: Uri): String {
        val baos = ByteArrayOutputStream()
        getBitmapFromUri(uri).compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}
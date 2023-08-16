package com.project.chatgpt

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.storage.FirebaseStorage
import com.project.chatgpt.Adapters.ShowCaseAdapter
import com.project.chatgpt.Model.ShowCaseData
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.Utils.AppUtility
import com.project.chatgpt.databinding.ActivityShowcaseBinding

class Showcase : AppCompatActivity() {
    lateinit var binding:ActivityShowcaseBinding
    lateinit var adapter: ShowCaseAdapter
    val list=ArrayList<ShowCaseData>()
    var p=0
    var tr=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_showcase)
        window.statusBarColor = Color.WHITE
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        list.add(ShowCaseData(null,false))
        list.add(ShowCaseData(null,false))

        adapter= ShowCaseAdapter(this,list)
        binding.rv.adapter=adapter

        binding.addmore.setOnClickListener {
            if (list.size==4) {
                AppUtility.SnacView("Can't add more than four.",binding.addmore)
            }else if (list.size==3) {
                list.add(ShowCaseData(null, false))
                adapter.notifyItemInserted(list.size)
            }else{
                list.add(ShowCaseData(null, false))
                list.add(ShowCaseData(null, false))
                adapter.notifyItemInserted(list.size)
            }
        }
        binding.btback.setOnClickListener {

        }
        binding.btsub.setOnClickListener {
            var flag=true
            for (i in 0 until list.size){
                if (list[i].uri==null){
                    flag=false
                    if (i==list.size-1){
                        AppUtility.SnacView("Can't Upload Empty Showcase",binding.btsub)
                    }
                }else if (i==list.size-1){
                    if (flag){
                        UploadShowcase()
                    }else AppUtility.SnacView("Can't Upload Empty Showcase",binding.btsub)
                }
            }
        }
    }

    fun UploadShowcase() {
        if (tr==list.size){
            AppUtility.SnacView("Upload Complete",binding.btsub)
            onBackPressedDispatcher.onBackPressed()
            finish()
        }else{
            list[tr].isCheck=true
            FirebaseStorage.getInstance().reference.child(AppPreferences.getUserName(this).replace(".","")).child("Showcase-"+(tr+1).toString()).putFile(list[tr].uri).addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                list[tr].progrs=progress.toInt()
                adapter.notifyItemChanged(tr)
                if (progress.toInt()==100) {
                    tr++
                    UploadShowcase()
                }
            }
        }
    }

    fun PickerPhoto(pos:Int){
        p=pos
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }else{
            if (checkAndRequestPermissions(this)){
                pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }    }
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
            list[p].uri=uri
            adapter.notifyItemChanged(p)
        }
    }

    fun DeletePhoto(pos: Int){
        if (list.size>1){
            list.removeAt(pos)
            adapter.notifyItemRemoved(pos)
            object :CountDownTimer(1000,1000){
                override fun onTick(p0: Long) {

                }

                override fun onFinish() {
                    adapter.notifyDataSetChanged()
                }
            }.start()
        }else AppUtility.SnacView("Can't Delete",binding.btsub)
    }
}
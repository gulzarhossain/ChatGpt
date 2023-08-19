package com.project.chatgpt
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.project.chatgpt.Adapters.ProfileAdapter
import com.project.chatgpt.Model.ImageData
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.Utils.AppUtility
import com.project.chatgpt.databinding.ActivityProfileBinding
class Profile : AppCompatActivity() {
    lateinit var binding:ActivityProfileBinding
    lateinit var adapter: ProfileAdapter
    val list = ArrayList<ImageData>()
    lateinit var storageReference:StorageReference
    val myRef = Firebase.database.getReferenceFromUrl("https://chatgpt-5941d-default-rtdb.firebaseio.com/")
    var ch=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_profile)
        window.statusBarColor=Color.parseColor("#33000000")

        myRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (sanp in snapshot.child("users").children){
                    if (sanp.key.equals(intent.getStringExtra("ID"))){
                        binding.email.text=sanp.child("email").value.toString()
                        binding.name.text=sanp.child("name").value.toString()
                        Glide.with(this@Profile).load(sanp.child("pic").value.toString()).listener(object :RequestListener<Drawable>{
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.pbar.visibility=View.GONE
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.pbar.visibility=View.GONE
                                return false
                            }
                        }).into(binding.img)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        storageReference=FirebaseStorage.getInstance().reference

        binding.vpager.layoutParams.height=AppUtility.getScreenResolusion(this,"h")/2

        adapter=ProfileAdapter(this,list)
        binding.vpager.adapter=adapter

        binding.vpager.offscreenPageLimit = 1

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
//             page.alpha = 0.25f + (1 - kotlin.math.abs(position))
        }
        binding.vpager.setPageTransformer(pageTransformer)

        val itemDecoration = HorizontalMarginItemDecoration(this, R.dimen.viewpager_current_item_horizontal_margin)
        binding.vpager.addItemDecoration(itemDecoration)

        binding.btupdate.setOnClickListener {
            startActivity(Intent(this,Showcase::class.java))
        }
        binding.img.setOnClickListener {
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.S) {
                pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }else{
                if (checkAndRequestPermissions(this)) {
                    pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            }
        }
        binding.btback.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            finish()
        }
        if (!AppPreferences.getUserName(this@Profile).replace(".","").equals(intent.getStringExtra("ID"))){
            binding.btupdate.visibility=View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        ch=false
        list.clear()
        storageReference.child(intent.getStringExtra("ID").toString()).listAll().addOnSuccessListener { it ->
            if (it.items.size>0) {
                    for (i in 0 until it.items.size) {
                            if (it.items[i].name.contains("Showcase")){
                                it.items[i].downloadUrl.addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        ch=true
                                        list.add(ImageData(it.result.toString(), 0))
                                    }
                                    adapter.notifyDataSetChanged()
                                }
                            }else if (!ch && i==0){
                                list.add(ImageData("",R.drawable.pose1))
                                list.add(ImageData("",R.drawable.pose2))
                                list.add(ImageData("",R.drawable.pose3))
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }else{
                list.add(ImageData("",R.drawable.pose1))
                list.add(ImageData("",R.drawable.pose2))
                list.add(ImageData("",R.drawable.pose3))
            }
            adapter.notifyDataSetChanged()
        }
    }
    override fun onBackPressed() {
        onBackPressedDispatcher.onBackPressed()
        finish()
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
            permissn.launch(listPermissionsNeeded.toTypedArray())
            return false
        }
        return true
    }

    val pickImg=registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if (uri!=null){
            binding.pbar.visibility=View.VISIBLE
            storageReference.child(intent.getStringExtra("ID").toString()).child("profilepic").putFile(uri).addOnSuccessListener {
                it.storage.downloadUrl.addOnCompleteListener {
                    if (it.isSuccessful){
                        binding.pbar.visibility=View.GONE
                        myRef.child("users")
                            .child(intent.getStringExtra("ID").toString())
                            .child("pic").setValue(it.result.toString())
                        Glide.with(this).load(it.result.toString()).skipMemoryCache(true).into(binding.img)
                    }
                }
            }
        }
    }
    val permissn=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){it1->
        var tr=0
        var chek=true
        it1.entries.forEach {
            tr++
            if (!it.value){
                chek=false
            }
            if (tr==it1.size && chek){
                pickImg.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }
    class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) : RecyclerView.ItemDecoration() {
        private val horizontalMarginInPx: Int =
            context.resources.getDimension(horizontalMarginInDp).toInt()
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            outRect.right = horizontalMarginInPx
            outRect.left = horizontalMarginInPx
        }

    }
}
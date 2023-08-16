package com.project.chatgpt

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.project.chatgpt.Utils.AppPreferences
import com.project.chatgpt.Utils.AppUtility
import com.project.chatgpt.databinding.ActivityLoginBinding
import org.json.JSONObject

class Login : AppCompatActivity(), OnCompleteListener<AuthResult>, OnFailureListener {
    lateinit var binding: ActivityLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

    val database = Firebase.database
    val myRef = database.getReferenceFromUrl("https://chatgpt-5941d-default-rtdb.firebaseio.com/")
    val callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.clientID))
            .requestProfile().requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this@Login, gso)

        binding.fbbtn.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {
                AppUtility.SnacView("Error Occurred!!", binding.fbbtn)
            }

            override fun onSuccess(result: LoginResult) {
                val graphRequest =
                    GraphRequest.newMeRequest(result.accessToken) { `object`, response ->
                        val jsonObject = JSONObject(`object`!!.getString("picture"))
                        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.child("users")
                                        .hasChild(`object`.getString("name"))
                                ) {
                                    AppPreferences.setUserName(
                                        this@Login,
                                        `object`.getString("name")
                                    )
                                    val intent =
                                        Intent(this@Login, Home::class.java)
                                    intent.putExtra("acc", `object`.getString("name"))
                                    AppPreferences.setUserLoginStatus(this@Login, true)
                                    AppPreferences.setUserLoginType(this@Login, "FB")
                                    startActivity(intent)
                                } else {
                                    AppPreferences.setUserName(
                                        this@Login,
                                        `object`.getString("name")
                                    )
                                    myRef.child("users")
                                        .child(`object`.getString("name"))
                                        .child("name").setValue(`object`.getString("name"))
                                    myRef.child("users")
                                        .child(`object`.getString("name"))
                                        .child("pic")
                                        .setValue(jsonObject.getJSONObject("data").getString("url"))
                                    myRef.child("users")
                                        .child(`object`.getString("name"))
                                        .child("email").setValue(`object`.getString("name"))
                                    val intent =
                                        Intent(this@Login, Home::class.java)
                                    intent.putExtra("acc", `object`.getString("name"))
                                    AppPreferences.setUserLoginType(this@Login, "FB")
                                    AppPreferences.setUserLoginStatus(this@Login, true)
                                    startActivity(intent)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }
                        })
                    }
                val parameters = Bundle()
                parameters.putString("fields", "id,email,birthday,picture,name")
                graphRequest.parameters = parameters
                graphRequest.executeAsync()
            }
        })

        binding.btfb.setOnClickListener {
            binding.fbbtn.performClick()
        }

        binding.btggl.setOnClickListener {
            binding.btggl.text = ""
            binding.pbarggl.visibility = View.VISIBLE
            binding.btggl.icon = null
            launcher.launch(googleSignInClient.signInIntent)
        }
        binding.btlog.setOnClickListener {
            binding.btlog.text = ""
            binding.pbar.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(
                binding.etemail.text.toString(),
                binding.etypass.text.toString()
            )
                .addOnCompleteListener(this)
                .addOnFailureListener(this)
        }
        binding.etemail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.lyemail.error = ""
                binding.lypass.error = ""
            }
        })
        binding.etypass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.lypass.error = ""
                binding.lyemail.error = ""
            }
        })

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                if (task.isSuccessful && task.result != null) {
                    val credential = GoogleAuthProvider.getCredential(task.result.idToken, null)
                    firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.child("users")
                                            .hasChild(task.result.email.toString().replace(".", ""))
                                    ) {
                                        AppPreferences.setUserName(
                                            this@Login,
                                            task.result.email.toString()
                                        )
                                        val intent =
                                            Intent(this@Login, Home::class.java)
                                        intent.putExtra("acc", task.result.displayName)
                                        AppPreferences.setUserLoginType(this@Login, "GGL")
                                        AppPreferences.setUserLoginStatus(this@Login, true)
                                        startActivity(intent)
                                    } else {
                                        AppPreferences.setUserName(
                                            this@Login,
                                            task.result.email.toString()
                                        )
                                        myRef.child("users")
                                            .child(task.result.email.toString().replace(".", ""))
                                            .child("name")
                                            .setValue(task.result.displayName.toString())
                                        myRef.child("users")
                                            .child(task.result.email.toString().replace(".", ""))
                                            .child("pic").setValue(task.result.photoUrl.toString())
                                        myRef.child("users")
                                            .child(task.result.email.toString().replace(".", ""))
                                            .child("email").setValue(task.result.email.toString())
                                        val intent =
                                            Intent(this@Login, Home::class.java)
                                        intent.putExtra("acc", task.result.email)
                                        AppPreferences.setUserLoginType(this@Login, "GGL")
                                        AppPreferences.setUserLoginStatus(this@Login, true)
                                        startActivity(intent)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    binding.btggl.text = "Login with Google"
                                    binding.pbarggl.visibility = View.GONE
                                    binding.btggl.icon =
                                        resources.getDrawable(R.drawable.google, null)
                                }
                            })
                        } else {
                            Toast.makeText(this@Login, "Not Complete!!", Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this@Login, "Failed!!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    binding.btggl.text = "Login with Google"
                    binding.pbarggl.visibility = View.GONE
                    binding.btggl.icon = resources.getDrawable(R.drawable.google, null)
                }
            } else {
                binding.btggl.text = "Login with Google"
                binding.pbarggl.visibility = View.GONE
                binding.btggl.icon = resources.getDrawable(R.drawable.google, null)
            }
        }

    override fun onComplete(p0: Task<AuthResult>) {
        if (p0.isSuccessful) {
            if (!p0.result.user!!.email!!.isEmpty()) {
                myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.child("users").hasChild(firebaseAuth.currentUser!!.email.toString().replace(".", ""))) {
                            AppPreferences.setUserName(this@Login, firebaseAuth.currentUser!!.email.toString())
                            val intent = Intent(this@Login, Home::class.java)
                            intent.putExtra("acc", firebaseAuth.currentUser!!.email.toString().substring(0, 6))
                            AppPreferences.setUserLoginType(this@Login, "EML")
                            AppPreferences.setUserLoginStatus(this@Login, true)
                            startActivity(intent)
                        } else {
                            AppPreferences.setUserName(
                                this@Login,
                                firebaseAuth.currentUser!!.email.toString()
                            )
                            myRef.child("users")
                                .child(firebaseAuth.currentUser!!.email.toString().replace(".", ""))
                                .child("name").setValue(
                                    firebaseAuth.currentUser!!.email.toString().substring(0, 6)
                                )
                            myRef.child("users")
                                .child(firebaseAuth.currentUser!!.email.toString().replace(".", ""))
                                .child("pic")
                                .setValue(firebaseAuth.currentUser!!.photoUrl.toString())
                            myRef.child("users")
                                .child(firebaseAuth.currentUser!!.email.toString().replace(".", ""))
                                .child("email")
                                .setValue(firebaseAuth.currentUser!!.email.toString())

                            val intent = Intent(this@Login, Home::class.java)
                            intent.putExtra(
                                "acc",
                                firebaseAuth.currentUser!!.email.toString().substring(0, 6)
                            )
                            AppPreferences.setUserLoginType(this@Login, "EML")
                            AppPreferences.setUserLoginStatus(this@Login, true)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
        }
    }

    override fun onFailure(p0: Exception) {
        Log.e("Failed", p0.message.toString())
        if (p0.message!!.contains("already in use", true)) {
            Log.e("Failed2", p0.message.toString())

            firebaseAuth.signInWithEmailAndPassword(
                binding.etemail.text.toString(),
                binding.etypass.text.toString()
            ).addOnCompleteListener(this)
                .addOnFailureListener(this)
        } else {
            binding.btlog.text = "Login"
            binding.pbar.visibility = View.GONE
            val dialog = MaterialAlertDialogBuilder(this).create()
            dialog.setIcon(R.drawable.google)
            dialog.setCancelable(false)
            dialog.setTitle("Login failed!")
            dialog.setMessage(p0.message)
            dialog.setButton(
                AlertDialog.BUTTON_POSITIVE,
                "Retry",
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        dialog.dismiss()
                    }
                })
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}

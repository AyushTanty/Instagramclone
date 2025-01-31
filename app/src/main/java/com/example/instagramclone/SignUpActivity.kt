package com.example.instagramclone

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.instagramclone.Models.User
import com.example.instagramclone.databinding.ActivitySignUpBinding
import com.example.instagramclone.utils.USER_PROFILE_FOLDER
import com.example.instagramclone.utils.uploadImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class SignUpActivity : AppCompatActivity() {
    val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }
    lateinit var user: User
    private val launcher= registerForActivityResult(ActivityResultContracts.GetContent()){
        uri->
        uri?.let {
             uploadImage(uri, USER_PROFILE_FOLDER){
                if (it==null){

                }else{
                    user.image=it
                    binding.profileImage.setImageURI(uri)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val text = "<font color=#FF000000>Already have an Account</font> <font color=#1E88E5>Login ?</font>"
        binding.login.setText(Html.fromHtml(text))
        user = User()
        binding.signUpBtn.setOnClickListener {
            if (binding.name.editText?.text.toString().equals("") or
                binding.email.editText?.text.toString().equals("") or
                binding.password.editText?.text.toString().equals("")
            ) {
                Toast.makeText(
                    this@SignUpActivity,
                    "please fill all the information",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.email.editText?.text.toString(),
                    binding.password.editText?.text.toString()
                ).addOnCompleteListener {
                    result ->
                    if (result.isSuccessful) {
                        user.name = binding.name.editText?.text.toString()
                        user.email = binding.email.editText?.text.toString()
                        user.password = binding.password.editText?.text.toString()
                        Firebase.firestore.collection("USER_NODE")
                            .document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                               startActivity(Intent(this@SignUpActivity,HomeActivity::class.java))
                                finish()
                            }
                        Toast.makeText(
                            this@SignUpActivity,
                            "Account Created Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            result.exception?.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }
            }
        }
        binding.addImage.setOnClickListener{
            launcher.launch("image/*")
        }
    }
}


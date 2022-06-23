package com.example.snapchatclone

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? =null
    var passEditText: EditText? =null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emailEditText = findViewById(R.id.emailEditText);
        passEditText = findViewById(R.id.passEditText);

        if(mAuth.currentUser != null ) //checks if logged in already
        {  logIn()
        }
    }

    fun goButtonClick(view: View){

        //check if can log in the existing user else sign up
        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, "signInWithEmail:success")
                    logIn()
                } else {
                    // If sign in fails, sign up the user
                    mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passEditText?.text.toString())
                        .addOnCompleteListener(this) { task ->
                            if(task.isSuccessful)
                            {
                                //add to database
                                FirebaseDatabase.getInstance().getReference().child("users").child(
                                    task.result.user!!.uid)
                                    .child("email").setValue(emailEditText?.text.toString())


                                logIn()

                            }else{
                                Log.e(TAG,"Login Failed. Try Again.")
                                Toast.makeText(baseContext,"Login Failed. Try Again.",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }



    }

    fun logIn(){
        //move to next activity
        val intent = Intent(this,SnapsActivity :: class.java)
        startActivity(intent)
    }
}

// Firebase Authentication to allow users to sign in to your app using one or more sign-in methods,
// including email address and password sign-in, and federated identity providers such as
// Google Sign-in and Facebook Login.


//var is like general variable and it's known as a mutable variable in kotlin and can be assigned
//multiple times. val is like Final variable and it's known as immutable in kotlin and can be
//initialized only single time.
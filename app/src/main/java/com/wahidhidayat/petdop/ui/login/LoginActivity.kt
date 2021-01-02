package com.wahidhidayat.petdop.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.okhttp.ResponseBody
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.User
import com.wahidhidayat.petdop.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    companion object {
        const val TAG = "LoginActivity"
        const val RC_SIGN_IN: Int = 101
    }

    private var db = FirebaseFirestore.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        btn_google.setOnClickListener {
            pb_login.visibility = View.VISIBLE
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun signIn() {
        googleSignInClient.signOut()
        pb_login.visibility = View.GONE

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        pb_login.visibility = View.VISIBLE

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // hide loading
                    pb_login.visibility = View.GONE

                    val user = auth.currentUser
                    val name = user?.displayName
                    val email = user?.email
                    val avatar = user?.photoUrl.toString()
                    val phone = user?.phoneNumber
                    val address = null

                    val userReference = FirebaseFirestore.getInstance().collection("users")
                    val query = userReference.whereEqualTo("email", email)
                    query.get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            for (documentSnapshot: DocumentSnapshot in it.result!!) {
                                val userEmail = documentSnapshot.get("email")
                                if (userEmail?.equals(email)!!) {
                                    startActivity(Intent(this, MainActivity::class.java))
                                }
                            }
                        }

                        if (it.result?.size() == 0) {
                            Log.i(TAG, "user not exist")
                            if (email != null && name != null) {
                                register(email, name, phone, address, avatar)
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Something went wrong...")
                }
            }
    }

    private fun register(
        email: String,
        name: String,
        phone: String?,
        address: String?,
        avatar: String
    ) {
        val user = User(email, name, phone, address, avatar)
        db.collection("users").document(user.email)
            .set(user)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener {
                Log.e(TAG, it.toString())
            }
    }

}
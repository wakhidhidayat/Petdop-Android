package com.wahidhidayat.petdop.ui.account

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.ui.login.LoginActivity
import com.wahidhidayat.petdop.ui.login.LoginActivity.Companion.TAG
import kotlinx.android.synthetic.main.fragment_account.*
import java.util.*


class AccountFragment : Fragment() {
    companion object {
        const val CAMERA_PERMISSION_CODE = 101
        const val CAMERA_REQUEST_CODE = 102
    }

    private val mUser = FirebaseAuth.getInstance().currentUser
    private val mDb = FirebaseFirestore.getInstance()
    private val userRef = mDb.document("users/${mUser?.email.toString()}")
    private val mStorageRef = FirebaseStorage.getInstance().reference
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this.activity!!, gso)

        userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    getUser(
                            documentSnapshot.getString("name"),
                            documentSnapshot.getString("email"),
                            documentSnapshot.getString("phone"),
                            documentSnapshot.getString("address"),
                            documentSnapshot.getString("avatar")
                    )
                    if (pb_account != null) {
                        pb_account.visibility = View.GONE
                    }

                    Log.d(
                            "AccountFragment",
                            "DocumentSnapshot data: ${documentSnapshot.getString("name")}."
                    )
                }
                .addOnFailureListener { exception ->
                    Log.d("AccountFragment", "get failed with ", exception)
                }

        btn_camera.setOnClickListener {
            askCameraPermission()
        }

        btn_update_user.setOnClickListener {
            if (pb_account != null) {
                pb_account.visibility = View.VISIBLE
            }

            userRef.update(
                    mapOf(
                            "name" to et_name.text.toString(),
                            "address" to et_address.text.toString(),
                            "phone" to et_phone.text.toString()
                    )
            ).addOnSuccessListener {
                if (pb_account != null) {
                    pb_account.visibility = View.GONE
                }

                Toast.makeText(activity, "Profil berhasil di update!", Toast.LENGTH_SHORT).show()
            }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        }

        btn_logout.setOnClickListener {
            // firebase signout
            FirebaseAuth.getInstance().signOut()

            // google signout
            mGoogleSignInClient.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity!!.finish()
        }
    }

    private fun askCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                        this.context!!,
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this.activity!!,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
            )
        } else {
            capturePhoto()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto()
            } else {
                Toast.makeText(activity, "Camera Permission is required!", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (pb_account != null) {
                pb_account.visibility = View.VISIBLE
            }
            val imageUri = data?.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            image_avatar.setImageBitmap(imageBitmap)

            if (imageUri != null) {
                val imageRef = mStorageRef.child("images/${UUID.randomUUID()}")

                imageRef.putFile(imageUri)
                        .addOnSuccessListener {
                            if (pb_account != null) {
                                pb_account.visibility = View.GONE
                            }
                            Toast.makeText(activity, "Update foto sukses!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            if (pb_account != null) {
                                pb_account.visibility = View.GONE
                            }
                            Log.e("AccountFragment", it.toString())
                        }
            } else {
                if (pb_account != null) {
                    pb_account.visibility = View.GONE
                }
            }
            Log.d("imageUri", imageUri.toString())
        }
    }

    private fun getUser(
            name: String?,
            email: String?,
            phone: String?,
            address: String?,
            avatar: String?
    ) {
        if (name != null) {
            et_name.setText(name)
        } else {
            et_name.setText("")
        }

        if (et_address != null) {
            et_address.setText(address)
        } else {
            et_address.setText("")
        }

        if (phone != null) {
            et_phone.setText(phone)
        } else {
            et_phone.setText("")
        }

        if (et_email != null) {
            et_email.setText(email)
        } else {
            et_email.setText("")
        }

        if (avatar != null) {
            Glide.with(this)
                    .load(avatar)
                    .into(image_avatar)
        }
    }
}
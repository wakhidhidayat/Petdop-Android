package com.wahidhidayat.petdop.ui.account

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wahidhidayat.petdop.R
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
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userRef.get()
            .addOnSuccessListener { documentSnapshot ->
                getUser(
                    documentSnapshot.getString("name"),
                    documentSnapshot.getString("email"),
                    documentSnapshot.getString("phone"),
                    documentSnapshot.getString("address"),
                    documentSnapshot.getString("avatar")
                )
                pb_account.visibility = View.GONE
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
            pb_account.visibility = View.VISIBLE

            userRef.update(
                mapOf(
                    "name" to et_name.text.toString(),
                    "address" to et_address.text.toString(),
                    "phone" to et_phone.text.toString()
                )
            ).addOnSuccessListener {
                pb_account.visibility = View.GONE
                Toast.makeText(activity, "Profil berhasil di update!", Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

            fileUpload()
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
        if (requestCode == CAMERA_REQUEST_CODE) {
            imageUri = data?.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            image_avatar.setImageBitmap(imageBitmap)
        }
    }

    private fun fileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    private fun fileUpload() {
        val imageRef = mStorageRef.child("avatars/${UUID.randomUUID()}")
        imageRef.putFile(imageUri!!)
            .addOnSuccessListener {
                Toast.makeText(activity, "Upload success!", Toast.LENGTH_SHORT).show()
                imageRef.downloadUrl.addOnSuccessListener {
                    Log.d("uri", it.toString())
                }
            }
            .addOnFailureListener {
                Log.e("AccountFragment", it.toString())
            }
    }

    private fun getUser(
        name: String?,
        email: String?,
        phone: String?,
        address: String?,
        avatar: String?
    ) {
        et_name.setText(name)
        et_address.setText(address)
        et_phone.setText(phone)
        et_email.setText(email)

        if (avatar != null) {
            Glide.with(this)
                .load(avatar)
                .into(image_avatar)
        }
    }
}
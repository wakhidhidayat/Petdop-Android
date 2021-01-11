package com.wahidhidayat.petdop.ui.upload

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.ui.account.AccountFragment
import kotlinx.android.synthetic.main.fragment_upload.*


class UploadFragment : Fragment() {
    companion object {
        const val RESULT_LOAD_IMAGE = 1
    }

    private val mPostRef = FirebaseFirestore.getInstance().collection("posts")
    private val mUserRef = FirebaseFirestore.getInstance().collection("users")
    private val mUser = FirebaseAuth.getInstance().currentUser

    private val mStorageRef = FirebaseStorage.getInstance().reference

    private val mListName: MutableList<String> = mutableListOf()
    private val mAdapter = ImageAdapter(mListName)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = mPostRef.document().id

        btn_choose_images.setOnClickListener {
            askGalleryPermission()
        }

        rv_images.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        btn_upload.setOnClickListener {
            val categoryId: Int = radioGroup1.checkedRadioButtonId
            val tervaksinId: Int = radioGroup2.checkedRadioButtonId
            val genderId: Int = radioGroup.checkedRadioButtonId

            if (genderId != -1 && categoryId != -1 && tervaksinId != -1) {
                val gender = view.findViewById(genderId) as RadioButton
                val genderName = gender.text.toString()

                val category = view.findViewById(categoryId) as RadioButton
                val categoryName = category.text.toString()

                var tervaksinValue = false
                val tervaksin = view.findViewById(tervaksinId) as RadioButton
                val tervaksinString = tervaksin.text.toString()

                if (tervaksinString == "Tervaksin") {
                    tervaksinValue = true
                }

                mUserRef.document(mUser?.email.toString()).get()
                    .addOnSuccessListener {
                        val address = it.getString("address").toString()
                        val phone = it.getString("phone").toString()
                        val post = Post(
                            id,
                            address,
                            phone,
                            et_age.text.toString().toInt(),
                            mUser?.email.toString(),
                            categoryName,
                            et_description.text.toString(),
                            genderName,
                            et_name.text.toString(),
                            mListName,
                            et_reason.text.toString(),
                            "Menunggu",
                            tervaksinValue,
                            et_weight.text.toString().toDouble()
                        )
                        Log.d("postData", post.toString())
                        mPostRef.document(id).set(post)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    activity,
                                    "Berhasil mengupload postingan!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            } else {
                Toast.makeText(activity, "Mohon isi form dengan lengkap!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun askGalleryPermission() {
        if (ContextCompat.checkSelfPermission(
                this.context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.activity!!,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                103
            )
        } else {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Images"), RESULT_LOAD_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == AccountFragment.CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                Toast.makeText(activity, "Camera Permission is required!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data?.clipData != null) {
                val totalItemSelected = data.clipData!!.itemCount
                for (i in 0..totalItemSelected - 1) {
                    val fileUri = data.clipData!!.getItemAt(i).uri
                    val fileName = getFileName(fileUri)
                    if (fileName != null) {
                        mListName.add(fileName)
                        mAdapter.notifyDataSetChanged()

                        val file = mStorageRef.child("images").child(fileName)
                        file.putFile(fileUri)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    activity,
                                    "Berhasil mengupload foto",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(activity, it.toString(), Toast.LENGTH_LONG).show()
                            }
                    }
                    Log.d("listFileName", fileName.toString())
                    Log.d("listFileUri", fileUri.toString())
                }
            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor: Cursor? = context?.contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (cut != null) {
                    result = result?.substring(cut + 1)
                }
            }
        }
        return result
    }
}
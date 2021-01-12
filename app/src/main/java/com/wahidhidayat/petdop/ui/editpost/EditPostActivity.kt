package com.wahidhidayat.petdop.ui.editpost

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.ui.account.AccountFragment
import com.wahidhidayat.petdop.ui.detailpost.DetailPostActivity
import com.wahidhidayat.petdop.ui.upload.ImageAdapter
import com.wahidhidayat.petdop.ui.upload.UploadFragment
import kotlinx.android.synthetic.main.activity_edit_post.*
import kotlinx.android.synthetic.main.activity_edit_post.btn_choose_images
import kotlinx.android.synthetic.main.activity_edit_post.constraintLayout
import kotlinx.android.synthetic.main.activity_edit_post.et_age
import kotlinx.android.synthetic.main.activity_edit_post.et_description
import kotlinx.android.synthetic.main.activity_edit_post.et_name
import kotlinx.android.synthetic.main.activity_edit_post.et_reason
import kotlinx.android.synthetic.main.activity_edit_post.et_weight
import kotlinx.android.synthetic.main.activity_edit_post.pb_upload
import kotlinx.android.synthetic.main.activity_edit_post.radioGroup
import kotlinx.android.synthetic.main.activity_edit_post.radioGroup1
import kotlinx.android.synthetic.main.activity_edit_post.radioGroup2
import kotlinx.android.synthetic.main.activity_edit_post.rv_images

class EditPostActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_POST = "extra_post"
    }

    private val mPostRef = FirebaseFirestore.getInstance().collection("posts")
    private val mUserRef = FirebaseFirestore.getInstance().collection("users")
    private val mUser = FirebaseAuth.getInstance().currentUser

    private val mStorageRef = FirebaseStorage.getInstance().reference

    private val mListName: MutableList<String> = mutableListOf()
    private val mAdapter = ImageAdapter(mListName)

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_post)

        post = intent.getParcelableExtra(EXTRA_POST) as Post

        et_age.setText(post.age.toString())
        et_description.setText(post.description)
        et_name.setText(post.name)
        et_reason.setText(post.reason)
        et_weight.setText(post.weight.toString())
        if(post.gender == "Jantan") {
            radioGroup.check(R.id.radio_male)
        } else {
            radioGroup.check(R.id.radio_female)
        }
        if(post.category == "Anjing") {
            radioGroup1.check(R.id.radio_dog)
        } else {
            radioGroup1.check(R.id.radio_cat)
        }
        if(post.tervaksin) {
            radioGroup2.check(R.id.radio_tervaksin)
        } else {
            radioGroup2.check(R.id.radio_belum_tervaksin)
        }

        btn_choose_images.setOnClickListener {
            askGalleryPermission()
        }

        rv_images.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@EditPostActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = mAdapter
        }

        btn_update.setOnClickListener {
            if (pb_upload != null) {
                pb_upload.visibility = View.VISIBLE
            }

            val categoryId: Int = radioGroup1.checkedRadioButtonId
            val tervaksinId: Int = radioGroup2.checkedRadioButtonId
            val genderId: Int = radioGroup.checkedRadioButtonId

            if (et_age.text.toString() != "" && et_description.text.toString() != "" && et_name.text.toString() != "" && et_reason.text.toString() != "" && et_weight.text.toString() != "" && mListName.isNotEmpty()) {
                val gender = findViewById<RadioButton>(genderId)
                val genderName = gender.text.toString()

                val category = findViewById<RadioButton>(categoryId)
                val categoryName = category.text.toString()

                var tervaksinValue = false
                val tervaksin = findViewById<RadioButton>(tervaksinId)
                val tervaksinString = tervaksin.text.toString()

                if (tervaksinString == "Tervaksin") {
                    tervaksinValue = true
                }

                mUserRef.document(mUser?.email.toString()).get()
                        .addOnSuccessListener {
                            val address = it.getString("address").toString()
                            val phone = it.getString("phone").toString()
                            val newPost = Post(
                                    post.id,
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
                                    "Tersedia",
                                    tervaksinValue,
                                    et_weight.text.toString().toDouble()
                            )
                            Log.d("postData", newPost.toString())
                            mPostRef.document(post.id).set(newPost, SetOptions.merge())
                                    .addOnSuccessListener {
                                        if (pb_upload != null) {
                                            pb_upload.visibility = View.GONE
                                        }
                                        val snackbar = Snackbar.make(
                                                constraintLayout,
                                                "Berhasil mengubah postingan!",
                                                Snackbar.LENGTH_LONG
                                        )
                                        snackbar.setAction("Lihat Post") {
                                            val intent = Intent(this, DetailPostActivity::class.java)
                                            intent.putExtra(DetailPostActivity.EXTRA_POST, post)
                                            startActivity(intent)
                                        }
                                        snackbar.show()
                                    }
                        }

            } else if (mListName.size < 2) {
                if (pb_upload != null) {
                    pb_upload.visibility = View.GONE
                }

                Toast.makeText(this, "Foto hewan minimal 2 foto!", Toast.LENGTH_SHORT)
                        .show()
            } else {
                if (pb_upload != null) {
                    pb_upload.visibility = View.GONE
                }

                Toast.makeText(this, "Mohon isi form dengan lengkap!", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    private fun askGalleryPermission() {
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
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
        startActivityForResult(Intent.createChooser(intent, "Select Images"), UploadFragment.RESULT_LOAD_IMAGE)
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
                Toast.makeText(this, "Camera Permission is required!", Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UploadFragment.RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data?.clipData != null) {
                val totalItemSelected = data.clipData!!.itemCount
                for (i in 0 until totalItemSelected) {
                    val fileUri = data.clipData!!.getItemAt(i).uri
                    val fileName = getFileName(fileUri)
                    if (fileName != null) {
                        mListName.add(fileName)
                        mAdapter.notifyDataSetChanged()

                        val file = mStorageRef.child("images").child(fileName)
                        file.putFile(fileUri)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                            this,
                                            "Berhasil mengupload foto",
                                            Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
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
            val cursor: Cursor? = contentResolver?.query(uri, null, null, null, null)
            cursor.use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
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
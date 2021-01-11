package com.wahidhidayat.petdop.ui.detailpost

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.synnapps.carouselview.ImageListener
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.ui.adoption.AdoptionActivity
import kotlinx.android.synthetic.main.activity_detail_post.*

class DetailPostActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_POST = "extra_post"
    }

    private val mUser = FirebaseAuth.getInstance().currentUser
    private val mUserReference = FirebaseFirestore.getInstance().collection("users")
    private val mUserEmail = mUser!!.email
    private var inBookmark = false

    private val  mStorageRef = FirebaseStorage.getInstance().reference

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_post)

        post = intent.getParcelableExtra(EXTRA_POST) as Post
        inBookmark = intent.getBooleanExtra("inBookmark", false)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = post.name

        insertData()

        image_carousel.setImageClickListener { position ->
            val intent = Intent(this@DetailPostActivity, DetailPhotoActivity::class.java)
            intent.putExtra(DetailPhotoActivity.EXTRA_PHOTOS, post)
            intent.putExtra("IMAGE_POSITION", position)
            startActivity(intent)
        }

        if (inBookmark) {
            image_like.setImageDrawable(
                    ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_baseline_favorite_24
                    )
            )
        } else {
            image_like.setImageDrawable(
                    ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_baseline_favorite_border_24
                    )
            )
        }
        Log.d("isBookmarked", inBookmark.toString())

        image_like.setOnClickListener {
            if (inBookmark) {
                removeFromBookmark()
            } else {
                addToBookmark()
            }
        }

        btn_adoption.setOnClickListener {
            val intent = Intent(this, AdoptionActivity::class.java)
            intent.putExtra(AdoptionActivity.EXTRA_POST, post)
            startActivity(intent)
        }
    }

    private fun addToBookmark() {
        mUserReference.document(mUserEmail!!).collection("bookmarks").document(post.id).set(post)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil menambahkan ke bookmark!", Toast.LENGTH_SHORT).show()
                    image_like.setImageDrawable(
                            ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_baseline_favorite_24
                            )
                    )
                    inBookmark = true
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menambahkan ke bookmark: $it", Toast.LENGTH_SHORT)
                            .show()
                }
    }

    private fun removeFromBookmark() {
        mUserReference.document(mUserEmail!!).collection("bookmarks").document(post.id).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil menghapus dari bookmark!", Toast.LENGTH_SHORT).show()
                    image_like.setImageDrawable(
                            ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_baseline_favorite_border_24
                            )
                    )
                    inBookmark = false
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menambahkan ke bookmark: $it", Toast.LENGTH_SHORT)
                            .show()
                }
    }

    private fun insertData() {
        image_carousel.setImageListener(imageListener)
        image_carousel.pageCount = post.photos.size
        text_address.text = post.address
        text_age.text = "${post.age} tahun"
        text_category.text = post.category
        text_description.text = post.description
        text_gender.text = post.gender
        text_reason.text = post.reason
        text_name.text = post.name
        text_weight.text = post.weight.toString()
        if (post.tervaksin) {
            text_status.text = getString(R.string.vaccinated)
        }
        text_status.text = getString(R.string.not_vaccinated)
        text_author.text = post.author
    }

    private var imageListener: ImageListener =
            ImageListener { position, imageView ->
                mStorageRef.child("images/${post.photos[position]}").downloadUrl
                    .addOnSuccessListener {
                        Glide.with(this)
                            .load(it)
                            .into(imageView)
                    }
            }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
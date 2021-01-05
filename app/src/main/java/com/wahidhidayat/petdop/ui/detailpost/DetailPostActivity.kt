package com.wahidhidayat.petdop.ui.detailpost

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.synnapps.carouselview.ImageListener
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import kotlinx.android.synthetic.main.activity_detail_post.*

class DetailPostActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_POST = "extra_post"
    }

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_post)

        post = intent.getParcelableExtra(EXTRA_POST) as Post

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

//        image_like.setOnClickListener {
//            addToBookmark()
//        }
    }

    private fun insertData() {
        image_carousel.setImageListener(imageListener)
        image_carousel.pageCount = post.photos.size
        text_address.text = post.address
        text_age.text = "${post.age.toString()} tahun"
        text_category.text = post.category
        text_description.text = post.description
        text_gender.text = post.gender
        text_reason.text = post.reason
        text_name.text = post.name
        text_weight.text = post.weight.toString()
        if(post.tervaksin) {
            text_status.text = getString(R.string.vaccinated)
        }
        text_status.text = getString(R.string.not_vaccinated)
        text_author.text = post.author
    }

    private var imageListener: ImageListener =
        ImageListener { position, imageView ->
            Glide.with(this)
                .load(post.photos[position])
                .into(imageView)
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
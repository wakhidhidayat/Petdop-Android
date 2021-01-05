package com.wahidhidayat.petdop.ui.detailpost

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import kotlinx.android.synthetic.main.activity_detail_photo.*

class DetailPhotoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PHOTOS = "extra_photos"
    }

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_photo)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_close_24)

        post = intent.getParcelableExtra(EXTRA_PHOTOS) as Post
        val positionInt = intent.getIntExtra("IMAGE_POSITION", 0)
        val photos = post.photos

        val adapter = DetailPhotoAdapter(this, photos, positionInt)
        image_viewpager.adapter = adapter
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
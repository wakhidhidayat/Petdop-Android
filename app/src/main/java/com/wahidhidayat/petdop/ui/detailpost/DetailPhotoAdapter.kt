package com.wahidhidayat.petdop.ui.detailpost

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.wahidhidayat.petdop.R
import kotlinx.android.synthetic.main.item_detail_photo.view.*

class DetailPhotoAdapter(context: Context, private val mListImages: List<String>, val pos: Int) :
    PagerAdapter() {

    private val mContext: Context = context

    private val mStorageRef = FirebaseStorage.getInstance().reference

    override fun getCount(): Int {
        return mListImages.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater: LayoutInflater =
            container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = inflater.inflate(R.layout.item_detail_photo, null)

        mStorageRef.child("images/${mListImages[position]}").downloadUrl
            .addOnSuccessListener {
                Glide.with(mContext)
                    .load(it)
                    .into(layout.image_pet)
            }

        container.addView(layout, 0)
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}
package com.wahidhidayat.petdop.ui.bookmark

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.ui.adoption.AdoptionActivity
import com.wahidhidayat.petdop.ui.detailpost.DetailPostActivity
import kotlinx.android.synthetic.main.item_bookmark.view.*
import kotlinx.android.synthetic.main.item_bookmark.view.btn_adoption
import kotlinx.android.synthetic.main.item_bookmark.view.text_category
import kotlinx.android.synthetic.main.item_bookmark.view.text_gender
import kotlinx.android.synthetic.main.item_bookmark.view.text_name

class BookmarkAdapter(
    private val mListBookmark: MutableList<Post?>,
    private val mContext: Context?,
    private val mDb: FirebaseFirestore
) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(bookmark: Post) {
            Glide.with(itemView.context)
                .load(bookmark.photos[0])
                .into(itemView.image_pet)

            itemView.text_name.text = bookmark.name
            itemView.text_category.text = bookmark.category
            itemView.text_gender.text = bookmark.gender

            itemView.cv_bookmark.setOnClickListener {
                val intent = Intent(itemView.context, DetailPostActivity::class.java)
                intent.putExtra(DetailPostActivity.EXTRA_POST, bookmark)
                intent.putExtra("inBookmark", true)
                itemView.context.startActivity(intent)
            }

            itemView.btn_adoption.setOnClickListener {
                val intent = Intent(itemView.context, AdoptionActivity::class.java)
                intent.putExtra(AdoptionActivity.EXTRA_POST, bookmark)
                itemView.context.startActivity(intent)
            }


            itemView.image_message.setOnClickListener {
                itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://wa.me/${bookmark.phone}")))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mListBookmark.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post? = mListBookmark[position]
        if (post != null) {
            holder.bind(post)
        }
    }
}
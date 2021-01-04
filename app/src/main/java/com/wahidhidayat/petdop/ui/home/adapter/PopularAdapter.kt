package com.wahidhidayat.petdop.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import kotlinx.android.synthetic.main.item_near_you.view.*

class PopularAdapter(
    private val mListPopular: MutableList<Post?>,
    private val mContext: Context?,
    private val mDb: FirebaseFirestore
) : RecyclerView.Adapter<PopularAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(popular: Post) {
            Glide.with(itemView.context)
                .load(popular.photos[0])
                .into(itemView.image_pet)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_popular, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pet: Post? = mListPopular[position]
        if (pet != null) {
            holder.bind(pet)
        }
    }

    override fun getItemCount(): Int {
        return mListPopular.size
    }
}
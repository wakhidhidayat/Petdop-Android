package com.wahidhidayat.petdop.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.ui.detailpost.DetailPostActivity
import kotlinx.android.synthetic.main.item_near_you.view.*

class NearbyAdapter(
    private val mListNearby: MutableList<Post?>,
    private val mContext: Context?,
    private val mDb: FirebaseFirestore
) : RecyclerView.Adapter<NearbyAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(nearby: Post) {
            Glide.with(itemView.context)
                .load(nearby.photos[0])
                .into(itemView.image_pet)

            itemView.image_pet.setOnClickListener {
                val intent = Intent(itemView.context, DetailPostActivity::class.java)
                intent.putExtra(DetailPostActivity.EXTRA_POST, nearby)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_near_you, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pet: Post? = mListNearby[position]
        if (pet != null) {
            holder.bind(pet)
        }
    }

    override fun getItemCount(): Int {
        return mListNearby.size
    }
}
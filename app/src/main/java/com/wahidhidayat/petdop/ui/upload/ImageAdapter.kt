package com.wahidhidayat.petdop.ui.upload

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.wahidhidayat.petdop.R
import kotlinx.android.synthetic.main.item_image.view.*

class ImageAdapter(
        private val mListNames: List<String>
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mStorageRef = FirebaseStorage.getInstance().reference

        fun bind(fileName: String) {
            itemView.text_image_name.text = fileName
            mStorageRef.child("image/${fileName}").downloadUrl
                    .addOnSuccessListener {
                        Glide.with(itemView.context)
                                .load(it)
                    }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mListNames.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileName = mListNames[position]
        holder.bind(fileName)
    }

}
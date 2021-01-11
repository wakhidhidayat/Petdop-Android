package com.wahidhidayat.petdop.ui.upload

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wahidhidayat.petdop.R
import kotlinx.android.synthetic.main.item_images.view.*

class ImageAdapter(
    private val mListNames: List<String>
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_images, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mListNames.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fileName = mListNames[position]
        holder.itemView.text_image_name.text = fileName
    }

}
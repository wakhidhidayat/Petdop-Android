package com.wahidhidayat.petdop.ui.home.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import com.wahidhidayat.petdop.ui.detailpost.DetailPostActivity
import kotlinx.android.synthetic.main.item_popular.view.*

class PopularAdapter(
        private val mListPopular: MutableList<Post?>,
        private val mContext: Context?,
        private val mDb: FirebaseFirestore
) : RecyclerView.Adapter<PopularAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val mUser = FirebaseAuth.getInstance().currentUser
        private val mUserReference = FirebaseFirestore.getInstance().collection("users")
        private val mUserEmail = mUser!!.email
        private val mStorageRef = FirebaseStorage.getInstance().reference

        fun bind(popular: Post) {
            mStorageRef.child("images/${popular.photos[0]}").downloadUrl
                    .addOnSuccessListener {
                        Glide.with(itemView.context)
                                .load(it)
                                .into(itemView.image_pet)
                    }

            itemView.image_pet.setOnClickListener {
                mUserReference.document(mUserEmail!!).collection("bookmarks").get()
                        .addOnCompleteListener {
                            var inBookmark = false

                            if (it.result!!.isEmpty) {
                                inBookmark = false
                            } else {
                                for (document in it.result!!) {
                                    Log.d("document", document.id)
                                    if (document.id == popular.id) {
                                        inBookmark = true
                                    }
                                }
                            }
                            val intent = Intent(itemView.context, DetailPostActivity::class.java)
                            intent.putExtra(DetailPostActivity.EXTRA_POST, popular)
                            intent.putExtra("inBookmark", inBookmark)
                            itemView.context.startActivity(intent)
                        }
            }
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
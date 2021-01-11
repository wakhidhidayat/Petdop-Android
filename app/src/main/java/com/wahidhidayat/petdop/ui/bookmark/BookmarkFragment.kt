package com.wahidhidayat.petdop.ui.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import kotlinx.android.synthetic.main.fragment_bookmark.*

class BookmarkFragment : Fragment() {

    private val mUser = FirebaseAuth.getInstance().currentUser
    private val mUserReference = FirebaseFirestore.getInstance().collection("users")
    private val mUserEmail = mUser!!.email

    private val mList: MutableList<Post?> = mutableListOf()
    private val mAdapter = BookmarkAdapter(mList, context, FirebaseFirestore.getInstance())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadBookmarks()

        swipe_bookmark.setOnRefreshListener {
            mList.clear()
            loadBookmarks()
        }

        rv_bookmark.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = mAdapter
        }
    }

    private fun loadBookmarks() {
        swipe_bookmark.isRefreshing = true
        mUserReference.document(mUserEmail!!).collection("bookmarks")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    swipe_bookmark.isRefreshing = false
                    for (doc in task.result!!) {
                        val post: Post = doc.toObject(Post::class.java)
                        mList.add(post)
                        mAdapter.notifyDataSetChanged()
                    }
                } else {
                    swipe_bookmark.isRefreshing = false
                    Toast.makeText(
                        activity,
                        "Error getting documents: ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                swipe_bookmark.isRefreshing = false
                Toast.makeText(activity, "Error getting documents: $it", Toast.LENGTH_SHORT).show()
            }
    }
}
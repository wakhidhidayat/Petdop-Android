package com.wahidhidayat.petdop.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.wahidhidayat.petdop.R
import com.wahidhidayat.petdop.data.Post
import kotlinx.android.synthetic.main.fragment_explore.*

class ExploreFragment : Fragment() {

    private val mDb = FirebaseFirestore.getInstance()
    private val postList: MutableList<Post?> = mutableListOf()

    private val mAdapter = ExploreAdapter(postList, context, mDb)

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPosts()

        swipe_explore.setOnRefreshListener {
            postList.clear()
            loadPosts()
        }

        rv_explore.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(activity, 3)
            adapter = mAdapter
        }
    }

    private fun loadPosts() {
        swipe_explore.isRefreshing = true
        mDb.collection("posts")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        swipe_explore.isRefreshing = false
                        for (doc in task.result!!) {
                            val post: Post = doc.toObject(Post::class.java)
                            postList.add(post)
                            mAdapter.notifyDataSetChanged()
                        }

                    } else {
                        swipe_explore.isRefreshing = false
                        Toast.makeText(
                                activity,
                                "Error getting documents: ${task.exception}",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    swipe_explore.isRefreshing = false
                    Toast.makeText(activity, "Error getting documents: $it", Toast.LENGTH_SHORT).show()
                }
    }
}